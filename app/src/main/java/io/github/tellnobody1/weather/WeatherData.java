package io.github.tellnobody1.weather;

import java.util.List;

public class WeatherData {
    private final List<Day> days;

    public WeatherData(List<Day> days) {
        this.days = days;
    }

    public List<Day> getWeather() {
        return days;
    }

    public static final class Day {
        private final List<Hourly> hourly;

        public Day(List<Hourly> hourly) {
            this.hourly = hourly;
        }

        public List<Hourly> hourly() {
            return hourly;
        }
    }

    public static class Hourly {
        private final String time;
        private final String uvIndex;

        public Hourly(String time, String uvIndex) {
            this.time = time;
            this.uvIndex = uvIndex;
        }

        public int getTime() {
            return Integer.parseInt(time) / 100;
        }

        public int getUvIndex() {
            return Integer.parseInt(uvIndex);
        }
    }
}

