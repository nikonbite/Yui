package net.aravix.yui.messenger.bundle.impl;

import net.aravix.yui.messenger.bundle.Bundle;
import net.aravix.yui.messenger.event.Event;

import java.net.InetSocketAddress;
import java.util.UUID;

public class Client {
    public static class Gamer {
        // ClientGamerJoin
        public record Join(UUID uuid, String server) implements Bundle { }
        public record JoinEvent(Join join) implements Event { }
    }

    public static class Network {
        // ClientNetworkConnect
        public record Connect(String name, InetSocketAddress address) implements Bundle { }
        public record ConnectEvent(Connect connect) implements Event { }
    }
}