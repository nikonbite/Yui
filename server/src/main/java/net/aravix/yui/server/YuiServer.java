package net.aravix.yui.server;

import com.google.common.eventbus.EventBus;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.aravix.yui.messenger.bundle.Bundle;
import net.aravix.yui.messenger.client.Server;

@Getter
@Log4j2
public class YuiServer extends Server {
    public static boolean RUNNING = false;
    public static YuiServer INSTANCE;
    private EventBus eventBus;

    public static void main(String[] args) {
        new YuiServer().start();
    }

    @Override
    public void start() {
        INSTANCE = this;
        RUNNING = true;

        log.info("Начинается инициализация Yui!");

        eventBus = new EventBus();
        log.info("Система EventBus подгружена.");
    }

    @Override
    protected void registerProcessors() {

    }

    @Override
    public void stop() {
        RUNNING = false;

        System.exit(0);
    }

    public void broadcastPacket(Bundle... packets) {
        //ClientManager.getConnectedClients().forEach(client -> client.sendPacket(packets));
    }
}
