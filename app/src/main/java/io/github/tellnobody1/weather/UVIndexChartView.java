package io.github.tellnobody1.weather;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;
import static android.graphics.Paint.Align.CENTER;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UVIndexChartView extends View {
    private final Paint paint = new Paint(ANTI_ALIAS_FLAG);
    private List<Integer> uvIndexList = new ArrayList<>();
    private int maxUvIndex = 1;
    private Integer color;

    public UVIndexChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int padding = 50;
        int chartWidth = width - 2 * padding;
        int chartHeight = height - 2 * padding;
        paint.setColor(color != null ? color : Color.GREEN);
        paint.setStrokeWidth(2);

        // Draw axes
        canvas.drawLine(padding, padding, padding, height - padding, paint); // Y-axis
        canvas.drawLine(padding, height - padding, width - padding, height - padding, paint); // X-axis

        drawAxisValues(canvas, height, padding, chartWidth, chartHeight);

        // Draw title
        drawCenteredText(getContext().getString(R.string.uv_index), (float) width / 2, padding, canvas);

        // Draw data points and lines
        if (!uvIndexList.isEmpty()) {
            int prevX = padding;
            int prevY = height - padding - uvIndexList.get(0) * chartHeight / maxUvIndex;
            for (int i = 1; i < uvIndexList.size(); i++) {
                int x = padding + i * chartWidth / (uvIndexList.size() - 1);
                int y = height - padding - uvIndexList.get(i) * chartHeight / maxUvIndex;
                canvas.drawLine(prevX, prevY, x, y, paint);
                prevX = x;
                prevY = y;
            }
        }
    }

    private void drawAxisValues(Canvas canvas, int height, int padding, int chartWidth, int chartHeight) {
        var textSize = paint.getTextSize();
        paint.setTextSize(20);
        // Y-axis values
        for (int i = 0; i <= maxUvIndex; i++) {
            String value = String.valueOf(i);
            float y = height - padding - ((float) (i * chartHeight) / maxUvIndex);
            canvas.drawText(value, padding - 40, y + 10, paint);
        }
        // X-axis values (assuming 24 hours)
        for (int i = 0; i <= 24; i += 3) {
            String value = String.format("%02d", i);
            float x = padding + ((float) (i * chartWidth) / 24);
            canvas.drawText(value, x - 20, height - padding + 30, paint);
        }
        paint.setTextSize(textSize);
    }

    public void setUvIndexValues(List<Integer> uvIndexList) {
        this.uvIndexList = uvIndexList;
        maxUvIndex = Collections.max(uvIndexList) + 1;
    }

    public void setTextColor(int color) {
        this.color = color;
    }

    private void drawCenteredText(String text, float centerX, float centerY, Canvas canvas) {
        var textAlign = paint.getTextAlign();
        var textSize = paint.getTextSize();
        paint.setTextAlign(CENTER);
        paint.setTextSize(30);

        // Calculate the vertical centering offset
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float textHeight = fontMetrics.descent - fontMetrics.ascent;
        float baselineOffset = textHeight - fontMetrics.descent;

        // Draw the text centered around (centerX, centerY)
        canvas.drawText(text, centerX, centerY + baselineOffset, paint);

        paint.setTextAlign(textAlign);
        paint.setTextSize(textSize);
    }
}
