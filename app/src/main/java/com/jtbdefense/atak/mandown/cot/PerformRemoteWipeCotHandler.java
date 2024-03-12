package com.jtbdefense.atak.mandown.cot;

import static com.atakmap.android.maps.MapView.getMapView;
import static com.atakmap.coremap.filesystem.FileSystemUtils.isEmpty;
import static com.jtbdefense.atak.mandown.preferences.ManDownPreferencesResolver.getWipePassword;

import android.util.Log;

import com.atakmap.android.cot.detail.CotDetailHandler;
import com.atakmap.android.maps.MapItem;
import com.atakmap.comms.CommsMapComponent;
import com.atakmap.coremap.cot.event.CotDetail;
import com.atakmap.coremap.cot.event.CotEvent;
import com.jtbdefense.atak.mandown.services.EncryptionService;
import com.jtbdefense.atak.mandown.services.WipeDataService;

import java.util.Objects;

public class PerformRemoteWipeCotHandler extends CotDetailHandler {

    public static final String PERFORM_REMOTE_WIPE_COT_KEY = "PERFORM_REMOTE_WIPE_COT_KEY";
    public static final String DETAILS_META_KEY_PERFORM_REMOTE_WIPE_UID = "DETAILS_META_KEY_PERFORM_REMOTE_WIPE";
    public static final String DETAILS_META_KEY_PERFORM_REMOTE_WIPE_PASSWORD = "DETAILS_META_KEY_PERFORM_REMOTE_WIPE_PASSWORD";
    private static final String TAG = "ManDownRemoteWipeCotHan";

    public PerformRemoteWipeCotHandler() {
        super(PERFORM_REMOTE_WIPE_COT_KEY);
    }

    @Override
    public CommsMapComponent.ImportResult toItemMetadata(MapItem item, CotEvent event, CotDetail detail) {
        String uidToRemoteWipe = detail.getAttribute(DETAILS_META_KEY_PERFORM_REMOTE_WIPE_UID);
        String providedPassword = detail.getAttribute(DETAILS_META_KEY_PERFORM_REMOTE_WIPE_PASSWORD);

        if (isEmpty(uidToRemoteWipe)) {
            return CommsMapComponent.ImportResult.FAILURE;
        }
        item.setMetaString(DETAILS_META_KEY_PERFORM_REMOTE_WIPE_UID, uidToRemoteWipe);
        item.setMetaString(DETAILS_META_KEY_PERFORM_REMOTE_WIPE_PASSWORD, providedPassword);

        String myUid = getMapView().getSelfMarker().getUID();
        String decryptedPassword = getDecryptedPassword(providedPassword);

        if (myUid.equals(uidToRemoteWipe) && Objects.equals(getWipePassword(), decryptedPassword)) {
            Log.d(TAG, "UIDs matches (" + uidToRemoteWipe + "), password matches (" + decryptedPassword + ") - start wipe");
            WipeDataService.wipeData();
        } else {
            Log.d(TAG, "passwords NOT OK:" + getWipePassword() + "!=" + decryptedPassword);
        }

        Log.d(TAG, "Setting metadata for performRemoteWipe to " + uidToRemoteWipe);
        return CommsMapComponent.ImportResult.SUCCESS;
    }

    private String getDecryptedPassword(String providedPassword) {
        try {
            return EncryptionService.decryptStrAndFromBase64(providedPassword);
        } catch (Exception e) {
            Log.e(TAG, "Cannot decrypt password");
            return "";
        }
    }

    @Override
    public boolean toCotDetail(MapItem item, CotEvent event, CotDetail cotDetail) {
        String uidToWipe = item.getMetaString(DETAILS_META_KEY_PERFORM_REMOTE_WIPE_UID, null);
        String password = item.getMetaString(DETAILS_META_KEY_PERFORM_REMOTE_WIPE_PASSWORD, null);
        cotDetail.setAttribute(DETAILS_META_KEY_PERFORM_REMOTE_WIPE_UID, uidToWipe);
        cotDetail.setAttribute(DETAILS_META_KEY_PERFORM_REMOTE_WIPE_PASSWORD, password);
        Log.d(TAG, "Setting cotDetail for performRemoteWipe to " + uidToWipe);
        return true;
    }
}
