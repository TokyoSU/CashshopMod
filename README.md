# Cash Shop Mod
![Capture d'écran 2025-06-25 123426](https://github.com/user-attachments/assets/9708f4f3-1466-4200-a1e2-593327a29ee0)

This mod is inspired by the Grand Fantasia Item Mall (Cash Shop):
![Grand-Fantasia-Item-Mall](https://github.com/user-attachments/assets/e20dd899-351e-4601-b96e-fec5d0488a0d)

It allows modpackers to create a cash shop using KubeJS only, without any configuration files:
(I would have liked to use the Grand Fantasia UI instead of the Minecraft one, but it’s too large :'D)

# Mod supported:
- JEI: [Curseforge](https://www.curseforge.com/minecraft/mc-mods/jei) Enables the JEI UI to adapt to the shop UI size.
- FTBQuest: [Curseforge](https://www.curseforge.com/minecraft/mc-mods/ftb-quests-forge) Not implemented yet but planned. The goal is to add quests that reward currency upon completion!

# KubeJS commands
```
0) You need to create the register event (It's a startup event so inside the "startup_scripts" folder):
CashshopJSEvents.register(event => {
  // You register tab and item here!
})

1) For creating tabs inside the shop:
createTab(tabId, translateName, iconNamespace)
- TabID is a unique identifier by you, start from 0.
- TranslateName is a identifier for the tab name, example: "tab.your_tab_name"
- IconNamespace is a item identifier, example: "minecraft:apple"

2) For adding item to these tabs:
addItem(namespace, tabId, itemCount, gold, silver, copper, discountPercent)
- Namespace is the item identifier, example: "minecraft:apple"
- TabID The identifier you created using createTab()
- ItemCount The number of item in this stack (min: 1, max: 64)
- Gold, Silver, Copper The price (Gold min: 0, max: 99999999) (Silver min: 0, max: 99) (Copper min: 0, max: 99)
- DiscountPercentage The discount if any, at zero it's full price and 100 is free !
```

# Examples
```
function makeAllTabs(event) {
	event.createTab(0, "tab.cashshop.discount", "minecraft:redstone");
	event.createTab(1, "tab.cashshop.armor", "minecraft:leather_chestplate");
}

function makeForTab0(event) {
	event.addItem("minecraft:apple", 0, 32, 0, 40, 80, 0);
	event.addItem("minecraft:stick", 0, 32, 1, 0, 0, 5);
	event.addItem("minecraft:golden_apple", 0, 32, 0, 0, 0, 5);
	event.addItem("minecraft:grass_block", 0, 32, 5, 0, 0, 5);
	event.addItem("minecraft:stone", 0, 32, 0, 0, 2, 5);
	event.addItem("minecraft:enchanting_table", 0, 32, 0, 0, 8, 80);
	event.addItem("minecraft:oak_planks", 0, 32, 0, 1, 0, 5);
	event.addItem("minecraft:iron_ingot", 0, 32, 0, 0, 0, 5);
	event.addItem("minecraft:gold_ingot", 0, 32, 111, 0, 0, 5);
	event.addItem("minecraft:gold_nugget", 0, 32, 20, 0, 0, 40);
	event.addItem("minecraft:raw_iron", 0, 32, 0, 0, 0, 5);
	event.addItem("minecraft:dirt", 0, 32, 0, 0, 50, 5);
	event.addItem("minecraft:raw_gold", 0, 32, 0, 40, 0, 5);
	event.addItem("minecraft:weathered_copper", 0, 32, 0, 0, 0, 5);
	event.addItem("minecraft:waxed_copper_block", 0, 32, 0, 0, 0, 5);
	event.addItem("minecraft:raw_gold", 0, 32, 0, 0, 50, 5);
	event.addItem("minecraft:raw_gold", 0, 16, 150, 0, 0, 95);
	event.addItem("minecraft:raw_gold", 0, 4, 0, 0, 0, 5);
	event.addItem("minecraft:raw_gold", 0, 8, 9999, 0, 0, 40);
	event.addItem("minecraft:raw_gold", 0, 64, 0, 0, 0, 5);
}

function makeForTab1(event) {
	event.addItem("minecraft:coarse_dirt", 1, 64, 0, 0, 0, 5);
}

function makeAllItemsForTabs(event) {
	makeForTab0(event);
	makeForTab1(event);
}

CashshopJSEvents.register(event => {
	//event.disableChatLog();
	makeAllTabs(event);
	makeAllItemsForTabs(event);
})
```

# Changelogs
## v0.0.2 (Release)
- Added copper, silver and gold coin that if pickup or inside inventory will be converted to the cash shop.
- Added a creative tabs for the coins.
- Now the confirm ui work and show the discount price (or the price if discount = 0).
- Now the discount text can show if the item is free and change color based on the discount value.
- Removed "Most buy" ui (not useful), the shop ui size was changed.
- Added Discount string, with a pulsating effect.
- Fixed a bug where if the shop did not have any page could crash when clicking the left/right page button.

## v0.0.1 (Alpha)
- Base version
![cashshop](https://github.com/user-attachments/assets/44647afa-b2ef-4e6a-b204-0a16ce005c43)
