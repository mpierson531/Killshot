package com.killshot;

import com.mojang.authlib.GameProfile;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.StatHandler;
import net.minecraft.util.Arm;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;

public class KillshotClient implements ClientModInitializer {
	private class KillshotException extends Throwable {
		String prologue = "";
		String message = "";
		String finalMessage = "";

		KillshotException(final String prologue, final String message) {
			this.prologue = prologue;
			this.message = message;
			this.finalMessage = prologue + message;
		}
	}

	KeyBinding keyBinding;
	MinecraftServer server;
	PlayerEntity playerEntity;
	String playerName;

	private void logError(final String errorMessage) {
		Killshot.LOGGER.error(errorMessage);
	}

	private void logInfo(final String info) {
		Killshot.LOGGER.info(info);
	}

	private PlayerEntity getPlayer() {
		return server.getPlayerManager().getPlayer(playerName);
	}

	private void registerClientPlayer(MinecraftServer server) throws KillshotException {
		try {
			logInfo("Attempting to register client player...");
			this.server = server;
			playerName = MinecraftClient.getInstance().getSession().getUsername();
			playerEntity = getPlayer();
		} catch (Exception e) {
			throw new KillshotException("Exception caught while registering player: ", e.getMessage());
		}

		if (playerEntity == null) {
			throw new KillshotException("While registering player entity: ", "player entity was null");
		}
	}

	private void kill() {
		playerEntity.kill();
		playerEntity = getPlayer();
	}

	@Override
	public void onInitializeClient() {
		keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.killshot.kill", // The translation key of the keybinding's name
				InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
				GLFW.GLFW_KEY_COMMA, // The keycode of the key
				"category.examplemod.test" // The translation key of the keybinding's category.
		));

		logInfo("Registered kill key!");

		ClientPlayConnectionEvents.JOIN.register((networkHandler, packetSender, client) -> {
			try {
				registerClientPlayer(client.getServer());
				logInfo("Registered player!");
			} catch (KillshotException ke) {
				logError(ke.finalMessage);
				logError("Killshot will not work!");
			}
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (keyBinding.wasPressed()) {
				kill();
			}
		});

		logInfo("Killshot initialized!");
	}
}