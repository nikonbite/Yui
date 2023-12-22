package net.aravix.yui.client.processor;

import io.netty.channel.ChannelHandlerContext;
import lombok.SneakyThrows;
import net.aravix.yui.client.YuiClient;
import net.aravix.yui.messenger.bundle.Bundle;
import net.aravix.yui.messenger.bundle.processor.Processor;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
public class ClientProcessor implements Processor {

    private final Set<Processor> childProcessors = new HashSet<>();
    public static ClientProcessor INSTANCE;

    @Override
    public void onChannelActive(ChannelHandlerContext ctx) {
        childProcessors.forEach(BundleProcessor -> BundleProcessor.onChannelActive(ctx));
    }

    @Override
    @SneakyThrows
    public void onChannelInactive(ChannelHandlerContext ctx) {
        childProcessors.forEach(BundleProcessor -> BundleProcessor.onChannelInactive(ctx));

        YuiClient.INSTANCE.init();
    }

    @Override
    public void onChannelRead(ChannelHandlerContext ctx, Bundle msg) {
        Processor.super.onChannelRead(ctx, msg);

        childProcessors.forEach(BundleProcessor -> BundleProcessor.onChannelRead(ctx, msg));
    }

    public Set<Processor> childProcessors() {
        return childProcessors;
    }

    {
        INSTANCE = this;
    }
}