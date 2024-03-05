package io.github.tellnobody1.weather;

import java.util.List;

public record WeatherData(Current current, List<Day> days) {
    public record Current(int feelsLike, String dateTime, int windSpeed) {}
    public record Day(String sunset, List<Hour> hours) {
        public record Hour(int time, int uvIndex) {}
    }
}
