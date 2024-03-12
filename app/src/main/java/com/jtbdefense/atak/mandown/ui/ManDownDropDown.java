package com.jtbdefense.atak.mandown.ui;

import static com.jtbdefense.atak.mandown.cot.AllowRemoteWipeCotHandler.DETAILS_META_KEY_ALLOW_REMOTE_WIPE;
import static com.jtbdefense.atak.mandown.preferences.ManDownPreferences.PREFERENCES_KEYS;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.atak.plugins.impl.PluginLayoutInflater;
import com.atakmap.android.dropdown.DropDownReceiver;
import com.atakmap.android.maps.MapView;
import com.jtbdefense.atak.mandown.plugin.R;
import com.jtbdefense.atak.mandown.ui.recyclerview.RecyclerView;
import com.jtbdefense.atak.mandown.ui.recyclerview.RecyclerViewAdapter;

public class ManDownDropDown extends DropDownReceiver implements com.atakmap.android.dropdown.DropDown.OnStateListener {

    public static final String SHOW_DROP_DOWN = "com.jtbdefense.atak.mandown.SHOW_DROP_DOWN";
    private static final String TAG = "ManDownDropDown";

    private final View dropDownView;
    private final RecyclerViewAdapter userListViewAdapter;
    private final TextView statusText;
    private final Context context;

    public ManDownDropDown(final MapView mapView, final Context context) {
        super(mapView);
        dropDownView = PluginLayoutInflater.inflate(context, R.layout.drop_down_view);
        this.context = context;

        RecyclerView userListRecyclerView = dropDownView.findViewById(R.id.rView);
        statusText = dropDownView.findViewById(R.id.remote_wip_status);
        userListViewAdapter = new RecyclerViewAdapter(getMapView(), context);
        userListRecyclerView.setAdapter(userListViewAdapter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action == null) return;

        if (action.equals(SHOW_DROP_DOWN)) {
            if (!isClosed()) {
                unhideDropDown();
                return;
            }
            showDropDown(dropDownView, HALF_WIDTH, FULL_HEIGHT, FULL_WIDTH, HALF_HEIGHT, false, this);
            setAssociationKey(PREFERENCES_KEYS);
        }
    }

    public void rerenderDropdown() {
        if (!isClosed()) {
            userListViewAdapter.refreshUserList(getMapView().getRootGroup());
            refreshRemoteWipeStatus();
        }
    }

    private void refreshRemoteWipeStatus() {
        boolean allowRemoteWipe = getMapView().getSelfMarker().getMetaBoolean(DETAILS_META_KEY_ALLOW_REMOTE_WIPE, false);
        String status = context.getString(allowRemoteWipe ? R.string.enabled : R.string.disabled);
        statusText.setText(status);
        statusText.setTextColor(allowRemoteWipe ? Color.GREEN : Color.RED);
    }

    @Override
    public void onDropDownSelectionRemoved() {
    }

    @Override
    public void onDropDownClose() {
    }

    @Override
    public void onDropDownSizeChanged(double v, double v1) {
    }

    @Override
    public void onDropDownVisible(boolean b) {
    }

    @Override
    public void disposeImpl() {
    }
}