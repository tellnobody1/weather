package io.github.tellnobody1.weather;

import java.util.*;
import static java.lang.Integer.max;
import static java.util.Collections.max;
import static java.util.Collections.min;

record TempForecast(List<Integer> values) {}

public record WeatherData(Calendar dateTime, List<Day> days, TempForecast tempForecast, String areaName, String json) {

    public record Day(String sunset, List<Hour> hours) {
        record Hour(int time, int feels, int uvIndex, int windSpeed, int windGustSpeed) {}
    }

    public record UVData(List<Integer> indexes, List<Integer> times) {}

    public UVData uvData() {
        var uvIndexes = new LinkedList<Integer>();
        var uvTimes = new LinkedList<Integer>();
        if (!days.isEmpty()) {
            var today = days.get(0);
            for (var hour : today.hours()) {
                uvIndexes.add(hour.uvIndex());
                uvTimes.add(hour.time());
            }
            if (days.size() > 1) {
                var tomorrow = days.get(1);
                var hours = tomorrow.hours();
                var noon = hours.get(0);
                uvIndexes.add(noon.uvIndex());
                uvTimes.add(24);
            } else {
                uvIndexes.add(0);
                uvTimes.add(24);
            }
        }
        return new UVData(uvIndexes, uvTimes);
    }

    public record TodayWeather(int feelsMin, int feelsMax, int feelsNow, int windForce, int windGustForce) {}

    public TodayWeather todayWeather(int time) {
        var temps = new LinkedList<Integer>();
        Integer feels = null;
        Integer windForce = null;
        Integer windGustForce = null;
        for (var i = 0; i < days.size(); i++) {
            var day = days.get(i);
            var hours = day.hours();
            switch (i) {
                case 0 -> {
                    for (var j = 0; j < hours.size(); j++) {
                        var hour = hours.get(j);
                        if (hour.time() >= max(9, time - 1)) {
                            temps.add(hour.feels());
                            if (feels == null) feels = hour.feels();
                            if (windForce == null) windForce = windForce(hour.windSpeed);
                            if (windGustForce == null) windGustForce = windForce(hour.windGustSpeed);
                        }
                    }
                }
                case 1 -> {
                    if (!hours.isEmpty()) {
                        var hour = hours.get(0);
                        if (hour.time() == 0) {
                            temps.add(hour.feels());
                            if (feels == null) feels = hour.feels();
                            if (windForce == null) windForce = windForce(hour.windSpeed);
                            if (windGustForce == null) windGustForce = windForce(hour.windGustSpeed);
                        }
                    }
                }
                default -> {}
            }
        }
        return temps.isEmpty() ? null : new TodayWeather(min(temps), max(temps), feels, windForce, windGustForce);
    }

    private int windForce(int windSpeed) {
        int windForce;
        if (windSpeed < 2) windForce = R.string.wind_0;
        else if (windSpeed < 6) windForce = R.string.wind_1;
        else if (windSpeed < 12) windForce = R.string.wind_2;
        else if (windSpeed < 20) windForce = R.string.wind_3;
        else if (windSpeed < 29) windForce = R.string.wind_4;
        else if (windSpeed < 39) windForce = R.string.wind_5;
        else if (windSpeed < 50) windForce = R.string.wind_6;
        else if (windSpeed < 62) windForce = R.string.wind_7;
        else if (windSpeed < 75) windForce = R.string.wind_8;
        else if (windSpeed < 88) windForce = R.string.wind_9;
        else if (windSpeed < 103) windForce = R.string.wind_10;
        else if (windSpeed < 118) windForce = R.string.wind_11;
        else windForce = R.string.wind_12;
        return windForce;
    }
}
