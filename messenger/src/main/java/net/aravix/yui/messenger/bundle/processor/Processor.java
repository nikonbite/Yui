package net.aravix.yui.messenger.bundle.processor;

import io.netty.channel.ChannelHandlerContext;
import net.aravix.yui.messenger.bundle.Bundle;
import net.aravix.yui.messenger.bundle.processor.annotations.BundleProcessor;
import net.aravix.yui.messenger.bundle.processor.impl.BossProcessor;

import java.util.Arrays;

public interface Processor {

    default void onChannelActive(ChannelHandlerContext ctx) {
    }

    default void onChannelInactive(ChannelHandlerContext ctx) {
    }

    default void onChannelRead(ChannelHandlerContext ctx, Bundle msg) {
        handle(msg);
    }

    default void handle(Bundle packet) {
        var methodOptional = Arrays.stream(getClass().getDeclaredMethods())
                .filter(method -> Arrays.equals(method.getParameterTypes(), new Class[]{packet.getClass()}))
                .filter(method -> method.isAnnotationPresent(BundleProcessor.class))
                .findFirst();

        methodOptional.ifPresent(method -> {
            try {
                var processor = BossProcessor.PROCESSORS.stream()
                        .filter(bundleProcessor -> bundleProcessor.getClass().equals(getClass()))
                        .findFirst()
                        .orElseGet(() -> {
                            try {
                                return getClass().getDeclaredConstructor().newInstance();
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        });

                method.invoke(processor, packet);
            } catch (Exception | Error e) {
                throw new RuntimeException(e);
            }
        });

    }
}
