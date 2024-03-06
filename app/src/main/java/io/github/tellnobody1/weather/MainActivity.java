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
        WeatherFetcher.fetch("https://wttr.in/?format=j1", data -> {
            var current = data.current();
            this.<TextView>findViewById(R.id.dateTime).setText(current.dateTime());

            int windScale;
            var windSpeed = current.windSpeed();
            if (windSpeed < 2) windScale = R.string.wind_0;
            else if (windSpeed < 6) windScale = R.string.wind_1;
            else if (windSpeed < 12) windScale = R.string.wind_2;
            else if (windSpeed < 20) windScale = R.string.wind_3;
            else if (windSpeed < 29) windScale = R.string.wind_4;
            else if (windSpeed < 39) windScale = R.string.wind_5;
            else if (windSpeed < 50) windScale = R.string.wind_6;
            else if (windSpeed < 62) windScale = R.string.wind_7;
            else if (windSpeed < 75) windScale = R.string.wind_8;
            else if (windSpeed < 88) windScale = R.string.wind_9;
            else if (windSpeed < 103) windScale = R.string.wind_10;
            else if (windSpeed < 118) windScale = R.string.wind_11;
            else windScale = R.string.wind_12;
            this.<TextView>findViewById(R.id.windSpeed).setText(getString(R.string.wind, getString(windScale)));

            var days = data.days();
            if (!days.isEmpty()) {
                var today = days.get(0);

                var minTemp = data.getMinTemp();
                this.<TextView>findViewById(R.id.feelsLike).setText(getString(R.string.feels_like, current.feelsLike(), minTemp != null ? minTemp : today.maxTemp(), today.maxTemp()));

                this.<TextView>findViewById(R.id.sunset).setText(getString(R.string.sunset, today.sunset()));

                var uvIndexes = new LinkedList<Integer>();
                var uvTimes = new LinkedList<Integer>();
                for (var hour : today.hours()) {
                    uvIndexes.add(hour.uvIndex());
                    uvTimes.add(hour.time());
                }
                if (days.size() > 1) {
                    var tomorrow = days.get(1);
                    var hours = tomorrow.hours();
                    var noon = hours.get(hours.size() - 1);
                    uvIndexes.add(noon.uvIndex());
                    uvTimes.add(24);
                } else {
                    uvIndexes.add(uvIndexes.get(uvIndexes.size() - 1));
                    uvTimes.add(24);
                }

                var textView = this.<TextView>findViewById(R.id.dateTime);
                this.<UVChart>findViewById(R.id.uvChart).init(uvIndexes, uvTimes, textView.getTextSize(), textView.getCurrentTextColor());
            }
        });
    }
}
