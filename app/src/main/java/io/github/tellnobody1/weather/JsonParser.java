package io.github.tellnobody1.weather;

import android.util.Log;
import io.github.tellnobody1.weather.WeatherData.*;
import io.github.tellnobody1.weather.WeatherData.Day.Hour;
import java.util.ArrayList;
import org.json.JSONObject;

public class JsonParser {

    public static WeatherData parseWeatherData(String json) {
        var weatherData = (WeatherData) null;
        try {
            var jsonObject = new JSONObject(json);

            var currentCondition = jsonObject.getJSONArray("current_condition").getJSONObject(0);
            var feelsLike = Integer.parseInt(currentCondition.getString("FeelsLikeC"));
            var dateTime = currentCondition.getString("localObsDateTime");
            var windSpeed = Integer.parseInt(currentCondition.getString("windspeedKmph"));
            var current = new Current(dateTime, feelsLike, windSpeed);

            var weatherArray = jsonObject.getJSONArray("weather");
            var days = new ArrayList<Day>();

            for (var i = 0; i < weatherArray.length(); i++) {
                var dayObject = weatherArray.getJSONObject(i);
                var sunset = dayObject.getJSONArray("astronomy").getJSONObject(0).getString("sunset");
                var minTemp = Integer.parseInt(dayObject.getString("mintempC"));
                var maxTemp = Integer.parseInt(dayObject.getString("maxtempC"));
                var hourlyArray = dayObject.getJSONArray("hourly");
                var hours = new ArrayList<Hour>();

                for (var j = 0; j < hourlyArray.length(); j++) {
                    var hourObject = hourlyArray.getJSONObject(j);
                    var time = Integer.parseInt(hourObject.getString("time")) / 100;
                    var temp = Integer.parseInt(hourObject.getString("tempC"));
                    var uvIndex = Integer.parseInt(hourObject.getString("uvIndex")) - 1;
                    hours.add(new Hour(time, temp, uvIndex));
                }

                var day = new Day(sunset, maxTemp, hours);
                days.add(day);
            }

            weatherData = new WeatherData(current, days);
        } catch (Exception e) {
            Log.e(JsonParser.class.getSimpleName(), "parseWeatherData", e);
        }
        return weatherData;
    }
}
