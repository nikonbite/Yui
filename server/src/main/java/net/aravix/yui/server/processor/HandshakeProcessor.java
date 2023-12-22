package net.aravix.yui.server.processor;

import com.google.common.collect.Sets;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.log4j.Log4j2;
import net.aravix.yui.messenger.bundle.impl.Gamer;
import net.aravix.yui.messenger.bundle.impl.Network;
import net.aravix.yui.messenger.bundle.processor.Processor;
import net.aravix.yui.messenger.bundle.processor.annotations.BundleProcessor;
import net.aravix.yui.server.client.Client;
import net.aravix.yui.server.client.manager.ClientManager;

import java.util.Set;

@Log4j2
public class HandshakeProcessor implements Processor {
    private final Set<ChannelHandlerContext> channelsWaitingApproval = Sets.newHashSet();

    @Override
    public void onChannelActive(ChannelHandlerContext channelHandlerContext) {
        channelsWaitingApproval.add(channelHandlerContext);
    }

    @Override
    public void onChannelInactive(ChannelHandlerContext channelHandlerContext) {
        channelsWaitingApproval.removeIf(channelHandlerContext1 -> channelHandlerContext1.equals(channelHandlerContext));

        ClientManager.getConnectedClients().stream()
                .filter(client -> client.ctx().equals(channelHandlerContext))
                .findFirst().ifPresent(ClientManager::onDisconnect);
    }

    @BundleProcessor
    public void handle(Network.Connect packet) {
        var context = channelsWaitingApproval.stream()
                .filter(channelHandlerContext -> channelHandlerContext.channel()
                        .remoteAddress().equals(packet.address()))
                .findFirst();

        context.ifPresent(channelHandlerContext -> {
            var client = new Client(packet.name(), /*packet.type(),*/ channelHandlerContext);

            if (ClientManager.getConnectedClients().contains(client)) return;

            ClientManager.onConnect(client);
        });
    }


    @BundleProcessor
    public void handle(Gamer.Join packet) {
        log.info(packet + " | " + packet.uuid() + " | " + packet.server());
    }
}