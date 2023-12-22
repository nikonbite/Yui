package net.aravix.yui.messenger.pipeline;

import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFactory;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.compression.JdkZlibDecoder;
import io.netty.handler.codec.compression.JdkZlibEncoder;
import lombok.experimental.UtilityClass;
import net.aravix.yui.messenger.codec.BundleDecoder;
import net.aravix.yui.messenger.codec.BundleEncoder;
import net.aravix.yui.messenger.bundle.processor.impl.BossProcessor;
import net.aravix.yui.messenger.codec.BundleFramer;

@UtilityClass
public class NetworkPipeline {
    public final String BUNDLE_DECODER = "bundle-decoder";
    public final String BUNDLE_ENCODER = "bundle-encoder";
    public final String BUNDLE_FRAMER = "bundle-framer";
    public final String BUNDLE_HANDLER = "boss-handler";
    public final String BUNDLE_DEFLATER = "bundle-deflater";
    public final String BUNDLE_INFLATER = "bundle-inflater";

    public void initPipeline(Channel ch) {
        var config = ch.config();
        config.setOption(ChannelOption.IP_TOS, 0x18);
        config.setOption(ChannelOption.SO_SNDBUF, Integer.MAX_VALUE);
        config.setOption(ChannelOption.TCP_NODELAY, true);
        config.setOption(ChannelOption.SO_KEEPALIVE, true);
        config.setAllocator(new PooledByteBufAllocator(false));

        var pipeline = ch.pipeline();
        pipeline.addLast(BUNDLE_DECODER, new BundleDecoder());
        pipeline.addLast(BUNDLE_ENCODER, new BundleEncoder());
        pipeline.addLast(BUNDLE_FRAMER, new BundleFramer());
        pipeline.addLast(BUNDLE_HANDLER, new BossProcessor());
    }

    public EventLoopGroup getEventLoopGroup(int threads) {
        return Epoll.isAvailable() ? new EpollEventLoopGroup(threads) : new NioEventLoopGroup(threads);
    }

    public ChannelFactory<ServerSocketChannel> getChannelFactory() {
        return Epoll.isAvailable() ? EpollServerSocketChannel::new : NioServerSocketChannel::new;
    }

    public Class<? extends Channel> getClientChannel() {
        return Epoll.isAvailable() ? EpollSocketChannel.class : NioSocketChannel.class;
    }
}