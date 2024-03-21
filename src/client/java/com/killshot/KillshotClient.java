package com.killshot;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;

public class KillshotClient implements ClientModInitializer {
	ComplexKey binding;
	MinecraftServer server;
	PlayerEntity playerEntity;
	String playerName;

	private PlayerEntity getPlayer() {
		return server.getPlayerManager().getPlayer(playerName);
	}

	private void registerClientPlayer(MinecraftServer server) throws KillshotException {
		try {
			Killshot.logInfo("Attempting to initialize client player...");
			this.server = server;
			playerName = MinecraftClient.getInstance().getSession().getUsername();
			playerEntity = getPlayer();
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

	private void kill() {
		playerEntity.kill();
		playerEntity = getPlayer();
	}

	@Override
	public void onInitializeClient() {
		initBinding();

		ClientPlayConnectionEvents.JOIN.register((networkHandler, packetSender, client) -> {
			try {
				registerClientPlayer(client.getServer());
			} catch (KillshotException ke) {
				Killshot.logError(ke.finalMessage);
				Killshot.logError("Killshot will not work!");
			}
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (binding.isPressed()) {
				kill();
			}
		});

		Killshot.logInfo("Killshot initialized!");
	}
}