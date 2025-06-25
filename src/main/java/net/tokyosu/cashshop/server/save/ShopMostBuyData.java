package net.tokyosu.cashshop.server.save;

public class ShopMostBuyData {
    public int slotId; // The slot of the item
    public int tabId; // This tab
    public int pageId; // The page the item is at !
    public int buyCount; // The buy count on the current server.
    public ShopMostBuyData(int slotId, int tabId, int pageId, int buyCount) {
        this.slotId = slotId;
        this.tabId = tabId;
        this.pageId = pageId;
        this.buyCount = buyCount;
    }
}
