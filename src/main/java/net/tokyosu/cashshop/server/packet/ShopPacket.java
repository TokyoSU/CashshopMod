package net.tokyosu.cashshop.server.packet;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.tokyosu.cashshop.plugin.kubejs.events.KubeStartupRegister;
import net.tokyosu.cashshop.utils.CurrencyData;
import net.tokyosu.cashshop.utils.CurrencyUtils;

import java.util.function.Supplier;

public class ShopPacket {
    public ItemStack stack;
    public CurrencyData price;
    public int discount;

    public ShopPacket(ItemStack stack, long goldPrice, long silverPrice, long copperPrice, int discount) {
        this.stack = stack;
        this.price = new CurrencyData(goldPrice, silverPrice, copperPrice);
        this.discount = discount;
    }

    public static void encode(ShopPacket msg, FriendlyByteBuf buf) {
        buf.writeItem(msg.stack);
        buf.writeLong(msg.price.gold);
        buf.writeLong(msg.price.silver);
        buf.writeLong(msg.price.copper);
        buf.writeInt(msg.discount);
    }

    public static ShopPacket decode(FriendlyByteBuf buf) {
        ItemStack stack = buf.readItem();
        long gold = buf.readLong();
        long silver = buf.readLong();
        long copper = buf.readLong();
        int discount = buf.readInt();
        return new ShopPacket(stack, gold, silver, copper, discount);
    }

    private static void handleBuy(ItemStack stack, CurrencyData pPrice, int discount, ServerPlayer player) {
        stack.setCount(Math.min(stack.getCount(), stack.getMaxStackSize())); // Make sure it's from 1 to 64 !

        CurrencyData currency = CurrencyUtils.getCurrency(player);
        CurrencyData price = CurrencyUtils.getDiscountedPriceBreakdown(pPrice, discount);

        if (currency.isGreaterOrEqual(price)) {
            currency.subtract(price);
            CurrencyUtils.setCurrency(player, currency, 0);
            if (KubeStartupRegister.isChatLogEnabled()) {
                player.displayClientMessage(Component.translatable("menu.cashshop.message.buy_item")
                        .append(stack.getDisplayName().getString() + " x" + stack.getCount())
                        .withStyle(ChatFormatting.GREEN), false);
            }
            player.addItem(stack);
        } else {
            player.displayClientMessage(Component.translatable("menu.cashshop.message.buy_item_failed")
                    .append(stack.getDisplayName().getString() + " x" + stack.getCount())
                    .append(Component.translatable("menu.cashshop.message.buy_item_failed_part2"))
                    .withStyle(ChatFormatting.RED), false);
        }
    }

    public static void handle(ShopPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            // Check if the player try to buy an empty stack or invalid item !
            if (msg.stack.isEmpty() || !ForgeRegistries.ITEMS.containsValue(msg.stack.getItem()))
                return;
            if (player != null) {
                handleBuy(msg.stack, msg.price, msg.discount, player); // Check if player is valid before handling the buy !
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
