package io.github.tellnobody1.weather;

import android.util.Log;
import io.github.tellnobody1.weather.WeatherData.*;
import io.github.tellnobody1.weather.WeatherData.Day.Hour;
import java.text.*;
import java.util.*;
import org.json.JSONObject;
import static java.text.DateFormat.SHORT;
import static java.util.Locale.US;

public class JsonParser {

    public static WeatherData parseWeatherData(String json, Locale locale) {
        var weatherData = (WeatherData) null;
        try {
            var jsonObject = new JSONObject(json);
            var timeFormat = DateFormat.getTimeInstance(SHORT, locale);

            var currentCondition = jsonObject.getJSONArray("current_condition").getJSONObject(0);
            var feelsLike = Integer.parseInt(currentCondition.getString("FeelsLikeC"));
            var dateTime = timeFormat.format(new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US).parse(currentCondition.getString("localObsDateTime")));
            var windSpeed = Integer.parseInt(currentCondition.getString("windspeedKmph"));
            var current = new Current(dateTime, feelsLike, windSpeed);

            var weatherArray = jsonObject.getJSONArray("weather");
            var days = new ArrayList<Day>();

            for (var i = 0; i < weatherArray.length(); i++) {
                var dayObject = weatherArray.getJSONObject(i);
                var sunset = timeFormat.format(DateFormat.getTimeInstance(SHORT, US).parse(dayObject.getJSONArray("astronomy").getJSONObject(0).getString("sunset")));
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
