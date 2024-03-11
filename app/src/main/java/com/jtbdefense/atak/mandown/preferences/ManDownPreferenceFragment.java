package com.jtbdefense.atak.mandown.preferences;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static android.text.TextUtils.isEmpty;
import static com.atakmap.android.maps.MapView.getMapView;
import static com.jtbdefense.atak.mandown.domain.Events.ALLOW_REMOTE_WIPE_PREFERENCE_CHANGED;
import static com.jtbdefense.atak.mandown.domain.Events.ALLOW_REMOTE_WIPE_PREFERENCE_CHANGED_VALUE;
import static com.jtbdefense.atak.mandown.preferences.ManDownPreferences.ALLOW_REMOTE_WIPE;
import static com.jtbdefense.atak.mandown.preferences.ManDownPreferences.REMOTE_WIPE_PASSWORD;
import static com.jtbdefense.atak.mandown.preferences.ManDownPreferencesResolver.getWipePassword;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.widget.Toast;

import com.atakmap.android.preference.PluginPreferenceFragment;
import com.jtbdefense.atak.mandown.plugin.R;


public class ManDownPreferenceFragment extends PluginPreferenceFragment {

    private static Context staticPluginContext;
    public static final String TAG = "ManDownPreferenceFr";

    /**
     * Only will be called after this has been instantiated with the 1-arg constructor.
     * Fragments must has a zero arg constructor.
     */
    public ManDownPreferenceFragment() {
        super(staticPluginContext, R.xml.preferences);
    }

    @SuppressLint("ValidFragment")
    public ManDownPreferenceFragment(final Context pluginContext) {
        super(pluginContext, R.xml.preferences);
        staticPluginContext = pluginContext;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Preference allowRemoteWipePreference = findPreference(ALLOW_REMOTE_WIPE);
        Preference wipePasswordPreference = findPreference(REMOTE_WIPE_PASSWORD);

        SharedPreferences preferences = getDefaultSharedPreferences(getMapView().getContext());
        broadcastAllowRemoteWipePreference(preferences.getBoolean(ALLOW_REMOTE_WIPE, false));

        allowRemoteWipePreference.setOnPreferenceChangeListener((preference, newValue) -> {
            if ((Boolean) newValue && isEmpty(getWipePassword())) {
                Toast.makeText(staticPluginContext, R.string.no_password_content, Toast.LENGTH_LONG).show();
            }
            broadcastAllowRemoteWipePreference((Boolean) newValue);
            return true;
        });

        wipePasswordPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            if (isEmpty((String)newValue)) {
                Toast.makeText(staticPluginContext, R.string.password_required, Toast.LENGTH_LONG).show();
                return false;
            }
            return true;
        });
    }

    private static void broadcastAllowRemoteWipePreference(Boolean newValue) {
        Intent statsIntent = new Intent();
        statsIntent.setAction(ALLOW_REMOTE_WIPE_PREFERENCE_CHANGED);
        statsIntent.putExtra(ALLOW_REMOTE_WIPE_PREFERENCE_CHANGED_VALUE, newValue);
        getMapView().getContext().sendBroadcast(statsIntent);
    }
}
