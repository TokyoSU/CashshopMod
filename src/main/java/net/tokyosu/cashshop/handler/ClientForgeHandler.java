package net.tokyosu.cashshop.handler;

import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.tokyosu.cashshop.CashShop;
import net.tokyosu.cashshop.Keybindings;
import net.tokyosu.cashshop.plugin.kubejs.events.KubeStartupRegister;
import net.tokyosu.cashshop.server.NetworkHandler;
import net.tokyosu.cashshop.utils.CurrencyData;
import net.tokyosu.cashshop.utils.CurrencyUtils;
import net.tokyosu.cashshop.utils.InvUtils;
import org.slf4j.Logger;

@Mod.EventBusSubscriber(modid = CashShop.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientForgeHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    static void removeCoinInInventory(ServerPlayer player, Inventory inventory) {
        for (int slotId = 0; slotId < inventory.getContainerSize(); slotId++) {
            ItemStack stack = inventory.getItem(slotId);
            if (stack.isEmpty()) continue; // Avoid is item is ItemStack.EMPTY !

            if (InvUtils.isSameItem(stack, CashShop.COPPER_ITEM.get())) {
                CurrencyUtils.setCurrency(player, new CurrencyData(0, 0, stack.getCount()), 1);
                if (KubeStartupRegister.isChatLogEnabled())
                    player.sendSystemMessage(Component.translatable("menu.cashshop.pickup_inv").append(stack.getCount() + " ").append(Component.translatable("item.cashshop.copper_item")), false);
                inventory.setItem(slotId, ItemStack.EMPTY);
            } else if (InvUtils.isSameItem(stack, CashShop.SILVER_ITEM.get())) {
                CurrencyUtils.setCurrency(player, new CurrencyData(0, stack.getCount(), 0), 1);
                if (KubeStartupRegister.isChatLogEnabled())
                    player.sendSystemMessage(Component.translatable("menu.cashshop.pickup_inv").append(stack.getCount() + " ").append(Component.translatable("item.cashshop.silver_item")), false);
                inventory.setItem(slotId, ItemStack.EMPTY);
            } else if (InvUtils.isSameItem(stack, CashShop.GOLD_ITEM.get())) {
                CurrencyUtils.setCurrency(player, new CurrencyData(stack.getCount(), 0, 0), 1);
                if (KubeStartupRegister.isChatLogEnabled())
                    player.sendSystemMessage(Component.translatable("menu.cashshop.pickup_inv").append(stack.getCount() + " ").append(Component.translatable("item.cashshop.gold_item")), false);
                inventory.setItem(slotId, ItemStack.EMPTY);
            }
        }
    }

    @SubscribeEvent
    static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        // Check if the player opened the shop !
        if (Keybindings.INSTANCE.cashShopKey.consumeClick() && event.player != null) {
            NetworkHandler.sendOpenShop();
        }

        // Check for inventory each tick:
        if (event.player instanceof ServerPlayer serverPlayer) {
            removeCoinInInventory(serverPlayer, serverPlayer.getInventory());
        }
    }

    @SubscribeEvent
    static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer playerServer) {
            CurrencyUtils.handleFirstTimeLogin(playerServer);
        }
    }

    static int onSetCurrencyCommand(CommandContext<CommandSourceStack> context) {
        long gold = LongArgumentType.getLong(context, "gold");
        long silver = LongArgumentType.getLong(context, "silver");
        long copper = LongArgumentType.getLong(context, "copper");
        NetworkHandler.sendCurrency(new CurrencyData(gold, silver, copper), 0);
        context.getSource().sendSuccess(() -> Component.translatable("menu.cashshop.command_add_currency.success").withStyle(ChatFormatting.GREEN), false);
        return 1;
    }

    static int onAddCurrencyCommand(CommandContext<CommandSourceStack> context) {
        long gold = LongArgumentType.getLong(context, "gold");
        long silver = LongArgumentType.getLong(context, "silver");
        long copper = LongArgumentType.getLong(context, "copper");
        NetworkHandler.sendCurrency(new CurrencyData(gold, silver, copper), 1);
        context.getSource().sendSuccess(() -> Component.translatable("menu.cashshop.command_add_currency.success").withStyle(ChatFormatting.GREEN), false);
        return 1;
    }

    @SubscribeEvent
    static void onCommandEvent(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("shop_set_currency")
                        .requires(source -> source.hasPermission(1))
                        .then(
                                Commands.argument("gold", LongArgumentType.longArg(0, 99999999))
                                        .then(
                                                Commands.argument("silver", LongArgumentType.longArg(0, 99))
                                                        .then(
                                                                Commands.argument("copper", LongArgumentType.longArg(0, 99))
                                                                        .executes(ClientForgeHandler::onSetCurrencyCommand)
                                                        )
                                        )
                        )
        );
        event.getDispatcher().register(
                Commands.literal("shop_add_currency")
                        .requires(source -> source.hasPermission(1))
                        .then(
                                Commands.argument("gold", LongArgumentType.longArg(0, 99999999))
                                        .then(
                                                Commands.argument("silver", LongArgumentType.longArg(0, 99))
                                                        .then(
                                                                Commands.argument("copper", LongArgumentType.longArg(0, 99))
                                                                        .executes(ClientForgeHandler::onAddCurrencyCommand)
                                                        )
                                        )
                        )
        );
    }
}
