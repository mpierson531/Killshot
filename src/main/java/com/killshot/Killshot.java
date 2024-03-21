package com.killshot;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Killshot implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("killshot");

	public static void logInfo(final String info) {
		LOGGER.info(info);
	}

	public static void logWarning(final String warning) {
		LOGGER.warn(warning);
	}

	public static void logError(final String error) {
		LOGGER.error(error);
	}

	@Override
	public void onInitialize() {

	}
}