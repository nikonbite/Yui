package net.aravix.yui.messenger.bundle.processor.impl;

import com.google.common.collect.Sets;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.NonNull;
import net.aravix.yui.messenger.bundle.Bundle;
import net.aravix.yui.messenger.bundle.processor.Processor;

import java.net.SocketException;
import java.util.Set;

public class BossProcessor extends SimpleChannelInboundHandler<Bundle> {

    public static Set<Processor> PROCESSORS = Sets.newHashSet();

    public static void registerProcessor(Processor processor) {
        PROCESSORS.add(processor);
    }

    public static void unregisterProcessor(Processor processor) {
        PROCESSORS.remove(processor);
    }

    @Override
    public void channelActive(@NonNull ChannelHandlerContext ctx) {
        PROCESSORS.forEach(processor -> processor.onChannelActive(ctx));
    }

    @Override
    public void channelInactive(@NonNull ChannelHandlerContext ctx) {
        PROCESSORS.forEach(processor -> processor.onChannelInactive(ctx));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Bundle packet) {
        PROCESSORS.forEach(processor -> processor.onChannelRead(channelHandlerContext, packet));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (!(cause instanceof SocketException)) cause.printStackTrace(System.out);
    }
}

