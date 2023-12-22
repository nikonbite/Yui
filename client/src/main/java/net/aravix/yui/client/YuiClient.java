package net.aravix.yui.client;

import net.aravix.yui.client.processor.ClientProcessor;
import net.aravix.yui.messenger.bundle.processor.impl.BossProcessor;
import net.aravix.yui.messenger.client.Client;

public class YuiClient extends Client {
    public static YuiClient INSTANCE;

    public YuiClient(String name) {
        super(name);
    }

    public static void main(String[] args) {
        new YuiClient("Client-1").start();
    }

    @Override
    protected void registerProcessors() {
        BossProcessor.registerProcessor(new ClientProcessor());
    }

    @Override
    protected void start() {
        System.out.println("hello");
    }

    @Override
    protected void stop() {

    }
}