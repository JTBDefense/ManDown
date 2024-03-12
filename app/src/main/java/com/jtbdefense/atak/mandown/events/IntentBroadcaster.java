package com.jtbdefense.atak.mandown.events;

import static com.atakmap.android.maps.MapView.getMapView;
import static com.jtbdefense.atak.mandown.events.Events.ALLOW_REMOTE_WIPE_PREFERENCE_CHANGED;
import static com.jtbdefense.atak.mandown.events.Events.ALLOW_REMOTE_WIPE_PREFERENCE_CHANGED_VALUE;

import android.content.Intent;
import android.util.Log;

public class IntentBroadcaster {

    private static final String TAG = "ManDownBroadcaster";

    public static void broadcastAllowRemoteWipePreference(Boolean value) {
        Intent statsIntent = new Intent();
        statsIntent.setAction(ALLOW_REMOTE_WIPE_PREFERENCE_CHANGED);
        statsIntent.putExtra(ALLOW_REMOTE_WIPE_PREFERENCE_CHANGED_VALUE, value);
        getMapView().getContext().sendBroadcast(statsIntent);
        Log.d(TAG, String.format("Broadcasting event: %s (%s=%s)", ALLOW_REMOTE_WIPE_PREFERENCE_CHANGED, ALLOW_REMOTE_WIPE_PREFERENCE_CHANGED_VALUE, value));
    }
}
