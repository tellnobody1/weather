package io.github.tellnobody1.weather;

import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;

import io.github.tellnobody1.weather.WeatherData.Current;
import io.github.tellnobody1.weather.WeatherData.Day;
import io.github.tellnobody1.weather.WeatherData.Day.Hour;

public class JsonParser {

    public static WeatherData parseWeatherData(String json) {
        var weatherData = (WeatherData) null;
        try {
            var jsonObject = new JSONObject(json);

            var currentConditions = jsonObject.getJSONArray("current_condition");
            var current = (Current) null;
            if (currentConditions.length() > 0) {
                var currentCondition = currentConditions.getJSONObject(0);
                var feelsLike = Integer.parseInt(currentCondition.getString("FeelsLikeC"));
                var dateTime = currentCondition.getString("localObsDateTime");
                var windSpeed = Integer.parseInt(currentCondition.getString("windspeedKmph"));
                current = new Current(feelsLike, dateTime, windSpeed);
            }

            var weatherArray = jsonObject.getJSONArray("weather");
            var days = new ArrayList<Day>();

            for (var i = 0; i < weatherArray.length(); i++) {
                var dayObject = weatherArray.getJSONObject(i);
                var hourlyArray = dayObject.getJSONArray("hourly");
                var hourlies = new ArrayList<Hour>();

                for (var j = 0; j < hourlyArray.length(); j++) {
                    var hourObject = hourlyArray.getJSONObject(j);
                    var time = Integer.parseInt(hourObject.getString("time")) / 100;
                    var uvIndex = Integer.parseInt(hourObject.getString("uvIndex"));
                    hourlies.add(new Hour(time, uvIndex));
                }

                Day day = new Day(hourlies);
                days.add(day);
            }

            weatherData = new WeatherData(current, days);
        } catch (Exception e) {
            Log.e(JsonParser.class.getSimpleName(), "parseWeatherData", e);
        }
        return weatherData;
    }
}
