package io.github.tellnobody1.weather;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import io.github.tellnobody1.weather.WeatherData.UVData;
import java.util.Collections;
import static android.graphics.Color.*;
import static android.graphics.Paint.ANTI_ALIAS_FLAG;
import static android.graphics.Paint.Align.CENTER;
import static android.graphics.Paint.Join.ROUND;
import static android.graphics.Paint.Style.STROKE;

public class UVChart extends View {
    public static final int ORANGE = Color.parseColor("#FFA500");
    public static final int VIOLET = Color.parseColor("#8A2BE2");

    private final Paint paint = new Paint(ANTI_ALIAS_FLAG) {{
        setTextAlign(CENTER);
        setStrokeWidth(2);
        setStrokeJoin(ROUND);
    }};

    private float width() { return (float) getWidth(); }
    private float height() { return (float) getHeight(); }
    private float padding;
    private float chartWidth() { return width() - padding; }
    private float chartHeight() { return height() - padding; }

    private final Path path = new Path();

    private UVData uvData;
    private int maxUvIndex;
    private boolean init = false;
    private float timeProgress;

    public UVChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (init) {
            drawNow(canvas);
            drawAxes(canvas);
            drawAxesValues(canvas);
            drawTitle(canvas);
            drawLine(canvas);
        }
    }

    private void drawLine(Canvas canvas) {
        var color = paint.getColor();
        var strokeWidth = paint.getStrokeWidth();
        var style = paint.getStyle();
        paint.setStrokeWidth(width() / 120);
        paint.setColor(switch (maxUvIndex - 1) {
            case 0, 1, 2 -> GREEN;
            case 3, 4, 5 -> YELLOW;
            case 6, 7 -> ORANGE;
            case 8, 9, 10 -> RED;
            default -> VIOLET;
        });
        paint.setStyle(STROKE);

        path.reset();
        for (var i = 0; i < uvData.indexes().size(); i++) {
            var index = uvData.indexes().get(i);
            if (i == 0) {
                path.moveTo(padding, chartHeight() - index * chartHeight() / maxUvIndex);
            } else {
                var x = padding + i * chartWidth() / (uvData.indexes().size() - 1);
                var y = chartHeight() - index * chartHeight() / maxUvIndex;
                path.lineTo(x, y);
            }
        }
        canvas.drawPath(path, paint);

        paint.setColor(color);
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(style);
    }

    private void drawNow(Canvas canvas) {
        var effect = paint.getPathEffect();
        var alpha = paint.getAlpha();
        var interval = padding / 5;
        paint.setPathEffect(new DashPathEffect(new float[]{interval, interval}, 0));
        paint.setAlpha(128);

        var x = padding + timeProgress * chartWidth();
        var y = chartHeight() / maxUvIndex;
        canvas.drawLine(x, y, x, chartHeight(), paint);

        paint.setPathEffect(effect);
        paint.setAlpha(alpha);
    }

    private void drawAxes(Canvas canvas) {
        canvas.drawLine(padding, padding / 2, padding, chartHeight(), paint); // Y-axis
        canvas.drawLine(padding, chartHeight(), width(), chartHeight(), paint); // X-axis
    }

    private void drawAxesValues(Canvas canvas) {
        var fontMetrics = paint.getFontMetrics();
        // Y-axis values
        for (var i = 0; i < maxUvIndex; i++) {
            var value = String.valueOf(i);
            var y = chartHeight() - (i * chartHeight() / maxUvIndex);
            canvas.drawText(value, padding / 2, y + fontMetrics.descent, paint);
        }
        // X-axis values
        for (var t : uvData.times()) {
            if (t == 0 || t == 24) continue;
            var value = String.format("%02d", t);
            var x = padding + t * chartWidth() / 24;
            canvas.drawText(value, x, chartHeight() - fontMetrics.ascent, paint);
        }
    }

    private void drawTitle(Canvas canvas) {
        var fontMetrics = paint.getFontMetrics();
        var y = fontMetrics.descent - fontMetrics.ascent - (fontMetrics.bottom - fontMetrics.descent);
        var x = width() / 2;
        canvas.drawText(getContext().getString(R.string.uv_index), x, y, paint);
    }

    public void init(UVData uvData, float textSize, int textColor, float timeProgress) {
        if (uvData.indexes().isEmpty()) {
            this.init = false;
        } else {
            this.uvData = uvData;
            this.maxUvIndex = Collections.max(uvData.indexes()) + 1;
            this.timeProgress = timeProgress;

            paint.setTextSize(textSize);
            paint.setColor(textColor);

            this.padding = paint.measureText("10");

            this.init = true;
            invalidate();
        }
    }
}
