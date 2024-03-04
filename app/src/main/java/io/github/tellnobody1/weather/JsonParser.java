package io.github.tellnobody1.weather;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JsonParser {

    public static WeatherData parseWeatherData(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray weatherArray = jsonObject.getJSONArray("weather");
            List<WeatherData.Day> days = new ArrayList<>();

            for (int i = 0; i < weatherArray.length(); i++) {
                JSONObject dayObject = weatherArray.getJSONObject(i);
                JSONArray hourlyArray = dayObject.getJSONArray("hourly");
                List<WeatherData.Hour> hourlies = new ArrayList<>();

                for (int j = 0; j < hourlyArray.length(); j++) {
                    JSONObject hourObject = hourlyArray.getJSONObject(j);
                    var time = Integer.parseInt(hourObject.getString("time")) / 100;
                    var uvIndex = Integer.parseInt(hourObject.getString("uvIndex"));
                    hourlies.add(new WeatherData.Hour(time, uvIndex));
                }

                WeatherData.Day day = new WeatherData.Day(hourlies);
                days.add(day);
            }

            return new WeatherData(days);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new WeatherData(Collections.emptyList());
    }
}
