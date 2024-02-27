package com.jtbdefense.atak.mandown.services;

import static com.atakmap.android.maps.MapView.getMapView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.atakmap.android.data.DataMgmtReceiver;
import com.atakmap.android.dropdown.DropDownManager;
import com.atakmap.android.ipc.AtakBroadcast;
import com.atakmap.android.missionpackage.MissionPackageMapComponent;
import com.atakmap.app.preferences.PreferenceControl;
import com.atakmap.coremap.filesystem.FileSystemUtils;
import com.atakmap.coremap.io.IOProvider;
import com.atakmap.coremap.io.IOProviderFactory;
import com.atakmap.net.AtakAuthenticationDatabase;
import com.atakmap.net.AtakCertificateDatabase;

import java.io.File;

public class WipeDataService {

    private static final String TAG = "ManDownWipeDataService";
    public static void wipeData() {
        Thread.currentThread().setName(TAG);

        // work to be performed by background thread
        Log.d(TAG, "Executing data wipe");

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getMapView().getContext());
        prefs.edit().putBoolean("clearingContent", true).apply();

        //close dropdowns/tools
        AtakBroadcast.getInstance().sendBroadcast(new Intent("com.atakmap.android.maps.toolbar.END_TOOL"));
        DropDownManager.getInstance().closeAllDropDowns();

        // Prevent errors during secure delete
        MissionPackageMapComponent mp = MissionPackageMapComponent.getInstance();
        if (mp != null) {
            mp.getFileIO().disableFileWatching();
        }

        //delete majority of files here on background thread rather then tying up UI
        //thread by having components delete large numbers of files
        //while processing ZEROIZE_CONFIRMED_ACTION intent
        DataMgmtReceiver.deleteDirs(new String[] {
                "grg", "attachments", "cert", "overlays",
                FileSystemUtils.EXPORT_DIRECTORY,
                FileSystemUtils.TOOL_DATA_DIRECTORY,
                FileSystemUtils.SUPPORT_DIRECTORY,
                FileSystemUtils.CONFIG_DIRECTORY
        }, true);

        // reset all prefs and stored credentials
        AtakAuthenticationDatabase.clear();
        AtakCertificateDatabase.clear();

        //Clear all pref groups
        Log.d(TAG, "Clearing preferences");
        for (String name : PreferenceControl.getInstance(getMapView().getContext()).PreferenceGroups) {
            prefs = getMapView().getContext().getSharedPreferences(name, Context.MODE_PRIVATE);
            if (prefs != null) {
                prefs.edit().clear().apply();
            }
        }

        final File databaseDir = FileSystemUtils.getItem("Databases");
        final File[] files = IOProviderFactory.listFiles(databaseDir);
        if (files != null) {
            for (File file : files) {
                if (IOProviderFactory.isFile(file)) {
                    final String name = file.getName();
                    // skip list for now
                    if (name.equals("files.sqlite3") || name.equals("GRGs2.sqlite") || name.equals("layers3.sqlite") || name.equals("GeoPackageImports.sqlite")) {
                        Log.d(TAG, "skipping: " + name);
                    } else {
                        Log.d(TAG, "purging: " + name);
                        IOProviderFactory.delete(file, IOProvider.SECURE_DELETE);
                    }
                }
            }
        }
        DataMgmtReceiver.deleteDirs(new String[] {"layers", "native", "mobac", "mrsid", "imagery", "pri", "pfi", "imagecache" }, true);
        DataMgmtReceiver.deleteDirs(new String[] {"DTED", "pfps"}, false);

        AtakBroadcast.getInstance().sendBroadcast(new Intent("com.atakmap.app.QUITAPP").putExtra("FORCE_QUIT", true));
    }
}
