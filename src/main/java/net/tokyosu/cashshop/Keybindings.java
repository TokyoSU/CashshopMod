package net.tokyosu.cashshop;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;

public final class Keybindings {
    public static final Keybindings INSTANCE = new Keybindings();
    public final KeyMapping cashShopKey = new KeyMapping("key." + CashShop.MOD_ID + ".cashshop_key", KeyConflictContext.IN_GAME, InputConstants.getKey(InputConstants.KEY_I, -1), KeyMapping.CATEGORY_MISC);
}
