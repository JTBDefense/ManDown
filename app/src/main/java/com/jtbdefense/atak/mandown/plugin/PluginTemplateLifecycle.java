package com.jtbdefense.atak.mandown.plugin;

import com.atak.plugins.impl.AbstractPlugin;
import gov.tak.api.plugin.IServiceController;
import com.atak.plugins.impl.PluginContextProvider;
import com.jtbdefense.atak.mandown.ManDownMapComponent;

public class PluginTemplateLifecycle extends AbstractPlugin {

   public PluginTemplateLifecycle(IServiceController serviceController) {
        super(serviceController, new ManDownTool(serviceController.getService(PluginContextProvider.class).getPluginContext()), new ManDownMapComponent());
        PluginNativeLoader.init(serviceController.getService(PluginContextProvider.class).getPluginContext());
    }
}

