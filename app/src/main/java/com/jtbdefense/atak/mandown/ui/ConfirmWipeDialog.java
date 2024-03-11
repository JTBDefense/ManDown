package com.jtbdefense.atak.mandown.ui;

import static com.atakmap.android.maps.MapView.getMapView;
import static com.jtbdefense.atak.mandown.domain.Events.PERFORM_REMOTE_WIPE;
import static com.jtbdefense.atak.mandown.domain.Events.PERFORM_REMOTE_WIPE_PASSWORD;
import static com.jtbdefense.atak.mandown.domain.Events.PERFORM_REMOTE_WIPE_UID;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.atak.plugins.impl.PluginLayoutInflater;
import com.atakmap.android.maps.MapItem;
import com.jtbdefense.atak.mandown.plugin.R;

public class ConfirmWipeDialog {
    public static void showWipeConfirmationDialog(MapItem mapItem, Context context) {
        displayDialog(mapItem, context);
    }

    private static void displayDialog(MapItem mapItem, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getMapView().getContext());
        builder.setCancelable(false);
        View view = PluginLayoutInflater.inflate(context, R.layout.delete_elevation_data_dialog);
        builder.setView(view);
        builder.setCancelable(true);

        builder.setNegativeButton(context.getString(R.string.cancel), null);
        builder.setPositiveButton(context.getString(R.string.wipe), (dialog, which) -> {
            final Switch switch1 = view.findViewById(R.id.toggleSwitch1);
            final Switch switch2 = view.findViewById(R.id.toggleSwitch2);
            if (!switch1.isChecked() || !switch2.isChecked()) {
                Toast.makeText(context, R.string.zeroize_lock_both_switches_to_clear_content, Toast.LENGTH_LONG).show();
                return;
            }
            dialog.dismiss();
            EditText passwordInput = view.findViewById(R.id.wipePassword);
            String password = passwordInput.getText().toString();
            broadcastWipeEvent(mapItem, password);
            Toast.makeText(context, R.string.wipe_done, Toast.LENGTH_LONG).show();
        });
        builder.create().show();
    }

    private static void broadcastWipeEvent(MapItem mapItem, String password) {
        Intent intent = new Intent();
        intent.setAction(PERFORM_REMOTE_WIPE);
        intent.putExtra(PERFORM_REMOTE_WIPE_UID, mapItem.getUID());
        intent.putExtra(PERFORM_REMOTE_WIPE_PASSWORD, password);
        getMapView().getContext().sendBroadcast(intent);
    }
}
