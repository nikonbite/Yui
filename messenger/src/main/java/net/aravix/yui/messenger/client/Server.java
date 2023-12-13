package net.aravix.yui.messenger.client;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import lombok.NonNull;
import net.aravix.yui.messenger.bundle.manager.BundleManager;
import net.aravix.yui.messenger.pipeline.NetworkPipeline;

public abstract class Server {
    public Server() {
        init();
        registerProcessors();
    }

    private void init() {
        new ServerBootstrap()
                .childHandler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(@NonNull Channel channel) {
                        NetworkPipeline.initPipeline(channel);
                    }
                })
                .group(NetworkPipeline.getEventLoopGroup(1), NetworkPipeline.getEventLoopGroup(4))
                .channelFactory(NetworkPipeline.getChannelFactory())
                .bind(8888);
    }

    abstract protected void registerProcessors();

    abstract protected void start();

    abstract protected void stop();
}