package io.github.tellnobody1.weather;

import android.os.*;
import android.util.Log;
import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.util.Calendar;
import javax.net.ssl.SSLHandshakeException;
import static java.net.URLEncoder.encode;

public class WeatherFetcher {

    public static void fetch(String city, DateFormat timeFormat, Calendar now, Consumer<WeatherData> onReceived, Consumer<Void> onCertError) {
        new Thread(() -> {
            try {
                var encodedCity = encode(city, "utf8");
                var urlString = "https://wttr.in/" + encodedCity + "?format=j1";
                var url = new URL(urlString);
                var connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                var responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
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
                new Handler(Looper.getMainLooper()).post(() -> onCertError.accept(null));
            } catch (Exception e) {
                Log.e(WeatherFetcher.class.getSimpleName(), "fetchWeatherData", e);
            }
        }).start();
    }

    public interface Consumer<T> {
        void accept(T t);
    }
}
