package net.tokyosu.cashshop.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.tokyosu.cashshop.CashShop;
import net.tokyosu.cashshop.menu.CashShopMenu;
import net.tokyosu.cashshop.menu.button.ShopSlotButton;
import net.tokyosu.cashshop.menu.button.ShopTabButton;
import net.tokyosu.cashshop.plugin.kubejs.events.KubeStartupRegister;
import net.tokyosu.cashshop.plugin.kubejs.events.TabNames;
import net.tokyosu.cashshop.plugin.kubejs.events.TabResource;
import net.tokyosu.cashshop.server.NetworkHandler;
import net.tokyosu.cashshop.utils.CurrencyData;
import net.tokyosu.cashshop.utils.CurrencyUtils;
import net.tokyosu.cashshop.utils.InvBuilder;
import net.tokyosu.cashshop.utils.ShopUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class CashShopScreen extends AbstractContainerScreen<CashShopMenu> {
    private CurrencyData currency = new CurrencyData(0, 0, 0);
    private final InvBuilder baseGUI;
    private InvBuilder confirmGUI;
    private Button pOkButton;
    private Button pCancelButton;
    private ItemStack pConfirmItem;
    private int pConfirmItemSlotId = -1;
    private int pConfirmType = 0;
    private boolean showConfirmScreen = false;

    public CashShopScreen(CashShopMenu menu, Inventory playerInv, Component menuName) {
        super(menu, playerInv, menuName);
        this.baseGUI = new InvBuilder(ResourceLocation.fromNamespaceAndPath(CashShop.MOD_ID, "textures/gui/cashshop/cashshop.png"), 411, 300, 512, 512);
        this.inventoryLabelX = 8000;
        this.inventoryLabelY = 8000;
        this.titleLabelX = 4;
        this.titleLabelY = 4;
    }

    /// Type: 0 = price, 1 = discount
    private void createConfirmUI(ItemStack stack, int slotId, int type) {
        int width = type == 0 ? 176 : 217;
        int buttonX = type == 0 ? 85 : 125;
        this.confirmGUI = new InvBuilder(ResourceLocation.fromNamespaceAndPath(CashShop.MOD_ID, "textures/gui/cashshop/cashshop_confirm.png"), width, 60, 256, 256);
        this.confirmGUI.init(this.width, this.height);
        this.confirmGUI.setFont(this.font);
        this.showConfirmScreen = true;
        this.pConfirmType = type;
        this.pConfirmItem = stack.copy();
        this.pConfirmItemSlotId = slotId;
        this.pOkButton = this.confirmGUI.createButton(buttonX, 39, 42, 18, Component.translatable("menu.cashshop.confirm_ok"), (ok) -> {
            this.showConfirmScreen = false;
            this.menu.onItemBuyButtonClicked(this.menu.currentTab, this.menu.currentPage, slotId);
            this.removeWidget(this.pOkButton); this.pOkButton = null;
            this.removeWidget(this.pCancelButton); this.pCancelButton = null;
            this.confirmGUI = null;
            this.pConfirmItem = null;
            this.pConfirmItemSlotId = -1;
        });
        this.pCancelButton = this.confirmGUI.createButton(buttonX + 45, 39, 40, 18, Component.translatable("menu.cashshop.confirm_cancel"), (cancel) -> {
            this.showConfirmScreen = false;
            this.removeWidget(this.pOkButton); this.pOkButton = null;
            this.removeWidget(this.pCancelButton); this.pCancelButton = null;
            this.confirmGUI = null;
            this.pConfirmItem = null;
            this.pConfirmItemSlotId = -1;
        });
    }

    @Override
    protected void init() {
        super.init(); // This setup the left and top position.
        // Initialize both gui.
        this.baseGUI.setFont(this.font);
        this.baseGUI.init(this.width, this.height);
        this.makeTabs();
        this.makeItemBuyButtons();
        this.makePageSwitchButtons();
        this.makeCloseButton();
    }

    @Override
    protected void containerTick() {
        NetworkHandler.sendShopUpdate();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        if (this.showConfirmScreen) { // Close the confirm screen instead !
            this.removeWidget(pOkButton); pOkButton = null;
            this.removeWidget(pCancelButton); pCancelButton = null;
            this.pConfirmItem = null;
            this.showConfirmScreen = false;
            return false;
        }
        return true;
    }

    @Override
    public void render(@NotNull GuiGraphics pGui, int pMouseX, int pMouseY, float pPartialTick) {
        this.leftPos = this.baseGUI.getPosX();
        this.topPos = this.baseGUI.getPosY();
        super.render(pGui, pMouseX, pMouseY, pPartialTick);

        this.baseGUI.drawString(206, 5, Component.literal(String.valueOf(this.menu.currentPage + 1)), 0xFFFFFFFF);
        this.baseGUI.drawString(169, 275, Component.literal(String.valueOf(this.currency.gold)), 0xFFFFFFFF);
        this.baseGUI.drawString(232, 275, Component.literal(String.valueOf(this.currency.silver)), 0xFFFFFFFF);
        this.baseGUI.drawString(262, 275, Component.literal(String.valueOf(this.currency.copper)), 0xFFFFFFFF);
        for (TabNames tab : KubeStartupRegister.getTabNamesList()) {
            this.baseGUI.drawTooltip(tab.iconStack, 8 + (tab.id * 26), -15, Component.translatable(tab.name), pMouseX, pMouseY);
        }

        // Now show the price for each item (they will update auto) + discount !
        List<TabResource> resources = ShopUtils.getPageTabResource(this.menu.currentTab, this.menu.currentPage);
        for (int i = 0; i < resources.size(); i++) {
            TabResource resource = resources.get(i);
            if (resource.isEmpty()) continue;
            int columnId = i % 3;
            int rowId = i / 3;
            int x = 16 + (columnId * 132);
            int y = 45 + (rowId * 40);
            if (resource.discount > 0 && !resource.isFree()) {
                this.baseGUI.drawPulsatingString(resource.getColorByDiscount(Component.literal("-" + resource.discount + "%")), x + 40, y - 30);
            } else if (resource.isFree()) {
                this.baseGUI.drawPulsatingString(Component.translatable("menu.cashshop.free"), x + 45, y - 30);
            }
            this.baseGUI.drawString(x, y, Component.literal(String.valueOf(resource.price.gold)), 0xFFFFFFFF);
            this.baseGUI.drawString(x + 64, y, Component.literal(String.valueOf(resource.price.silver)), 0xFFFFFFFF);
            this.baseGUI.drawString(x + 94, y, Component.literal(String.valueOf(resource.price.copper)), 0xFFFFFFFF);
        }

        if (this.showConfirmScreen && this.confirmGUI != null) {
            pGui.pose().pushPose();
            pGui.pose().translate(0, 0, 1200.0F); // Make it above everything !

            this.renderBackground(pGui);
            this.confirmGUI.drawBackground(0, 0, 0, this.pConfirmType == 0 ? 61 : 0);
            this.confirmGUI.drawString(28, 6, Component.translatable("menu.cashshop.buy_ask"), 0xFFFFFFFF);

            if (this.pConfirmItemSlotId != -1) {
                TabResource buyItem = resources.get(this.pConfirmItemSlotId);
                int priceX = this.pConfirmType == 0 ? 53 : 94;
                if (buyItem.discount > 0) {
                    this.confirmGUI.drawString(4, 27, Component.translatable("menu.cashshop.after_discount"), 0xFFFFFFFF);
                } else {
                    this.confirmGUI.drawString(4, 27, Component.translatable("menu.cashshop.no_discount"), 0xFFFFFFFF);
                }
                CurrencyData afterDiscount = CurrencyUtils.getDiscountedPriceBreakdown(buyItem.price, buyItem.discount);
                this.confirmGUI.drawString(priceX, 29, Component.literal(String.valueOf(afterDiscount.gold)), 0xFFFFFFFF);
                this.confirmGUI.drawString(priceX + 63, 29, Component.literal(String.valueOf(afterDiscount.silver)), 0xFFFFFFFF);
                this.confirmGUI.drawString(priceX + 93, 29, Component.literal(String.valueOf(afterDiscount.copper)), 0xFFFFFFFF);
            }

            this.confirmGUI.drawIconWithTooltip(this.pConfirmItem, 6, 6, pMouseX, pMouseY);
            this.pOkButton.render(pGui, pMouseX, pMouseY, pPartialTick);
            this.pCancelButton.render(pGui, pMouseX, pMouseY, pPartialTick);

            pGui.pose().popPose();
        } else {
            this.renderTooltip(pGui, pMouseX, pMouseY);
        }
    }

    @Override
    public boolean mouseClicked(double x, double y, int type) {
        if (this.showConfirmScreen && this.pOkButton != null && this.pCancelButton != null) {
            if (this.pOkButton.mouseClicked(x, y, type)) return true;
            if (this.pCancelButton.mouseClicked(x, y, type)) return true;
        }
        return super.mouseClicked(x, y, type);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics pGui, float pPartialTick, int pMouseX, int pMouseY) {
        this.renderBackground(pGui);
        this.baseGUI.setGraphics(pGui);
        this.baseGUI.drawBackground(0, 0, 0, 0);
        if (this.confirmGUI != null) {
            this.confirmGUI.setGraphics(pGui);
        }
    }

    private void onBuyButtonPressed(ShopSlotButton button) {
        TabResource resource = ShopUtils.getItemResource(this.menu.currentTab, this.menu.currentPage, button.getShopSlotId());
        if (resource.stack == null || resource.stack.isEmpty()) {
            if (KubeStartupRegister.isChatLogEnabled())
                this.menu.player.displayClientMessage(Component.translatable("menu.cashshop.message.slot_empty_buy").withStyle(ChatFormatting.RED), false);
        }
        else if (resource.isFree()) {
            this.menu.onItemBuyButtonClicked(this.menu.currentTab, this.menu.currentPage, button.getShopSlotId());
        } else {
            this.createConfirmUI(resource.stack.copy(), button.getShopSlotId(), resource.discount > 0 ? 1 : 0);
        }
    }

    private void makeItemBuyButtons() {
        for (int rowId = 0; rowId < 4; rowId++) {
            for (int columnId = 0; columnId < 3; columnId++) {
                this.addRenderableWidget(this.baseGUI.createBuyButton(103 + (columnId * 132), 23 + (rowId * 40), 32, 16, columnId + (rowId * 3), Component.translatable("menu.cashshop.buy"), this::onBuyButtonPressed));
            }
        }
    }

    private void makeCloseButton() {
        this.addRenderableWidget(this.baseGUI.createDualButtonTexture(398, 3, 10, 10,
                new Rect2i(490, 1, 0, 0),
                new Rect2i(490, 12, 0, 0), (button) -> {
            Minecraft.getInstance().setScreen(null);
        }));
    }

    private void makePageSwitchButtons() {
        this.addRenderableWidget(this.baseGUI.createDualButtonTexture(205 + 20, 4, 9, 10, new Rect2i(479, 1, 0, 0), new Rect2i(479, 15, 0, 0), (right) -> {
            this.menu.onPageButtonClicked(true);
        }));
        this.addRenderableWidget(this.baseGUI.createDualButtonTexture(205 - 20, 4, 9, 10, new Rect2i(466, 1, 0, 0), new Rect2i(466, 15, 0, 0), (left) -> {
            this.menu.onPageButtonClicked(false);
        }));
    }

    private void makeTabs() {
        // Remove all previous widget if any
        for (ShopTabButton button : this.menu.pTabButtonList) {
            this.removeWidget(button);
        }
        this.menu.pTabButtonList.clear();

        // Now register tab again (or first)
        for (TabNames tab : KubeStartupRegister.getTabNamesList()) {
            ShopTabButton tabButton = this.addRenderableWidget(this.baseGUI.createTabButton(3 + (tab.id * 26), -19, 26, 20,
                    new Rect2i(412, 1, 0, 0),
                    new Rect2i(439, 1, 0, 0), (button) -> {
                        this.menu.onTabClicked(tab.id, button);
                    })
            );
            if (tab.id == 0) {
                tabButton.onPress(); // First is already pressed !
            }
            this.menu.pTabButtonList.add(tabButton);
        }
    }

    /// Update currency, send from server each tick.
    public void updateCurrencyFromServer(CurrencyData currency) {
        this.currency = currency;
    }
}
