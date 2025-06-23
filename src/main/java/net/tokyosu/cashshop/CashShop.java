package net.tokyosu.cashshop;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.tokyosu.cashshop.menu.CashShopMenu;
import net.tokyosu.cashshop.server.NetworkHandler;

@Mod(CashShop.MOD_ID)
public class CashShop {
    public static final String MOD_ID = "cashshop";
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MOD_ID);
    public static final RegistryObject<MenuType<CashShopMenu>> CASH_SHOP_MENU = MENU_TYPES.register("menu." + MOD_ID + ".cashshop", () -> IForgeMenuType.create(CashShopMenu::new));

    @SuppressWarnings("removal")
    public CashShop() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MENU_TYPES.register(modEventBus);
        NetworkHandler.register();
        MinecraftForge.EVENT_BUS.register(this);
    }
}
