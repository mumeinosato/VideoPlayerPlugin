package mumeinosato.videoplayerplugin;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import mumeinosato.videoplayerplugin.common.model.PlayState;
import mumeinosato.videoplayerplugin.common.network.PacketContainer;
import mumeinosato.videoplayerplugin.common.patch.VideoPatch;
import mumeinosato.videoplayerplugin.common.patch.VideoPatchOperation;
import mumeinosato.videoplayerplugin.common.util.Timer;
import mumeinosato.videoplayerplugin.server.network.PacketDispatcherServer;
import mumeinosato.videoplayerplugin.server.patch.VideoPatchRecieveEventServer;
import mumeinosato.videoplayerplugin.server.patch.VideoPatchSendEventServer;
import mumeinosato.videoplayerplugin.server.video.VDisplayManagerServer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ProxyServer implements Listener {

    private static Plugin plugin;
    private static Server server;

    private static VDisplayManagerServer displayManager;

    public static Server getServer() {
        return server;
    }

    public static VDisplayManagerServer getDisplayManager() {
        return displayManager;
    }

    public void registerEvents(Plugin plugin) {
        // Register ourselves for server and other game events we are interested in
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        // Tick
        new BukkitRunnable() {
            @Override
            public void run() {
                onTick();
            }
        }.runTaskTimer(plugin, 0, 0);

        // Packet
        PacketDispatcherServer.register(plugin);
    }

    public void onServerStart(JavaPlugin pluginIn) {
        plugin = pluginIn;
        server = plugin.getServer();

        File dataFolder = plugin.getDataFolder();
        dataFolder.mkdirs();
        displayManager = VDisplayManagerServer.get(dataFolder);
        displayManager.read();

        PacketContainer packet = new PacketContainer(VideoPatchOperation.SYNC, displayManager.list().stream()
                .map(e -> new VideoPatch(e.getUUID(), e.getQuad(), e.fetchState()))
                .collect(Collectors.toList())
        );
        getServer().getOnlinePlayers()
                .forEach(p -> PacketDispatcherServer.send(p, packet));
    }

    public void onServerClose() {
        getDisplayManager().write();

        PacketContainer packet = new PacketContainer(VideoPatchOperation.SYNC, Collections.emptyList());
        getServer().getOnlinePlayers()
                .forEach(p -> PacketDispatcherServer.send(p, packet));
    }

    @EventHandler
    public void onWorldSave(WorldSaveEvent event) {
        getDisplayManager().write();
    }

    public void onTick() {
        Timer.tick();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        new BukkitRunnable() {
            @Override
            public void run() {
                VDisplayManagerServer state = getDisplayManager();
                PacketContainer packet = new PacketContainer(VideoPatchOperation.SYNC, state.list().stream()
                        .map(p -> new VideoPatch(p.getUUID(), p.getQuad(), p.fetchState())).collect(Collectors.toList()));
                PacketDispatcherServer.send(player, packet);
            }
        }.runTaskLaterAsynchronously(plugin, 40);
    }

    @EventHandler
    public void onServerPatchSend(VideoPatchSendEventServer event) {
        if (getServer() == null)
            return;

        PacketContainer packet = new PacketContainer(event.getOperation(), event.getPatches());
        getServer().getOnlinePlayers()
                .forEach(p -> PacketDispatcherServer.send(p, packet));
    }

    private final Table<UUID, UUID, Double> durationTable = HashBasedTable.create();

    @EventHandler
    public void onServerPatchReceive(VideoPatchRecieveEventServer event) {
        if (event.getOperation() == VideoPatchOperation.UPDATE) {
            VDisplayManagerServer state = getDisplayManager();

            event.getPatches().forEach(e -> {
                Optional.ofNullable(state.get(e.getId())).ifPresent(d -> {
                    UUID displayId = d.getUUID();
                    UUID playerId = event.getPlayer().getPlayerProfile().getId();
                    PlayState newState = e.getState();
                    if (newState != null && playerId != null) {
                        float duration = newState.duration;
                        if (duration >= 0)
                            durationTable.put(displayId, playerId, (double) duration);
                    }
                    durationTable.row(displayId).values().stream()
                            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                            .entrySet()
                            .stream()
                            .max(Map.Entry.comparingByValue())
                            .ifPresent(f -> {
                                float key = (float) (double) f.getKey();
                                PlayState playState = d.fetchState();
                                if (playState.duration != key) {
                                    playState.duration = key;
                                    d.dispatchState(playState);
                                }
                            });
                });
            });
        }
    }

}
