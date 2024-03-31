package com.killshot;

import com.killshot.config.KillshotConfigModel;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;

public class KillshotClient implements ClientModInitializer {
	private ComplexKey binding;
	private PlayerEntity playerEntity;
	private String playerName;
	private KillshotConfigModel config;
	private boolean isInWorld;

	private static KillshotClient instance;

	public static KillshotClient getInstance() {
		return instance;
	}

	private PlayerEntity getPlayer(final MinecraftServer server) {
		return server.getPlayerManager().getPlayer(playerName);
	}

	private void registerClientPlayer(final MinecraftClient client) throws KillshotException {
		try {
			Killshot.logInfo("Attempting to initialize client player...");
			playerName = client.getSession().getUsername();
			playerEntity = getPlayer(client.getServer());
		} catch (Exception e) {
			throw new KillshotException("Exception caught while registering player: ", e.getMessage());
		}

		if (playerEntity == null) {
			throw new KillshotException("While registering player entity: ", "player entity was null");
		}

		Killshot.logInfo("Player entity initialized!");
	}

	private void initBinding() {
		Killshot.logInfo("Initializing kill key...");

		binding = ComplexKey.getDefaultBinding().register();

		Killshot.logInfo("Kill key initialized!");
	}

	public void kill(final MinecraftServer server) {
		playerEntity = getPlayer(server);
		playerEntity.kill();
	}

	public void respawn(final MinecraftServer server) {
		final GameRules.BooleanRule immediateRespawnKey = server.getGameRules().get(GameRules.DO_IMMEDIATE_RESPAWN);

		immediateRespawnKey.set(true, server);
		kill(server);
		immediateRespawnKey.set(false, server);
	}

	public KillshotConfigModel getConfig() {
		return config;
	}

	public boolean isInWorld() {
		return isInWorld;
	}

	@Override
	public void onInitializeClient() {
		instance = this;
		config = KillshotConfigModel.init();

		initBinding();

		ClientPlayConnectionEvents.JOIN.register((networkHandler, packetSender, client) -> {
			try {
				registerClientPlayer(client);
				isInWorld = true;
			} catch (KillshotException ke) {
				Killshot.logError(ke.finalMessage);
				Killshot.logError("Killshot will not work!");
			}
		});

		ClientPlayConnectionEvents.DISCONNECT.register((clientNetworkHandler, client) -> {
			isInWorld = false;
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (!config.isEnabled()) {
				return;
			}

			if (binding.wasPressed()) {
				if (config.respawnImmediately()) {
					respawn(client.getServer());
				} else {
					kill(client.getServer());
				}
			}
		});

		Killshot.logInfo("Killshot initialized!");
	}
}