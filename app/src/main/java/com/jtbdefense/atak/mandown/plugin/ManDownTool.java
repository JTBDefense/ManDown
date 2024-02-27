package com.jtbdefense.atak.mandown.plugin;


import static com.jtbdefense.atak.mandown.ui.ManDownDropDown.SHOW_DROP_DOWN;

import android.content.Context;

import com.atak.plugins.impl.AbstractPluginTool;


public class ManDownTool extends AbstractPluginTool {

    public ManDownTool(final Context context) {
        super(context, context.getString(R.string.app_name), context.getString(R.string.app_name), context.getResources().getDrawable(R.drawable.skull), SHOW_DROP_DOWN);
    }
}
