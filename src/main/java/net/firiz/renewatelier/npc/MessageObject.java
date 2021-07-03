package net.firiz.renewatelier.npc;

import net.firiz.ateliercommonapi.FakeId;
import net.firiz.ateliercommonapi.nms.entity.EntityData;
import net.firiz.ateliercommonapi.nms.packet.PacketUtils;
import net.firiz.renewatelier.entity.player.Char;
import net.firiz.renewatelier.entity.player.sql.load.PlayerSaveManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MessageObject {

    protected static final int FAKE_UNIQUE_ID = FakeId.createUniqueId();
    private final Char player;
    private EntityData entityData;

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
        messagePacket(Component.text(text));
    }

    public void messagePacket(@NotNull final Component text) {
        showStand(text);
    }

    private void showStand(Component message) {
        final Player bukkitPlayer = this.player.getPlayer();
        if (this.entityData == null) {
            this.entityData = new EntityData(FAKE_UNIQUE_ID, EntityType.ARMOR_STAND);
            PacketUtils.sendPacket(bukkitPlayer, this.entityData.spawnPacket(this.baseLocation));
            this.player.getMessageObjectRun().add(this);
        } else {
            this.player.getMessageObjectRun().resetTime();
        }
        PacketUtils.sendPacket(bukkitPlayer, EntityData.armorStand(this.entityData, bukkitPlayer.getWorld(), message, false).metaPacket());
    }

    protected void hideStand() {
        PacketUtils.sendPacket(this.player.getPlayer(), entityData.despawnPacket());
        this.entityData = null;
    }
}
