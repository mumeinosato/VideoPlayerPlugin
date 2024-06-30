package mumeinosato.videoplayerplugin.common.model;

public interface LifecycleDisplay extends Display {
    void destroy();

    boolean isDestroyed();

    enum VState {
        INVALIDATED,
        VALIDATED,
    }
}