package com.killshot.config.modmenu_impls;

import com.killshot.Killshot;
import com.killshot.KillshotClient;
import com.killshot.config.KillshotConfigModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;

import java.io.IOException;

@Environment(EnvType.CLIENT)
public class KillshotConfigScreen extends Screen {
    protected Screen parent;

    protected ButtonWidget enabledButton;
    protected ButtonWidget respawnImmediatelyButton;
    protected ButtonWidget killButton;
    protected ButtonWidget respawnButton;

    protected ButtonWidget doneButton;

    private static final int VERTICAL_SPACING = 5;
    private static final int HORIZONTAL_SPACING = 5;
    private static final int BUTTON_WIDTH = 150;
    private static final int HALF_BUTTON_WIDTH = BUTTON_WIDTH / 2;
    private static final int HALF_BUTTON_WIDTH_W_SPACING = HALF_BUTTON_WIDTH - HORIZONTAL_SPACING;
    private static final int BUTTON_HEIGHT = 20;

    private int getEnabledY() {
        return (super.height / 2) - (BUTTON_HEIGHT / 2);
    }

    private static String enabledToString(final boolean enabled) {
        return enabled ? "Disable" : "Enable";
    }

    private static Tooltip getEnabledTooltip() {
        return Tooltip.of(Text.literal(enabledToString(KillshotClient.getInstance().getConfig().isEnabled()) + " Killshot"));
    }

    private static Text getEnabledButtonText() {
        return Text.literal("Enabled: " + String.valueOf(KillshotClient.getInstance().getConfig().isEnabled()));
    }

    private int getRespawnImmediatelyY() {
        return enabledButton.getY() + BUTTON_HEIGHT + VERTICAL_SPACING;
    }

    private static Tooltip getRespawnImmediatelyTooltip() {
        return Tooltip.of(Text.literal(enabledToString(KillshotClient.getInstance().getConfig().respawnImmediately()) + " respawning immediately when Killshot is activated"));
    }

    private static Text getRespawnImmediatelyButtonText() {
        return Text.literal("Respawn immediately: " + String.valueOf(KillshotClient.getInstance().getConfig().respawnImmediately()));
    }

    private int getKillY() {
        return respawnImmediatelyButton.getY() + BUTTON_HEIGHT + VERTICAL_SPACING;
    }

    private static Text getKillText() {
        return Text.literal("Kill");
    }

    private static Tooltip getKillTooltip() {
        return Tooltip.of(Text.literal("Kill yourself"));
    }

    private int getRespawnX() {
        return killButton.getX() + HALF_BUTTON_WIDTH + HORIZONTAL_SPACING;
    }

    private int getRespawnY() {
        return killButton.getY();
    }

    private static Text getRespawnText() {
        return Text.literal("Respawn");
    }

    private static Tooltip getRespawnTooltip() {
        return Tooltip.of(Text.literal("Kill yourself and respawn"));
    }

    private int getDoneY() {
        return super.height - 28;
    }

    protected KillshotConfigScreen(Screen parent) {
        super(Text.literal("Killshot Config"));

        this.parent = parent;
    }

    private void isEnabledOnClick() {
        try {
            final KillshotConfigModel config = KillshotClient.getInstance().getConfig();
            config.isEnabled(!config.isEnabled());
            config.saveToFile();

            enabledButton.setMessage(getEnabledButtonText());
            enabledButton.setTooltip(getEnabledTooltip());
        } catch (IOException e) {
            Killshot.logError("While saving config: " + e.getMessage());
        }
    }

    private void respawnImmediatelyOnClick() {
        try {
            final KillshotConfigModel config = KillshotClient.getInstance().getConfig();
            config.respawnImmediately(!config.respawnImmediately());
            config.saveToFile();

            respawnImmediatelyButton.setMessage(getRespawnImmediatelyButtonText());
            respawnImmediatelyButton.setTooltip(getRespawnImmediatelyTooltip());
        } catch (IOException e) {
            Killshot.logError("While saving config: " + e.getMessage());
        }
    }

    @Override
    protected void init() {
        super.init();

        final int x = (super.width / 2) - (BUTTON_WIDTH / 2);

        final Text enabledButtonText = getEnabledButtonText();
        final Text respawnImmediatelyButtonText = getRespawnImmediatelyButtonText();
        final Text killText = getKillText();
        final Text respawnText = getRespawnText();

        final int doneY = getDoneY();

        enabledButton = ButtonWidget.builder(enabledButtonText, button -> isEnabledOnClick())
                .dimensions(x, getEnabledY(), BUTTON_WIDTH, BUTTON_HEIGHT)
                .tooltip(getEnabledTooltip())
                .build();

        respawnImmediatelyButton = ButtonWidget.builder(respawnImmediatelyButtonText, button -> respawnImmediatelyOnClick())
                .dimensions(x, getRespawnImmediatelyY(), BUTTON_WIDTH, BUTTON_HEIGHT)
                .tooltip(getRespawnImmediatelyTooltip())
                .build();

        if (KillshotClient.getInstance().isInWorld()) {
            killButton = ButtonWidget.builder(killText, button -> KillshotClient.getInstance().kill(MinecraftClient.getInstance().getServer()))
                    .dimensions(x, getKillY(), HALF_BUTTON_WIDTH, BUTTON_HEIGHT)
                    .tooltip(getKillTooltip())
                    .build();

            respawnButton = ButtonWidget.builder(respawnText, button -> KillshotClient.getInstance().respawn(MinecraftClient.getInstance().getServer()))
                    .dimensions(getRespawnX(), getRespawnY(), HALF_BUTTON_WIDTH_W_SPACING, BUTTON_HEIGHT)
                    .tooltip(getRespawnTooltip())
                    .build();
        }

        doneButton = ButtonWidget.builder(Text.literal("Done"), button -> close())
                .dimensions(x, doneY, BUTTON_WIDTH, BUTTON_HEIGHT)
                .build();

        TextWidget label = new TextWidget(Text.literal("Killshot Config"), MinecraftClient.getInstance().textRenderer);
        label.setDimensionsAndPosition(100, 20, super.width / 2 - 50, 10);
        label.alignCenter();

        super.addDrawableChild(label);

        super.addDrawableChild(enabledButton);
        super.addDrawableChild(respawnImmediatelyButton);

        if (KillshotClient.getInstance().isInWorld()) {
            super.addDrawableChild(killButton);
            super.addDrawableChild(respawnButton);
        }

        super.addDrawableChild(doneButton);
    }

    @Override
    public void close() {
        client.setScreen(parent);
    }
}
