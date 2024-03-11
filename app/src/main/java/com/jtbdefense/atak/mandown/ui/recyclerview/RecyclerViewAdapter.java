package com.jtbdefense.atak.mandown.ui.recyclerview;

import static com.atakmap.android.maps.MapView.getMapView;
import static com.jtbdefense.atak.mandown.cot.AllowRemoteWipeCotHandler.DETAILS_META_KEY_ALLOW_REMOTE_WIPE;
import static com.jtbdefense.atak.mandown.ui.ConfirmWipeDialog.showWipeConfirmationDialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.atakmap.android.maps.MapGroup;
import com.atakmap.android.maps.MapItem;
import com.atakmap.android.maps.MapTouchController;
import com.atakmap.android.maps.MapView;
import com.atakmap.android.math.MathUtils;
import com.atakmap.android.util.ATAKUtilities;
import com.atakmap.coremap.maps.time.CoordinatedTime;
import com.jtbdefense.atak.mandown.plugin.R;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter {

    private static final String TAG = "ManDownRVAdapter";

    private final MapView mapView;
    private final LayoutInflater layoutInflater;
    private final Context context;
    private final List<MapItem> items = new ArrayList<>();

    public RecyclerViewAdapter(MapView mapView, Context context) {
        this.mapView = mapView;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        refreshUserList(getMapView().getRootGroup());
    }

    private static boolean showMapItem(MapItem mapItem) {
        MapTouchController.goTo(mapItem, true);
        return true;
    }

    public void refreshUserList(MapGroup group) {
        items.clear();
        for (MapItem item : group.getItemsRecursive()) {
            boolean remoteWipeAllowed = item.getMetaBoolean(DETAILS_META_KEY_ALLOW_REMOTE_WIPE, false);
            boolean isUserType = item.hasMetaValue("atakRoleType");
            if (isUserType && remoteWipeAllowed) {
                items.add(item);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.wipe_list_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder rh, int pos) {
        if (!(rh instanceof ViewHolder))
            return;
        MapItem mapItem = items.get(pos);
        ViewHolder h = (ViewHolder) rh;

        ATAKUtilities.SetIcon(mapView.getContext(), h.icon, mapItem);

        h.callsign.setText(mapItem.getTitle());

        long now = new CoordinatedTime().getMilliseconds();
        long lastUpdateTime = mapItem.getMetaLong("lastUpdateTime", 0);
        String timeString = MathUtils.GetTimeRemainingOrDateString(now, now - lastUpdateTime, true);
        h.lastUpdate.setText(timeString);

        boolean allowRemoteWipe = mapItem.getMetaBoolean(DETAILS_META_KEY_ALLOW_REMOTE_WIPE, false);
        if (allowRemoteWipe) {
            h.wipeButton.setEnabled(true);
            h.wipeButton.setOnClickListener(v -> showWipeConfirmationDialog(mapItem, context));
        } else {
            h.wipeButton.setEnabled(false);
            h.wipeButton.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView icon;
        final TextView callsign;
        final TextView lastUpdate;
        final ImageButton wipeButton;

        public ViewHolder(View v) {
            super(v);
            icon = v.findViewById(R.id.icon);
            callsign = v.findViewById(R.id.callsign);
            lastUpdate = v.findViewById(R.id.last_update);
            wipeButton = v.findViewById(R.id.wipeBtn);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            if (pos < 0 || pos >= getItemCount()) {
                return;
            }
            showMapItem(items.get(pos));
        }
    }
}
