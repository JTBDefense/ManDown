package com.jtbdefense.atak.mandown.cot;

import static com.atakmap.coremap.filesystem.FileSystemUtils.isEmpty;

import android.util.Log;

import com.atakmap.android.cot.detail.CotDetailHandler;
import com.atakmap.android.maps.MapItem;
import com.atakmap.comms.CommsMapComponent;
import com.atakmap.coremap.cot.event.CotDetail;
import com.atakmap.coremap.cot.event.CotEvent;

public class AllowRemoteWipeCotHandler extends CotDetailHandler {

    public static final String ALLOW_REMOTE_WIPE_COT_KEY = "ALLOW_REMOTE_WIPE_COT_KEY";
    public static final String DETAILS_META_KEY_ALLOW_REMOTE_WIPE = "DETAILS_META_KEY_ALLOW_REMOTE_WIPE";
    private static final String TAG = "ManDownRemoteWipeCotHan";

    public AllowRemoteWipeCotHandler() {
        super(ALLOW_REMOTE_WIPE_COT_KEY);
    }

    @Override
    public CommsMapComponent.ImportResult toItemMetadata(MapItem item, CotEvent event, CotDetail detail) {
        String allowRemoteWipeString = detail.getAttribute(DETAILS_META_KEY_ALLOW_REMOTE_WIPE);

        if (isEmpty(allowRemoteWipeString)) {
            return CommsMapComponent.ImportResult.FAILURE;
        }

        boolean allowRemoteWipe = Boolean.parseBoolean(allowRemoteWipeString);
        item.setMetaBoolean(DETAILS_META_KEY_ALLOW_REMOTE_WIPE, allowRemoteWipe);

        Log.d(TAG, "Setting metadata for allowRemoteWipe to " + allowRemoteWipe);
        return CommsMapComponent.ImportResult.SUCCESS;
    }

    @Override
    public boolean toCotDetail(MapItem item, CotEvent event, CotDetail cotDetail) {
        boolean allowRemoteWipe = item.getMetaBoolean(DETAILS_META_KEY_ALLOW_REMOTE_WIPE, false);
        cotDetail.setAttribute(DETAILS_META_KEY_ALLOW_REMOTE_WIPE, Boolean.toString(allowRemoteWipe));
        Log.d(TAG, "Setting cotDetail for allowRemoteWipe to " + allowRemoteWipe);
        return true;
    }
}
