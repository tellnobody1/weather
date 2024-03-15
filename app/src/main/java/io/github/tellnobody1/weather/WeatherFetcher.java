package io.github.tellnobody1.weather;

import android.os.*;
import android.util.Log;
import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.concurrent.*;
import javax.net.ssl.SSLHandshakeException;
import static java.net.URLEncoder.encode;

public class WeatherFetcher {
    private final ExecutorService exec = Executors.newSingleThreadExecutor();

    public void fetch(
            String location,
            DateFormat timeFormat,
            Calendar now,
            Consumer onReceived,
            ConsumerErr onError
    ) {
        exec.execute(() -> {
            try {
                var baseUrl = "https://wttr.in/";
                var format = "?format=j1";
                var urlString = baseUrl + (location != null ? encode(location, "utf8") : "") + format;
                var url = new URL(urlString);
                var connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                var responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    var in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    var response = new StringBuilder();
                    String line;

                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    // Parse the response
                    var weatherData = JsonParser.parseWeatherData(response.toString(), timeFormat, now);

                    // Notify the listener on the main thread
                    if (weatherData != null)
                        new Handler(Looper.getMainLooper()).post(() -> onReceived.accept(weatherData));
                } else {
                    throw new Exception("GET request not successful. Response code: " + responseCode);
                }
            } catch (SSLHandshakeException e) {
                Log.d(WeatherFetcher.class.getSimpleName(), "fetchWeatherData", e);
                new Handler(Looper.getMainLooper()).post(() -> onError.accept(R.string.outdated_certificates, null));
            } catch (Exception e) {
                Log.e(WeatherFetcher.class.getSimpleName(), "fetchWeatherData", e);
                new Handler(Looper.getMainLooper()).post(() -> onError.accept(null, e.getLocalizedMessage()));
            }
        });
    }

    public void close() {
        exec.shutdownNow();
    }

    public interface Consumer {
        void accept(WeatherData data);
    }

    public interface ConsumerErr {
        void accept(Integer code, String msg);
    }
}
