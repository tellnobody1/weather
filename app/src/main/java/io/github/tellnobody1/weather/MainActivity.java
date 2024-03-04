package io.github.tellnobody1.weather;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.LinkedList;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        WeatherFetcher.fetch("https://wttr.in/Kyiv?format=j1", data -> {
            var current = data.current();
            if (current != null) {
                var dateTimeView = this.<TextView>findViewById(R.id.dateTime);
                dateTimeView.setText(current.dateTime());
                var feelsLikeView = this.<TextView>findViewById(R.id.feelsLike);
                feelsLikeView.setText(getString(R.string.feels_like, current.feelsLike()));
                var windSpeedView = this.<TextView>findViewById(R.id.windSpeed);
                windSpeedView.setText(getString(R.string.wind_speed, current.windSpeed()));
            }

            var days = data.days();
            if (days.size() > 0) {
                var day = days.get(0);
                var uvIndexes = new LinkedList<Integer>();
                for (var hour : day.hours())
                    uvIndexes.add(hour.uvIndex());
                if (days.size() > 1) {
                    var hourly = days.get(1).hours();
                    uvIndexes.add(hourly.get(hourly.size() - 1).uvIndex());
                } else {
                    uvIndexes.add(uvIndexes.get(uvIndexes.size() - 1));
                }
                var uvIndexChartView = this.<UVIndexChartView>findViewById(R.id.uvIndexChart);
                uvIndexChartView.setUvIndexValues(uvIndexes);
                uvIndexChartView.setTextColor(this.<TextView>findViewById(R.id.dateTime).getCurrentTextColor());
                uvIndexChartView.invalidate();
            }
        });
    }
}
