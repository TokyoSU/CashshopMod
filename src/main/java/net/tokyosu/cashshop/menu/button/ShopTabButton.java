package net.tokyosu.cashshop.menu.button;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class ShopTabButton extends ImageButton {
    private final ResourceLocation normalTexture;
    private final ResourceLocation selectedTexture;
    private final OnTabPress tabPressed;
    private Rect2i normalTextureRect;
    private Rect2i selectedTextureRect;
    private boolean isSelected = false;

    public ShopTabButton(int x, int y, int width, int height, int textureX, int textureY, ResourceLocation texture, OnPress onPress) {
        super(x, y, width, height, textureX, textureY, texture, onPress);
        this.normalTexture = texture;
        this.selectedTexture = null;
        this.tabPressed = null;
    }

    public ShopTabButton(int x, int y, int width, int height, int textureX, int textureY, int textureYOffset, ResourceLocation texture, OnPress onPress) {
        super(x, y, width, height, textureX, textureY, textureYOffset, texture, onPress);
        this.normalTexture = texture;
        this.selectedTexture = null;
        this.tabPressed = null;
    }

    public ShopTabButton(int x, int y, int width, int height, int textureX, int textureY, int textureYOffset, ResourceLocation texture, int textureWidth, int textureHeight, OnPress onPress) {
        super(x, y, width, height, textureX, textureY, textureYOffset, texture, textureWidth, textureHeight, onPress);
        this.normalTexture = texture;
        this.selectedTexture = null;
        this.tabPressed = null;
    }

    public ShopTabButton(int x, int y, int width, int height, int textureX, int textureY, int textureYOffset, ResourceLocation texture, int textureWidth, int textureHeight, OnPress onPress, Component message) {
        super(x, y, width, height, textureX, textureY, textureYOffset, texture, textureWidth, textureHeight, onPress, message);
        this.normalTexture = texture;
        this.selectedTexture = null;
        this.tabPressed = null;
    }

    public ShopTabButton(int x, int y, int width, int height, int textureYOffset, ResourceLocation texture, Rect2i normalTextureRect, Rect2i selectedTextureRect, OnTabPress onTabPress, Component message) {
        super(x, y, width, height, normalTextureRect.getX(), normalTextureRect.getY(), textureYOffset, texture, normalTextureRect.getWidth(), normalTextureRect.getHeight(), (button) -> {}, message);
        this.normalTexture = texture;
        this.normalTextureRect = normalTextureRect;
        this.selectedTexture = null;
        this.selectedTextureRect = selectedTextureRect;
        this.tabPressed = onTabPress;
    }

    public ShopTabButton(int x, int y, int width, int height, int textureYOffset, ResourceLocation normalTexture, Rect2i normalTextureRect, ResourceLocation selectedTexture, Rect2i selectedTextureRect, OnTabPress onTabPress, Component message) {
        super(x, y, width, height, normalTextureRect.getX(), normalTextureRect.getY(), textureYOffset, normalTexture, normalTextureRect.getWidth(), normalTextureRect.getHeight(), (button) -> {}, message);
        this.normalTexture = normalTexture;
        this.normalTextureRect = normalTextureRect;
        this.selectedTexture = selectedTexture;
        this.selectedTextureRect = selectedTextureRect;
        this.tabPressed = onTabPress;
    }

    @Override
    public void onPress() {
        super.onPress();
        if (this.tabPressed != null && !this.isSelected) {
            this.tabPressed.onPress(this);
            this.isSelected = true;
        }
    }

    /// Reset selection of the tab, now able to call onPress() again !
    public void reset() {
        this.isSelected = false;
    }

    @Override
    public void renderTexture(@NotNull GuiGraphics pGraphics, @NotNull ResourceLocation texture, int x, int y, int textureX, int textureY, int textureYDiff, int width, int height, int textureWidth, int textureHeight) {
        if (this.active && this.isSelected)
            super.renderTexture(pGraphics, selectedTexture != null ? selectedTexture : normalTexture, x, y, selectedTextureRect.getX(), selectedTextureRect.getY(), textureYDiff, width, height, selectedTextureRect.getWidth(), selectedTextureRect.getHeight());
        else
            super.renderTexture(pGraphics, normalTexture, x, y, normalTextureRect.getX(), normalTextureRect.getY(), textureYDiff, width, height, normalTextureRect.getWidth(), normalTextureRect.getHeight());
    }

    @OnlyIn(Dist.CLIENT)
    public interface OnTabPress {
        void onPress(ShopTabButton button);
    }
}
