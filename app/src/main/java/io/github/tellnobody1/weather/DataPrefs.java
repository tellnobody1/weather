package io.github.tellnobody1.weather;

import android.content.Context;
import java.util.concurrent.Executors;
import static android.content.Context.MODE_PRIVATE;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.GINGERBREAD;

public class DataPrefs {
    private static final String PREFS_KEY = "data";
    private static final String PREF_KEY = "json";

    private final Context ctx;

    public DataPrefs(Context ctx) {
        this.ctx = ctx;
    }

    public void save(String json) {
        var prefs = ctx.getSharedPreferences(PREFS_KEY, MODE_PRIVATE).edit();
        prefs.putString(PREF_KEY, json);
        if (SDK_INT >= GINGERBREAD) prefs.apply();
        else Executors.newSingleThreadExecutor().execute(prefs::commit);
    }

    public String load() {
        var prefs = ctx.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        return prefs.getString(PREF_KEY, null);
    }
}
