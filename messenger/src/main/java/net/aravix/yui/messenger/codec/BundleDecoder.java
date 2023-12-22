package net.aravix.yui.messenger.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.log4j.Log4j2;
import net.aravix.yui.messenger.bundle.Bundle;
import net.aravix.yui.messenger.bundle.manager.BundleManager;
import net.aravix.yui.messenger.util.BundleUtil;

import java.util.List;

@Log4j2
public class BundleDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> output) {
        if (!byteBuf.isReadable() || byteBuf.readableBytes() == 0)
            return;

        try {
            var id = BundleUtil.readVarInt(byteBuf);

            if (id < 0)
                return;

            var bundleClass = BundleManager.getBundle(id);

            if (bundleClass == null) {
                byteBuf.skipBytes(byteBuf.readableBytes());
                return;
            }

            var json = BundleUtil.readString(byteBuf);
            var bundle = Bundle.fromJson(json);

            output.add(bundle);
        } catch (Exception e) {
            byteBuf.skipBytes(byteBuf.readableBytes());
        }
    }
}