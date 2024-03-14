package io.github.tellnobody1.weather;

import android.content.Context;
import android.net.ConnectivityManager;
import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.net.ConnectivityManager.RESTRICT_BACKGROUND_STATUS_DISABLED;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.N;

public class NetworkOps {
    private final Context ctx;

    public NetworkOps(Context ctx) {
        this.ctx = ctx;
    }

    public boolean internetEnabled() {
        var connectivityManager = (ConnectivityManager) ctx.getSystemService(CONNECTIVITY_SERVICE);
        return connectivityManager != null && !internetDisabled(connectivityManager);
    }

    public boolean internetDisabled(ConnectivityManager connectivityManager) {
        var activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork == null || !activeNetwork.isConnectedOrConnecting();
    }

    public boolean dataSaver(ConnectivityManager connectivityManager) {
        return SDK_INT >= N && connectivityManager.getRestrictBackgroundStatus() != RESTRICT_BACKGROUND_STATUS_DISABLED;
    }

    public boolean cellular(ConnectivityManager connectivityManager) {
        var activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
    }
}
