package net.aravix.yui.messenger.bundle;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import lombok.SneakyThrows;
import net.aravix.yui.messenger.util.BundleUtil;
import net.aravix.yui.messenger.util.ReflectionUtil;


public interface Bundle {
    ObjectMapper MAPPER = new ObjectMapper();

    @SneakyThrows
    default void write(ByteBuf buf) {
        var json = MAPPER.writeValueAsString(this);
        var type = MAPPER.createObjectNode();

        type.put("@type", this.getClass().getName());
        json = json.concat(",\n").concat(type.toString());

        BundleUtil.writeString(json, buf);
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    static Bundle fromJson(String json) {
        return MAPPER.readValue(json, (Class<? extends Bundle>) Class.forName(ReflectionUtil.extractClassName(json)));
    }

}