package net.aravix.yui.messenger.bundle.impl;

import net.aravix.yui.messenger.bundle.Bundle;
import net.aravix.yui.messenger.event.Event;

import java.net.InetSocketAddress;

public class Network {
    // ClientNetworkConnect
    public record Connect(String name, InetSocketAddress address) implements Bundle { }
    public record ConnectEvent(Connect connect) implements Event { }
}