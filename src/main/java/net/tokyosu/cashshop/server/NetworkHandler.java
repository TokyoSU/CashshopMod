package net.tokyosu.cashshop.server;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.tokyosu.cashshop.CashShop;
import net.tokyosu.cashshop.server.packet.ShopCurrencyPacket;
import net.tokyosu.cashshop.server.packet.ShopOpenPacket;
import net.tokyosu.cashshop.server.packet.ShopPacket;
import net.tokyosu.cashshop.server.packet.ShopUpdatePacket;
import net.tokyosu.cashshop.server.save.ShopSavedData;
import net.tokyosu.cashshop.utils.CurrencyData;

@SuppressWarnings("InstantiationOfUtilityClass")
public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    private static final SimpleChannel NETWORK_CHANNEL = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(CashShop.MOD_ID, "shop_network"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        NETWORK_CHANNEL.registerMessage(0, ShopPacket.class, ShopPacket::encode, ShopPacket::decode, ShopPacket::handle); // Need to register a packet, so the shop can send item correctly (without disappear) !
        NETWORK_CHANNEL.registerMessage(1, ShopCurrencyPacket.class, ShopCurrencyPacket::encode, ShopCurrencyPacket::decode, ShopCurrencyPacket::handle);
        NETWORK_CHANNEL.registerMessage(2, ShopOpenPacket.class, ShopOpenPacket::encode, ShopOpenPacket::decode, ShopOpenPacket::handle);
        NETWORK_CHANNEL.registerMessage(3, ShopUpdatePacket.class, ShopUpdatePacket::encode, ShopUpdatePacket::decode, ShopUpdatePacket::handle);
    }

    public static void sendBuy(ItemStack stack, int slotId, int pageId, int tabId, CurrencyData price, int discount) {
        NETWORK_CHANNEL.sendToServer(new ShopPacket(stack, slotId, pageId, tabId, price.gold, price.silver, price.copper, discount));
    }

    public static void sendOpenShop() {
        NETWORK_CHANNEL.sendToServer(new ShopOpenPacket());
    }

    public static void sendCurrency(CurrencyData currency, int type) {
        NETWORK_CHANNEL.sendToServer(new ShopCurrencyPacket(currency, type));
    }

    public static void sendCurrencyToPlayer(ServerPlayer player, CurrencyData currency, int type) {
        NETWORK_CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new ShopCurrencyPacket(currency, type));
    }

    public static void sendShopUpdate() {
        NETWORK_CHANNEL.sendToServer(new ShopUpdatePacket());
    }

    public static ShopSavedData getShopSavedData(ServerLevel world) {
        return world.getDataStorage().computeIfAbsent(
                ShopSavedData::load,
                ShopSavedData::new,
                "shop_saved_data" // This is the save file name (shop_data.dat)
        );
    }
}
