package io.github.tellnobody1.weather;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import java.util.*;
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
    private float width;
    private float height;
    private float padding;
    private float chartWidth;
    private float chartHeight;

    private final Path path = new Path();

    private List<Integer> indexes;
    private List<Integer> times;
    private int maxUvIndex;
    private boolean init = false;

    public UVChart(Context context, AttributeSet attrs) {
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

    private void drawLine(Canvas canvas) {
        final var color = paint.getColor();
        final var strokeWidth = paint.getStrokeWidth();
        final var style = paint.getStyle();
        paint.setStrokeWidth(width / 120);
        paint.setColor(switch (maxUvIndex - 1) {
            case 0, 1, 2 -> Color.GREEN;
            case 3, 4, 5 -> Color.YELLOW;
            case 6, 7 -> ORANGE;
            case 8, 9, 10 -> Color.RED;
            default -> VIOLET;
        });
        paint.setStyle(STROKE);

        final var prevX = padding;
        final var prevY = height - padding - indexes.get(0) * chartHeight / maxUvIndex;
        path.moveTo(prevX, prevY);
        for (var i = 1; i < indexes.size(); i++) {
            var index = indexes.get(i);
            var x = padding + i * chartWidth / (indexes.size() - 1);
            var y = height - padding - index * chartHeight / maxUvIndex;
            path.lineTo(x, y);
        }
        canvas.drawPath(path, paint);

        paint.setColor(color);
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(style);
    }

    private void drawAxes(Canvas canvas) {
        canvas.drawLine(padding, padding / 2, padding, height - padding, paint); // Y-axis
        canvas.drawLine(padding, height - padding, width, height - padding, paint); // X-axis
    }

    private void drawAxesValues(Canvas canvas) {
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

    private void drawTitle(Canvas canvas) {
        var fontMetrics = paint.getFontMetrics();
        var y = fontMetrics.descent - fontMetrics.ascent - (fontMetrics.bottom - fontMetrics.descent);
        var x = width / 2;
        canvas.drawText(getContext().getString(R.string.uv_index), x, y, paint);
    }

    public void init(List<Integer> indexes, List<Integer> times, float textSize, int textColor) {
        if (indexes.isEmpty()) {
            init = false;
        } else {
            this.indexes = indexes;
            this.maxUvIndex = Collections.max(indexes) + 1;
            this.times = times;

            paint.setTextSize(textSize);
            paint.setColor(textColor);

            this.width = (float) getWidth();
            this.height = (float) getHeight();
            this.padding = paint.measureText("10");
            this.chartWidth = width - padding;
            this.chartHeight = height - padding;

            init = true;
            invalidate();
        }
    }
}
