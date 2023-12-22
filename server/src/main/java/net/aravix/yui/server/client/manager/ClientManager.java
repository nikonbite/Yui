package net.aravix.yui.server.client.manager;

import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import net.aravix.yui.server.YuiServer;
import net.aravix.yui.server.client.Client;

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Log4j2
@UtilityClass
public class ClientManager {

    @Getter
    private final Set<Client> connectedClients = Sets.newHashSet();
    private final YuiServer tower = YuiServer.INSTANCE;

    public void onConnect(Client client) {
        connectedClients.add(client);

//        if (client.type().equals(ConnectionType.PROXY)) {
//            SchedulerUtil.runAsyncTaskLater(() -> {
//                var registeredCommandsCopy = new HashSet<>(TowerCommandManager.REGISTERED_COMMANDS);
//
//                registeredCommandsCopy.forEach(command -> TowerServer.INSTANCE.broadcastPacket(
//                        ConnectionType.PROXY,
//                        new TowerCommandBundle(
//                                command.name(),
//                                command.group(),
//                                command.authorized(),
//                                Arrays.stream(command.aliases()).toList()
//                        )
//                ));
//            }, 1, TimeUnit.SECONDS);
//        }

        // new ClientConnectedEvent(client).call();

        log.info("Клиент {} зарегистрирован [IP: {}]", client.name(), client.ctx().channel().remoteAddress());
    }

    public void onDisconnect(Client client) {
        connectedClients.remove(client);

        log.info("Клиент {} разрегистрирован [IP: {}]", client.name(), client.ctx().channel().remoteAddress());
    }

    public Set<Client> getConnectedClients(Predicate<Client> predicate) {
        return connectedClients.stream().filter(predicate).collect(Collectors.toSet());
    }

/*    public Set<Client> getConnectedClients(ConnectionType type) {
        return connectedClients.stream().filter(client -> client.type().equals(type)).collect(Collectors.toSet());
    }*/
}
