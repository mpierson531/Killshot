package com.killshot;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.*;
import net.minecraft.registry.entry.RegistryEntry;
import org.jetbrains.annotations.Nullable;

public class KillshotDamageSource extends DamageSource {
    private static DamageType killshotDamageType = new DamageType("", DamageScaling.ALWAYS, 0.0f, DamageEffects.HURT, DeathMessageType.DEFAULT);
    private static Entity sourceEntity = null;

    static void initSourceEntity(Entity entity) {
        sourceEntity = entity;
    }

    public KillshotDamageSource() {
        super(RegistryEntry.of(killshotDamageType), sourceEntity, null);
    }
}
