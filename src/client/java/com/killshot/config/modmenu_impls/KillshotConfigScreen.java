package com.killshot.config.modmenu_impls;

import com.killshot.Killshot;
import com.killshot.KillshotClient;
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
    protected ButtonWidget doneButton;

    private static final int BUTTON_WIDTH = 150;
    private static final int BUTTON_HEIGHT = 20;
    private static final int VERTICAL_SPACING = 5;

    private int getEnabledX() {
        return (super.width / 2) - (BUTTON_WIDTH / 2);
    }

    private int getEnabledY() {
        return (super.height / 2) - (BUTTON_HEIGHT / 2);
    }

    private static Tooltip getEnabledTooltip() {
        return Tooltip.of(Text.literal((KillshotClient.config.isEnabled() ? "Disable" : "Enable") + " Killshot"));
    }

    private static Text getEnabledButtonText() {
        return Text.literal("Enabled: " + String.valueOf(KillshotClient.config.isEnabled()));
    }

    private int getRespawnX() {
        return enabledButton.getX();
    }

    private int getRespawnY() {
        return enabledButton.getY() + BUTTON_HEIGHT + VERTICAL_SPACING;
    }

    private static Tooltip getRespawnTooltip() {
        return Tooltip.of(Text.literal("Respawn/don't respawn when Killshot key is pressed"));
    }

    private static Text getRespawnImmediatelyButtonText() {
        return Text.literal("Respawn immediately: " + String.valueOf(KillshotClient.config.respawnImmediately()));
    }

    private int getDoneX() {
        return enabledButton.getX();
    }

    private int getDoneY() {
        return height - 28;
    }

    protected KillshotConfigScreen(Screen parent) {
        super(Text.literal("Killshot Config"));

        this.parent = parent;
    }

    private void isEnabledOnClick() {
        try {
            KillshotClient.config.isEnabled(!KillshotClient.config.isEnabled());
            KillshotClient.config.saveToFile();
            enabledButton.setMessage(getEnabledButtonText());
            enabledButton.setTooltip(getEnabledTooltip());
        } catch (IOException e) {
            Killshot.logError("While saving config: " + e.getMessage());
        }
    }

    private void respawnImmediatelyOnClick() {
        try {
            KillshotClient.config.respawnImmediately(!KillshotClient.config.respawnImmediately());
            KillshotClient.config.saveToFile();
            respawnImmediatelyButton.setMessage(getRespawnImmediatelyButtonText());
        } catch (IOException e) {
            Killshot.logError("While saving config: " + e.getMessage());
        }
    }

    @Override
    protected void init() {
        super.init();

        final Text enabledButtonText = getEnabledButtonText();
        final Text respawnImmediatelyButtonText = getRespawnImmediatelyButtonText();

        enabledButton = ButtonWidget.builder(enabledButtonText, button -> isEnabledOnClick())
                .dimensions(getEnabledX(), getEnabledY(), BUTTON_WIDTH, BUTTON_HEIGHT)
                .tooltip(getEnabledTooltip())
                .build();

        respawnImmediatelyButton = ButtonWidget.builder(respawnImmediatelyButtonText, button -> respawnImmediatelyOnClick())
                .dimensions(getRespawnX(), getRespawnY(), BUTTON_WIDTH, BUTTON_HEIGHT)
                .tooltip(getRespawnTooltip())
                .build();

        doneButton = ButtonWidget.builder(Text.literal("Done"), button -> close())
                .dimensions(getDoneX(), getDoneY(), BUTTON_WIDTH, BUTTON_HEIGHT)
                .build();

        TextWidget label = new TextWidget(Text.literal("Killshot Config"), MinecraftClient.getInstance().textRenderer);
        label.setDimensionsAndPosition(100, 20, width / 2 - 50, 10);
        label.alignCenter();

        super.addDrawableChild(label);
        super.addDrawableChild(enabledButton);
        super.addDrawableChild(respawnImmediatelyButton);
        super.addDrawableChild(doneButton);
    }

    @Override
    public void close() {
        client.setScreen(parent);
    }
}
