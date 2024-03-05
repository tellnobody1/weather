package io.github.tellnobody1.weather;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import java.util.*;

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
            this.<TextView>findViewById(R.id.windSpeed).setText(getString(R.string.wind_speed, current.windSpeed()));

            var days = data.days();
            if (!days.isEmpty()) {
                var today = days.get(0);

                this.<TextView>findViewById(R.id.feelsLike).setText(getString(R.string.feels_like, current.feelsLike(), today.minTemp(), today.maxTemp()));

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
                this.<UVIndexChartView>findViewById(R.id.uvIndexChart).init(uvIndexes, uvTimes, textView.getTextSize(), textView.getCurrentTextColor());
            }
        });
    }
}
