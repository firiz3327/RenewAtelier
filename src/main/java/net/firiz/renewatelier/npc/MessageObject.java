package net.firiz.renewatelier.npc;

import net.firiz.ateliercommonapi.FakeId;
import net.firiz.renewatelier.entity.player.Char;
import net.firiz.renewatelier.entity.player.sql.load.PlayerSaveManager;
import net.firiz.renewatelier.version.packet.EntityPacket;
import net.firiz.renewatelier.version.packet.FakeEntity;
import net.firiz.renewatelier.version.packet.PacketUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MessageObject {

    protected static final int FAKE_UNIQUE_ID = FakeId.createUniqueId();
    private final Char player;
    private FakeEntity fakeEntity;

    private Location baseLocation;

    public MessageObject(Player player, Location location) {
        this(PlayerSaveManager.INSTANCE.getChar(player), location);
    }

    public MessageObject(Char character, Location location) {
        this.player = character;
        this.baseLocation = location;
    }

    public void setLocation(@NotNull Location location) {
        this.baseLocation = location;
    }

    public void messagePacket(@NotNull final String text) {
        showStand(text);
    }

    private void showStand(String message) {
        final Player bukkitPlayer = this.player.getPlayer();
        if (fakeEntity == null) {
            this.fakeEntity = new FakeEntity(FAKE_UNIQUE_ID, FakeEntity.FakeEntityType.ARMOR_STAND, 0);
            PacketUtils.sendPacket(bukkitPlayer, EntityPacket.getSpawnPacket(this.fakeEntity, this.baseLocation));
            player.getMessageObjectRun().add(this);
        } else {
            player.getMessageObjectRun().resetTime();
        }
        PacketUtils.sendPacket(bukkitPlayer, EntityPacket.getMessageStandMeta(bukkitPlayer.getWorld(), message).compile(FAKE_UNIQUE_ID));
    }

    protected void hideStand() {
        PacketUtils.sendPacket(this.player.getPlayer(), EntityPacket.getDespawnPacket(FAKE_UNIQUE_ID));
        this.fakeEntity = null;
    }
}
