package mumeinosato.videoplayerplugin.server.patch;

import mumeinosato.videoplayerplugin.common.patch.VideoPatch;
import mumeinosato.videoplayerplugin.common.patch.VideoPatchEvent;
import mumeinosato.videoplayerplugin.common.patch.VideoPatchOperation;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;
import java.util.List;

public class VideoPatchSendEventServer extends VideoPatchEvent {
    public VideoPatchSendEventServer(VideoPatchOperation operation, List<VideoPatch> patches) {
        super(operation, patches);
    }

    private static final HandlerList handlers = new HandlerList();

    @Nonnull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @Nonnull
    public static HandlerList getHandlerList() {
        return handlers;
    }
}