package mumeinosato.videoplayerplugin.common.video;

import mumeinosato.videoplayerplugin.common.model.PlayState;
import mumeinosato.videoplayerplugin.common.util.Timer;

public class VPlayStateStore {
    protected String file;
    protected final Timer timer = Timer.createUnstarted();
    protected boolean paused;
    protected float duration = -1;

    public PlayState fetch() {
        PlayState state = new PlayState();
        state.file = file;
        state.time = timer.getTime();
        state.paused = paused;
        state.duration = duration;
        return state;
    }

    public void dispatch(PlayState action) {
        file = action.file;
        timer.set(action.time);
        paused = action.paused;
        timer.setPaused(paused);
        duration = action.duration;
    }
}
