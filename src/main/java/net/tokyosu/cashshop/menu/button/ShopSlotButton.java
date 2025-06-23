package net.tokyosu.cashshop.menu.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class ShopSlotButton extends AbstractWidget {
    private final OnBuyPressed onBuyPressed;
    private int shopSlotId = 0;

    public ShopSlotButton(int x, int y, int width, int height, Component text, OnBuyPressed onPress, int shopSlotId) {
        super(x, y, width, height, text);
        this.shopSlotId = shopSlotId;
        this.onBuyPressed = onPress;
    }

    // Set the texture by Y position.
    private int getOffsetY() {
        int i;
        if (!this.active)
            i = 0;
        else if (this.isHovered())
            i = 2;
        else
            i = 1;
        return 46 + i * 20;
    }

    public int getShopSlotId() {
        return this.shopSlotId;
    }

    @Override
    public void onClick(double x, double y) {
        if (this.onBuyPressed != null) {
            this.onBuyPressed.onPress(this);
        }
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics pGui, int pMouseX, int pMouseY, float pPartialTick) {
        pGui.setColor(1.0F, 1.0F, 1.0F, this.alpha);
        pGui.blitNineSliced(WIDGETS_LOCATION, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 20, 4, 200, 20, 0, this.getOffsetY());
        pGui.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        this.renderScrollingString(pGui, Minecraft.getInstance().font, 2, getFGColor() | Mth.ceil(this.alpha * 255.0F) << 24);
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narration) {
        this.defaultButtonNarrationText(narration);
    }

    @OnlyIn(Dist.CLIENT)
    public interface OnBuyPressed {
        void onPress(ShopSlotButton button);
    }
}
