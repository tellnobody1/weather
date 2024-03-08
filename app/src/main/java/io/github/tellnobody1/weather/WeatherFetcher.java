package io.github.tellnobody1.weather;

import android.os.*;
import android.util.Log;
import java.io.*;
import java.net.*;
import java.text.DateFormat;

public class WeatherFetcher {

    public static void fetch(String urlString, DateFormat timeFormat, OnWeatherDataReceivedListener listener) {
        new Thread(() -> {
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    // Parse the response
                    WeatherData weatherData = JsonParser.parseWeatherData(response.toString(), timeFormat);

                    // Notify the listener on the main thread
                    if (weatherData != null)
                        new Handler(Looper.getMainLooper()).post(() -> listener.onReceived(weatherData));
                } else {
                    throw new Exception("GET request not successful. Response code: " + responseCode);
                }
            } catch (Exception e) {
                Log.e(WeatherFetcher.class.getSimpleName(), "fetchWeatherData", e);
            }
        }).start();
    }

    public interface OnWeatherDataReceivedListener {
        void onReceived(WeatherData weatherData);
    }
}
