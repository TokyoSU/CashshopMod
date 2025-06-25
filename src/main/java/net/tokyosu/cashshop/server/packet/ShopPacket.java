package net.tokyosu.cashshop.server.packet;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.tokyosu.cashshop.plugin.kubejs.events.KubeStartupRegister;
import net.tokyosu.cashshop.server.NetworkHandler;
import net.tokyosu.cashshop.server.save.ShopSavedData;
import net.tokyosu.cashshop.utils.CurrencyData;
import net.tokyosu.cashshop.utils.CurrencyUtils;

import java.util.function.Supplier;

public class ShopPacket {
    public CurrencyData price;
    public ItemStack stack;
    public int slotId;
    public int pageId;
    public int tabId;
    public int discount;

    public ShopPacket(ItemStack stack, int slotId, int pageId, int tabId, long goldPrice, long silverPrice, long copperPrice, int discount) {
        this.stack = stack;
        this.slotId = slotId;
        this.tabId = tabId;
        this.pageId = pageId;
        this.price = new CurrencyData(goldPrice, silverPrice, copperPrice);
        this.discount = discount;
    }

    public static void encode(ShopPacket msg, FriendlyByteBuf buf) {
        buf.writeItem(msg.stack);
        buf.writeInt(msg.slotId);
        buf.writeInt(msg.tabId);
        buf.writeInt(msg.pageId);
        buf.writeLong(msg.price.gold);
        buf.writeLong(msg.price.silver);
        buf.writeLong(msg.price.copper);
        buf.writeInt(msg.discount);
    }

    public static ShopPacket decode(FriendlyByteBuf buf) {
        ItemStack stack = buf.readItem();
        int slotId = buf.readInt();
        int tabId = buf.readInt();
        int pageId = buf.readInt();
        long gold = buf.readLong();
        long silver = buf.readLong();
        long copper = buf.readLong();
        int discount = buf.readInt();
        return new ShopPacket(stack, slotId, pageId, tabId, gold, silver, copper, discount);
    }

    private static void handleBuy(ItemStack stack, int slotId, int pageId, int tabId, CurrencyData pPrice, int discount, ServerPlayer player) {
        stack.setCount(Math.min(stack.getCount(), stack.getMaxStackSize())); // Make sure it's from 1 to 64 !

        CurrencyData currency = CurrencyUtils.getCurrency(player);
        CurrencyData price = CurrencyUtils.getDiscountedPriceBreakdown(pPrice, discount);

        if (currency.isGreaterOrEqual(price)) {
            currency.subtract(price);
            CurrencyUtils.setCurrency(player, currency, 0);
            if (KubeStartupRegister.isChatLogEnabled()) {
                player.displayClientMessage(Component.translatable("menu.cashshop.message.buy_item").append(stack.getDisplayName().getString() + " x" + stack.getCount() + ": " + price), false);
            }
            ServerLevel level = player.serverLevel();
            ShopSavedData savedData = NetworkHandler.getShopSavedData(level);
            savedData.incrementBuyCount(tabId, slotId, pageId, 1);
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
            if (player != null) {
                if (msg.stack.isEmpty() || !ForgeRegistries.ITEMS.containsValue(msg.stack.getItem())) return;
                handleBuy(msg.stack, msg.slotId, msg.pageId, msg.tabId, msg.price, msg.discount, player); // Check if player is valid before handling the buy !
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
