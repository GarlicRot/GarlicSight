package org.garlicsight.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.block.state.BlockState;
import org.rusherhack.client.api.feature.hud.TextHudElement;
import org.rusherhack.core.setting.BooleanSetting;
import org.rusherhack.core.setting.StringSetting;

/**
 * HUD element that displays information about the block or entity
 * the player is looking at in Minecraft.
 */
public class GarlicSightHudElement extends TextHudElement {

    private final Minecraft minecraft = Minecraft.getInstance();

    // Settings for customizing the display of information
    private final StringSetting titleSetting = new StringSetting("Title", "GarlicSight");

    // Block information settings
    private final BooleanSetting blockInfo = new BooleanSetting("Block Info", true);
    private final BooleanSetting blockTitle = new BooleanSetting("'Block:' Title", true);
    private final BooleanSetting blockPosition = new BooleanSetting("Block Position", false);
    private final BooleanSetting positionTitle = new BooleanSetting("'Pos:' Title", true);

    // Entity information settings
    private final BooleanSetting entityInfo = new BooleanSetting("Entity Info", true);
    private final BooleanSetting entityTitle = new BooleanSetting("'Entity:' Title", true);
    private final BooleanSetting entityHealth = new BooleanSetting("Entity Health", true);
    private final BooleanSetting healthTitle = new BooleanSetting("'Health:' Title", true);

    /**
     * Constructor for the GarlicSight HUD element. Sets up the default
     * settings and registers them.
     */
    public GarlicSightHudElement() {
        super("GarlicSight", true);

        // Set up sub-settings for block information
        blockInfo.addSubSettings(blockTitle, blockPosition);
        blockPosition.addSubSettings(positionTitle);

        // Set up sub-settings for entity information
        entityInfo.addSubSettings(entityTitle, entityHealth);
        entityHealth.addSubSettings(healthTitle);

        // Register the settings with the framework
        this.registerSettings(titleSetting, blockInfo, entityInfo);

        // Set the default snap point for the HUD element
        this.setSnapPoint(SnapPoint.TOP_LEFT);
    }

    /**
     * Gets the label for the HUD element, which can be customized via settings.
     *
     * @return The label to display for the HUD element.
     */
    @Override
    public String getLabel() {
        return titleSetting.getValue();
    }

    /**
     * Gets the text to display in the HUD element based on the block or entity
     * the player is currently looking at.
     *
     * @return The information text to display.
     */
    @Override
    public String getText() {
        if (minecraft.level == null || minecraft.player == null) {
            return "";
        }

        HitResult hitResult = minecraft.hitResult;
        if (hitResult == null) {
            return "";
        }

        StringBuilder info = new StringBuilder();

        switch (hitResult.getType()) {
            case BLOCK:
                if (blockInfo.getValue()) {
                    BlockHitResult blockHit = (BlockHitResult) hitResult;
                    BlockState blockState = minecraft.level.getBlockState(blockHit.getBlockPos());
                    if (blockTitle.getValue()) {
                        info.append("Block: ");
                    }
                    info.append(blockState.getBlock().getName().getString());
                    if (blockPosition.getValue()) {
                        info.append('\n');
                        if (positionTitle.getValue()) {
                            info.append("Pos: ");
                        }
                        info.append(blockHit.getBlockPos().toShortString());
                    }
                }
                break;
            case ENTITY:
                if (entityInfo.getValue()) {
                    EntityHitResult entityHit = (EntityHitResult) hitResult;
                    Entity entity = entityHit.getEntity();
                    if (entityTitle.getValue()) {
                        info.append("Entity: ");
                    }
                    info.append(entity.getName().getString());
                    if (entity instanceof LivingEntity livingEntity && entityHealth.getValue()) {
                        info.append('\n');
                        if (healthTitle.getValue()) {
                            info.append("Health: ");
                        }
                        info.append(
                                String.format("%.1f / %.1f", livingEntity.getHealth(), livingEntity.getMaxHealth()));
                    }
                }
                break;
            default:
                return "";
        }


        return info.toString();
    }

    /**
     * Calculates the width of the HUD element, considering the label and the text.
     *
     * @return The width of the HUD element.
     */
    @Override
    public double getWidth() {
        double textWidth = super.getWidth();
        double labelWidth = this.getFontRenderer().getStringWidth(this.getLabel());
        return Math.max(textWidth, labelWidth);
    }
}
