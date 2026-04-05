package com.thestone.tg.item;

import com.thestone.tg.ghoul.GhoulPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class KakuhoeItem extends Item {
    public KakuhoeItem(Properties properties) {
        super(properties);
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {

        if (!level.isClientSide) {

            GhoulPlayer ghoul = GhoulPlayer.get(player);

            // Активация состояния гуля
            ghoul.setGhoul(true);
            ghoul.getHunger().setHungerLevel(20);
            ghoul.getHunger().setMaxHunger(20);

            // Мгновенная синхронизация с клиентом
            ghoul.syncToClient(player);
        }
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
}
