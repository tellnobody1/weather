package io.github.tellnobody1.weather;

import android.content.Context;
import java.text.DateFormat;
import java.util.Calendar;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.N;
import static java.text.DateFormat.SHORT;
import static java.util.Calendar.*;

public class TimeOps {
    private static final int MS_IN_HOUR = 3_600_000;

    private final Context ctx;

    public TimeOps(Context ctx) {
        this.ctx = ctx;
    }

    public DateFormat timeFormat() {
        var conf = ctx.getResources().getConfiguration();
        var locale = SDK_INT >= N ? conf.getLocales().get(0) : conf.locale;
        return DateFormat.getTimeInstance(SHORT, locale);
    }

    public Calendar now() {
        return Calendar.getInstance();
    }

    public int hourOfNow() {
        return now().get(HOUR_OF_DAY);
    }

    public int hoursBeforeNow(Calendar dateTime) {
        var msDiff = now().getTimeInMillis() - dateTime.getTimeInMillis();
        return (int) ((float) msDiff / MS_IN_HOUR);
    }

    public float timeProgress() {
        return (hourOfNow() * 60 + now().get(MINUTE)) / ((float) 24 * 60);
    }
}
