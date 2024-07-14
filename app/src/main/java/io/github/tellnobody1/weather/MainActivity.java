package io.github.tellnobody1.weather;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import java.text.*;
import java.util.Locale;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH;
import static android.view.View.*;
import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends Activity {
    private WeatherData data;
    private String location = null;
    private final DataPrefs dataPrefs = new DataPrefs(this);
    private final WeatherFetcher weatherFetcher = new WeatherFetcher();
    private final NetworkOps networkOps = new NetworkOps(this);
    private final TimeOps timeOps = new TimeOps(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        var json = dataPrefs.load();
        if (json != null)
            data = JsonParser.parseWeatherData(json, timeOps.timeFormat(), timeOps.now());

        if (shouldFetch())
            fetchData();

        if (SDK_INT >= ICE_CREAM_SANDWICH)
            if (!ViewConfiguration.get(this).hasPermanentMenuKey()) {
                var menu = findViewById(R.id.menu);
                menu.setVisibility(VISIBLE);

                Button refresh = findViewById(R.id.refresh);
                refresh.setOnClickListener(this::fetchData);

                Button location = findViewById(R.id.location);
                location.setOnClickListener(this::location);
            }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        var itemId = item.getItemId();
        if (itemId == R.id.action_refresh) {
            fetchData();
            return true;
        } else if (itemId == R.id.action_location) {
            location(null);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // consider current time for data display
        if (data != null) updateUI(data, false);
    }

    @Override
    protected void onDestroy() {
        dataPrefs.close();
        weatherFetcher.close();
        super.onDestroy();
    }

    private boolean shouldFetch() {
        var connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (connectivityManager == null)
            return false;
        if (networkOps.internetDisabled(connectivityManager))
            return false;
        if (networkOps.dataSaver(connectivityManager))
            return false;
        if (networkOps.cellular(connectivityManager))
            return false;
        if (timeOps.hoursBeforeNow(data.dateTime()) < 1)
            return false;
        return true;
    }

    private void fetchData(View v) { fetchData(); }

    private void location(View v) {
        new LocationDialog(this, data.areaName(), (location) -> {
            this.location = location;
            fetchData();
        }).show();
    }

    private void fetchData() {
        if (networkOps.internetEnabled())
            weatherFetcher.fetch(
                location,
                timeOps.timeFormat(),
                timeOps.now(),
                data -> {
                    updateUI(data, true);
                    dataPrefs.save(data.json());
                },
                (id, msg) -> {
                    if (id != null) Toast.makeText(this, id, LENGTH_SHORT).show();
                    else if (msg != null) Toast.makeText(this, msg, LENGTH_SHORT).show();
                }
            );
        else
            Toast.makeText(this, R.string.no_internet, LENGTH_SHORT).show();
    }

    private void updateUI(WeatherData data, boolean now) {
        this.data = data;
        setTime(data, now);
        setTodayWeather(data);
        setSunset(data);
        setUVChart(data);
        setTempForecast(data.tempForecast());
    }

    private void setTime(WeatherData data, boolean now) {
        TextView dateTime = findViewById(R.id.dateTime);
        dateTime.setText(getString(R.string.updated, getString(now ?
            R.string.now :
            switch (timeOps.hoursBeforeNow(data.dateTime())) {
                case 0 -> R.string.hour_0;
                case 1 -> R.string.hour_1;
                case 2 -> R.string.hour_2;
                default -> R.string.hour_3;
            }
        )));
    }

    private void setTodayWeather(WeatherData data) {
        var todayWeather = data.todayWeather(timeOps.hourOfNow());
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
        var textView = this.<TextView>findViewById(R.id.dateTime);
        var textSize = textView.getTextSize();
        var textColor = textView.getCurrentTextColor();
        this.<UVChart>findViewById(R.id.uvChart).init(data.uvData(), textSize, textColor, timeOps.timeProgress());
    }

    private void setTempForecast(TempForecast tempForecast) {
        var textView = this.<TextView>findViewById(R.id.dateTime);
        var textSize = textView.getTextSize();
        var textColor = textView.getCurrentTextColor();
        this.<TempChart>findViewById(R.id.tempChart).init(tempForecast, textSize, textColor);
    }

    private String dateToDayOfWeek(String date) {
        var inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        try {
            return outputFormat.format(inputFormat.parse(date));
        } catch (Exception e) {
            e.printStackTrace();
            return date;
        }
    }
}
