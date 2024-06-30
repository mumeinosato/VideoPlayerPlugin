package mumeinosato.videoplayerplugin.common.network;

import mumeinosato.videoplayerplugin.VideoPlayerPlugin;
import mumeinosato.videoplayerplugin.common.data.DataSerializer;
import mumeinosato.videoplayerplugin.common.patch.VideoPatch;
import mumeinosato.videoplayerplugin.common.patch.VideoPatchOperation;
import mumeinosato.videoplayerplugin.common.util.PacketBuffer;

import java.util.List;

public class PacketContainer {

    private final VideoPatchOperation operation;
    private final List<VideoPatch> patches;

    public PacketContainer(VideoPatchOperation operation, List<VideoPatch> patches) {
        this.operation = operation;
        this.patches = patches;
    }

    public VideoPatchOperation getOperation() {
        return operation;
    }

    public List<VideoPatch> getPatches() {
        return patches;
    }

    public static void encode(PacketContainer message, PacketBuffer buf) {
        String string = DataSerializer.encode(message);
        buf.writeString(string);
    }

    public static PacketContainer decode(PacketBuffer buf) {
        String string = buf.readString();
        PacketContainer data = DataSerializer.decode(string, PacketContainer.class);
        if (data == null)
            VideoPlayerPlugin.LOGGER.warning("Invalid Packet");
        return data;
    }

}
