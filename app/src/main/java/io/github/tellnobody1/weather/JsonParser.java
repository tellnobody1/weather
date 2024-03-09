package io.github.tellnobody1.weather;

import android.util.Log;
import io.github.tellnobody1.weather.WeatherData.Day;
import io.github.tellnobody1.weather.WeatherData.Day.Hour;
import java.text.DateFormat;
import java.util.*;
import org.json.JSONObject;
import static java.text.DateFormat.SHORT;
import static java.util.Locale.US;

public class JsonParser {

    public static WeatherData parseWeatherData(String json, DateFormat timeFormat, Calendar now) {
        var weatherData = (WeatherData) null;
        try {
            var jsonObject = new JSONObject(json);

            var weatherArray = jsonObject.getJSONArray("weather");
            var days = new ArrayList<Day>();

            for (var i = 0; i < weatherArray.length(); i++) {
                var dayObject = weatherArray.getJSONObject(i);
                var sunset = timeFormat.format(DateFormat.getTimeInstance(SHORT, US).parse(dayObject.getJSONArray("astronomy").getJSONObject(0).getString("sunset")));
                var hourlyArray = dayObject.getJSONArray("hourly");
                var hours = new ArrayList<Hour>();

                for (var j = 0; j < hourlyArray.length(); j++) {
                    var hourObject = hourlyArray.getJSONObject(j);
                    var time = Integer.parseInt(hourObject.getString("time")) / 100;
                    var feels = Integer.parseInt(hourObject.getString("FeelsLikeC"));
                    var uvIndex = Integer.parseInt(hourObject.getString("uvIndex")) - 1;
                    var windSpeed = Integer.parseInt(hourObject.getString("windspeedKmph"));
                    hours.add(new Hour(time, feels, uvIndex, windSpeed));
                }

                var day = new Day(sunset, hours);
                days.add(day);
            }

            weatherData = new WeatherData(now, days);
        } catch (Exception e) {
            Log.e(JsonParser.class.getSimpleName(), "parseWeatherData", e);
        }
        return weatherData;
    }
}
