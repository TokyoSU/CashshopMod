package net.tokyosu.cashshop.menu;

import com.eliotlash.mclib.utils.MathUtils;
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
import net.tokyosu.cashshop.menu.button.ButtonDualTexture;
import net.tokyosu.cashshop.menu.button.ShopSlotButton;
import net.tokyosu.cashshop.menu.button.ShopTabButton;
import net.tokyosu.cashshop.menu.slot.ShopSlotHandler;
import net.tokyosu.cashshop.plugin.kubejs.events.KubeStartupRegister;
import net.tokyosu.cashshop.plugin.kubejs.events.TabResource;
import net.tokyosu.cashshop.server.NetworkHandler;
import net.tokyosu.cashshop.utils.InvUtils;
import net.tokyosu.cashshop.utils.ShopUtils;
import org.jetbrains.annotations.NotNull;

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
        makeBestInventorySlots(playerInv);
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

    /// Position: 13x27
    private void makeBestInventorySlots(@NotNull Inventory playerInv) {
        int posX = 13;
        int posY = 24;
        int posYOffset = 28;
        for (rowId = 0; rowId < 6; rowId++) {
            addSlot(new ShopSlotHandler(Constants.SHOP_BEST_BUY_INV, rowId, posX, posY + (rowId * posYOffset)));
        }
    }

    /// Position: 149x63
    private void makeShopInventorySlots(@NotNull Inventory playerInv) {
        int posX = 147;
        int posY = 24;
        int posXOffset = 132;
        int posYOffset = 40;
        for (rowId = 0; rowId < 4; rowId++) {
            for (columnId = 0; columnId < 3; columnId++) {
                addSlot(new ShopSlotHandler(Constants.SHOP_PAGE_INV, columnId + (rowId * 3), posX + (columnId * posXOffset), posY + (rowId * posYOffset)));
            }
        }
    }

    /// Position: 511x129
    protected void makePlayerInventory(@NotNull Inventory playerInv) {
        int posX = 161;
        int posY = 198;
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
        int posX = 161;
        int posY = 256;
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
    public void onPageButtonClicked(ButtonDualTexture button, boolean leftOrRight) {
        Constants.SHOP_PAGE_INV.removeAllItems();
        this.currentPage = 0;
        if (leftOrRight)
            this.currentPage++;
        else
            this.currentPage--;
        this.currentPage = MathUtils.clamp(this.currentPage, 0, KubeStartupRegister.getTabPageCount(this.currentTab) - 1);
        ShopUtils.updateCurrentPage(this.currentTab, this.currentPage); // -1 because we start at 1 for the render
    }

    public void onItemBuyButtonClicked(int tabId, int currentPage, ShopSlotButton button) {
        TabResource resource = ShopUtils.getItemResource(tabId, currentPage, button.getShopSlotId());
        ItemStack stack = InvUtils.createItem(resource.namespace, resource.name, resource.count);

        if (!stack.isEmpty()) {
            NetworkHandler.sendBuy(stack, resource.price, resource.discount);
            return;
        }

        if (KubeStartupRegister.isChatLogEnabled()) {
            this.player.displayClientMessage(Component.translatable("menu.cashshop.message.stack_empty").withStyle(ChatFormatting.RED), false);
        }
    }

    public void onTabClicked(int tabId, ShopTabButton button) {
        if (this.currentTab == tabId) return; // Avoid the same tab to update !
        this.currentTab = tabId;
        this.currentPage = 0;
        Constants.SHOP_PAGE_INV.removeAllItems();
        ShopUtils.updateCurrentPage(tabId, this.currentPage);
        resetTabExcept(button);
    }


}
