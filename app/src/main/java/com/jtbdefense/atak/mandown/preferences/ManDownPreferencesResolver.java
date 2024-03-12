package com.jtbdefense.atak.mandown.preferences;

import static com.atakmap.android.maps.MapView.getMapView;
import static com.jtbdefense.atak.mandown.preferences.ManDownPreferences.ALLOW_REMOTE_WIPE;
import static com.jtbdefense.atak.mandown.preferences.ManDownPreferences.INTERVAL1_TIME;
import static com.jtbdefense.atak.mandown.preferences.ManDownPreferences.INTERVAL2_TIME;
import static com.jtbdefense.atak.mandown.preferences.ManDownPreferences.REMOTE_WIPE_PASSWORD;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class ManDownPreferencesResolver {

    public static String getWipePassword() {
        return PreferenceManager
                .getDefaultSharedPreferences(getMapView().getContext())
                .getString(REMOTE_WIPE_PASSWORD, null);
    }

    public static boolean getAllowRemoteWipe() {
        return PreferenceManager
                .getDefaultSharedPreferences(getMapView().getContext())
                .getBoolean(ALLOW_REMOTE_WIPE, false);
    }
    public static int getInterval1Time() {
        return resolveIntervalTime(60, INTERVAL1_TIME);
    }

    public static int getInterval2Time() {
        return resolveIntervalTime(300, INTERVAL2_TIME);
    }

    private static int resolveIntervalTime(int defaultValue, String preferencesKey) {
        try {
            String intervalString = PreferenceManager
                    .getDefaultSharedPreferences(getMapView().getContext())
                    .getString(preferencesKey, Integer.toString(defaultValue));
            return Integer.parseInt(intervalString);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
