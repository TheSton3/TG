package com.thestone.tg.ghoul;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;

public class GhoulHungerStats {
    private float hungerLevel;
    private final float maxHunger;
    private long lastUpdateTick;

    public GhoulHungerStats() {
        this(0f, 100f, 0L);
    }

    public GhoulHungerStats(float hungerLevel, float maxHunger, long lastUpdateTick) {
        this.hungerLevel = hungerLevel;
        this.maxHunger = maxHunger;
        this.lastUpdateTick = lastUpdateTick;
    }


    public static final Codec<GhoulHungerStats> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.FLOAT.fieldOf("level").forGetter(GhoulHungerStats::getHungerLevel),
            Codec.FLOAT.fieldOf("max").forGetter(GhoulHungerStats::getMaxHunger),
            Codec.LONG.fieldOf("last_tick").forGetter(GhoulHungerStats::getLastUpdateTick)
    ).apply(inst, GhoulHungerStats::new));

    public void tick(long currentTick) {
        if (currentTick - lastUpdateTick >= 400) { // +0.05 каждые 20 сек
            this.hungerLevel = Mth.clamp(this.hungerLevel + 0.05f, 0f, this.maxHunger);
            this.lastUpdateTick = currentTick;
        }
    }

    public void addSatiation(float nutrition) {
        this.hungerLevel = Mth.clamp(this.hungerLevel - nutrition * 2.5f, 0f, this.maxHunger);
    }

    public boolean isStarving() { return this.hungerLevel >= 80f; }
    public float getHungerPercent() { return this.maxHunger > 0 ? this.hungerLevel / this.maxHunger : 0f; }


    public float getHungerLevel() { return hungerLevel; }
    public float getMaxHunger() { return maxHunger; }
    public long getLastUpdateTick() { return lastUpdateTick; }
}