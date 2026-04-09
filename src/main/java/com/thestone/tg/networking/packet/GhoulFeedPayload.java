package com.thestone.tg.networking.packet;

import com.thestone.tg.Tg;
import com.thestone.tg.ghoul.GhoulPlayer;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record GhoulFeedPayload(int targetEntityId) implements CustomPacketPayload {


    public static final CustomPacketPayload.Type<GhoulFeedPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Tg.MOD_ID, "ghoulfeedpayload"));

    public static final StreamCodec<ByteBuf, GhoulFeedPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, GhoulFeedPayload::targetEntityId,
                    GhoulFeedPayload::new
            );


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(GhoulFeedPayload payload, IPayloadContext context) {
        // Выполняем в главном потоке сервера
        context.enqueueWork(() -> {
            Player feeder = context.player();
            if (feeder == null || feeder.level().isClientSide) return;

            GhoulPlayer ghoul = GhoulPlayer.get(feeder);
            if (!ghoul.isGhoul()) return;

            // Проверка кулдауна
            if (isOnCooldown(feeder)) {
                return;
            }
            // Получаем цель
            Entity target = feeder.level().getEntity(payload.targetEntityId());
            if (!(target instanceof LivingEntity livingTarget) || !isValidTarget(livingTarget, feeder)) {
                return;
            }
            // Выполняем питание
            if (performFeed(feeder, livingTarget)) {
                setCooldown(feeder);
            }
        });
    }

    public static boolean isValidTarget(LivingEntity target, Player feeder) {
        // Игрок-жертва
        if (target instanceof Player victim) {
            // if (!GhoulFeedingConfig.CAN_FEED_ON_PLAYERS.get()) return false;
            if (victim.isCreative() || victim.isSpectator()) return false;
            if (victim == feeder) return false;
            if (GhoulPlayer.get(victim).isGhoul()) return false; // Нельзя есть гулей
            return victim.isAlive() && victim.getHealth() > 0;
        }

        // Житель
        if (target instanceof Villager villager) {
            // if (!GhoulFeedingConfig.CAN_FEED_ON_VILLAGERS.get()) return false;
            return villager.isAlive() && villager.getHealth() > 0;
        }

        return false; // Другие сущности не поддерживаются
    }

    private static boolean performFeed(Player feeder, LivingEntity victim) {
        //   double damage = GhoulFeedingConfig.DAMAGE_PER_FEED.get();
        //   double hungerRestore = GhoulFeedingConfig.HUNGER_PER_FEED.get();
        double damage = 10.0f;
        double hungerRestore = 4.0f;

        // Наносим урон
        DamageSource source = feeder.damageSources().mobAttack(feeder);
        boolean hurt = victim.hurt(source, (float) damage);

        if (!hurt) {
            return false;
        }
        // Восстанавливаем голод гуля
        GhoulPlayer ghoul = GhoulPlayer.get(feeder);
        ghoul.getHunger().addFood((int) hungerRestore, 1.0f);
        ghoul.syncToClient(feeder);
        return true;
    }


    private static boolean isOnCooldown(Player player) {
        // Простая реализация: можно вынести в Attachment при необходимости
        return player.getCooldowns().isOnCooldown(Items.AIR); // Заглушка
    }

    private static void setCooldown(Player player) {
        //int cooldownTicks = GhoulFeedingConfig.FEED_COOLDOWN_TICKS.get();
        int cooldownTicks = 2;
        // Используем ванильную систему кулдаунов для простоты
        player.getCooldowns().addCooldown(Items.AIR, cooldownTicks);
    }
}

