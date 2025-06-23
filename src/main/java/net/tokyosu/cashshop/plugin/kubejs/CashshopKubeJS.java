package net.tokyosu.cashshop.plugin.kubejs;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import net.tokyosu.cashshop.plugin.kubejs.events.KubeEventHandler;

public class CashshopKubeJS extends KubeJSPlugin {
    @Override
    public void registerEvents() {
        KubeEventHandler.register();
    }
}
