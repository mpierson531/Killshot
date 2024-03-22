package com.killshot;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ComplexKey {
    private final KeyBinding simpleBinding;

    ComplexKey(KeyBinding simpleBinding) {
        this.simpleBinding = simpleBinding;
    }

    static String getDefaultBindingName() {
        return "Shoot the shot";
    }

    static InputUtil.Type getDefaultType() {
        return InputUtil.Type.KEYSYM;
    }

    static int getDefaultKeycode() {
        return GLFW.GLFW_KEY_COMMA;
    }

    private static KeyBinding getDefaultBaseBinding() {
        return new KeyBinding(
                getDefaultBindingName(),
                getDefaultType(),
                getDefaultKeycode(),
                "Killshot"
        );
    }

    static ComplexKey getDefaultBinding() {
        KeyBinding defaultBase = getDefaultBaseBinding();
        return new ComplexKey(defaultBase);
    }

    ComplexKey register() {
        KeyBindingHelper.registerKeyBinding(simpleBinding);
        return this;
    }

    ComplexKey deregister() throws KillshotException {
        throw new KillshotException("com.killshot.ComplexKey::deregister not implemented yet!", "");
    }

    private boolean simpleIsPressed() {
        return simpleBinding.isPressed();
    }

    private boolean simpleWasPressed() {
        return simpleBinding.wasPressed();
    }

    boolean isPressed() {
        return simpleIsPressed();
    }

    boolean wasPressed() {
        return simpleWasPressed();
    }
}
