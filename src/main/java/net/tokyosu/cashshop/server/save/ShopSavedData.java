package net.tokyosu.cashshop.server.save;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ShopSavedData extends SavedData {
    private final List<ShopMostBuyData> buyList = new LinkedList<>();
    private static final String BUY_DATA = "shop_buy_data";
    private static final String SLOT_ID = "shop_slot_id";
    private static final String TAB_ID = "shop_tab_id";
    private static final String PAGE_ID = "shop_page_id";
    private static final String BUY_COUNT = "shop_buy_count";

    public ShopSavedData() {}

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag) {
        // Save buyCounts map as a list of compounds
        ListTag list = new ListTag();
        for (ShopMostBuyData entry : buyList) {
            CompoundTag entryTag = new CompoundTag();
            entryTag.putInt(SLOT_ID, entry.slotId);
            entryTag.putInt(TAB_ID, entry.tabId);
            entryTag.putInt(PAGE_ID, entry.pageId);
            entryTag.putInt(BUY_COUNT, entry.buyCount);
            list.add(entryTag);
        }
        tag.put(BUY_DATA, list);
        return tag;
    }

    public static ShopSavedData load(CompoundTag tag) {
        ShopSavedData data = new ShopSavedData();
        if (tag.contains(BUY_DATA)) {
            ListTag list = tag.getList(BUY_DATA, 10); // 10 = CompoundTag type
            for (int i = 0; i < list.size(); i++) {
                CompoundTag entryTag = list.getCompound(i);
                int slotId = entryTag.getInt(SLOT_ID);
                int tabId = entryTag.getInt(TAB_ID);
                int pageId = entryTag.getInt(PAGE_ID);
                int count = entryTag.getInt(BUY_COUNT);
                data.buyList.add(new ShopMostBuyData(slotId, tabId, pageId, count));
            }
        }
        return data;
    }

    // Get buy count for an item, default 0
    public int getBuyCount(int tabId, int slotId, int pageId) {
        for (ShopMostBuyData data : buyList) {
            if (data.tabId == tabId && data.slotId == slotId && data.pageId == pageId) {
                return data.buyCount;
            }
        }
        return 0;
    }

    // Set buy count for an item, mark dirty to save
    public void setBuyCount(int tabId, int slotId, int pageId, int buyCount) {
        for (ShopMostBuyData data : buyList) {
            if (data.tabId == tabId && data.slotId == slotId && data.pageId == pageId) {
                data.buyCount = buyCount;
                setDirty();
                return;
            }
        }

        // Not found, create it then !
        buyList.add(new ShopMostBuyData(slotId, tabId, pageId, buyCount));
        setDirty();
    }

    // Increment buy count by amount
    public void incrementBuyCount(int tabId, int slotId, int pageId, int amount) {
        for (ShopMostBuyData data : buyList) {
            if (data.tabId == tabId && data.slotId == slotId && data.pageId == pageId) {
                data.buyCount += amount;
                setDirty();
                return;
            }
        }

        // Not found, not created then !
        buyList.add(new ShopMostBuyData(slotId, tabId, pageId, amount));
        setDirty();
    }

    public List<ShopMostBuyData> getMostBuySlotList() {
        return buyList.stream()
                .sorted((a, b) -> Integer.compare(b.buyCount, a.buyCount)) // Descending
                .limit(6)
                .collect(Collectors.toList());
    }
}
