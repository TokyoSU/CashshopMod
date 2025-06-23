package net.tokyosu.cashshop.plugin.kubejs.events;

import com.mojang.logging.LogUtils;
import net.minecraft.world.item.ItemStack;
import net.tokyosu.cashshop.utils.InvUtils;
import org.slf4j.Logger;

public class TabNames {
    private static final Logger LOGGER = LogUtils.getLogger();
    public int id;
    public String name;
    public String iconNamespace;
    public String iconName;
    public ItemStack iconStack;

    public TabNames(int tabId, String translateName, String iconPath) {
        String[] separatedTexts = iconPath.split(":");
        if (separatedTexts.length != 2) {
            LOGGER.error("Failed to add {} inside tab id: {} for the icon, namespace have less or more than 2 values ('mod:name' required)", iconPath, tabId);
            return;
        }
        this.id = tabId;
        this.iconNamespace = separatedTexts[0];
        this.iconName = separatedTexts[1];
        this.name = translateName;
        this.makeIcon();
    }

    public void makeIcon() {
        if (this.iconNamespace.isEmpty() || this.iconName.isEmpty()) return;
        this.iconStack = InvUtils.createItem(this.iconNamespace, this.iconName, 1);
    }
}
