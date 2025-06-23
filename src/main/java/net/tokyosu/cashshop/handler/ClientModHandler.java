package net.tokyosu.cashshop.handler;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.tokyosu.cashshop.CashShop;
import net.tokyosu.cashshop.Keybindings;
import net.tokyosu.cashshop.plugin.kubejs.events.KubeEventHandler;
import net.tokyosu.cashshop.plugin.kubejs.events.KubeStartupRegister;
import net.tokyosu.cashshop.screen.CashShopScreen;

@Mod.EventBusSubscriber(modid = CashShop.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModHandler {
    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(Keybindings.INSTANCE.cashShopKey);
    }

    @SubscribeEvent
    static void onCommonSetup(FMLCommonSetupEvent event) {
        if (KubeEventHandler.STARTUP_REGISTER.hasListeners()) {
            KubeEventHandler.STARTUP_REGISTER.post(new KubeStartupRegister());
        }
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(CashShop.CASH_SHOP_MENU.get(), CashShopScreen::new);
        });
    }
}
