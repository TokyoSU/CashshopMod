package net.tokyosu.cashshop.utils;

import com.eliotlash.mclib.utils.MathUtils;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import javax.annotation.Nullable;

public class InvUtils {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static @NotNull ItemStack createItem(String namespace, String name, int stackCount) {
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(namespace, name);
        if (!doesItemFromRegistryExist(location)) {
            LOGGER.warn("Failed to create item: {}:{}, item does not exist !", namespace, name);
            return ItemStack.EMPTY;
        }

        Item item = getItemFromRegistry(location);
        if (item == null) {
            LOGGER.warn("Failed to create item: {}:{}, InvUtils.getItemFromRegistry() returned null !", namespace, name);
            return ItemStack.EMPTY;
        }

        ItemStack stack = new ItemStack(item);
        stack.setCount(MathUtils.clamp(stackCount, 1, 64));
        return stack;
    }


    public static boolean doesItemFromRegistryExist(ResourceLocation location) {
        return ForgeRegistries.ITEMS.containsKey(location);
    }

    public static @Nullable Item getItemFromRegistry(ResourceLocation location) {
        return ForgeRegistries.ITEMS.getValue(location);
    }
}
