package mumeinosato.videoplayerplugin.server.network;

import mumeinosato.videoplayerplugin.ProxyServer;
import mumeinosato.videoplayerplugin.common.network.PacketContainer;
import mumeinosato.videoplayerplugin.common.network.PacketDispatcher;
import mumeinosato.videoplayerplugin.server.patch.VideoPatchRecieveEventServer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PacketDispatcherServer {
    public static void register(Plugin plugin) {
        PacketDispatcher.registerServer(plugin, PacketDispatcherServer::handle);
    }

    public static void handle(PacketContainer message, Player player) {
        if (message == null || message.getOperation() == null)
            return;

        ProxyServer.getServer().getPluginManager().callEvent(new VideoPatchRecieveEventServer(message.getOperation(), message.getPatches(), player));
    }

    public static void send(Player network, PacketContainer packet) {
        PacketDispatcher.channel.sendTo(packet, network);
    }
}
