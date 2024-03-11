package com.jtbdefense.atak.mandown.domain;

public interface Events {
    String STATS_CHANGED = "com.jtbdefense.atak.mandown.STATS_CHANGED";
    String STATS_CHANGED_PARAM_STATS = "com.jtbdefense.atak.mandown.STATS_CHANGED_PARAM_STATS";
    String INTERVAL1_EXPIRED = "com.jtbdefense.atak.mandown.INTERVAL1_EXPIRED";
    String INTERVAL2_EXPIRED = "com.jtbdefense.atak.mandown.INTERVAL2_EXPIRED";
    String ALLOW_REMOTE_WIPE_PREFERENCE_CHANGED = "com.jtbdefense.atak.mandown.ALLOW_REMOTE_WIPE_PREFERENCE_CHANGED";
    String ALLOW_REMOTE_WIPE_PREFERENCE_CHANGED_VALUE = "com.jtbdefense.atak.mandown.ALLOW_REMOTE_WIPE_PREFERENCE_CHANGED_VALUE";

    String PERFORM_REMOTE_WIPE = "com.jtbdefense.atak.mandown.PERFORM_REMOTE_WIPE";
    String PERFORM_REMOTE_WIPE_UID = "com.jtbdefense.atak.mandown.PERFORM_REMOTE_WIPE_UID";
    String PERFORM_REMOTE_WIPE_PASSWORD = "com.jtbdefense.atak.mandown.PERFORM_REMOTE_WIPE_PASSWORD";

}
