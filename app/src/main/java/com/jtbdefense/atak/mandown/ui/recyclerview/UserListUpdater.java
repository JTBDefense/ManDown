package com.jtbdefense.atak.mandown.ui.recyclerview;

import android.app.Activity;
import android.content.Context;
import android.util.Log;


import com.jtbdefense.atak.mandown.ui.ManDownDropDown;

import java.util.TimerTask;

public class UserListUpdater extends TimerTask {
    private static final String TAG = "ManDownCotUpdaterTask";
    private final Context context;
    private final ManDownDropDown manDownDropDown;

    public UserListUpdater(Context context, ManDownDropDown manDownDropDown) {
        this.context = context;
        this.manDownDropDown = manDownDropDown;
    }

    @Override
    public void run() {
        if (!manDownDropDown.isClosed()) {
            Log.d(TAG, "Refreshing user list");
            ((Activity) context).runOnUiThread(manDownDropDown::rerenderDropdown);
        }
    }
}
