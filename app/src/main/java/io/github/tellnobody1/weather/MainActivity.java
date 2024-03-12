package io.github.tellnobody1.weather;

import android.app.Activity;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import java.text.DateFormat;
import java.util.Calendar;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH;
import static android.os.Build.VERSION_CODES.N;
import static android.view.View.*;
import static java.text.DateFormat.SHORT;
import static java.util.Calendar.HOUR_OF_DAY;

public class MainActivity extends Activity {
    private WeatherData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        fetchData();

        if (SDK_INT >= ICE_CREAM_SANDWICH)
            if (!ViewConfiguration.get(this).hasPermanentMenuKey()) {
                Button btn = findViewById(R.id.refresh);
                btn.setVisibility(VISIBLE);
                btn.setOnClickListener(this::fetchData);
            }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            fetchData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (data != null) updateUI(data, false);
    }

    private DateFormat timeFormat() {
        var conf = getResources().getConfiguration();
        var locale = SDK_INT >= N ? conf.getLocales().get(0) : conf.locale;
        return DateFormat.getTimeInstance(SHORT, locale);
    }

    private Calendar now() {
        return Calendar.getInstance();
    }

    private void fetchData(View v) { fetchData(); }

    private void fetchData() {
        WeatherFetcher.fetch(
            "Київ",
            timeFormat(),
            now(),
            data -> updateUI(data, true),
            x -> Toast.makeText(this, "Outdated certificates", Toast.LENGTH_SHORT).show()
        );
    }

    private void updateUI(WeatherData data, boolean now) {
        this.data = data;
        setTime(data, now);
        setTodayWeather(data);
        setSunset(data);
        setUVChart(data);
    }

    private void setTime(WeatherData data, boolean now) {
        TextView dateTime = findViewById(R.id.dateTime);
        dateTime.setText(getString(R.string.updated, getString(now ?
            R.string.now :
            switch (hoursBeforeNow(data)) {
                case 0 -> R.string.hour_0;
                case 1 -> R.string.hour_1;
                case 2 -> R.string.hour_2;
                default -> R.string.hour_3;
            }
        )));
    }

    private int hoursBeforeNow(WeatherData data) {
        var msDiff = now().getTimeInMillis() - data.dateTime().getTimeInMillis();
        var msInDay = 3600 * 1000;
        return (int) ((float) msDiff / msInDay);
    }

    private void setTodayWeather(WeatherData data) {
        var todayWeather = data.todayWeather(now().get(HOUR_OF_DAY));
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
        TextView windGustForce = findViewById(R.id.windGustForce);
        if (todayWeather != null) {
            windForce.setVisibility(VISIBLE);
            windForce.setText(getString(
                    R.string.wind,
                    getString(todayWeather.windForce())
            ));
            if (todayWeather.windGustForce() != todayWeather.windForce()) {
                windGustForce.setVisibility(VISIBLE);
                windGustForce.setText(getString(
                        R.string.wind_gust,
                        getString(todayWeather.windGustForce())
                ));
            } else {
                windGustForce.setVisibility(GONE);
            }
        } else {
            windForce.setVisibility(GONE);
            windGustForce.setVisibility(GONE);
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
        this.<UVChart>findViewById(R.id.uvChart).init(data.uvData(), textSize, textColor, now());
    }
}
