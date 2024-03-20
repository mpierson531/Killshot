package com.killshot;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class ComplexKey {
    private List<KeyBinding> complexBinding;
    private KeyBinding simpleBinding;
    private final boolean isComplex;

    ComplexKey(List<KeyBinding> complexBinding) throws KillshotException {
        if (complexBinding.isEmpty()) {
            throw new KillshotException("While registering keybinding: ", "binding was empty");
        }

        this.complexBinding = complexBinding;
        this.isComplex = complexBinding.size() > 1;

        if (!isComplex) {
            this.simpleBinding = complexBinding.get(0);
            this.complexBinding = null;
        } else {
            this.simpleBinding = null;
        }
    }

    ComplexKey(KeyBinding simpleBinding) {
        this.complexBinding = null;
        this.simpleBinding = simpleBinding;
        this.isComplex = false;
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
        if (isComplex()) {
            for (KeyBinding binding : complexBinding) {
                KeyBindingHelper.registerKeyBinding(binding);
            }
        } else {
            KeyBindingHelper.registerKeyBinding(simpleBinding);
        }

        return this;
    }

    ComplexKey deregister() throws KillshotException {
        throw new KillshotException("com.killshot.ComplexKey::deregister not implemented yet!", "");
    }

    private boolean complexIsPressed() {
        return complexBinding.stream().allMatch(KeyBinding::isPressed);
    }

    private boolean complexWasPressed() {
        return complexBinding.stream().allMatch(KeyBinding::wasPressed);
    }

    private boolean simpleIsPressed() {
        return simpleBinding.isPressed();
    }

    private boolean simpleWasPressed() {
        return simpleBinding.wasPressed();
    }

    boolean isComplex() {
        return isComplex;
    }

    boolean isPressed() {
        if (isComplex()) {
            return complexIsPressed();
        }

        return simpleIsPressed();
    }

    boolean wasPressed() {
        if (isComplex()) {
            return complexWasPressed();
        }

        return simpleWasPressed();
    }
}
