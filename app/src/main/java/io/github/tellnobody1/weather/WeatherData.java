package io.github.tellnobody1.weather;

import java.util.*;

public record WeatherData(Current current, List<Day> days) {

    public record Current(String dateTime, int feelsLike, int windSpeed) {}

    public record Day(String sunset, int maxTemp, List<Hour> hours) {
        record Hour(int time, int temp, int uvIndex) {}
    }

    public record UVData(List<Integer> indexes, List<Integer> times) {}

    public UVData getUVData() {
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
                var noon = hours.get(hours.size() - 1);
                uvIndexes.add(noon.uvIndex());
                uvTimes.add(24);
            } else {
                uvIndexes.add(uvIndexes.get(uvIndexes.size() - 1));
                uvTimes.add(24);
            }
        }
        return new UVData(uvIndexes, uvTimes);
    }

    public int getWindForce() {
        int windForce;
        var windSpeed = current.windSpeed();
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

    public Integer getMinTemp() {
        var xs = new LinkedList<Integer>();
        for (var i = 0; i < days.size(); i++) {
            var day = days.get(i);
            var hours = day.hours();
            switch (i) {
                case 0 -> {
                    for (var j = 0; j < hours.size(); j++) {
                        var hour = hours.get(j);
                        if (hour.time() >= 9)
                            xs.add(hour.temp());
                    }
                }
                case 1 -> {
                    if (!hours.isEmpty()) {
                        var hour = hours.get(0);
                        if (hour.time() == 0)
                            xs.add(hour.temp());
                    }
                }
                default -> {}
            }
        }
        return xs.isEmpty() ? null : Collections.min(xs);
    }
}
