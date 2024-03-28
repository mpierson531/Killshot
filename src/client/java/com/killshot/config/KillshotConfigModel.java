package com.killshot.config;

import com.killshot.Killshot;
import com.killshot.config.util.IntWrapper;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;

public class KillshotConfigModel {
    private boolean isEnabled;
    private boolean respawnImmediately;

    public static final String CONFIG_PATH = getConfigDirectory() + File.separatorChar + "killshot_config.txt";

    public static String getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir().toString();
    }

    private static boolean isEnabledDefault() {
        return true;
    }

    private static boolean respawnImmediatelyDefault() {
        return false;
    }

    private static String loadSettingError(final String name, final String value) {
        return "Could not load setting \"" + name + "\" with value of \"" + value + "\".";
    }

    public static KillshotConfigModel defaultConfig() {
        return new KillshotConfigModel(isEnabledDefault(), respawnImmediatelyDefault());
    }

    public static KillshotConfigModel init() {
        KillshotConfigModel config = KillshotConfigModel.defaultConfig();
        final File configFile = new File(CONFIG_PATH);

        try {
            if (!configFile.exists()) {
                Killshot.logInfo("Creating config file...");
                config.saveToFile(configFile);
                Killshot.logInfo("Config file created!");
            } else {
                final String configContent = Files.readString(configFile.toPath());
                config.load(configContent);
            }
        } catch (Exception e) {
            Killshot.logWarning("Exception caught while loading config: " + e.getMessage());
            Killshot.logWarning("Using default config.");
        }

        return config;
    }

    public KillshotConfigModel(final boolean isEnabled, final boolean respawnImmediately) {
        this.isEnabled = isEnabled;
        this.respawnImmediately = respawnImmediately;
    }

    public boolean isEnabled() {
        return this.isEnabled;
    }

    public void isEnabled(final boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public boolean respawnImmediately() {
        return this.respawnImmediately;
    }

    public void respawnImmediately(final Boolean respawnImmediately) {
        this.respawnImmediately = respawnImmediately;
    }

    public String save() {
        StringBuilder builder = new StringBuilder();

        builder.append("isEnabled ");
        builder.append(isEnabled);

        builder.append(System.lineSeparator());

        builder.append("respawnImmediately ");
        builder.append(respawnImmediately);

        return builder.toString();
    }

    private void saveToFile(final File file) throws IOException {
        Files.writeString(file.toPath().toAbsolutePath(), save());
    }

    public void saveToFile() throws IOException {
        saveToFile(new File(CONFIG_PATH));
    }

    private String getWhileNotSpace(final String string, IntWrapper startIndex) {
        final int length = string.length();

        if (startIndex.value >= length) {
            return "";
        }

        StringBuilder value = new StringBuilder();

        char c = string.charAt(startIndex.postfixIncrement());

        while (c != ' ') {
            value.append(c);
            c = startIndex.value < length ? string.charAt(startIndex.postfixIncrement()) : ' ';
        }

        return value.toString();
    }

    private boolean parseBoolean(final String name, final String stringValue) {
        final String trimmedAndLowered = stringValue.trim().toLowerCase();

        if (trimmedAndLowered.equals("true")) {
            return true;
        } else if (trimmedAndLowered.equals("false")) {
            return false;
        }

        Killshot.logWarning(loadSettingError(name, stringValue));
        return false;
    }

    private boolean loadValueFromLine(final String line, IntWrapper indexWrapper) {
        final String name = getWhileNotSpace(line, indexWrapper);

        if (indexWrapper.value == line.length()) {
            Killshot.logWarning("Unexpected end of config file. Using default config.");
            return false;
        }

        final String stringValue = getWhileNotSpace(line, indexWrapper);
        boolean wasSuccessful = true;

        if (name.equals("isEnabled")) {
            isEnabled = parseBoolean(name, stringValue);
        } else if (name.equals("respawnImmediately")) {
            respawnImmediately = parseBoolean(name, stringValue);
        } else {
            Killshot.logWarning(loadSettingError(name, stringValue));
            wasSuccessful = false;
        }

        return wasSuccessful;
    }

    private boolean load(final String content) {
        if (content.isEmpty()) {
            Killshot.logWarning("No config content found. Using default config.");
            setToDefault();
            return false;
        }

        boolean wasSuccessful = true;

        try (Scanner scanner = new Scanner(content)) {
            String line = scanner.nextLine();
            IntWrapper indexWrapper = new IntWrapper();

            if (!loadValueFromLine(line, indexWrapper)) {
                wasSuccessful = false;
            }

            if (indexWrapper.value >= content.length()) {
                return wasSuccessful;
            }

            line = scanner.nextLine();
            indexWrapper.zeroOut();

            if (!loadValueFromLine(line, indexWrapper)) {
                wasSuccessful = false;
            }
        }

        if (!wasSuccessful) {
            setToDefault();
        }

        return wasSuccessful;
    }

    public void setToDefault() {
        isEnabled = isEnabledDefault();
        respawnImmediately = respawnImmediatelyDefault();
    }
}