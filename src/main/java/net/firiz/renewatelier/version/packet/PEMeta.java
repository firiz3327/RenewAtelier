package net.firiz.renewatelier.version.packet;

import net.minecraft.server.v1_14_R1.DataWatcher;
import net.minecraft.server.v1_14_R1.PacketPlayOutEntityMetadata;

public class PEMeta {

    private final DataWatcher dataWatcher;
    private final boolean value;

    protected PEMeta(final DataWatcher dataWatcher, final boolean value) {
        this.dataWatcher = dataWatcher;
        this.value = value;
    }

    public PacketPlayOutEntityMetadata compile(int entityId) {
        return new PacketPlayOutEntityMetadata(
                entityId,
                dataWatcher,
                value
        );
    }

    public DataWatcher getDataWatcher() {
        return dataWatcher;
    }

}
