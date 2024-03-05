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
    private List<Integer> indexes;
    private List<Integer> times;
    private Integer maxUvIndex;
    private Float textSize;
    private Integer textColor;
    private boolean init = false;

    public UVIndexChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!init)
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
        if (!indexes.isEmpty()) {
            var prevX = padding;
            var prevY = height - padding - indexes.get(0) * chartHeight / maxUvIndex;
            for (var i = 1; i < indexes.size(); i++) {
                var x = padding + i * chartWidth / (indexes.size() - 1);
                var y = height - padding - indexes.get(i) * chartHeight / maxUvIndex;
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
        for (var t : times) {
            if (t == 0 || t == 24) continue;
            var value = String.format("%02d", t);
            var x = padding + t * chartWidth / 24;
            canvas.drawText(value, x, height - padding - fontMetrics.ascent, paint);
        }
    }

    public void init(List<Integer> indexes, List<Integer> times, float textSize, int textColor) {
        this.indexes = indexes;
        this.maxUvIndex = Collections.max(indexes) + 1;
        this.times = times;
        this.textSize = textSize;
        this.textColor = textColor;
        this.init = true;
        invalidate();
    }

    private void drawTitle(float centerX, Canvas canvas) {
        var text = getContext().getString(R.string.uv_index);
        var y = -paint.getFontMetrics().ascent + paint.getFontMetrics().leading;
        canvas.drawText(text, centerX, y, paint);
    }
}
