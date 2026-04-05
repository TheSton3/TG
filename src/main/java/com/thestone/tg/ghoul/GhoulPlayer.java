package com.thestone.tg.ghoul;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class GhoulPlayer {
    private boolean isGhoul;
    private GhoulHungerStats hungerStats;
    private long transformationTime;
    public GhoulPlayer() {
        this(false, new GhoulHungerStats(), 0L);
    }

    public GhoulPlayer(boolean isGhoul, GhoulHungerStats hungerStats, long transformationTime) {
        this.isGhoul = isGhoul;
        this.hungerStats = hungerStats;
        this.transformationTime = transformationTime;
    }

    public static final Codec<GhoulPlayer> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.BOOL.fieldOf("is_ghoul").forGetter(GhoulPlayer::isGhoul),
            GhoulHungerStats.CODEC.fieldOf("hunger").forGetter(GhoulPlayer::getHungerStats),
            Codec.LONG.fieldOf("transform_time").forGetter(GhoulPlayer::getTransformationTime)
    ).apply(inst, GhoulPlayer::new));

    public static GhoulPlayer empty() { return new GhoulPlayer(); }


    public void becomeGhoul(long worldTime) {
        if (!this.isGhoul) {
            this.isGhoul = true;
            this.hungerStats = new GhoulHungerStats(); // сброс голода при превращении
            this.transformationTime = worldTime;
        }
    }


    public void tick(long worldTime) {
        if (this.isGhoul) {
            this.hungerStats.tick(worldTime);
        }
    }


    public boolean tryEat(float nutrition) {
        if (!this.isGhoul) return false;
        this.hungerStats.addSatiation(nutrition);
        return true;
    }

    public boolean isStarving() { return this.isGhoul && this.hungerStats.isStarving(); }
    public float getHungerPercent() { return this.isGhoul ? this.hungerStats.getHungerPercent() : 0f; }


    public  boolean isGhoul() { return isGhoul; }
    public GhoulHungerStats getHungerStats() { return hungerStats; }
    public long getTransformationTime() { return transformationTime; }

}
