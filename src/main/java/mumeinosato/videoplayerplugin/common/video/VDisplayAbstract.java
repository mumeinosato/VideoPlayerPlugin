package mumeinosato.videoplayerplugin.common.video;

import mumeinosato.videoplayerplugin.common.model.LifecycleDisplay;
import mumeinosato.videoplayerplugin.common.model.Quad;

import javax.annotation.Nullable;
import java.util.UUID;

public abstract class VDisplayAbstract implements LifecycleDisplay {
    protected final UUID uuid;
    @Nullable
    protected Quad quad;
    protected VState state = VState.INVALIDATED;
    protected boolean destroyRequested;

    public VDisplayAbstract(UUID uuidIn) {
        uuid = uuidIn;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public void setQuad(@Nullable Quad quadIn) {
        quad = quadIn;
    }

    @Nullable
    @Override
    public Quad getQuad() {
        return quad;
    }

    @Override
    public void destroy() {
        destroyRequested = true;
        state = VState.INVALIDATED;
    }

    @Override
    public boolean isDestroyed() {
        return state == VState.INVALIDATED && destroyRequested;
    }
}