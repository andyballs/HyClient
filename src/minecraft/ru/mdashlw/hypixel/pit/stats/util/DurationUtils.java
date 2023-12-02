package ru.mdashlw.hypixel.pit.stats.util;

import java.time.Duration;
import java.util.ArrayList;

import ru.mdashlw.hypixel.api.util.NumberUtils;

public final class DurationUtils {

    public static String format(Duration duration) {
        ArrayList output = new ArrayList();
        long days = duration.toDays();
        long hours = duration.toHours() % 24L;
        long minutes = duration.toMinutes() % 60L;
        long seconds = duration.getSeconds() % 60L;

        if (days != 0L) {
            output.add(NumberUtils.plural(days, "day"));
        }

        if (hours != 0L) {
            output.add(NumberUtils.plural(hours, "hour"));
        }

        if (minutes != 0L) {
            output.add(NumberUtils.plural(minutes, "minute"));
        }

        if (seconds != 0L && output.size() < 3) {
            output.add(NumberUtils.plural(seconds, "second"));
        }

        if (output.isEmpty()) {
            output.add("now");
        }

        return String.join(" ", output);
    }
}
