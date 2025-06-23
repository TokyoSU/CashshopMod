package net.tokyosu.cashshop.menu.base;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;

public abstract class MenuBase extends AbstractContainerMenu {
    protected MenuBase(@NotNull MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
    }

    protected MenuBase(@NotNull MenuType<?> menuType, int containerId, @NotNull Inventory playerInventory) {
        super(menuType, containerId);
        init(playerInventory);
    }

    /// Only called by using the second constructor (playerInventory) !
    public abstract void init(@NotNull Inventory playerInventory);



    /// Make the player hotbar slots at a specific position.
    protected void makePlayerHotbarSlotsAt(Inventory inventory, int posX, int posY) {
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(inventory, column,posX + (column * 18), posY));
        }
    }

    /// Make the player inventory slots at a specific position
    protected void makePlayerInventorySlotsAt(Inventory inventory, int posX, int posY) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(inventory,
                                9 + column + (row * 9),
                        posX + (column * 18),
                        posY + (row * 18)
                        )
                );
            }
        }
    }
}
