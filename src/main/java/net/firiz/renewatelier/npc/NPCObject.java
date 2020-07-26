package net.firiz.renewatelier.npc;

import net.firiz.renewatelier.utils.CommonUtils;
import net.firiz.renewatelier.utils.chores.CObjects;
import net.firiz.renewatelier.version.packet.FakePlayerPacket;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class NPCObject {

    private static final NamespacedKey persistentDataKey = CommonUtils.createKey("npcObject");

    @NotNull
    private final EntityType entityType;
    @NotNull
    private final String name;
    @NotNull
    private final String script;
    @NotNull
    private final World world;

    private final double x;
    private final double y;
    private final double z;

    @Nullable
    private final String skinUUID;
    @Nullable
    private final String villagerType;
    @Nullable
    private final String profession;

    public NPCObject(
            @NotNull String name,
            @NotNull String script,
            @NotNull EntityType entityType,
            @NotNull World world,
            double x,
            double y,
            double z,
            @Nullable UUID skinUUID,
            @Nullable String villagerType,
            @Nullable String profession
    ) {
        this.entityType = entityType;
        this.name = name;
        this.script = script;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.skinUUID = skinUUID == null ? null : skinUUID.toString();
        this.villagerType = villagerType;
        this.profession = profession;
    }

    public static boolean hasEntity(Entity entity) {
        return entity.getPersistentDataContainer().has(persistentDataKey, PersistentDataType.BYTE);
    }

    @NotNull
    public String getScript() {
        return script;
    }

    @NotNull
    public World getWorld() {
        return world;
    }

    @NotNull
    protected NPC spawnEntity() {
        final String colorName = ChatColor.translateAlternateColorCodes('&', name);
        final Location location = new Location(world, x, y, z);
        final NPC npc;
        switch (entityType) {
            case VILLAGER:
                final Villager villager = (Villager) world.spawnEntity(location, EntityType.VILLAGER, CreatureSpawnEvent.SpawnReason.CUSTOM);
                npc = new NPC(this, villager);
                villager.setCustomName(colorName);
                villager.setVillagerType(CObjects.nullIfFunction(villagerType, Villager.Type::valueOf, Villager.Type.PLAINS));
                villager.setProfession(CObjects.nullIfFunction(profession, Villager.Profession::valueOf, Villager.Profession.NONE));
                break;
            case PLAYER:
                npc = new NPC(this, FakePlayerPacket.createEntityPlayer(
                        location.getWorld(),
                        location,
                        CObjects.nullIfFunction(skinUUID, UUID::fromString, null),
                        colorName
                ));
                break;
            default:
                final Entity entity = world.spawnEntity(location, entityType, CreatureSpawnEvent.SpawnReason.CUSTOM);
                entity.setCustomName(colorName);
                npc = new NPC(this, entity);
                break;
        }
        if (!npc.isPlayer()) {
            setProperty(npc.getEntity());
        }
        return npc;
    }

    private void setProperty(@NotNull Entity entity) {
        if (entity instanceof LivingEntity) {
            final LivingEntity livingEntity = (LivingEntity) entity;
            livingEntity.setAI(false);
            livingEntity.setCanPickupItems(false);
            livingEntity.setInvulnerable(true);
        }
        entity.getPersistentDataContainer().set(persistentDataKey, PersistentDataType.BYTE, (byte) 0);
    }

}
