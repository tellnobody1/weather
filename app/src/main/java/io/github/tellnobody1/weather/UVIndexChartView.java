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
    private int maxUvIndex;
    private float textSize;
    private int textColor;
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

        drawTitle(width / 2, canvas);

        // Draw data points and lines
        if (!indexes.isEmpty()) {
            var prevX = padding;
            var prevY = height - padding - indexes.get(0) * chartHeight / maxUvIndex;
            for (var i = 1; i < indexes.size(); i++) {
                var index = indexes.get(i);
                var prevIndex = indexes.get(i - 1);
                var x = padding + i * chartWidth / (indexes.size() - 1);
                var y = height - padding - index * chartHeight / maxUvIndex;
                if (prevIndex != 0 || index != 0) {
                    var color = paint.getColor();
                    var strokeWidth = paint.getStrokeWidth();
                    paint.setStrokeWidth(width / 120);
                    paint.setColor(switch (Math.max(prevIndex, index)) {
                        case 0, 1, 2 -> Color.GREEN;
                        case 3, 4, 5 -> Color.YELLOW;
                        case 6, 7 -> Color.parseColor("#FFA500");
                        case 8, 9, 10 -> Color.RED;
                        default -> Color.parseColor("#8A2BE2");
                    });
                    canvas.drawLine(prevX, prevY, x, y, paint);
                    paint.setColor(color);
                    paint.setStrokeWidth(strokeWidth);
                }
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
        if (indexes.isEmpty()) return;
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
