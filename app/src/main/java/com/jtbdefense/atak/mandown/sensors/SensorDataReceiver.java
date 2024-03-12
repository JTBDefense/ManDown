package com.jtbdefense.atak.mandown.sensors;

import static android.hardware.Sensor.TYPE_ACCELEROMETER;
import static android.hardware.Sensor.TYPE_MAGNETIC_FIELD;
import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static com.atakmap.android.maps.MapView.getMapView;
import static com.jtbdefense.atak.mandown.events.Events.INTERVAL1_EXPIRED;
import static com.jtbdefense.atak.mandown.events.Events.INTERVAL2_EXPIRED;
import static com.jtbdefense.atak.mandown.events.Events.STATS_CHANGED;
import static com.jtbdefense.atak.mandown.events.Events.STATS_CHANGED_PARAM_STATS;
import static com.jtbdefense.atak.mandown.preferences.ManDownPreferences.INTERVAL1_IGNORE_FLAT;
import static com.jtbdefense.atak.mandown.preferences.ManDownPreferences.INTERVAL2_IGNORE_FLAT;
import static java.lang.System.arraycopy;
import static java.lang.System.currentTimeMillis;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.jtbdefense.atak.mandown.preferences.ManDownPreferencesResolver;

import java.util.Timer;
import java.util.TimerTask;

public class SensorDataReceiver implements SensorEventListener {

    private SensorManager mSensorManager;
    private final float[] rotationMatrix = new float[9];
    private final float[] newValues = new float[3];
    private final float[] currentValues = new float[3];
    private float[] gravityMatrix;
    private float[] geoMagnetic;
    private long lastChanged1 = currentTimeMillis();
    private long lastChanged2 = currentTimeMillis();

    private final static String TAG = "ManDownSensorDataRec";
    private Timer timer;
    private boolean inactivityInterval1Handled = false;
    private boolean inactivityInterval2Handled = false;

    class CheckOrientationChange extends TimerTask {

        private static final float CHANGE_THRESHOLD = .1f;
        private static final float FLAT_THRESHOLD = .1f;


        public void run() {
            float azimuth = newValues[0];
            float pitch = newValues[1];
            float roll = newValues[2];

            boolean liesFlatTop = Math.abs(pitch) <= FLAT_THRESHOLD && Math.abs(roll) <= FLAT_THRESHOLD;
            boolean liesFlatBack = Math.abs(pitch) <= FLAT_THRESHOLD && Math.abs(roll) >= Math.PI - FLAT_THRESHOLD;
            boolean isFlat = liesFlatTop || liesFlatBack;

            SharedPreferences preferences = getDefaultSharedPreferences(getMapView().getContext());
            boolean ignoreWhenFlat1 = preferences.getBoolean(INTERVAL1_IGNORE_FLAT, false);
            boolean ignoreWhenFlat2 = preferences.getBoolean(INTERVAL2_IGNORE_FLAT, false);

            if (isChanged(currentValues[0], azimuth) || isChanged(currentValues[1], pitch) || isChanged(currentValues[2], roll)) {
                Log.d(TAG, "Phone moved");
                arraycopy(newValues, 0, currentValues, 0, currentValues.length);
                lastChanged1 = currentTimeMillis();
                lastChanged2 = currentTimeMillis();
            } else {
                if (isFlat && ignoreWhenFlat1) {
                    lastChanged1 = currentTimeMillis();
                }
                if (isFlat && ignoreWhenFlat2) {
                    lastChanged2 = currentTimeMillis();
                }
            }

            long inactivityPeriod1 = currentTimeMillis() - lastChanged1;
            long inactivityPeriod2 = currentTimeMillis() - lastChanged2;

            Intent statsIntent = new Intent();
            statsIntent.setAction(STATS_CHANGED);
            statsIntent.putExtra(STATS_CHANGED_PARAM_STATS, new Stats(azimuth, pitch, roll, isFlat, inactivityPeriod1 / 1000, inactivityPeriod2 / 1000));
            getMapView().getContext().sendBroadcast(statsIntent);

            int interval1 = ManDownPreferencesResolver.getInterval1Time() * 1000;
            int interval2 = ManDownPreferencesResolver.getInterval2Time() * 1000;

            if (inactivityPeriod1 <= 1000) {
                inactivityInterval1Handled = false;
            }
            if (inactivityPeriod2 <= 1000) {
                inactivityInterval2Handled = false;
            }
            if (inactivityPeriod1 > interval1 && !inactivityInterval1Handled) {
                Intent warningIntent = new Intent();
                warningIntent.setAction(INTERVAL1_EXPIRED);
                getMapView().getContext().sendBroadcast(warningIntent);
                inactivityInterval1Handled = true;
            }
            if (inactivityPeriod2 > interval2 && !inactivityInterval2Handled) {
                Intent warningIntent = new Intent();
                warningIntent.setAction(INTERVAL2_EXPIRED);
                getMapView().getContext().sendBroadcast(warningIntent);
                inactivityInterval2Handled = true;
            }
        }

        private boolean isChanged(float a, float b) {
            return Math.abs(a - b) > CHANGE_THRESHOLD;
        }
    }

    public void initialize() {
        mSensorManager = (SensorManager) getMapView().getContext().getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometer = mSensorManager.getDefaultSensor(TYPE_ACCELEROMETER);
        Sensor magnetometer = mSensorManager.getDefaultSensor(TYPE_MAGNETIC_FIELD);

        timer = new Timer();
        mSensorManager.registerListener(this, accelerometer, 60000);
        mSensorManager.registerListener(this, magnetometer, 60000);
        timer.schedule(new CheckOrientationChange(), 0, 1000);
    }

    public void destroy() {
        mSensorManager.unregisterListener(this);
        timer.cancel();
        timer = null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            if (gravityMatrix == null) {
                gravityMatrix = new float[9];
            }
            arraycopy(event.values, 0, gravityMatrix, 0, event.values.length);
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            if (geoMagnetic == null) {
                geoMagnetic = new float[3];
            }
            arraycopy(event.values, 0, geoMagnetic, 0, event.values.length);
        }

        if (gravityMatrix != null && geoMagnetic != null) {
            SensorManager.getRotationMatrix(rotationMatrix, null, gravityMatrix, geoMagnetic);
            SensorManager.getOrientation(rotationMatrix, newValues);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}