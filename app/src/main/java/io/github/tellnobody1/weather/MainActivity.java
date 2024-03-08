package io.github.tellnobody1.weather;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import java.text.DateFormat;
import java.util.Calendar;
import static android.view.View.*;
import static java.text.DateFormat.SHORT;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        this.<Button>findViewById(R.id.refresh).setOnClickListener(this::fetchData);
        fetchData();
    }

    private DateFormat timeFormat() {
        var locale = getResources().getConfiguration().locale;
        return DateFormat.getTimeInstance(SHORT, locale);
    }

    private int nowHours() {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }

    private String nowTime() {
        return timeFormat().format(Calendar.getInstance().getTime());
    }

    private void fetchData(View v) { fetchData(); }

    private void fetchData() {
        WeatherFetcher.fetch("https://wttr.in/?format=j1", timeFormat(), this::updateUI);
    }

    private void updateUI(WeatherData data) {
        setTime();
        setTodayWeather(data);
        setSunset(data);
        setUVChart(data);
    }

    private void setTime() {
        this.<TextView>findViewById(R.id.dateTime).setText(getString(R.string.updated_time, nowTime()));
    }

    private void setTodayWeather(WeatherData data) {
        var todayWeather = data.todayWeather(nowHours());
        // temperature (now, min, max)
        TextView temperature = findViewById(R.id.temperature);
        temperature.setVisibility(todayWeather == null ? GONE : VISIBLE);
        if (todayWeather != null)
            temperature.setText(getString(
                R.string.temperature,
                todayWeather.feelsNow(),
                todayWeather.feelsMin(),
                todayWeather.feelsMax()
            ));
        // wind force
        TextView windForce = findViewById(R.id.windForce);
        windForce.setVisibility(todayWeather == null ? GONE : VISIBLE);
        if (todayWeather != null) {
            windForce.setText(getString(
                    R.string.wind,
                    getString(todayWeather.windForce())
            ));
        }
    }

    private void setSunset(WeatherData data) {
        TextView sunset = findViewById(R.id.sunset);
        sunset.setVisibility(data.days().isEmpty() ? GONE : VISIBLE);
        if (!data.days().isEmpty()) {
            var today = data.days().get(0);
            sunset.setText(getString(R.string.sunset, today.sunset()));
        }
    }

    private void setUVChart(WeatherData data) {
        TextView textView = findViewById(R.id.dateTime);
        var textSize = textView.getTextSize();
        var textColor = textView.getCurrentTextColor();
        this.<UVChart>findViewById(R.id.uvChart).init(data.uvData(), textSize, textColor);
    }
}
