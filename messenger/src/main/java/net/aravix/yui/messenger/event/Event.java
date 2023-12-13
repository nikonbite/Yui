package net.aravix.yui.messenger.event;

import com.google.common.eventbus.EventBus;

public interface Event {
    default void call(EventBus bus) {
        bus.post(this);
    }
}
