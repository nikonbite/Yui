package net.aravix.yui.messenger.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.aravix.yui.messenger.bundle.Bundle;
import net.aravix.yui.messenger.bundle.manager.BundleManager;
import net.aravix.yui.messenger.util.BundleUtil;

public class BundleEncoder extends MessageToByteEncoder<Bundle> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Bundle bundle, ByteBuf output) {
        var id = BundleManager.getBundleId(bundle.getClass());

        if (id < 0) {
            System.out.println("Bundle " + bundle.getClass() + " not registered");
            return;
        }

        BundleUtil.writeVarInt(id, output);
        bundle.write(output);
    }
}
