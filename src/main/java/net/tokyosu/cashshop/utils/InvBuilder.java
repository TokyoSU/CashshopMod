package net.tokyosu.cashshop.utils;

import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.tokyosu.cashshop.menu.button.ButtonDualTexture;
import net.tokyosu.cashshop.menu.button.ShopSlotButton;
import net.tokyosu.cashshop.menu.button.ShopTabButton;

public class InvBuilder {
    private final ResourceLocation background;
    private final int textureWidth;
    private final int textureHeight;
    private final int width;
    private final int height;
    private GuiGraphics pGui;
    private Font pFont;
    private int posX;
    private int posY;

    public InvBuilder(ResourceLocation pBackground, int width, int height, int pTextureWidth, int pTextureHeight) {
        this.background = pBackground;
        this.textureWidth = pTextureWidth;
        this.textureHeight = pTextureHeight;
        this.width = width;
        this.height = height;
    }

    /// Use this.width and this.height, minecraft size not your texture !
    public void init(int width, int height) {
        this.posX = (width - this.width) / 2;
        this.posY = (height - this.height) / 2;
    }

    public int getPosX() {
        return this.posX;
    }

    public int getPosY() {
        return this.posY;
    }

    public void setGraphics(GuiGraphics pGuiGraphics) {
        this.pGui = pGuiGraphics;
    }

    public void setFont(Font pFont) {
        this.pFont = pFont;
    }

    public void drawBackground(int x, int y, int texX, int texY) {
        if (this.background != null) {
            pGui.blit(this.background, this.posX + x, this.posY + y, texX, texY, this.width, this.height, textureWidth, textureHeight);
        }
    }

    public void drawString(int x, int y, Component text, int colorRGB) {
        if (this.pFont != null) {
            pGui.drawString(this.pFont, text, this.posX + x, this.posY + y, colorRGB);
        }
    }

    public void drawPulsatingString(Component message, int x, int y) {
        float time = (float)(System.currentTimeMillis() % 1000L) / 1000.0F;
        float pulse = 1.0F + 0.05F * Mth.sin(time * (float)Math.PI * 2.0F);
        int alpha = (int)(255 * (0.7F + 0.3F * Mth.sin(time * (float)Math.PI * 2.0F)));

        // Measure string dimensions
        int textWidth = this.pFont.width(message.getString());
        int textHeight = this.pFont.lineHeight;

        this.pGui.pose().pushPose();
        this.pGui.pose().translate(this.posX + x, this.posY + y, 305.0F); // Make it above items !
        this.pGui.pose().scale(pulse, pulse, 1.0F);
        this.pGui.pose().rotateAround(
                Axis.ZP.rotationDegrees(-45.0F),
                0.0F,
                0.0F,
                0.0F
        );
        this.pGui.pose().translate(-textWidth / 2f, -textHeight / 2f, 0); // Align text correctly

        // Draw text with alpha
        this.pGui.drawCenteredString(
                this.pFont,
                message,
                -(textWidth / 2),
                -(textHeight / 2),
                0xFFFFFF | (alpha << 24)
        );

        this.pGui.pose().popPose();
    }

    public void drawIcon(ItemStack stack, int x, int y) {
        if (stack != null && !stack.isEmpty()) {
            pGui.renderFakeItem(stack, this.posX + x, this.posY + y);
        }
    }

    public void drawTooltip(ItemStack stack, int x, int y, Component text, int pMouseX, int pMouseY) {
        this.drawIcon(stack, x, y);
        if (stack != null && this.isHovering(x, y, pMouseX, pMouseY)) {
            pGui.renderTooltip(this.pFont, text, pMouseX, pMouseY);
        }
    }

    public void drawIconWithTooltip(ItemStack stack, int x, int y, int pMouseX, int pMouseY) {
        this.drawIcon(stack, x, y);
        if (stack != null && this.isHovering(x, y, pMouseX, pMouseY)) {
            pGui.renderTooltip(this.pFont, Screen.getTooltipFromItem(Minecraft.getInstance(), stack), stack.getTooltipImage(), pMouseX, pMouseY);
        }
    }

    public Button createButton(int x, int y, int width, int height, Component name, Button.OnPress onPress) {
        return Button.builder(name, onPress).bounds(this.posX + x, this.posY + y, width, height).build();
    }

    public ShopSlotButton createBuyButton(int x, int y, int width, int height, int soldSlotId, Component name, ShopSlotButton.OnBuyPressed onBuyPressed) {
        return new ShopSlotButton(this.posX + x, this.posY + y, width, height, name, onBuyPressed, soldSlotId);
    }

    public ShopTabButton createTabButton(int x, int y, int width, int height, Rect2i normalRect, Rect2i hoveredRect, ShopTabButton.OnTabPress onTabPress) {
        normalRect.setWidth(this.textureWidth);
        normalRect.setHeight(this.textureHeight);
        hoveredRect.setWidth(this.textureWidth);
        hoveredRect.setHeight(this.textureHeight);
        return new ShopTabButton(this.posX + x, this.posY + y, width, height, 0, this.background, normalRect, this.background, hoveredRect, onTabPress, Component.empty());
    }

    public ButtonDualTexture createDualButtonTexture(int x, int y, int width, int height, Rect2i normalRect, Rect2i hoveredRect, ButtonDualTexture.OnPress onPress) {
        normalRect.setWidth(this.textureWidth);
        normalRect.setHeight(this.textureHeight);
        hoveredRect.setWidth(this.textureWidth);
        hoveredRect.setHeight(this.textureHeight);
        return new ButtonDualTexture(this.posX + x, this.posY + y, width, height, 0, this.background, normalRect, this.background, hoveredRect, onPress, Component.empty());
    }

    private boolean isHovering(int x, int y, double pMouseX, double pMouseY) {
        pMouseX -= this.posX;
        pMouseY -= this.posY;
        return pMouseX >= (double)(x - 1) && pMouseX < (double)(x + 16 + 1) && pMouseY >= (double)(y - 1) && pMouseY < (double)(y + 16 + 1);
    }
}
