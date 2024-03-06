package io.github.tellnobody1.weather;

import java.util.*;

record WeatherData(Current current, List<Day> days) {

    record Current(String dateTime, int feelsLike, int windSpeed) {}

    record Day(String sunset, int maxTemp, List<Hour> hours) {
        record Hour(int time, int temp, int uvIndex) {}
    }

    Integer getMinTemp() {
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
