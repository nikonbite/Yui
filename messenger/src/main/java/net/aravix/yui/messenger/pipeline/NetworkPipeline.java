package net.aravix.yui.messenger.pipeline;

import io.netty.buffer.ByteBufAllocator;
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
import lombok.experimental.UtilityClass;
import net.aravix.yui.messenger.codec.BundleDecoder;
import net.aravix.yui.messenger.codec.BundleEncoder;
import net.aravix.yui.messenger.bundle.processor.impl.BossProcessor;

@UtilityClass
public class NetworkPipeline {
    public final String BUNDLE_DECODER = "bundle-decoder";
    public final String BUNDLE_ENCODER = "bundle-encoder";
    public final String BUNDLE_HANDLER = "boss-handler";

    public void initPipeline(Channel ch) {
        ch.config().setOption(ChannelOption.IP_TOS, 0x18);
        ch.config().setOption(ChannelOption.SO_SNDBUF, 262_144);
        ch.config().setOption(ChannelOption.TCP_NODELAY, true);
        ch.config().setOption(ChannelOption.SO_KEEPALIVE, true);
        ch.config().setOption(ChannelOption.TCP_NODELAY, true);
        ch.config().setAllocator(ByteBufAllocator.DEFAULT);

        ch.pipeline().addLast(BUNDLE_DECODER, new BundleDecoder());
        ch.pipeline().addLast(BUNDLE_ENCODER, new BundleEncoder());
        ch.pipeline().addLast(BUNDLE_HANDLER, new BossProcessor());
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