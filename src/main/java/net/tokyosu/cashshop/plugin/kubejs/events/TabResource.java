package net.tokyosu.cashshop.plugin.kubejs.events;

import com.mojang.logging.LogUtils;
import net.minecraft.world.item.ItemStack;
import net.tokyosu.cashshop.utils.CurrencyData;
import net.tokyosu.cashshop.utils.InvUtils;
import org.slf4j.Logger;

public class TabResource {
    private static final Logger LOGGER = LogUtils.getLogger();
    public int tabId;
    public String namespace;
    public String name;
    public int count;
    public CurrencyData price;
    public int discount;
    public ItemStack stack; // stack of the item being sold.

    public TabResource(String namespace, int tabId, int count, CurrencyData price, int discount) {
        String[] separatedTexts = namespace.split(":");
        if (separatedTexts.length != 2) {
            LOGGER.error("Failed to add {} inside tab id: {}, namespace have less or more than 2 values ('mod:name' required)", namespace, tabId);
            return;
        }
        this.namespace = separatedTexts[0];
        this.name = separatedTexts[1];
        this.tabId = tabId;
        this.count = count;
        this.price = price;
        this.discount = discount;
        this.makeItemStack();
    }

    public boolean isEmpty() {
        return this.namespace.equals("null") || (this.stack == null || this.stack.isEmpty() || this.stack == ItemStack.EMPTY);
    }

    public void makeItemStack() {
        if (this.stack == null) {
            this.stack = InvUtils.createItem(this.namespace, this.name, this.count);
        }
    }

    public static final TabResource EMPTY = new TabResource("null", 0, 0, new CurrencyData(0, 0, 0), 0);
}
