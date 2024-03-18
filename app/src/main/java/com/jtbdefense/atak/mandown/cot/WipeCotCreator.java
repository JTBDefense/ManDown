package com.jtbdefense.atak.mandown.cot;

import static com.atakmap.android.maps.MapView.getMapView;
import static com.jtbdefense.atak.mandown.cot.PerformRemoteWipeCotHandler.DETAILS_META_KEY_PERFORM_REMOTE_WIPE_PASSWORD;
import static com.jtbdefense.atak.mandown.cot.PerformRemoteWipeCotHandler.DETAILS_META_KEY_PERFORM_REMOTE_WIPE_UID;
import static com.jtbdefense.atak.mandown.cot.PerformRemoteWipeCotHandler.PERFORM_REMOTE_WIPE_COT_KEY;
import static com.jtbdefense.atak.mandown.services.EncryptionService.encryptStrAndToBase64;

import com.atakmap.android.maps.Marker;
import com.atakmap.coremap.cot.event.CotDetail;
import com.atakmap.coremap.cot.event.CotEvent;
import com.atakmap.coremap.cot.event.CotPoint;
import com.atakmap.coremap.maps.coords.GeoPoint;
import com.atakmap.coremap.maps.time.CoordinatedTime;

import java.util.UUID;

public class WipeCotCreator {
    public static CotEvent createWipeCotEvent(String providedPassword, String uidToWipe) throws Exception {
        final Marker item = (Marker) getMapView().getMapItem(uidToWipe);

        if (item == null) {
            throw new Exception("MapItem with UID " + uidToWipe + " not found. Skipping wipe CoT creation.");
        }

        CotEvent event = new CotEvent();
        event.setUID(UUID.randomUUID().toString());
        event.setHow("h-e");

        GeoPoint location = item.getPoint();
        CotPoint point = new CotPoint(location.getLatitude(),
                location.getLongitude(), location.getAltitude(),
                location.getCE(), location.getLE());
        event.setPoint(point);

        event.setStart(new CoordinatedTime());
        event.setTime(new CoordinatedTime());
        event.setStale(new CoordinatedTime().addMilliseconds(10000));

        event.setType("a-f-G-U-C");

        CotDetail detail = new CotDetail();
        CotDetail cd = new CotDetail(PERFORM_REMOTE_WIPE_COT_KEY);
        cd.setAttribute(DETAILS_META_KEY_PERFORM_REMOTE_WIPE_UID, item.getUID());
        cd.setAttribute(DETAILS_META_KEY_PERFORM_REMOTE_WIPE_PASSWORD, encryptStrAndToBase64(providedPassword));
        detail.addChild(cd);
        event.setDetail(detail);

        StringBuilder builder = new StringBuilder();
        event.buildXml(builder);
        return event;
    }
}
