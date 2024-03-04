package io.github.tellnobody1.weather;

import android.app.Activity;
import android.os.Bundle;

import java.util.LinkedList;
import java.util.List;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        WeatherFetcher.fetchWeatherData("https://wttr.in/Kyiv?format=j1", new WeatherFetcher.OnWeatherDataReceivedListener() {
            @Override
            public void onReceived(WeatherData weatherData) {
                List<WeatherData.Day> weather = weatherData.getWeather();
                for (int i = 0; i < weather.size(); i++) {
                    var day = weather.get(i);
                    var xs = new LinkedList<Integer>();
                    for (var x : day.hourly()) {
                        xs.add(x.getUvIndex());
                    }
                    if (i + 1 < weather.size()) {
                        xs.add(weather.get(i + 1).hourly().get(weather.get(i + 1).hourly().size() - 1).getUvIndex());
                    }
                    runOnUiThread(() -> {
                        SimpleLineChartView chartView = findViewById(R.id.simpleLineChartView);
                        chartView.setUvIndexValues(xs);
                        chartView.invalidate();
                    });
                    break;
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }
}
