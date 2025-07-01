package net.tokyosu.cashshop.server.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;
import net.tokyosu.cashshop.server.provider.ShopMenuProvider;

import java.util.function.Supplier;

@SuppressWarnings("InstantiationOfUtilityClass")
public class ShopOpenPacket {
    public static void encode(ShopOpenPacket msg, FriendlyByteBuf buf) {
    }

    public static ShopOpenPacket decode(FriendlyByteBuf buf) {
        return new ShopOpenPacket();
    }

    public static void handle(ShopOpenPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                NetworkHooks.openScreen(player, new ShopMenuProvider());
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
