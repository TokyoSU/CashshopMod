package net.tokyosu.cashshop.plugin.kubejs.events;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;

public interface KubeEventHandler {
    EventGroup EVENTS = EventGroup.of("CashshopJSEvents");
    EventHandler STARTUP_REGISTER = EVENTS.startup("register", () -> KubeStartupRegister.class);
    static void register() {
        EVENTS.register();
    }
}
