package net.tokyosu.cashshop.menu.button;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class ButtonDualTexture extends ImageButton {
    private final ResourceLocation normalTexture;
    private final ResourceLocation hoveredTexture;
    private final OnPress onButtonPressed;
    private Rect2i normalTextureRect;
    private Rect2i hoveredTextureRect;

    public ButtonDualTexture(int x, int y, int width, int height, int textureX, int textureY, ResourceLocation texture, Button.OnPress onPress) {
        super(x, y, width, height, textureX, textureY, texture, onPress);
        this.normalTexture = texture;
        this.hoveredTexture = null;
        this.onButtonPressed = null;
    }

    public ButtonDualTexture(int x, int y, int width, int height, int textureX, int textureY, int textureYOffset, ResourceLocation texture, Button.OnPress onPress) {
        super(x, y, width, height, textureX, textureY, textureYOffset, texture, onPress);
        this.normalTexture = texture;
        this.hoveredTexture = null;
        this.onButtonPressed = null;
    }

    public ButtonDualTexture(int x, int y, int width, int height, int textureX, int textureY, int textureYOffset, ResourceLocation texture, int textureWidth, int textureHeight, Button.OnPress onPress) {
        super(x, y, width, height, textureX, textureY, textureYOffset, texture, textureWidth, textureHeight, onPress);
        this.normalTexture = texture;
        this.hoveredTexture = null;
        this.onButtonPressed = null;
    }

    public ButtonDualTexture(int x, int y, int width, int height, int textureX, int textureY, int textureYOffset, ResourceLocation texture, int textureWidth, int textureHeight, Button.OnPress onPress, Component message) {
        super(x, y, width, height, textureX, textureY, textureYOffset, texture, textureWidth, textureHeight, onPress, message);
        this.normalTexture = texture;
        this.hoveredTexture = null;
        this.onButtonPressed = null;
    }

    public ButtonDualTexture(int x, int y, int width, int height, int textureYOffset, ResourceLocation texture, Rect2i normalTextureRect, Rect2i selectedTextureRect, OnPress onPress, Component message) {
        super(x, y, width, height, normalTextureRect.getX(), normalTextureRect.getY(), textureYOffset, texture, normalTextureRect.getWidth(), normalTextureRect.getHeight(), (button) -> {}, message);
        this.normalTexture = texture;
        this.normalTextureRect = normalTextureRect;
        this.hoveredTexture = null;
        this.hoveredTextureRect = selectedTextureRect;
        this.onButtonPressed = onPress;
    }

    public ButtonDualTexture(int x, int y, int width, int height, int textureYOffset, ResourceLocation normalTexture, Rect2i normalTextureRect, ResourceLocation selectedTexture, Rect2i selectedTextureRect, OnPress onPress, Component message) {
        super(x, y, width, height, normalTextureRect.getX(), normalTextureRect.getY(), textureYOffset, normalTexture, normalTextureRect.getWidth(), normalTextureRect.getHeight(), (button) -> {}, message);
        this.normalTexture = normalTexture;
        this.normalTextureRect = normalTextureRect;
        this.hoveredTexture = selectedTexture;
        this.hoveredTextureRect = selectedTextureRect;
        this.onButtonPressed = onPress;
    }

    @Override
    public void onPress() {
        super.onPress();
        if (this.onButtonPressed != null) {
            this.onButtonPressed.onPress(this);
        }
    }

    @Override
    public void renderTexture(@NotNull GuiGraphics pGraphics, @NotNull ResourceLocation texture, int x, int y, int textureX, int textureY, int textureYDiff, int width, int height, int textureWidth, int textureHeight) {
        if (this.active && this.isHovered())
            super.renderTexture(pGraphics, hoveredTexture != null ? hoveredTexture : normalTexture, x, y, hoveredTextureRect.getX(), hoveredTextureRect.getY(), textureYDiff, width, height, hoveredTextureRect.getWidth(), hoveredTextureRect.getHeight());
        else
            super.renderTexture(pGraphics, normalTexture, x, y, normalTextureRect.getX(), normalTextureRect.getY(), textureYDiff, width, height, normalTextureRect.getWidth(), normalTextureRect.getHeight());
    }

    @OnlyIn(Dist.CLIENT)
    public interface OnPress {
        void onPress(ButtonDualTexture button);
    }
}
