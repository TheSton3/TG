package com.thestone.tg.ghoul;

import com.thestone.tg.core.ModAttachments;
import net.minecraft.world.entity.player.Player;

public class GhoulData {
    public static GhoulPlayer get(Player player) {
        return player.getData(ModAttachments.GHOUL_PLAYER);
    }

    private static void set(Player player, GhoulPlayer data) {
        player.setData(ModAttachments.GHOUL_PLAYER, data);
    }

    public static boolean isGhoul(Player player) {
        return get(player).isGhoul();
    }

    public static void becomeGhoul(Player player) {
        GhoulPlayer data = get(player);
        if (!data.isGhoul()) {
            data.becomeGhoul(player.level().getGameTime());

            set(player, data);

            player.sendSystemMessage(
                    net.minecraft.network.chat.Component.translatable("ghoul.transformation.complete")
            );
        } else {
            player.sendSystemMessage(
                    net.minecraft.network.chat.Component.translatable("ghoul.transformation.already_ghoul")
            );
        }
    }

    public static void tick(Player player) {
        GhoulPlayer data = get(player);
        if (data.isGhoul()) {
            boolean wasStarving = data.isStarving();
            data.tick(player.level().getGameTime());

            if (!wasStarving && data.isStarving()) {
                player.sendSystemMessage(
                        net.minecraft.network.chat.Component.translatable("ghoul.hunger.starving")
                );
            }
        }
    }

    public static boolean tryEat(Player player, float nutrition) {
        GhoulPlayer data = get(player);
        if (data.tryEat(nutrition)) {
            set(player, data); // помечаем как изменённый
            return true;
        }
        return false;
    }
}