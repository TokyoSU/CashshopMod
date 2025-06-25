package net.tokyosu.cashshop;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.common.CreativeModeTabRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.tokyosu.cashshop.item.CoinItem;
import net.tokyosu.cashshop.menu.CashShopMenu;
import net.tokyosu.cashshop.plugin.kubejs.events.KubeStartupRegister;
import net.tokyosu.cashshop.server.NetworkHandler;
import net.tokyosu.cashshop.utils.CurrencyData;
import net.tokyosu.cashshop.utils.CurrencyUtils;
import net.tokyosu.cashshop.utils.InvUtils;

@Mod(CashShop.MOD_ID)
public class CashShop {
    public static final String MOD_ID = "cashshop";

    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final RegistryObject<MenuType<CashShopMenu>> CASH_SHOP_MENU = MENUS.register("menu." + MOD_ID + ".cashshop", () -> IForgeMenuType.create(CashShopMenu::new));
    public static final RegistryObject<Item> COPPER_ITEM = ITEMS.register("copper_item", () -> new CoinItem(new Item.Properties().stacksTo(64).fireResistant().rarity(Rarity.EPIC), 0));
    public static final RegistryObject<Item> SILVER_ITEM = ITEMS.register("silver_item", () -> new CoinItem(new Item.Properties().stacksTo(64).fireResistant().rarity(Rarity.EPIC), 1));
    public static final RegistryObject<Item> GOLD_ITEM = ITEMS.register("gold_item", () -> new CoinItem(new Item.Properties().stacksTo(64).fireResistant().rarity(Rarity.EPIC), 2));
    public static final RegistryObject<CreativeModeTab> CASH_SHOP_TAB = CREATIVE_TABS.register("cashshop_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("tab.creative.cashshop"))
            .icon(GOLD_ITEM.get()::getDefaultInstance)
            .displayItems((displayParams, output) -> {
                output.accept(COPPER_ITEM.get());
                output.accept(SILVER_ITEM.get());
                output.accept(GOLD_ITEM.get());
            })
            .build());

    @SuppressWarnings("removal")
    public CashShop() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MENUS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_TABS.register(modEventBus);
        NetworkHandler.register();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onEntityPickupItemEvent(EntityItemPickupEvent event) {
        ItemEntity entity = event.getItem();
        ItemStack stack = entity.getItem();
        if (event.getEntity() instanceof ServerPlayer player) {
            if (InvUtils.isSameItem(entity, COPPER_ITEM.get())) {
                CurrencyUtils.setCurrency(player, new CurrencyData(0, 0, stack.getCount()), 1);
                if (KubeStartupRegister.isChatLogEnabled())
                    player.sendSystemMessage(Component.translatable("menu.cashshop.pickup").append(stack.getCount() + " ").append(Component.translatable("item.cashshop.copper_item")), false);
                entity.setItem(ItemStack.EMPTY);
                event.setCanceled(true);
            }

            if (InvUtils.isSameItem(entity, SILVER_ITEM.get())) {
                CurrencyUtils.setCurrency(player, new CurrencyData(0, stack.getCount(), 0), 1);
                if (KubeStartupRegister.isChatLogEnabled())
                    player.sendSystemMessage(Component.translatable("menu.cashshop.pickup").append(stack.getCount() + " ").append(Component.translatable("item.cashshop.silver_item")), false);
                entity.setItem(ItemStack.EMPTY);
                event.setCanceled(true);
            }

            if (InvUtils.isSameItem(entity, GOLD_ITEM.get())) {
                CurrencyUtils.setCurrency(player, new CurrencyData(stack.getCount(), 0, 0), 1);
                if (KubeStartupRegister.isChatLogEnabled())
                    player.sendSystemMessage(Component.translatable("menu.cashshop.pickup").append(stack.getCount() + " ").append(Component.translatable("item.cashshop.gold_item")), false);
                entity.setItem(ItemStack.EMPTY);
                event.setCanceled(true);
            }
        }
    }
}
