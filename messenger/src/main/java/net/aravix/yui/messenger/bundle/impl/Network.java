package net.aravix.yui.messenger.bundle.impl;

import net.aravix.yui.messenger.bundle.Bundle;
import net.aravix.yui.messenger.util.ConnectionType;

import java.net.InetSocketAddress;

public class Network {
    // Network Connect
    public record Connect(String name, ConnectionType type, InetSocketAddress address) implements Bundle { }
    public record ConnectEvent(Connect bundle) implements Bundle { }
}