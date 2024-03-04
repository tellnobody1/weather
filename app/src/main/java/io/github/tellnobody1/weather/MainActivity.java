package io.github.tellnobody1.weather;

import android.app.Activity;
import android.os.Bundle;

import java.util.LinkedList;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        WeatherFetcher.fetchWeatherData("https://wttr.in/Kyiv?format=j1", data -> {
            var weather = data.days();
            if (weather.size() > 0) {
                var day = weather.get(0);
                var xs = new LinkedList<Integer>();
                for (var hour : day.hours())
                    xs.add(hour.uvIndex());
                if (weather.size() > 1) {
                    var hourly = weather.get(1).hours();
                    xs.add(hourly.get(hourly.size() - 1).uvIndex());
                } else {
                    xs.add(xs.get(xs.size() - 1));
                }
                UVIndexChartView chartView = findViewById(R.id.simpleLineChartView);
                chartView.setUvIndexValues(xs);
                chartView.invalidate();
            }
        });
    }
}
