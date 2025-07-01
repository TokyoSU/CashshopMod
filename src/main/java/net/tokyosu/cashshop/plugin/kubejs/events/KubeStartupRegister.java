package net.tokyosu.cashshop.plugin.kubejs.events;

import com.mojang.logging.LogUtils;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.typings.Generics;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.tokyosu.cashshop.Constants;
import net.tokyosu.cashshop.utils.CurrencyData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.*;

public class KubeStartupRegister extends EventJS {
    @HideFromJS
    private static final Logger LOGGER = LogUtils.getLogger();
    @HideFromJS
    private static final List<TabResource> tabResourceList = new LinkedList<>();
    @HideFromJS
    private static final List<TabNames> tabNamesList = new LinkedList<>();
    @HideFromJS
    private static boolean removeChatLog = false;

    public KubeStartupRegister() {}
    public static KubeStartupRegister create() {
        return new KubeStartupRegister();
    }

    @Info(value = "Register a new shop item.", params = {
            @Param(name = "namespace", value = "The namespace of the mod, example: 'minecraft:grass_block'"),
            @Param(name = "tabId", value = "Tab type using inside the shop"),
            @Param(name = "count", value = "Number of items given when buy, min = 1, max = 64"),
            @Param(name = "gold", value = "The price of this item in gold."),
            @Param(name = "silver", value = "The price of this item in silver."),
            @Param(name = "copper", value = "The price of this item in copper."),
            @Param(name = "discount", value = "The discount price of this item, 0 = no discount, 100 = free (in percentage)")
    })
    @Generics(value = {String.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class})
    public void addItem(String namespace, int tabId, int count, int gold, int silver, int copper, int discount) {
        tabResourceList.add(new TabResource(namespace, tabId, count, new CurrencyData(gold, silver, copper), discount));
    }

    @Info(value = "Register a new shop tab.", params = {
            @Param(name = "tabId", value = "Unique id for this tab."),
            @Param(name = "translateName", value = "Translatable name of a tab, example: 'menu.apocalypse.tab_name'."),
            @Param(name = "iconNamespace", value = "Namespace and name of the item, example: 'minecraft:grass_block'")
    })
    @Generics(value = {Integer.class, String.class, String.class})
    public void createTab(int tabId, String translateName, String iconNamespace) {
        for (TabNames tab : tabNamesList) { // Check if the tab already exist !
            if (tab.id == tabId) {
                LOGGER.error("Failed to add new tab shop of translatable name {}, with id: {}, this tab was already registered !", translateName, tabId);
                return;
            }
        }
        tabNamesList.add(new TabNames(tabId, translateName, !iconNamespace.isEmpty() ? iconNamespace : "minecraft:grass_block")); // Avoid missing icon !
    }

    @Info(value = "Disable the chat log sent by the shop !")
    @Generics(value = {Boolean.class})
    public void disableChatLog() {
        removeChatLog = true;
    }

    @HideFromJS
    public static List<TabNames> getTabNamesList() {
        return tabNamesList;
    }

    public static boolean isChatLogEnabled() {
        return !removeChatLog;
    }

    @HideFromJS
    public static @NotNull Dictionary<Integer, List<TabResource>> getAllTabs(int tabId) {
        Dictionary<Integer, List<TabResource>> list = new Hashtable<>();
        for (TabNames tab : tabNamesList) {
            list.put(tab.id, new LinkedList<>());
        }
        for (TabResource resource : tabResourceList) {
            if (resource.tabId == tabId)
                list.get(tabId).add(resource);
        }
        return list;
    }

    @HideFromJS
    public static @NotNull List<List<TabResource>> getTabPages(int tabId) {
        Dictionary<Integer, List<TabResource>> tabList = getAllTabs(tabId);
        List<TabResource> resources = tabList.get(tabId);
        List<List<TabResource>> pages = new ArrayList<>();
        if (resources == null || resources.isEmpty()) return pages;

        int itemsPerPage = Constants.MAX_ITEM_PER_SHOP_PAGE;
        List<TabResource> currentPage = new ArrayList<>(itemsPerPage);
        pages.add(currentPage);

        for (TabResource resource : resources) {
            if (currentPage.size() >= itemsPerPage) {
                currentPage = new ArrayList<>(itemsPerPage);
                pages.add(currentPage);
            }
            currentPage.add(resource);
        }

        return pages;
    }

    @HideFromJS
    public static int getTabPageCount(int tabId) {
        return getAllTabs(tabId).get(tabId).size();
    }

    @HideFromJS
    public static @NotNull List<TabResource> getTab(int tabId) {
        return getAllTabs(tabId).get(tabId);
    }
}
