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
import static java.lang.Math.*;

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
            var xIntervals = (int) ceil(log2(maxUvIndex)) + 1;
            var xInterval = chartHeight() / xIntervals;
            drawNow(canvas, xInterval);
            drawAxes(canvas);
            drawAxesValues(canvas, xIntervals, xInterval);
            drawTitle(canvas);
            drawLine(canvas, xInterval);
        }
    }

    private void drawLine(Canvas canvas, float xInterval) {
        var color = paint.getColor();
        var strokeWidth = paint.getStrokeWidth();
        var style = paint.getStyle();
        paint.setStrokeWidth(width() / 120);
        paint.setColor(switch (maxUvIndex) {
            case 0, 1, 2 -> GREEN;
            case 3, 4, 5 -> YELLOW;
            case 6, 7 -> ORANGE;
            case 8, 9, 10 -> RED;
            default -> VIOLET;
        });
        paint.setStyle(STROKE);

        path.reset();
        for (var i = 0; i < uvData.indexes().size(); i++) {
            var idx = uvData.indexes().get(i);
            var x = padding + i * chartWidth() / (uvData.indexes().size() - 1);
            var y = chartHeight();
            if (idx != 0) {
                var idxLog = log2(idx);
                var idxLogFloor = (int) floor(idxLog);
                var idxLogCeil = (int) ceil(idxLog);
                var idxFloor = pow2(idxLogFloor);
                var idxCeil = pow2(idxLogCeil);
                y -= idxLogFloor * xInterval;
                if (idxFloor != idxCeil)
                    y -= xInterval * (idx - idxFloor) / (idxCeil - idxFloor);
            }
            if (i == 0) path.moveTo(x, (int) y);
            else path.lineTo(x, (int) y);
        }
        canvas.drawPath(path, paint);

        paint.setColor(color);
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(style);
    }

    private void drawNow(Canvas canvas, float xInterval) {
        var effect = paint.getPathEffect();
        var alpha = paint.getAlpha();
        var interval = padding / 5;
        paint.setPathEffect(new DashPathEffect(new float[]{interval, interval}, 0));
        paint.setAlpha(128);

        var x = padding + timeProgress * chartWidth();
        canvas.drawLine(x, xInterval, x, chartHeight(), paint);

        paint.setPathEffect(effect);
        paint.setAlpha(alpha);
    }

    private void drawAxes(Canvas canvas) {
        canvas.drawLine(padding, padding / 2, padding, chartHeight(), paint); // Y-axis
        canvas.drawLine(padding, chartHeight(), width(), chartHeight(), paint); // X-axis
    }

    private void drawAxesValues(Canvas canvas, int xIntervals, float xInterval) {
        var fontMetrics = paint.getFontMetrics();
        // Y-axis values
        canvas.drawText("0", padding / 2, chartHeight() + fontMetrics.descent, paint);
        for (var i = 1; i < xIntervals; i++) {
            var value = String.valueOf(pow2(i));
            var y = chartHeight() - i * xInterval;
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
            this.maxUvIndex = Collections.max(uvData.indexes());
            this.timeProgress = timeProgress;

            paint.setTextSize(textSize);
            paint.setColor(textColor);

            this.padding = paint.measureText("10");

            this.init = true;
            invalidate();
        }
    }

    private static double log2(double x) {
        return log(x) / log(2);
    }

    private static int pow2(int x) {
        return (int) pow(2, x);
    }
}
