package com.thestone.tg.ghoul;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thestone.tg.Config;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.GameRules;
import org.jetbrains.annotations.Nullable;

public class GhoulHunger {

    public static final Codec<GhoulHunger> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("hungerLevel").forGetter(GhoulHunger::getHungerLevel),
                    Codec.INT.fieldOf("maxHunger").forGetter(GhoulHunger::getMaxHunger),
                    Codec.FLOAT.fieldOf("saturation").forGetter(GhoulHunger::getSaturation),
                    Codec.FLOAT.fieldOf("exhaustion").forGetter(GhoulHunger::getExhaustion),
                    Codec.INT.fieldOf("starvationTimer").forGetter(h -> h.starvationTimer),
                    Codec.INT.fieldOf("regenTimer").forGetter(h -> h.regenTimer)
            ).apply(instance, GhoulHunger::new)
    );

    private int hungerLevel;
    private int maxHunger;
    private float saturation;
    private float exhaustion;
    private int starvationTimer;
    private int regenTimer;
    private boolean dirty;
    private boolean wasOnGround = true;

    private GhoulHunger(int hungerLevel, int maxHunger, float saturation,
                        float exhaustion, int starvationTimer, int regenTimer) {
        this.hungerLevel = hungerLevel;
        this.maxHunger = maxHunger;
        this.saturation = saturation;
        this.exhaustion = exhaustion;
        this.starvationTimer = starvationTimer;
        this.regenTimer = regenTimer;
    }

    // Фабрика только для НОВЫХ игроков (не вызывается при загрузке мира!)
    public static GhoulHunger create(Player player) {
        return new GhoulHunger(20, 20, 5.0f, 0.0f, 0, 0);
    }

    public int getHungerLevel() {
        return hungerLevel;
    }

    public int getMaxHunger() {
        return maxHunger;
    }

    public float getSaturation() {
        return saturation;
    }

    public float getExhaustion() {
        return exhaustion;
    }

    public void setHungerLevel(int value) {
        int prev = this.hungerLevel;
        this.hungerLevel = Math.max(0, Math.min(value, maxHunger));
        if (this.hungerLevel != prev) dirty = true;
    }


    public void setMaxHunger(int value) {
        int prev = this.maxHunger;
        this.maxHunger = Math.max(1, value);
        if (this.hungerLevel > maxHunger) this.hungerLevel = maxHunger;
        if (this.maxHunger != prev) dirty = true;
    }

    public void addExhaustion(float amount) {
        float prev = this.exhaustion;
        this.exhaustion = Math.min(this.exhaustion + amount, 40.0f);
        if (this.exhaustion != prev) dirty = true;
    }

    public void setSaturationLevel(float saturation) {
        float prev = this.saturation;
        this.saturation = Math.min(this.saturation + saturation, 40.0f);

    }

    public void setExhaustionLevel(float exhaustion) {
        float prev = this.exhaustion;
        this.exhaustion = Math.max(this.exhaustion - exhaustion, 0.0f);
    }


    public void addFood(int amount, float saturationModifier) {
        int prev = hungerLevel;
        int added = Math.min(amount, maxHunger - hungerLevel);
        hungerLevel += added;
        saturation = Math.min(saturation + (float) added * saturationModifier * 2.0f, (float) hungerLevel);
        if (hungerLevel != prev) dirty = true;
    }

    /**
     * Вызывается каждый тик. Player передаётся явно, чтобы избежать null после загрузки.
     */
    public boolean onUpdate(Player player) {
        if (player.level().isClientSide || !player.isAlive()) return false;
        if (player.isCreative() || player.isSpectator()) {
            exhaustion = 0;
            hungerLevel = maxHunger;
            return false;
        }

        boolean changed = false;

        // БАЗОВАЯ ТРАТА ЗА ТИК (0.00375 = ~20 голода за 13 минут простоя)
        double tickDrain = Config.BASE_DRAIN.get();

        // МОДИФИКАТОРЫ АКТИВНОСТИ
        if (player.isSprinting()) tickDrain *= 2.0f;           // Спринт x2
        if (player.isSwimming()) tickDrain *= 1.5f;           // Плавание x1.5
        if (player.getDeltaMovement().horizontalDistance() > 0.01) tickDrain += 0.002f; // Ходьба

        // Прыжок: детектим по смене состояния onGround
        if (player.onGround() && !wasOnGround) {
            tickDrain += 0.05f; // Разовая трата за прыжок (ванильное значение)
        }
        wasOnGround = player.onGround();

        // Применяем глобальный множитель конфига
        addExhaustion((float) (tickDrain * Config.EXHAUSTION_MULTIPLIER.get()));

        // ПОРОГОВАЯ ЛОГИКА (4.0 exhaustion = 1 единица расхода)
        if (exhaustion >= 4.0f) {
            exhaustion -= 4.0f;
            if (saturation > 0.0f) {
                saturation = Math.max(0.0f, saturation - 1.0f);
            } else {
                setHungerLevel(hungerLevel - 1);
            }
            changed = true;
        }

        // БЛОКИРОВКА ВАННИЛЬНОГО ГОЛОДА (предотвращает двойной урон)
        FoodData vanilla = player.getFoodData();
        if (vanilla.getFoodLevel() < 20) vanilla.setFoodLevel(20);
        if (vanilla.getSaturationLevel() < 5.0f) vanilla.setSaturation(5.0f);
        if (vanilla.getExhaustionLevel() > 0.0f) vanilla.setExhaustion(0.0f);

        // РЕГЕНЕРАЦИЯ И ГОЛОДАНИЕ
        boolean naturalRegen = player.level().getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION);

        if (naturalRegen && saturation > 0.0f && hungerLevel >= maxHunger && player.getHealth() < player.getMaxHealth()) {
            regenTimer++;
            if (regenTimer >= 10) {
                player.heal(Math.min(saturation, 6.0f) / 6.0f);
                addExhaustion(6.0f);
                regenTimer = 0;
                changed = true;
            }
        } else if (naturalRegen && hungerLevel > 0 && player.getHealth() < player.getMaxHealth()) {
            regenTimer++;
            boolean fastHeal = hungerLevel >= 18 && regenTimer >= 80;
            if (fastHeal || regenTimer >= 300) {
                player.heal(fastHeal ? 1.0f : 0.5f);
                addExhaustion(fastHeal ? 6.0f : 3.0f);
                regenTimer = 0;
                changed = true;
            }
        } else if (hungerLevel <= 0 && player.level().getDifficulty() != Difficulty.PEACEFUL) {
            starvationTimer++;
            if (starvationTimer >= 80) {
                if (player.getHealth() > 10.0f || player.level().getDifficulty() == Difficulty.HARD) {
                    player.hurt(player.damageSources().starve(), 1.0f);
                }
                starvationTimer = 0;
                changed = true;
            }
        } else {
            regenTimer = 0;
            starvationTimer = 0;
        }

        boolean result = changed || dirty;
        dirty = false;
        return result;
    }
}