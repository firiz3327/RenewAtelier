package net.firiz.renewatelier.npc;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.firiz.ateliercommonapi.nms.entity.NMSLivingEntity;
import net.firiz.ateliercommonapi.nms.entity.player.NMSPlayer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class NPC {

    @NotNull
    private final NPCObject npcObject;
    @NotNull
    private final NMSLivingEntity entity;

    private final boolean isPlayer;
    private final List<UUID> viewer = new ObjectArrayList<>();

    public NPC(@NotNull NPCObject npcObject, @NotNull NMSLivingEntity entity) {
        this.npcObject = npcObject;
        this.entity = entity;
        this.isPlayer = entity instanceof NMSPlayer;
    }

    @NotNull
    public NPCObject getNpcObject() {
        return npcObject;
    }

    public boolean isPlayer() {
        return isPlayer;
    }

    public int getEntityId() {
        return entity.id();
    }

    public String getName() {
        if (isPlayer) {
            return PlainTextComponentSerializer.plainText().serialize(((NMSPlayer) entity).displayName());
        } else {
            return PlainTextComponentSerializer.plainText().serialize(entity.customName());
        }
    }

    public Location getLocation() {
        return entity.location();
    }

    public Location getEyeLocation() {
        return entity.eyeLocation();
    }

    public Location getMessageLocation() {
        return entity.eyeLocation().add(0, 0.4, 0);
    }

    @NotNull
    public NMSLivingEntity getEntity() {
        return entity;
    }

    @NotNull
    public NMSPlayer getEntityPlayer() {
        return (NMSPlayer) entity;
    }

    public void addViewer(Player player) {
        viewer.add(player.getUniqueId());
    }

    public void removeViewer(Player player) {
        viewer.remove(player.getUniqueId());
    }

    public boolean hasViewer(Player player) {
        return viewer.contains(player.getUniqueId());
    }

}
