package com.jtbdefense.atak.mandown.ui;

import static android.text.TextUtils.isEmpty;
import static com.atakmap.android.maps.MapView.getMapView;
import static com.jtbdefense.atak.mandown.events.Events.PERFORM_REMOTE_WIPE;
import static com.jtbdefense.atak.mandown.events.Events.PERFORM_REMOTE_WIPE_PASSWORD;
import static com.jtbdefense.atak.mandown.events.Events.PERFORM_REMOTE_WIPE_UID;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
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
        View view = PluginLayoutInflater.inflate(context, R.layout.wipe_data_dialog);
        builder.setView(view);
        builder.setCancelable(true);

        builder.setNegativeButton(context.getString(R.string.cancel), null);
        AlertDialog alertDialog = builder.create();

        final Switch switch1 = view.findViewById(R.id.toggleSwitch1);
        final Switch switch2 = view.findViewById(R.id.toggleSwitch2);
        EditText passwordInput = view.findViewById(R.id.wipePassword);
        Button wipeButton = view.findViewById(R.id.wipeButton);

        switch1.setOnCheckedChangeListener((buttonView, isChecked) -> validateRequiredFields(switch1, switch2, passwordInput, wipeButton));
        switch2.setOnCheckedChangeListener((buttonView, isChecked) -> validateRequiredFields(switch1, switch2, passwordInput, wipeButton));
        passwordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateRequiredFields(switch1, switch2, passwordInput, wipeButton);
            }
        });

        wipeButton.setOnClickListener(v -> {
            String password = passwordInput.getText().toString();
            broadcastWipeEvent(mapItem, password);
            Toast.makeText(context, R.string.wipe_done, Toast.LENGTH_LONG).show();
            alertDialog.dismiss();
        });

        alertDialog.show();
    }

    private static void validateRequiredFields(Switch switch1, Switch switch2, EditText passwordInput, Button wipeButton) {
        if (switch1.isChecked() && switch2.isChecked() && !isEmpty(passwordInput.getText().toString())) {
            wipeButton.setVisibility(View.VISIBLE);
        } else {
            wipeButton.setVisibility(View.INVISIBLE);
        }
    }

    private static void broadcastWipeEvent(MapItem mapItem, String password) {
        Intent intent = new Intent();
        intent.setAction(PERFORM_REMOTE_WIPE);
        intent.putExtra(PERFORM_REMOTE_WIPE_UID, mapItem.getUID());
        intent.putExtra(PERFORM_REMOTE_WIPE_PASSWORD, password);
        getMapView().getContext().sendBroadcast(intent);
    }
}
