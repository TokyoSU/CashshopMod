package net.tokyosu.cashshop.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class CoinItem extends Item {
    private final int coinType;

    public CoinItem(Properties properties, int coinType) {
        super(properties);
        this.coinType = coinType;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        MutableComponent text = switch (this.coinType) {
            case 0 -> Component.translatable("tooltip.cashshop.add_item_copper");
            case 1 -> Component.translatable("tooltip.cashshop.add_item_silver");
            case 2 -> Component.translatable("tooltip.cashshop.add_item_gold");
            default -> Component.translatable("tooltip.cashshop.add_unknown_item");
        };
        tooltip.add(text.withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.cashshop.fire_resistance").withStyle(ChatFormatting.RED));
    }
}
