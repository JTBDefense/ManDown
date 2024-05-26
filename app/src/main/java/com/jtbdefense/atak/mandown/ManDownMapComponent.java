package com.jtbdefense.atak.mandown;

import static android.content.Context.*;
import static com.atakmap.android.maps.MapView.getMapView;
import static com.jtbdefense.atak.mandown.events.Events.*;
import static com.jtbdefense.atak.mandown.events.IntentBroadcaster.broadcastAllowRemoteWipePreference;
import static com.jtbdefense.atak.mandown.preferences.ManDownPreferences.PREFERENCES_KEYS;
import static com.jtbdefense.atak.mandown.preferences.ManDownPreferencesResolver.getAllowRemoteWipe;
import static com.jtbdefense.atak.mandown.ui.ManDownDropDown.SHOW_DROP_DOWN;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

import com.atakmap.android.cot.detail.CotDetailManager;
import com.atakmap.android.dropdown.DropDownMapComponent;
import com.atakmap.android.ipc.AtakBroadcast;
import com.atakmap.android.maps.MapView;
import com.atakmap.app.preferences.ToolsPreferenceFragment;
import com.jtbdefense.atak.mandown.cot.AllowRemoteWipeCotHandler;
import com.jtbdefense.atak.mandown.cot.PerformRemoteWipeCotHandler;
import com.jtbdefense.atak.mandown.events.ManDownEventController;
import com.jtbdefense.atak.mandown.plugin.R;
import com.jtbdefense.atak.mandown.preferences.ManDownPreferenceFragment;
import com.jtbdefense.atak.mandown.sensors.SensorDataReceiver;
import com.jtbdefense.atak.mandown.ui.ManDownDropDown;
import com.jtbdefense.atak.mandown.ui.recyclerview.UserListUpdater;

import java.util.Timer;

public class ManDownMapComponent extends DropDownMapComponent {

    private ManDownDropDown manDownDropDown;
    private Context context;
    private ManDownEventController manDownEventController;
    private final SensorDataReceiver sensorDataReceiver = new SensorDataReceiver();
    private AllowRemoteWipeCotHandler allowRemoteWipeCotHandler;
    private PerformRemoteWipeCotHandler performRemoteWipeCotHandler;
    private Timer remoteWipeStatusTimer;

    @Override
    public void onCreate(final Context context, Intent intent, final MapView view) {
        this.context = context;
        context.setTheme(R.style.ATAKPluginTheme);
        registerDropDownMenu(context, view);
        sensorDataReceiver.initialize();
        registerRemoteWipeCotHandlers();
        registerEventController();
        registerPreferences();
        registerDropDownListUpdated();
        broadcastAllowRemoteWipePreference(getAllowRemoteWipe());
    }

    @Override
    protected void onDestroyImpl(Context context, MapView view) {
        super.onDestroyImpl(context, view);
        manDownDropDown.closeDropDown();
        manDownDropDown.disposeImpl();
        sensorDataReceiver.destroy();
        context.unregisterReceiver(manDownEventController);
        ToolsPreferenceFragment.unregister(PREFERENCES_KEYS);
        CotDetailManager.getInstance().unregisterHandler(allowRemoteWipeCotHandler);
        CotDetailManager.getInstance().unregisterHandler(performRemoteWipeCotHandler);
        remoteWipeStatusTimer.cancel();
    }

    private void registerDropDownMenu(Context context, MapView view) {
        manDownDropDown = new ManDownDropDown(view, context);
        AtakBroadcast.DocumentedIntentFilter ddFilter = new AtakBroadcast.DocumentedIntentFilter();
        ddFilter.addAction(SHOW_DROP_DOWN, "Show plugin's drop-down menu");
        this.registerDropDownReceiver(manDownDropDown, ddFilter);
    }

    private void registerEventController() {
        manDownEventController = new ManDownEventController(allowRemoteWipeCotHandler);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(STATS_CHANGED);
        intentFilter.addAction(INTERVAL1_EXPIRED);
        intentFilter.addAction(INTERVAL2_EXPIRED);
        intentFilter.addAction(ALLOW_REMOTE_WIPE_PREFERENCE_CHANGED);
        intentFilter.addAction(PERFORM_REMOTE_WIPE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.registerReceiver(manDownEventController, intentFilter, RECEIVER_EXPORTED);
        } else {
            context.registerReceiver(manDownEventController, intentFilter);
        }
    }

    private void registerPreferences() {
        ToolsPreferenceFragment.register(new ToolsPreferenceFragment.ToolPreference(context.getResources().getString(R.string.preferences_name), context.getResources().getString(R.string.preferences_description), PREFERENCES_KEYS, context.getResources().getDrawable(R.drawable.skull, null), new ManDownPreferenceFragment(context)));
    }

    private void registerRemoteWipeCotHandlers() {
        allowRemoteWipeCotHandler = new AllowRemoteWipeCotHandler();
        performRemoteWipeCotHandler = new PerformRemoteWipeCotHandler();
        CotDetailManager.getInstance().registerHandler(allowRemoteWipeCotHandler);
        CotDetailManager.getInstance().registerHandler(performRemoteWipeCotHandler);
    }

    private void registerDropDownListUpdated() {
        remoteWipeStatusTimer = new Timer();
        remoteWipeStatusTimer.schedule(new UserListUpdater(getMapView().getContext(), manDownDropDown), 5000, 5000);
    }
}