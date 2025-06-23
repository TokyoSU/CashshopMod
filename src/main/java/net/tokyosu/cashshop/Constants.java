package net.tokyosu.cashshop;

import net.minecraft.world.SimpleContainer;

public class Constants {
    public static final int MAX_ITEM_PER_SHOP_PAGE = 12;
    public static final int MAX_ITEM_BEST = 6;
    public static final SimpleContainer SHOP_PAGE_INV = new SimpleContainer(Constants.MAX_ITEM_PER_SHOP_PAGE);
    public static final SimpleContainer SHOP_BEST_BUY_INV = new SimpleContainer(Constants.MAX_ITEM_BEST);
}
