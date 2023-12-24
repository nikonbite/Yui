package net.aravix.yui.messenger.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import lombok.NonNull;
import net.aravix.yui.messenger.bundle.impl.Gamer;
import net.aravix.yui.messenger.bundle.impl.Network;
import net.aravix.yui.messenger.bundle.processor.impl.BossProcessor;
import net.aravix.yui.messenger.pipeline.NetworkPipeline;
import net.aravix.yui.messenger.util.ConnectionType;

import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public abstract class Client {
    private final String name;
    private Bootstrap clientBootstrap;
    private Channel channel;
    private Consumer<Client> startCallback;
    private Consumer<Client> stopCallback;

    public Client(String name) {
        this.name = name;
        init();
        registerProcessors();
    }

    public void init() {
        var listener = (ChannelFutureListener) channelFuture -> {
            var channel = channelFuture.channel();
            var eventLoop = channelFuture.channel().eventLoop();

            if (!channelFuture.isSuccess()) {
                eventLoop.schedule(this::init, 1, TimeUnit.SECONDS);

                System.out.println("reconnecting");

                if (stopCallback != null)
                    stopCallback.accept(this);
            } else {
                var address = (InetSocketAddress) channel.localAddress();

                channel.writeAndFlush(new Network.Connect(name, ConnectionType.PROXY, address));

                if (startCallback != null)
                    startCallback.accept(this);
            }
        };

        clientBootstrap = new Bootstrap()
                .handler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(@NonNull Channel channel) {
                        NetworkPipeline.initPipeline(channel);
                        BossProcessor.PROCESSORS.clear();
                    }
                })
                .channel(NetworkPipeline.getClientChannel())
                .group(NetworkPipeline.getEventLoopGroup(2))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .remoteAddress(new InetSocketAddress("localhost", 8888));

        channel = clientBootstrap.connect().addListener(listener).channel();
    }

    abstract protected void registerProcessors();

    abstract protected void start();

    abstract protected void stop();

    public Consumer<Client> startCallback() {
        return startCallback;
    }

    public void startCallback(Consumer<Client> startCallback) {
        this.startCallback = startCallback;
    }

    public Consumer<Client> stopCallback() {
        return stopCallback;
    }

    public void stopCallback(Consumer<Client> stopCallback) {
        this.stopCallback = stopCallback;
    }

    public String name() {
        return name;
    }

    public Bootstrap clientBootstrap() {
        return clientBootstrap;
    }

    public Channel channel() {
        return channel;
    }
}