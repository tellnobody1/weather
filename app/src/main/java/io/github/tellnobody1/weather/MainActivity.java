package io.github.tellnobody1.weather;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.os.*;
import android.view.*;
import android.widget.*;
import java.text.DateFormat;
import java.util.Calendar;
import static android.net.ConnectivityManager.RESTRICT_BACKGROUND_STATUS_DISABLED;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH;
import static android.os.Build.VERSION_CODES.N;
import static android.view.View.*;
import static android.widget.Toast.LENGTH_SHORT;
import static java.text.DateFormat.SHORT;
import static java.util.Calendar.HOUR_OF_DAY;

public class MainActivity extends Activity {
    private WeatherData data;
    private final DataPrefs dataPrefs = new DataPrefs(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        var json = dataPrefs.load();
        if (json != null)
            data = JsonParser.parseWeatherData(json, timeFormat(), now());

        if (shouldFetch())
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
        // consider current time for data display
        if (data != null) updateUI(data, false);
    }

    private boolean shouldFetch() {
        var connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (connectivityManager == null)
            return false;
        // internet is enabled
        var activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork == null || !activeNetwork.isConnectedOrConnecting())
            return false;
        // background data saver is disabled
        if (SDK_INT >= N)
            if (connectivityManager.getRestrictBackgroundStatus() != RESTRICT_BACKGROUND_STATUS_DISABLED)
                return false;
        // is not on cellular
        if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
            return false;
        return true;
    }

    private boolean internetEnabled() {
        var connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            var activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        return false;
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
        if (internetEnabled())
            WeatherFetcher.fetch(
                "Київ",
                timeFormat(),
                now(),
                data -> {
                    updateUI(data, true);
                    dataPrefs.save(data.json());
                },
                x -> Toast.makeText(this, R.string.outdated_certificates, LENGTH_SHORT).show()
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
