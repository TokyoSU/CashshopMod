package net.tokyosu.cashshop.menu;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.tokyosu.cashshop.CashShop;
import net.tokyosu.cashshop.Constants;
import net.tokyosu.cashshop.menu.button.ShopTabButton;
import net.tokyosu.cashshop.menu.slot.ShopSlotHandler;
import net.tokyosu.cashshop.plugin.kubejs.events.KubeStartupRegister;
import net.tokyosu.cashshop.plugin.kubejs.events.TabResource;
import net.tokyosu.cashshop.server.NetworkHandler;
import net.tokyosu.cashshop.utils.ShopUtils;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;

import java.util.LinkedList;
import java.util.List;

public class CashShopMenu extends AbstractContainerMenu {
    public final List<ShopTabButton> pTabButtonList = new LinkedList<>();
    public int currentTab = 0;
    public Player player;
    public Inventory inventory;
    public int currentPage = 0;
    private int rowId = 0;
    private int columnId = 0;

    public CashShopMenu(int containerId, Inventory playerInventory, Player player) {
        super(CashShop.CASH_SHOP_MENU.get(), containerId);
        init(playerInventory);
        this.player = player;
        this.inventory = playerInventory;
    }

    public CashShopMenu(int containerId, Inventory playerInventory, FriendlyByteBuf ignoredFriendlyByteBuf) {
        super(CashShop.CASH_SHOP_MENU.get(), containerId);
        init(playerInventory);
        this.player = playerInventory.player;
        this.inventory = playerInventory;
    }

    public void init(@NotNull Inventory playerInv) {
        // Make the shop slots !
        makePlayerHotbar(playerInv);
        makePlayerInventory(playerInv);
        makeShopInventorySlots(playerInv);

        // Initialize the first page of the shop !
        this.currentPage = 0;
        ShopUtils.updateCurrentPage(0, this.currentPage);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int slotId) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    /// Position: 149x63
    private void makeShopInventorySlots(@NotNull Inventory playerInv) {
        int posX = 14;
        int posY = 24;
        int posXOffset = 132;
        int posYOffset = 40;
        for (rowId = 0; rowId < 4; rowId++) {
            for (columnId = 0; columnId < 3; columnId++) {
                addSlot(new ShopSlotHandler(Constants.SHOP_PAGE_INV, 64, columnId + (rowId * 3), posX + (columnId * posXOffset), posY + (rowId * posYOffset)));
            }
        }
    }

    /// Position: 511x129
    protected void makePlayerInventory(@NotNull Inventory playerInv) {
        int posX = 129;
        int posY = 190;
        int posXOffset = 18;
        int posYOffset = 18;
        for (rowId = 0; rowId < 3; rowId++) {
            for (columnId = 0; columnId < 9; columnId++) {
                addSlot(new Slot(playerInv, 9 + columnId + (rowId * 9), posX + (columnId * posXOffset), posY + (rowId * posYOffset)));
            }
        }
    }

    /// Position: 511x187
    protected void makePlayerHotbar(@NotNull Inventory playerInv) {
        int posX = 129;
        int posY = 248;
        int posXOffset = 18;
        for (columnId = 0; columnId < 9; columnId++) {
            addSlot(new Slot(playerInv, columnId,posX + (columnId * posXOffset), posY));
        }
    }

    private void resetTabExcept(ShopTabButton button) {
        this.pTabButtonList.forEach((tab) -> {
            if (tab != button)
                tab.reset();
        });
    }

    /// Need to go left page or right page when button clicked !
    /// False = right and True = left
    public void onPageButtonClicked(boolean leftOrRight) {
        boolean isRightValid = ShopUtils.isNextPageTabValid(this.currentTab, this.currentPage + 1);
        boolean isLeftValid = ShopUtils.isNextPageTabValid(this.currentTab, this.currentPage - 1);
        if (leftOrRight && isRightValid)
        {
            this.currentPage++;
            this.currentPage = Math.clamp(0, KubeStartupRegister.getTabPageCount(this.currentTab) - 1, this.currentPage);
            ShopUtils.updateCurrentPage(this.currentTab, this.currentPage); // -1 because we start at 1 for the render
        }
        else if (!leftOrRight && isLeftValid)
        {
            this.currentPage--;
            this.currentPage = Math.clamp(0, KubeStartupRegister.getTabPageCount(this.currentTab) - 1, this.currentPage);
            ShopUtils.updateCurrentPage(this.currentTab, this.currentPage); // -1 because we start at 1 for the render
        }
    }

    public void onItemBuyButtonClicked(int tabId, int pageId, int slotId) {
        TabResource resource = ShopUtils.getItemResource(tabId, pageId, slotId);
        if (!resource.stack.isEmpty()) {
            NetworkHandler.sendBuy(resource.stack, slotId, pageId, tabId, resource.price, resource.discount);
            return;
        }
        if (KubeStartupRegister.isChatLogEnabled()) {
            this.player.displayClientMessage(Component.translatable("menu.cashshop.message.stack_empty").withStyle(ChatFormatting.RED), false);
        }
    }

    public void onTabClicked(int tabId, ShopTabButton button) {
        if (this.currentTab == tabId) return; // Avoid the same tab to update !
        Constants.SHOP_PAGE_INV.removeAllItems();
        this.currentTab = tabId;
        this.currentPage = 0;
        ShopUtils.updateCurrentPage(this.currentTab, this.currentPage);
        resetTabExcept(button);
    }
}
