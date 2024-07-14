package io.github.tellnobody1.weather;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import java.util.Collections;
import static android.graphics.Paint.ANTI_ALIAS_FLAG;
import static android.graphics.Paint.Align.CENTER;
import static android.graphics.Paint.Join.ROUND;
import static android.graphics.Paint.Style.STROKE;

public class TempChart extends View {
    public static final int ORANGE = Color.parseColor("#FFA500");

    private final Paint paint = new Paint(ANTI_ALIAS_FLAG) {{
        setTextAlign(CENTER);
        setStrokeWidth(2);
        setStrokeJoin(ROUND);
    }};

    private float width() { return (float) getWidth(); }
    private float height() { return (float) getHeight(); }
    private float padding;
    private float paddingTop() { return padding * 1.5f; }
    private float chartWidth() { return width() - padding; }
    private float chartHeight() { return height() - padding; }

    private final Path path = new Path();

    private TempForecast data;
    private boolean init = false;

    public TempChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (init) {
            drawAxes(canvas);
            drawAxesValues(canvas);
            drawTitle(canvas);
            drawLine(canvas);
        }
    }

    private float f(int t) {
        var p = paddingTop();
        var h = chartHeight();
        return p - (p - h) * (Collections.max(data.values()) - t) / (Collections.max(data.values()) - Collections.min(data.values()));
    }

    private void drawLine(Canvas canvas) {
        var color = paint.getColor();
        var strokeWidth = paint.getStrokeWidth();
        var style = paint.getStyle();
        paint.setStrokeWidth(width() / 120);
        paint.setColor(ORANGE);
        paint.setStyle(STROKE);

        path.reset();
        for (var i = 0; i < data.values().size(); i++) {
            var temp = data.values().get(i);
            if (i == 0) {
                path.moveTo(padding, f(temp));
            } else {
                var x = padding + i * chartWidth() / (data.values().size() - 1);
                var y = f(temp);
                path.lineTo(x, y);
            }
        }
        canvas.drawPath(path, paint);

        paint.setColor(color);
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(style);
    }

    private void drawAxes(Canvas canvas) {
        canvas.drawLine(padding, padding / 2, padding, chartHeight(), paint); // Y-axis
        canvas.drawLine(padding, chartHeight(), width(), chartHeight(), paint); // X-axis
    }

    private void drawAxesValues(Canvas canvas) {
        var fontMetrics = paint.getFontMetrics();
        // Y-axis values
        canvas.drawText(String.valueOf(Collections.min(data.values())), padding / 2, chartHeight() + fontMetrics.descent, paint);
        canvas.drawText(String.valueOf(Collections.max(data.values())), padding / 2, paddingTop() + fontMetrics.descent, paint);
    }

    private float titleHeight() {
        var fontMetrics = paint.getFontMetrics();
        return fontMetrics.descent - fontMetrics.ascent - (fontMetrics.bottom - fontMetrics.descent);
    }

    private void drawTitle(Canvas canvas) {
        var y = titleHeight();
        var x = width() / 2;
        canvas.drawText(getContext().getString(R.string.temp_forecast), x, y, paint);
    }

    public void init(TempForecast tempForecast, float textSize, int textColor) {
        this.data = tempForecast;

        paint.setTextSize(textSize);
        paint.setColor(textColor);

        this.padding = paint.measureText("10");

        this.init = true;
        invalidate();
    }
}
