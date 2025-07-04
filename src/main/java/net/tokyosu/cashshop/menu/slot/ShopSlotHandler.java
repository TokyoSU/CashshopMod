package net.tokyosu.cashshop.menu.slot;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/// You can't take or place item inside the shop, only through a list given by modpack or server !
public class ShopSlotHandler extends Slot {
    private int maxSlot = 64;

    public ShopSlotHandler(Container container, int maxSlots, int index, int xPosition, int yPosition) {
        super(container, index, xPosition, yPosition);
        this.maxSlot = maxSlots;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return false;
    }

    @Override
    public boolean mayPickup(@NotNull Player player) {
        return false;
    }

    @Override
    public int getMaxStackSize() {
        return this.maxSlot;
    }
}
