package io.github.tellnobody1.weather;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import java.util.*;
import static android.graphics.Paint.ANTI_ALIAS_FLAG;
import static android.graphics.Paint.Align.CENTER;

public class UVIndexChartView extends View {
    private final Paint paint = new Paint(ANTI_ALIAS_FLAG);
    private List<Integer> uvIndexes;
    private Integer maxUvIndex;
    private Float textSize;
    private Integer textColor;

    public UVIndexChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (uvIndexes == null || maxUvIndex == null || textColor == null)
            return;

        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(CENTER);
        paint.setStrokeWidth(2);

        var width = (float) getWidth();
        var height = (float) getHeight();
        var padding = paint.measureText("10");
        var chartWidth = width - padding;
        var chartHeight = height - padding;

        // Draw axes
        //   Y-axis
        canvas.drawLine(padding, 0, padding, height - padding, paint);
        //   X-axis
        canvas.drawLine(padding, height - padding, width, height - padding, paint);

        drawAxisValues(canvas, height, padding, chartWidth, chartHeight);

        // Draw title
        drawTitle(width / 2, canvas);

        // Draw data points and lines
        if (!uvIndexes.isEmpty()) {
            var prevX = padding;
            var prevY = height - padding - uvIndexes.get(0) * chartHeight / maxUvIndex;
            for (int i = 1; i < uvIndexes.size(); i++) {
                var x = padding + i * chartWidth / (uvIndexes.size() - 1);
                var y = height - padding - uvIndexes.get(i) * chartHeight / maxUvIndex;
                canvas.drawLine(prevX, prevY, x, y, paint);
                prevX = x;
                prevY = y;
            }
        }
    }

    private void drawAxisValues(Canvas canvas, float height, float padding, float chartWidth, float chartHeight) {
        var fontMetrics = paint.getFontMetrics();
        // Y-axis values
        for (var i = 0; i < maxUvIndex; i++) {
            var value = String.valueOf(i);
            var y = height - padding - (i * chartHeight / maxUvIndex);
            canvas.drawText(value, padding / 2, y + fontMetrics.descent, paint);
        }
        // X-axis values
        var step = 3;
        for (var i = step; i < 24; i += step) {
            var value = String.format("%02d", i);
            var x = padding + (i * chartWidth / 24);
            canvas.drawText(value, x, height - padding - fontMetrics.ascent, paint);
        }
    }

    public void init(List<Integer> uvIndexes, float textSize, int textColor) {
        this.uvIndexes = uvIndexes;
        this.maxUvIndex = Collections.max(uvIndexes) + 1;
        this.textSize = textSize;
        this.textColor = textColor;
        invalidate();
    }

    private void drawTitle(float centerX, Canvas canvas) {
        var text = getContext().getString(R.string.uv_index);
        var y = -paint.getFontMetrics().ascent + paint.getFontMetrics().leading;
        canvas.drawText(text, centerX, y, paint);
    }
}
