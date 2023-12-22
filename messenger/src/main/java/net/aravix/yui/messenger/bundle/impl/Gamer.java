package net.aravix.yui.messenger.bundle.impl;

import net.aravix.yui.messenger.bundle.Bundle;
import net.aravix.yui.messenger.event.Event;

import java.util.UUID;

public class Gamer {
    // ClientGamerJoin
    public record Join(String uuid, String server) implements Bundle { }
    public record JoinEvent(Join join) implements Event { }
}