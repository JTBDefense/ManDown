package com.jtbdefense.atak.mandown.cot;

import static com.atakmap.android.maps.MapView.*;
import static com.atakmap.coremap.filesystem.FileSystemUtils.isEmpty;

import android.util.Log;

import com.atakmap.android.cot.detail.CotDetailHandler;
import com.atakmap.android.maps.MapItem;
import com.atakmap.comms.CommsMapComponent;
import com.atakmap.coremap.cot.event.CotDetail;
import com.atakmap.coremap.cot.event.CotEvent;
import com.jtbdefense.atak.mandown.services.WipeDataService;

public class PerformRemoteWipeCotHandler extends CotDetailHandler {

    public static final String PERFORM_REMOTE_WIPE_COT_KEY = "PERFORM_REMOTE_WIPE_COT_KEY";
    public static final String DETAILS_META_KEY_PERFORM_REMOTE_WIPE = "DETAILS_META_KEY_PERFORM_REMOTE_WIPE";
    private static final String TAG = "ManDownRemoteWipeCotHan";

    public PerformRemoteWipeCotHandler() {
        super(PERFORM_REMOTE_WIPE_COT_KEY);
    }

    @Override
    public CommsMapComponent.ImportResult toItemMetadata(MapItem item, CotEvent event, CotDetail detail) {
        String uidToRemoteWipe = detail.getAttribute(DETAILS_META_KEY_PERFORM_REMOTE_WIPE);

        if (isEmpty(uidToRemoteWipe)) {
            return CommsMapComponent.ImportResult.FAILURE;
        }
        item.setMetaString(DETAILS_META_KEY_PERFORM_REMOTE_WIPE, uidToRemoteWipe);

        String myUid = getMapView().getSelfMarker().getUID();
        if (myUid.equals(uidToRemoteWipe)) {
            WipeDataService.wipeData();
        }

        Log.d(TAG, "Setting metadata for performRemoteWipe to " + uidToRemoteWipe);
        return CommsMapComponent.ImportResult.SUCCESS;
    }

    @Override
    public boolean toCotDetail(MapItem item, CotEvent event, CotDetail cotDetail) {
        boolean uidToWipe = item.getMetaBoolean(DETAILS_META_KEY_PERFORM_REMOTE_WIPE, false);
        cotDetail.setAttribute(DETAILS_META_KEY_PERFORM_REMOTE_WIPE, Boolean.toString(uidToWipe));
        Log.d(TAG, "Setting cotDetail for performRemoteWipe to " + uidToWipe);
        return true;
    }
}
