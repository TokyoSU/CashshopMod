package net.tokyosu.cashshop.utils;

import com.mojang.logging.LogUtils;
import net.minecraft.world.item.ItemStack;
import net.tokyosu.cashshop.Constants;
import net.tokyosu.cashshop.plugin.kubejs.events.KubeStartupRegister;
import net.tokyosu.cashshop.plugin.kubejs.events.TabResource;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.LinkedList;
import java.util.List;

public class ShopUtils {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static @NotNull ItemStack getItem(int tabId, int currentPage, int slotId) {
        List<List<TabResource>> tabPages = KubeStartupRegister.getTabPages(tabId);
        // Check if currentPage is within bounds
        if (currentPage < 0 || currentPage >= tabPages.size()) {
            return ItemStack.EMPTY;
        }
        List<TabResource> resources = tabPages.get(currentPage);
        // Check slotId bounds
        if (slotId < 0 || slotId >= resources.size() || slotId >= Constants.MAX_ITEM_PER_SHOP_PAGE) {
            return ItemStack.EMPTY;
        }
        return resources.get(slotId).stack;
    }

    public static @NotNull List<TabResource> getPageTabResource(int tabId, int currentPage) {
        List<List<TabResource>> tabPages = KubeStartupRegister.getTabPages(tabId);
        if (currentPage < 0 || currentPage >= tabPages.size()) return new LinkedList<>();
        return tabPages.get(currentPage);
    }

    public static @NotNull TabResource getItemResource(int tabId, int currentPage, int slotId) {
        List<List<TabResource>> tabPages = KubeStartupRegister.getTabPages(tabId);
        if (currentPage < 0 || currentPage >= tabPages.size()) return TabResource.EMPTY;
        List<TabResource> page = tabPages.get(currentPage);
        if (slotId < 0 || slotId >= page.size()) return TabResource.EMPTY;
        return page.get(slotId);
    }

    public static void updateCurrentPage(int tabId, int pageId) {
        List<List<TabResource>> tabPages = KubeStartupRegister.getTabPages(tabId);
        if (pageId < 0 || pageId >= tabPages.size()) {
            LOGGER.warn("Invalid pageId {} for tabId {}", pageId, tabId);
            return;
        }

        List<TabResource> resources = tabPages.get(pageId);
        int maxSlots = Constants.MAX_ITEM_PER_SHOP_PAGE;
        int displayedCount = Math.min(resources.size(), maxSlots);

        for (int i = 0; i < displayedCount; i++) {
            Constants.SHOP_PAGE_INV.setItem(i, resources.get(i).stack);
        }

        if (resources.size() > maxSlots) {
            int restSlotCount = resources.size() - maxSlots;
            LOGGER.warn("Failed to add all items for pageId: {}, tabId: {}, missing: {}", pageId, tabId, restSlotCount);
        }
    }
}
