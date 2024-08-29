package org.garlicsight.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ComparatorMode;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
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

    // Redstone information settings
    private final BooleanSetting redstoneInfo = new BooleanSetting("Redstone Info", true);

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

        // Set up sub-settings for redstone information
        BooleanSetting redstoneTitle = new BooleanSetting("'Redstone:' Title", true);
        redstoneInfo.addSubSettings(redstoneTitle);

        // Register the settings with the framework
        this.registerSettings(titleSetting, blockInfo, entityInfo, redstoneInfo);

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
                BlockHitResult blockHit = (BlockHitResult) hitResult;
                BlockState blockState = minecraft.level.getBlockState(blockHit.getBlockPos());

                // Check for block information
                if (blockInfo.getValue()) {
                    if (blockTitle.getValue()) {
                        info.append("Block: ");
                    }
                    info.append(blockState.getBlock().getName().getString());

                    // Add a colon for specific redstone components
                    boolean needsColon = blockState.is(Blocks.REDSTONE_TORCH) ||
                            blockState.is(Blocks.LEVER) ||
                            blockState.is(Blocks.REDSTONE_BLOCK) ||
                            blockState.is(Blocks.TRIPWIRE_HOOK) ||
                            blockState.is(Blocks.STICKY_PISTON) ||
                            blockState.is(Blocks.PISTON);

                    if (redstoneInfo.getValue() && needsColon) {
                        info.append(": ");
                    }

                    if (blockPosition.getValue()) {
                        info.append('\n');
                        if (positionTitle.getValue()) {
                            info.append("Pos: ");
                        }
                        info.append(blockHit.getBlockPos().toShortString());
                    }
                }

                // Add a space between block info and redstone info
                if (redstoneInfo.getValue()) {
                    info.append('\n');
                }

                // Check for redstone information
                if (redstoneInfo.getValue()) {
                    if (blockState.is(Blocks.REDSTONE_WIRE)) {
                        int powerLevel = blockState.getValue(BlockStateProperties.POWER);
                        info.append("Power: ").append(powerLevel);
                    } else if (blockState.is(Blocks.REDSTONE_TORCH)) {
                        boolean isActive = blockState.getValue(BlockStateProperties.LIT);
                        info.append(isActive ? "Active" : "Inactive");
                    } else if (blockState.is(Blocks.REDSTONE_BLOCK)) {
                        info.append("Constant Power");
                    } else if (blockState.is(Blocks.REPEATER)) {
                        int delay = blockState.getValue(BlockStateProperties.DELAY);
                        boolean isLocked = blockState.getValue(BlockStateProperties.LOCKED);
                        info.append("Delay: ").append(delay).append(" ticks");
                        if (isLocked) {
                            info.append("\nLocked");
                        }
                    } else if (blockState.is(Blocks.COMPARATOR)) {
                        ComparatorMode mode = blockState.getValue(BlockStateProperties.MODE_COMPARATOR);
                        info.append("Mode: ").append(mode == ComparatorMode.SUBTRACT ? "Subtract" : "Compare");
                    } else if (blockState.is(Blocks.LEVER)) {
                        boolean isPowered = blockState.getValue(BlockStateProperties.POWERED);
                        info.append(isPowered ? "On" : "Off");
                    } else if (blockState.is(Blocks.STICKY_PISTON) || blockState.is(Blocks.PISTON)) {
                        boolean isExtended = blockState.getValue(BlockStateProperties.EXTENDED);
                        info.append(isExtended ? "Extended" : "Retracted");
                    } else if (blockState.is(Blocks.NOTE_BLOCK)) {
                        int pitch = blockState.getValue(BlockStateProperties.NOTE);
                        info.append("Pitch: ").append(pitch);
                    } else if (blockState.is(Blocks.TRIPWIRE_HOOK)) {
                        boolean isPowered = blockState.getValue(BlockStateProperties.POWERED);
                        info.append(isPowered ? "Activated" : "Deactivated");
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
                        info.append(String.format("%.1f / %.1f", livingEntity.getHealth(), livingEntity.getMaxHealth()));
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
