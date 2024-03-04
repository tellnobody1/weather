package io.github.tellnobody1.weather;

import java.util.List;

public record WeatherData(List<Day> days) {
    public record Day(List<Hour> hours) {}
    public record Hour(int time, int uvIndex) {}
}
