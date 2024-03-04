package io.github.tellnobody1.weather;

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
    private final Paint paint = new Paint();
    private List<Integer> uvIndexList = new ArrayList<>();
    private int maxUvIndex = 1;

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

        // Draw axes
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(2);
        canvas.drawLine(padding, padding, padding, height - padding, paint); // Y-axis
        canvas.drawLine(padding, height - padding, width - padding, height - padding, paint); // X-axis

        // Draw axis values
        paint.setTextSize(20);
        // Y-axis values
        for (int i = 0; i <= maxUvIndex; i++) {
            String value = String.valueOf(i);
            float y = height - padding - ((float) (i * chartHeight) / maxUvIndex);
            canvas.drawText(value, padding - 40, y + 10, paint);
        }
        // X-axis values (assuming 24 hours)
        for (int i = 0; i <= 24; i += 3) {
            String value = String.format("%02d:00", i);
            float x = padding + ((float) (i * chartWidth) / 24);
            canvas.drawText(value, x - 20, height - padding + 30, paint);
        }

        // Draw data points and lines
        if (!uvIndexList.isEmpty()) {
            paint.setColor(Color.YELLOW);
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

    public void setUvIndexValues(List<Integer> uvIndexList) {
        this.uvIndexList = uvIndexList;
        maxUvIndex = Collections.max(uvIndexList) + 1;
    }
}
