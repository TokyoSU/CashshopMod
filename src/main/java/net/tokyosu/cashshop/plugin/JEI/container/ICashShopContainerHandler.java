package net.tokyosu.cashshop.plugin.JEI.container;

import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import net.minecraft.client.renderer.Rect2i;
import net.tokyosu.cashshop.screen.CashShopScreen;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ICashShopContainerHandler implements IGuiContainerHandler<CashShopScreen> {
    @Override
    public @NotNull List<Rect2i> getGuiExtraAreas(@NotNull CashShopScreen containerScreen) {
        List<Rect2i> extraAreas = new ArrayList<>();
        extraAreas.add(new Rect2i(containerScreen.getGuiLeft(), containerScreen.getGuiTop(), 544, 193)); // Shop part
        extraAreas.add(new Rect2i(containerScreen.getGuiLeft() + 150, containerScreen.getGuiTop() + 192, 180, 109)); // Inventory part
        return extraAreas;
    }
}
