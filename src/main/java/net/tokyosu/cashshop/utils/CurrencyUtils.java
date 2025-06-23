package net.tokyosu.cashshop.utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

public class CurrencyUtils {
    private static final String SHOP_CURRENCY_TAG_GOLD = "apocalypse:shop_currency_gold";
    private static final String SHOP_CURRENCY_TAG_SILVER = "apocalypse:shop_currency_silver";
    private static final String SHOP_CURRENCY_TAG_COPPER = "apocalypse:shop_currency_copper";
    private static final String SHOP_CURRENCY_FIRST_JOIN_TAG = "apocalypse:shop_currency_first_time";

    public static CurrencyData getDiscountedPriceBreakdown(CurrencyData price, int discountPercent) {
        long percent = Math.max(0, Math.min(discountPercent, 100L)); // Clamp discountPercent between 0 and 100
        long discounted = price.getTotal() * (100L - percent) / 100L;
        long gold = discounted / 10000L;
        long remainder = discounted % 10000L;
        long silver = remainder / 100L;
        long copper = remainder % 100L;
        return new CurrencyData(gold, silver, copper);
    }

    public static long getDifference(CurrencyData before, CurrencyData after) {
        return before.getTotal() - after.getTotal();
    }

    public static void setCurrency(ServerPlayer player, CurrencyData newCurrency, int type) {
        CompoundTag persistentData = player.getPersistentData();
        CompoundTag data;

        if (!persistentData.contains(ServerPlayer.PERSISTED_NBT_TAG)) {
            data = new CompoundTag();
            persistentData.put(ServerPlayer.PERSISTED_NBT_TAG, data);
        } else {
            data = persistentData.getCompound(ServerPlayer.PERSISTED_NBT_TAG);
        }

        // Get player currency
        long gold_p = data.getLong(SHOP_CURRENCY_TAG_GOLD);
        long silver_p = data.getLong(SHOP_CURRENCY_TAG_SILVER);
        long copper_p = data.getLong(SHOP_CURRENCY_TAG_COPPER);
        CurrencyData current = new CurrencyData(gold_p, silver_p, copper_p);

        switch (type) {
            case 0: // set
                current.set(newCurrency);
                break;
            case 1: // add
                current.add(newCurrency);
                break;
        }
        data.putLong(SHOP_CURRENCY_TAG_GOLD, current.gold);
        data.putLong(SHOP_CURRENCY_TAG_SILVER, current.silver);
        data.putLong(SHOP_CURRENCY_TAG_COPPER, current.copper);
    }

    public static CurrencyData getCurrency(ServerPlayer player) {
        CompoundTag persistentData = player.getPersistentData();
        if (persistentData.contains(ServerPlayer.PERSISTED_NBT_TAG)) {
            CompoundTag data = persistentData.getCompound(ServerPlayer.PERSISTED_NBT_TAG);
            int gold_p = data.getInt(SHOP_CURRENCY_TAG_GOLD);
            int silver_p = data.getInt(SHOP_CURRENCY_TAG_SILVER);
            int copper_p = data.getInt(SHOP_CURRENCY_TAG_COPPER);
            return new CurrencyData(gold_p, silver_p, copper_p);
        }
        return new CurrencyData(0, 0, 0);
    }

    public static void handleFirstTimeLogin(ServerPlayer player) {
        CompoundTag persistentData = player.getPersistentData();
        CompoundTag data;

        if (!persistentData.contains(ServerPlayer.PERSISTED_NBT_TAG)) {
            data = new CompoundTag();
            persistentData.put(ServerPlayer.PERSISTED_NBT_TAG, data);
        } else {
            data = persistentData.getCompound(ServerPlayer.PERSISTED_NBT_TAG);
        }

        if (!data.getBoolean(SHOP_CURRENCY_FIRST_JOIN_TAG)) {
            CurrencyUtils.setCurrency(player, new CurrencyData(0, 0, 0), 0);
            data.putBoolean(SHOP_CURRENCY_FIRST_JOIN_TAG, true);
        }
    }
}
