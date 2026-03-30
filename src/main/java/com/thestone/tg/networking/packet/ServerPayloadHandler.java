package com.thestone.tg.networking.packet;

import com.thestone.tg.core.ModAttachments;
import net.neoforged.neoforge.network.handling.IPayloadContext;
// Handle packets FROM the Server TO the Client
public class ServerPayloadHandler {
    // HERE WE ARE ON THE CLIENT
    public static void handleHungerOnClient(HungerData hungerData, IPayloadContext context) {
        context.player().setData(ModAttachments.GHOUL_HUNGER, hungerData.newValue());
    }
}
