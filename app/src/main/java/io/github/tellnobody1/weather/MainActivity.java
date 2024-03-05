package io.github.tellnobody1.weather;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import java.util.LinkedList;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        this.<Button>findViewById(R.id.refresh).setOnClickListener(this::fetchData);

        fetchData();
    }

    private void fetchData(View v) { fetchData(); }

    private void fetchData() {
        WeatherFetcher.fetch("https://wttr.in/Kyiv?format=j1", data -> {
            var current = data.current();
            this.<TextView>findViewById(R.id.dateTime).setText(current.dateTime());
            this.<TextView>findViewById(R.id.feelsLike).setText(getString(R.string.feels_like, current.feelsLike()));
            this.<TextView>findViewById(R.id.windSpeed).setText(getString(R.string.wind_speed, current.windSpeed()));

            var days = data.days();
            if (!days.isEmpty()) {
                var day = days.get(0);

                this.<TextView>findViewById(R.id.sunset).setText(getString(R.string.sunset, day.sunset()));

                var uvIndexes = new LinkedList<Integer>();
                for (var hour : day.hours())
                    uvIndexes.add(hour.uvIndex());
                if (days.size() > 1) {
                    var hourly = days.get(1).hours();
                    uvIndexes.add(hourly.get(hourly.size() - 1).uvIndex());
                } else {
                    uvIndexes.add(uvIndexes.get(uvIndexes.size() - 1));
                }

                var textView = this.<TextView>findViewById(R.id.dateTime);
                this.<UVIndexChartView>findViewById(R.id.uvIndexChart).init(uvIndexes, textView.getTextSize(), textView.getCurrentTextColor());
            }
        });
    }
}
