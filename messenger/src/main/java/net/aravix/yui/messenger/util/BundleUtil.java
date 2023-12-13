package net.aravix.yui.messenger.util;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import lombok.experimental.UtilityClass;

import java.nio.charset.StandardCharsets;

@UtilityClass
public class BundleUtil {
    public void writeString(String string, ByteBuf buf) {
        var bytes = string.getBytes(StandardCharsets.UTF_8);

        writeVarInt(bytes.length, buf);

        buf.writeBytes(bytes);
    }

    public String readString(ByteBuf buf) {
        var length = readVarInt(buf);
        var bytes = new byte[length];

        buf.readBytes(bytes);

        return new String(bytes, Charsets.UTF_8);
    }

    public int readVarInt(ByteBuf input) {
        var result = 0;
        var numRead = 0;
        var maxBytes = 5;

        byte read;

        do {
            read = input.readByte();
            result |= (read & 127) << numRead++ * 7;

            if (numRead > maxBytes) {
                throw new RuntimeException("VarInt is too big");
            }
        } while ((read & 128) == 128);

        return result;
    }

    public void writeVarInt(int value, ByteBuf output) {
        while ((value & -128) != 0) {
            output.writeByte(value & 127 | 128);
            value >>>= 7;
        }

        output.writeByte(value);
    }
}
