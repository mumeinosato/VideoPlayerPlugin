package mumeinosato.videoplayerplugin.common.patch;

import  mumeinosato.videoplayerplugin.common.model.PlayState;
import  mumeinosato.videoplayerplugin.common.model.Quad;

import javax.annotation.Nullable;
import java.util.UUID;


public class VideoPatch {
    private final UUID id;
    @Nullable
    private final Quad quad;
    @Nullable
    private final PlayState state;

    public VideoPatch(UUID id, @Nullable Quad quad, @Nullable PlayState state){
        this.id = id;
        this.quad = quad;
        this.state = state;
    }

    public UUID getId() {
        return id;
    }

    @Nullable
    public Quad getQuad() {
        return quad;
    }

    @Nullable
    public PlayState getState() {
        return state;
    }

}
