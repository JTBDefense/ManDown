package com.jtbdefense.atak.mandown.services;

import com.atakmap.android.emergency.tool.EmergencyManager;
import com.atakmap.android.emergency.tool.EmergencyType;

public class AlertService {

    public static void triggerAlert() {
        EmergencyManager emergencyManager = EmergencyManager.getInstance();
        emergencyManager.setEmergencyType(EmergencyType.Custom);
        emergencyManager.initiateRepeat(EmergencyType.Custom, false);
        emergencyManager.setEmergencyOn(true);
    }
}
