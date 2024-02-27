package com.jtbdefense.atak.mandown.domain;

import java.io.Serializable;

public class Stats implements Serializable {
    private final double azimuth;
    private final double pitch;
    private final double roll;
    private final boolean isFlat;
    private final long inactivityPeriod1;
    private final long inactivityPeriod2;

    public Stats(double azimuth, double pitch, double roll, boolean isFlat, long inactivityPeriod1, long inactivityPeriod2) {
        this.azimuth = azimuth;
        this.pitch = pitch;
        this.roll = roll;
        this.isFlat = isFlat;
        this.inactivityPeriod1 = inactivityPeriod1;
        this.inactivityPeriod2 = inactivityPeriod2;
    }

    public double getAzimuth() {
        return azimuth;
    }

    public double getPitch() {
        return pitch;
    }

    public double getRoll() {
        return roll;
    }

    public boolean isFlat() {
        return isFlat;
    }

    public long getInactivityPeriod1() {
        return inactivityPeriod1;
    }

    public long getInactivityPeriod2() {
        return inactivityPeriod2;
    }
}
