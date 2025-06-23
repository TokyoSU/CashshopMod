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
import net.tokyosu.cashshop.menu.button.ShopTabButton;
import net.tokyosu.cashshop.plugin.kubejs.events.KubeStartupRegister;
import net.tokyosu.cashshop.plugin.kubejs.events.TabNames;
import net.tokyosu.cashshop.plugin.kubejs.events.TabResource;
import net.tokyosu.cashshop.server.NetworkHandler;
import net.tokyosu.cashshop.utils.CurrencyData;
import net.tokyosu.cashshop.utils.InvBuilder;
import net.tokyosu.cashshop.utils.ShopUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class CashShopScreen extends AbstractContainerScreen<CashShopMenu> {
    private CurrencyData currency = new CurrencyData(0, 0, 0);
    private final InvBuilder baseGUI;
    private final InvBuilder confirmGUI;
    private Button pOkButton;
    private Button pCancelButton;
    private ItemStack pConfirmItem;
    private boolean showConfirmScreen = false;

    public CashShopScreen(CashShopMenu menu, Inventory playerInv, Component menuName) {
        super(menu, playerInv, menuName);
        this.baseGUI = new InvBuilder(ResourceLocation.fromNamespaceAndPath(CashShop.MOD_ID, "textures/gui/cashshop/cashshop.png"), 544, 300, 1024, 512);
        this.confirmGUI = new InvBuilder(ResourceLocation.fromNamespaceAndPath(CashShop.MOD_ID, "textures/gui/cashshop/cashshop_confirm.png"), 179, 60, 256, 256);
        this.inventoryLabelX = 8000;
        this.inventoryLabelY = 8000;
        this.titleLabelX = 4;
        this.titleLabelY = 4;
    }

    @Override
    protected void init() {
        super.init(); // This setup the left and top position.
        // Initialize both gui.
        this.baseGUI.setFont(this.font);
        this.confirmGUI.setFont(this.font);
        this.baseGUI.init(this.width, this.height);
        this.confirmGUI.init(this.width, this.height);
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
            this.removeWidget(pOkButton);
            this.removeWidget(pCancelButton);
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

        this.baseGUI.drawString(332, 6, Component.literal(String.valueOf(this.menu.currentPage + 1)), 0xFFFFFFFF);
        // Player currency:
        this.baseGUI.drawString(201, 283, Component.literal(String.valueOf(this.currency.gold)), 0xFFFFFFFF);
        this.baseGUI.drawString(265, 283, Component.literal(String.valueOf(this.currency.silver)), 0xFFFFFFFF);
        this.baseGUI.drawString(295, 283, Component.literal(String.valueOf(this.currency.copper)), 0xFFFFFFFF);
        for (TabNames tab : KubeStartupRegister.getTabNamesList()) {
            this.baseGUI.drawTooltip(tab.iconStack, 144 + (tab.id * 26), -15, Component.translatable(tab.name), pMouseX, pMouseY);
        }

        // Now show the price for each item (they will update auto)
        // TODO: add discount !
        List<TabResource> resources = ShopUtils.getPageTabResource(this.menu.currentTab, this.menu.currentPage);
        for (int i = 0; i < resources.size(); i++) {
            TabResource resource = resources.get(i);
            if (resource.isEmpty()) continue;
            int columnId = i % 3;
            int rowId = i / 3;
            int x = 149 + (columnId * 132);
            int y = 45 + (rowId * 40);
            this.baseGUI.drawString(x, y, Component.literal(String.valueOf(resource.price.gold)), 0xFFFFFFFF);
            this.baseGUI.drawString(x + 64, y, Component.literal(String.valueOf(resource.price.silver)), 0xFFFFFFFF);
            this.baseGUI.drawString(x + 94, y, Component.literal(String.valueOf(resource.price.copper)), 0xFFFFFFFF);
        }

        if (this.showConfirmScreen) {
            this.leftPos = this.confirmGUI.getPosX();
            this.topPos = this.confirmGUI.getPosY();
            this.confirmGUI.drawBackground(0, 0);
            this.confirmGUI.drawString(28, 6, Component.translatable("menu.cashshop.buy_ask"), 0xFFFFFFFF);
            this.confirmGUI.drawIconWithTooltip(this.pConfirmItem, 7, 7, pMouseX, pMouseY);
        }

        // Now render the tooltip, also include the buy item of the confirm screen !
        this.leftPos = this.baseGUI.getPosX();
        this.topPos = this.baseGUI.getPosY();
        this.renderTooltip(pGui, pMouseX, pMouseY);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics pGui, float pPartialTick, int pMouseX, int pMouseY) {
        this.renderBackground(pGui);
        this.baseGUI.setGraphics(pGui);
        this.baseGUI.drawBackground(0, 0);
        this.confirmGUI.setGraphics(pGui);
    }

    private void makeItemBuyButtons() {
        for (int rowId = 0; rowId < 4; rowId++) {
            for (int columnId = 0; columnId < 3; columnId++) {
                this.addRenderableWidget(this.baseGUI.createBuyButton(231 + (columnId * 132), 23 + (rowId * 40), 32, 16, columnId + (rowId * 3),
                        Component.translatable("menu.cashshop.buy"), (button) -> {
                            ItemStack stack = ShopUtils.getItem(this.menu.currentTab, this.menu.currentPage, button.getShopSlotId());
                            if (stack.isEmpty()) {
                                if (KubeStartupRegister.isChatLogEnabled())
                                    this.menu.player.displayClientMessage(Component.translatable("menu.cashshop.message.slot_empty_buy").withStyle(ChatFormatting.RED), false);
                            }
                            else {
                                this.showConfirmScreen = true;
                                this.pConfirmItem = stack.copy();
                                pOkButton = this.addRenderableWidget(this.confirmGUI.createButton(37, 20, 42, 18, Component.translatable("menu.cashshop.confirm_ok"), (ok) -> {
                                    this.menu.onItemBuyButtonClicked(this.menu.currentTab, this.menu.currentPage, button);
                                    this.removeWidget(pOkButton);
                                    this.removeWidget(pCancelButton);
                                    this.pConfirmItem = null;
                                    this.showConfirmScreen = false;
                                }));
                                pCancelButton = this.addRenderableWidget(this.confirmGUI.createButton(80, 20, 40, 18, Component.translatable("menu.cashshop.confirm_cancel"), (cancel) -> {
                                    this.removeWidget(pOkButton);
                                    this.removeWidget(pCancelButton);
                                    this.pConfirmItem = null;
                                    this.showConfirmScreen = false;
                                }));
                            }
                    })
                );
            }
        }
    }

    private void makeCloseButton() {
        this.addRenderableWidget(this.baseGUI.createDualButtonTexture(531, 3, 10, 10,
                new Rect2i(623, 1, 0, 0),
                new Rect2i(623, 12, 0, 0), (button) -> {
            Minecraft.getInstance().setScreen(null);
        }));
    }

    private void makePageSwitchButtons() {
        this.addRenderableWidget(this.baseGUI.createDualButtonTexture(315, 4, 9, 10, new Rect2i(599, 1, 0, 0), new Rect2i(599, 15, 0, 0), (right) -> {
            this.menu.onPageButtonClicked(right, false);
        }));
        this.addRenderableWidget(this.baseGUI.createDualButtonTexture(345, 4, 9, 10, new Rect2i(612, 1, 0, 0), new Rect2i(612, 15, 0, 0), (left) -> {
            this.menu.onPageButtonClicked(left, true);
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
            ShopTabButton tabButton = this.addRenderableWidget(this.baseGUI.createTabButton(139 + (tab.id * 26), -19, 26, 20,
                    new Rect2i(545, 1, 0, 0),
                    new Rect2i(572, 1, 0, 0), (button) -> {
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
    public void updateFromServer(CurrencyData currency) {
        this.currency = currency;
    }
}
