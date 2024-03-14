package com.jtbdefense.atak.mandown.events;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static android.text.TextUtils.isEmpty;
import static com.atakmap.android.maps.MapView.getMapView;
import static com.jtbdefense.atak.mandown.cot.AllowRemoteWipeCotHandler.ALLOW_REMOTE_WIPE_COT_KEY;
import static com.jtbdefense.atak.mandown.cot.AllowRemoteWipeCotHandler.DETAILS_META_KEY_ALLOW_REMOTE_WIPE;
import static com.jtbdefense.atak.mandown.cot.PerformRemoteWipeCotHandler.DETAILS_META_KEY_PERFORM_REMOTE_WIPE_PASSWORD;
import static com.jtbdefense.atak.mandown.cot.PerformRemoteWipeCotHandler.DETAILS_META_KEY_PERFORM_REMOTE_WIPE_UID;
import static com.jtbdefense.atak.mandown.cot.PerformRemoteWipeCotHandler.PERFORM_REMOTE_WIPE_COT_KEY;
import static com.jtbdefense.atak.mandown.events.Events.ALLOW_REMOTE_WIPE_PREFERENCE_CHANGED;
import static com.jtbdefense.atak.mandown.events.Events.ALLOW_REMOTE_WIPE_PREFERENCE_CHANGED_VALUE;
import static com.jtbdefense.atak.mandown.events.Events.INTERVAL1_EXPIRED;
import static com.jtbdefense.atak.mandown.events.Events.INTERVAL2_EXPIRED;
import static com.jtbdefense.atak.mandown.events.Events.PERFORM_REMOTE_WIPE;
import static com.jtbdefense.atak.mandown.events.Events.PERFORM_REMOTE_WIPE_PASSWORD;
import static com.jtbdefense.atak.mandown.events.Events.PERFORM_REMOTE_WIPE_UID;
import static com.jtbdefense.atak.mandown.events.Events.STATS_CHANGED;
import static com.jtbdefense.atak.mandown.events.Events.STATS_CHANGED_PARAM_STATS;
import static com.jtbdefense.atak.mandown.preferences.ManDownPreferences.INTERVAL1_ALERT;
import static com.jtbdefense.atak.mandown.preferences.ManDownPreferences.INTERVAL1_MESSAGE;
import static com.jtbdefense.atak.mandown.preferences.ManDownPreferences.INTERVAL1_WIPE;
import static com.jtbdefense.atak.mandown.preferences.ManDownPreferences.INTERVAL2_ALERT;
import static com.jtbdefense.atak.mandown.preferences.ManDownPreferences.INTERVAL2_MESSAGE;
import static com.jtbdefense.atak.mandown.preferences.ManDownPreferences.INTERVAL2_WIPE;
import static com.jtbdefense.atak.mandown.preferences.ManDownPreferencesResolver.getInterval1Time;
import static com.jtbdefense.atak.mandown.preferences.ManDownPreferencesResolver.getInterval2Time;
import static com.jtbdefense.atak.mandown.preferences.ManDownPreferencesResolver.getWipePassword;
import static com.jtbdefense.atak.mandown.services.EncryptionService.encryptStrAndToBase64;
import static java.lang.String.format;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.atakmap.android.cot.CotMapComponent;
import com.atakmap.android.ipc.AtakBroadcast;
import com.atakmap.android.maps.Marker;
import com.atakmap.comms.ReportingRate;
import com.atakmap.coremap.cot.event.CotDetail;
import com.atakmap.coremap.cot.event.CotEvent;
import com.atakmap.coremap.cot.event.CotPoint;
import com.atakmap.coremap.maps.coords.GeoPoint;
import com.atakmap.coremap.maps.time.CoordinatedTime;
import com.jtbdefense.atak.mandown.cot.AllowRemoteWipeCotHandler;
import com.jtbdefense.atak.mandown.cot.PerformRemoteWipeCotHandler;
import com.jtbdefense.atak.mandown.plugin.R;
import com.jtbdefense.atak.mandown.sensors.Stats;
import com.jtbdefense.atak.mandown.services.AlertService;
import com.jtbdefense.atak.mandown.services.ChatService;
import com.jtbdefense.atak.mandown.services.WipeDataService;

import java.util.UUID;


public class ManDownEventController extends BroadcastReceiver {

    private static final String TAG = "ManDownEventController";
    private final AllowRemoteWipeCotHandler allowRemoteWipeCotHandler;

    public ManDownEventController(AllowRemoteWipeCotHandler allowRemoteWipeCotHandler) {
        this.allowRemoteWipeCotHandler = allowRemoteWipeCotHandler;
    }

    public void destroy() {
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action == null) return;

        switch (action) {
            case STATS_CHANGED:
                handleStatsChanged(intent, context);
                break;
            case INTERVAL1_EXPIRED:
                handleInterval1Expired(context);
                break;
            case INTERVAL2_EXPIRED:
                handleInterval2Expired(context);
                break;
            case ALLOW_REMOTE_WIPE_PREFERENCE_CHANGED:
                handleRemoteWipePreferenceChanged(intent);
                break;
            case PERFORM_REMOTE_WIPE:
                handlePerformRemoteWipe(intent);
                break;
        }
    }

    private void handlePerformRemoteWipe(Intent intent) {
        try {
            String uidToWipe = intent.getStringExtra(PERFORM_REMOTE_WIPE_UID);
            String providedPassword = intent.getStringExtra(PERFORM_REMOTE_WIPE_PASSWORD);
            Log.d(TAG, "Handling PERFORM_REMOTE_WIPE event for UUID " + uidToWipe);


            final Marker item = (Marker) getMapView().getMapItem(uidToWipe);
            GeoPoint location = item.getPoint();

            CotEvent event = new CotEvent();
            event.setUID(UUID.randomUUID().toString());
            event.setHow("h-e");

            CotPoint point = new CotPoint(location.getLatitude(),
                    location.getLongitude(), location.getAltitude(),
                    location.getCE(), location.getLE());
            event.setPoint(point);

            event.setStart(new CoordinatedTime());
            event.setTime(new CoordinatedTime());
            event.setStale(new CoordinatedTime().addMilliseconds(10000));

            event.setType("a-f-G-U-C");

            CotDetail detail = new CotDetail();
            CotDetail cd = new CotDetail(PERFORM_REMOTE_WIPE_COT_KEY);
            cd.setAttribute(DETAILS_META_KEY_PERFORM_REMOTE_WIPE_UID, uidToWipe);
            cd.setAttribute(DETAILS_META_KEY_PERFORM_REMOTE_WIPE_PASSWORD, encryptStrAndToBase64(providedPassword));
            detail.addChild(cd);
            event.setDetail(detail);

            StringBuilder builder = new StringBuilder();
            event.buildXml(builder);

            CotMapComponent.getExternalDispatcher().dispatch(event);
            CotMapComponent.getInternalDispatcher().dispatch(event);
            Log.d(TAG, "CoT event send:" + builder);


        } catch (Exception e) {
            Log.e(TAG, "Cannot create CoT details " + e.getMessage());
        }
    }

    private void handleRemoteWipePreferenceChanged(Intent intent) {
        boolean allowRemoteWipeValue = intent.getBooleanExtra(ALLOW_REMOTE_WIPE_PREFERENCE_CHANGED_VALUE, false);
        Log.d(TAG, "Handling ALLOW_REMOTE_WIPE_PREFERENCE_CHANGED event with value " + allowRemoteWipeValue);

        boolean allowRemoteWipe = allowRemoteWipeValue && !isEmpty(getWipePassword());

        CotDetail cd = new CotDetail(ALLOW_REMOTE_WIPE_COT_KEY);
        cd.setAttribute(DETAILS_META_KEY_ALLOW_REMOTE_WIPE, Boolean.toString(allowRemoteWipe));

        allowRemoteWipeCotHandler.toItemMetadata(getMapView().getSelfMarker(), null, cd);
        CotMapComponent.getInstance().addAdditionalDetail(cd.getElementName(), cd);

        Intent reportIntent = new Intent(ReportingRate.REPORT_LOCATION);
        AtakBroadcast.getInstance().sendBroadcast(reportIntent.putExtra("reason", "detail update for remote wipe value"));
    }

    private void handleInterval1Expired(Context context) {
        Log.d(TAG, "Handling INTERVAL1_EXPIRED event.");
        handleIntervalExpired(context, getInterval1Time(), INTERVAL1_ALERT, INTERVAL1_MESSAGE, INTERVAL1_WIPE);
    }

    private void handleInterval2Expired(Context context) {
        Log.d(TAG, "Handling INTERVAL2_EXPIRED event.");
        handleIntervalExpired(context, getInterval2Time(), INTERVAL2_ALERT, INTERVAL2_MESSAGE, INTERVAL2_WIPE);
    }

    private void handleIntervalExpired(Context context, int intervalTime, String alertPrefKey, String messagePrefKey, String wipePrefKey) {
        SharedPreferences preferences = getDefaultSharedPreferences(getMapView().getContext());
        boolean triggerAlert = preferences.getBoolean(alertPrefKey, false);
        boolean sendMessage = preferences.getBoolean(messagePrefKey, false);
        boolean wipeData = preferences.getBoolean(wipePrefKey, false);

        if (triggerAlert) {
            Log.d(TAG, format("Interval %ds - Triggering alert", intervalTime));
            AlertService.triggerAlert();
        }

        if (sendMessage) {
            Log.d(TAG, format("Interval %ds - Sending message", intervalTime));
            String message = context.getString(R.string.chat_message, intervalTime);
            ChatService.sendTAKChatMessage(message);
        }

        if (wipeData) {
            Log.d(TAG, format("Interval %ds - Wiping data", intervalTime));
            WipeDataService.wipeData();
        }
    }

    private void handleStatsChanged(Intent intent, Context context) {
        Stats stats = (Stats) intent.getSerializableExtra(STATS_CHANGED_PARAM_STATS);
        if (stats == null) return;

        String statsText = context
                .getResources()
                .getString(
                        R.string.stats,
                        stats.getAzimuth(),
                        stats.getPitch(),
                        stats.getRoll(),
                        stats.isFlat(),
                        stats.getInactivityPeriod1(),
                        stats.getInactivityPeriod2()
                );
//        Log.d(TAG, statsText);
    }
}
