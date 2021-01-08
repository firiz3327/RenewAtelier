package net.firiz.renewatelier.npc;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.firiz.renewatelier.version.nms.VEntity;
import net.firiz.renewatelier.version.nms.VEntityPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class NPC {

    @NotNull
    private final NPCObject npcObject;
    @NotNull
    private final VEntity<?> entity;

    private final boolean isPlayer;
    private final List<UUID> viewer = new ObjectArrayList<>();

    public NPC(@NotNull NPCObject npcObject, @NotNull VEntity<?> entity) {
        this.npcObject = npcObject;
        this.entity = entity;
        this.isPlayer = entity instanceof VEntityPlayer;
    }

    @NotNull
    public NPCObject getNpcObject() {
        return npcObject;
    }

    public boolean isPlayer() {
        return isPlayer;
    }

    public int getEntityId() {
        return entity.getEntityId();
    }

    public String getName() {
        return entity.getName();
    }

    public Location getLocation() {
        return entity.getLocation();
    }

    @NotNull
    public VEntity<?> getEntity() {
        return entity;
    }

    @NotNull
    public VEntityPlayer getEntityPlayer() {
        return (VEntityPlayer) entity;
    }

    public void addViewer(Player player) {
//        player.sendMessage("add viewer " + getName());
        viewer.add(player.getUniqueId());
    }

    public void removeViewer(Player player) {
//        player.sendMessage("remove viewer " + getName());
        viewer.remove(player.getUniqueId());
    }

    public boolean hasViewer(Player player) {
        return viewer.contains(player.getUniqueId());
    }

}
