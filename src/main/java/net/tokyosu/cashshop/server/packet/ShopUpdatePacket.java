package net.tokyosu.cashshop.server.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.tokyosu.cashshop.screen.CashShopScreen;
import net.tokyosu.cashshop.utils.CurrencyUtils;

import java.util.function.Supplier;

@SuppressWarnings("InstantiationOfUtilityClass")
public class ShopUpdatePacket {
    public static void encode(ShopUpdatePacket msg, FriendlyByteBuf buf) {
    }

    public static ShopUpdatePacket decode(FriendlyByteBuf buf) {
        return new ShopUpdatePacket();
    }

    public static void handle(ShopUpdatePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                Minecraft mc = Minecraft.getInstance();
                if (mc.screen instanceof CashShopScreen screen) {
                    screen.updateFromServer(CurrencyUtils.getCurrency(player));
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
