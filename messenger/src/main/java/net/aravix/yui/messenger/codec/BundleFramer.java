package net.aravix.yui.messenger.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.CorruptedFrameException;
import net.aravix.yui.messenger.util.BundleUtil;

import java.util.List;

/**
 * Coded by <strong>Nikonbite</strong> at 30.07.2023
 * <br>
 * Telegram: <a href="https://nikonbite.t.me">t.me/nikonbite</a>
 */
public class BundleFramer extends ByteToMessageCodec<ByteBuf> {

    private static boolean DIRECT_WARNING = false;

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf from, ByteBuf to) {
        int packetSize = from.readableBytes();
        int headerSize = getVarIntSize(packetSize);

        to.ensureWritable(packetSize + headerSize);

        BundleUtil.writeVarInt(packetSize, to);
        to.writeBytes(from, from.readerIndex(), packetSize);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) {
        buf.markReaderIndex();

        byte[] header = new byte[3];

        for (int i = 0; i < header.length; ++i) {
            if (!buf.isReadable()) {
                buf.resetReaderIndex();
                return;
            }

            header[i] = buf.readByte();

            if (header[i] >= 0) {
                ByteBuf headerBuf = Unpooled.wrappedBuffer(header);

                try {
                    int length = BundleUtil.readVarInt(headerBuf);

                    if (buf.readableBytes() < length) {
                        buf.resetReaderIndex();
                        return;
                    }

                    if (buf.hasMemoryAddress()) {
                        out.add(buf.slice(buf.readerIndex(), length).retain());
                        buf.skipBytes(length);
                    } else {
                        if (!DIRECT_WARNING) {
                            DIRECT_WARNING = true;
                            System.err.println("Netty is not using direct IO buffers.");
                        }

                        ByteBuf dst = ctx.alloc().directBuffer(length);
                        buf.readBytes(dst);
                        out.add(dst);
                    }
                } finally {
                    headerBuf.release();
                }

                return;
            }
        }

        throw new CorruptedFrameException("length wider than 21-bit");
    }

    private int getVarIntSize(int input) {
        return (input & 0xFFFFFF80) == 0
                ? 1 : (input & 0xFFFFC000) == 0
                ? 2 : (input & 0xFFE00000) == 0
                ? 3 : (input & 0xF0000000) == 0
                ? 4 : 5;
    }
}