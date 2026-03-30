package com.thestone.tg.util;

import com.thestone.tg.core.ModAttachments;
import net.minecraft.world.entity.player.Player;

public class Util {

    public static boolean isGhoul(Player player){
        int value = player.getData(ModAttachments.GHOUL_HUNGER.get());
        return value >= 0;

    }
}
