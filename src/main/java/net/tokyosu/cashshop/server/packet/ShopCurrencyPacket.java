package net.tokyosu.cashshop.server.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.tokyosu.cashshop.utils.CurrencyData;
import net.tokyosu.cashshop.utils.CurrencyUtils;

import java.util.function.Supplier;

public class ShopCurrencyPacket {
    public CurrencyData currency;
    public int type;

    public ShopCurrencyPacket(CurrencyData currency, int type) {
        this.currency = currency;
        this.type = type;
    }

    public static void encode(ShopCurrencyPacket msg, FriendlyByteBuf buf) {
        buf.writeLong(msg.currency.gold);
        buf.writeLong(msg.currency.silver);
        buf.writeLong(msg.currency.copper);
        buf.writeInt(msg.type);
    }

    public static ShopCurrencyPacket decode(FriendlyByteBuf buf) {
        long gold = buf.readLong();
        long silver = buf.readLong();
        long copper = buf.readLong();
        int type = buf.readInt();
        return new ShopCurrencyPacket(new CurrencyData(gold, silver, copper), type);
    }

    public static void handle(ShopCurrencyPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                CurrencyUtils.setCurrency(player, msg.currency, msg.type);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
