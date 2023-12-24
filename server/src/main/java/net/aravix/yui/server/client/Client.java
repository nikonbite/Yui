package net.aravix.yui.server.client;

import io.netty.channel.ChannelHandlerContext;
import net.aravix.yui.messenger.bundle.Bundle;
import net.aravix.yui.messenger.util.ConnectionType;

import java.util.Arrays;

public record Client(String name, ConnectionType type, ChannelHandlerContext ctx) {
    public void sendPacket(Bundle... packet) {
        Arrays.stream(packet).forEach(ctx::writeAndFlush);
    }
}
