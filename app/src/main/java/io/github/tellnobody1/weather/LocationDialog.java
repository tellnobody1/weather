package io.github.tellnobody1.weather;

import android.app.AlertDialog;
import android.content.*;
import android.widget.*;

public class LocationDialog {
    private final AlertDialog dialog;

    public LocationDialog(Context ctx, String value, Listener listener) {
        var input = new EditText(ctx);
        input.setText(value);
        input.setSingleLine();

        var builder = new AlertDialog.Builder(ctx);
        builder.setTitle(R.string.location);
        builder.setView(input);

        builder.setPositiveButton(R.string.ok, (dialog, which) -> listener.accept(input.getText().toString()));

        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());

        dialog = builder.create();
    }

    public void show() {
        dialog.show();
    }

    public interface Listener {
        void accept(String value);
    }
}
