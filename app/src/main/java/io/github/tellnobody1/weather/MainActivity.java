package io.github.tellnobody1.weather;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import static android.view.View.*;

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
        WeatherFetcher.fetch("https://wttr.in/?format=j1", this::updateUI);
    }

    private void updateUI(WeatherData data) {
        setTime(data);
        setWindForce(data);
        setTemperature(data);
        setSunset(data);
        setUVChart(data);
    }

    private void setTime(WeatherData data) {
        this.<TextView>findViewById(R.id.dateTime).setText(data.current().dateTime());
    }

    private void setWindForce(WeatherData data) {
        this.<TextView>findViewById(R.id.windForce).setText(getString(
                R.string.wind,
                getString(data.getWindForce())
        ));
    }

    private void setTemperature(WeatherData data) {
        TextView temperature = findViewById(R.id.temperature);
        var minTemp = data.getMinTemp();
        if (minTemp == null || data.days().isEmpty()) {
            temperature.setText(getString(
                    R.string.temperature_short,
                    data.current().feelsLike()
            ));
        } else {
            var today = data.days().get(0);
            temperature.setText(getString(
                    R.string.temperature,
                    data.current().feelsLike(),
                    minTemp,
                    today.maxTemp()
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
        this.<UVChart>findViewById(R.id.uvChart).init(data.getUVData(), textSize, textColor);
    }
}
