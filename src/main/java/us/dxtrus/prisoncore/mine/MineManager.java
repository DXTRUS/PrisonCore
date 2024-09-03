package us.dxtrus.prisoncore.mine;

import us.dxtrus.prisoncore.PrisonCore;
import us.dxtrus.prisoncore.config.Config;
import us.dxtrus.prisoncore.config.Lang;
import us.dxtrus.prisoncore.mine.models.PrivateMine;
import us.dxtrus.prisoncore.mine.models.Server;
import us.dxtrus.prisoncore.mine.network.ServerManager;
import us.dxtrus.prisoncore.mine.network.broker.Broker;
import us.dxtrus.prisoncore.mine.network.broker.Message;
import us.dxtrus.prisoncore.mine.network.broker.Payload;
import us.dxtrus.prisoncore.mine.network.broker.Response;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages mines across the entire network and handles them.
 */
public class MineManager {
    private static MineManager instance;
    private final Map<UUID, PrivateMine> loadedMines = new ConcurrentHashMap<>();

    public static MineManager getInstance() {
        if (instance == null) {
            instance = new MineManager();
        }
        return instance;
    }

    public PrivateMine getMine(UUID player) {
        PrivateMine mine = loadedMines.get(player);
        if (mine == null) {
            mine = new PrivateMine(player);
        }
        return mine;
    }

    /**
     * Loads a mine and gives you the new update mine object.
     *
     * @param mine the mine to load
     * @return the new updated mine or the current mine object if mine was already loaded. or null if theres a fail (dont handle fails)
     */
    public CompletableFuture<PrivateMine> load(PrivateMine mine) {
        return CompletableFuture.supplyAsync(() -> {
            if (mine.isLoaded()) return mine;

            if (Config.getInstance().getServers().isSingleInstance()) {
                LocalMineManager.getInstance().load(mine);
                return handleLoadResponse(mine, ServerManager.getInstance().getThisServer());
            }

            Server server = ServerManager.getInstance().getRandomServer();

            Message.builder()
                    .type(Message.Type.MINE_LOAD)
                    .payload(Payload.withString(mine.getWorldName()))
                    .build().send(PrisonCore.getInstance().getBroker());

            return handleLoadResponse(mine, server);
        });
    }

    /**
     * Unloads a mine and returns the new mine object.
     *
     * @param mine the mine to load
     * @return the new updated mine or the current mine object if mine was already loaded. or null if theres a fail (dont handle fails)
     */
    public CompletableFuture<PrivateMine> unload(PrivateMine mine) {
        return CompletableFuture.supplyAsync(() -> {
            if (mine == null) return null;
            if (!mine.isLoaded()) return mine;

            if (Config.getInstance().getServers().isSingleInstance()) {
                LocalMineManager.getInstance().unload(mine);
                return handleUnLoadResponse(mine, ServerManager.getInstance().getThisServer());
            }

            Server server = ServerManager.getInstance().getRandomServer();

            Message.builder()
                    .type(Message.Type.MINE_UNLOAD)
                    .payload(Payload.withString(mine.getWorldName()))
                    .build().send(PrisonCore.getInstance().getBroker());

            return handleUnLoadResponse(mine, server);
        });
    }

    private PrivateMine handleLoadResponse(PrivateMine mine, Server server) {
        CompletableFuture<Response> future = new CompletableFuture<>();
        Broker.responses.put(mine.getWorldName(), future);
        Response response = future.join();

        Lang.Errors errorMessages = Lang.getInstance().getErrors();

        switch (response.getResponseType()) {
            case SUCCESS: {
                mine.setServer(server);
                mine.setLoaded(true);
                loadedMines.put(mine.getOwner(), mine);
                return mine;
            }
            case FAIL_NO_WORLD:
                Message.builder()
                        .type(Message.Type.NOTIFICATION)
                        .payload(Payload.withNotification(mine.getOwner(), errorMessages.getWorldNotFound().formatted(response.getTrackingCode())))
                        .build().send(PrisonCore.getInstance().getBroker());
                // todo: add automatic world creation even tho this error is rare
            case FAIL_OLD:
                Message.builder()
                        .type(Message.Type.NOTIFICATION)
                        .payload(Payload.withNotification(mine.getOwner(), errorMessages.getWorldOldFormat().formatted(response.getTrackingCode())))
                        .build().send(PrisonCore.getInstance().getBroker());
                // todo: add automatic world fix (delete world and recreate it as the world stores no data)
                // todo: not sure if we can do this one manually
            case FAIL_CORRUPTED:
                Message.builder()
                        .type(Message.Type.NOTIFICATION)
                        .payload(Payload.withNotification(mine.getOwner(), errorMessages.getWorldCorrupted().formatted(response.getTrackingCode())))
                        .build().send(PrisonCore.getInstance().getBroker());
                // todo: add automatic world fix (delete world and recreate it as the world stores no data)
            case FAIL_GENERIC:
                Message.builder()
                        .type(Message.Type.NOTIFICATION)
                        .payload(Payload.withNotification(mine.getOwner(), errorMessages.getGenericWorldError().formatted(response.getTrackingCode())))
                        .build().send(PrisonCore.getInstance().getBroker());
                // todo: add automatic world fix (delete world and recreate it as the world stores no data)
                // todo: we can attempt to fix a generic error because why not?
        }
        return null;
    }

    private PrivateMine handleUnLoadResponse(PrivateMine mine, Server server) {
        CompletableFuture<Response> future = new CompletableFuture<>();
        Broker.responses.put(mine.getWorldName(), future);
        Response response = future.join();

        Lang.Errors errorMessages = Lang.getInstance().getErrors();

        switch (response.getResponseType()) {
            case SUCCESS: {
                mine.setServer(null);
                mine.setLoaded(false);
                loadedMines.remove(mine.getOwner());
                return mine;
            }
            case FAIL_NO_WORLD:
                Message.builder()
                        .type(Message.Type.NOTIFICATION)
                        .payload(Payload.withNotification(mine.getOwner(), errorMessages.getWorldNotFoundUnload().formatted(response.getTrackingCode())))
                        .build().send(PrisonCore.getInstance().getBroker());
            default:
                Message.builder()
                        .type(Message.Type.NOTIFICATION)
                        .payload(Payload.withNotification(mine.getOwner(), errorMessages.getGenericWorldError().formatted(response.getTrackingCode())))
                        .build().send(PrisonCore.getInstance().getBroker());
        }
        return null;
    }
}
