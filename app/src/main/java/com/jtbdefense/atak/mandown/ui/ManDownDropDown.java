package com.jtbdefense.atak.mandown.ui;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

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

    public ManDownDropDown(final MapView mapView, final Context context) {
        super(mapView);
        dropDownView = PluginLayoutInflater.inflate(context, R.layout.drop_down_view);

        RecyclerView userListRecyclerView = dropDownView.findViewById(R.id.rView);
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
        }
    }

    public void rerenderDropdown() {
        if (!isClosed()) {
            userListViewAdapter.refreshUserList(getMapView().getRootGroup());
        }
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