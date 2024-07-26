package org.garlicsight;

import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.plugin.Plugin;
import org.rusherhack.client.api.system.IHudManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.garlicsight.hud.GarlicSightHudElement;

/**
 * Main plugin class for the GarlicSight mod.
 * Responsible for initializing and registering the HUD element.
 */
public class GarlicSightPlugin extends Plugin {
    private static final Logger LOGGER = LogManager.getLogger("GarlicSightPlugin");

    // Manager for handling HUD elements in the RusherHacks framework
    private final IHudManager hudManager = RusherHackAPI.getHudManager();

    // HUD element for displaying information about entities and blocks
    private final GarlicSightHudElement garlicSightHudElement = new GarlicSightHudElement();

    /**
     * Called when the plugin is loaded. Registers the HUD element.
     */
    @Override
    public void onLoad() {
        LOGGER.info("Bringing GarlicSight into focus...");

        // Register the GarlicSight HUD element
        this.hudManager.registerFeature(this.garlicSightHudElement);
        LOGGER.info("GarlicSight is now in clear view and ready!");
    }

    /**
     * Called when the plugin is unloaded. Deregisters the HUD element.
     */
    @Override
    public void onUnload() {
        LOGGER.info("Dimming GarlicSight...");

        // Deregister the GarlicSight HUD element
        this.garlicSightHudElement.setToggled(false);
        LOGGER.info("GarlicSight has been turned off and stored away.");
    }
}
