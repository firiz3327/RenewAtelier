package net.firiz.renewatelier.npc;

import net.firiz.renewatelier.utils.CommonUtils;
import net.firiz.renewatelier.utils.java.CObjects;
import net.firiz.renewatelier.version.minecraft.skin.SkinProperty;
import net.firiz.renewatelier.version.packet.EntityPacket;
import net.firiz.renewatelier.version.packet.FakePlayerPacket;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.*;
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

    protected NPC createNPC() {
        final String colorName = ChatColor.translateAlternateColorCodes('&', name);
        final Location location = new Location(world, x, y, z);
        final NPC npc;
        switch (entityType) {
            case VILLAGER:
                npc = new NPC(this, EntityPacket.createVillager(
                        location,
                        colorName,
                        CObjects.nullIfFunction(villagerType, Villager.Type::valueOf, Villager.Type.PLAINS),
                        CObjects.nullIfFunction(profession, Villager.Profession::valueOf, Villager.Profession.NONE)
                ));
                break;
            case PLAYER:
                UUID uuid;
                if (skinUUID == null) {
                    uuid = null;
                } else {
                    final SkinProperty skinProperty = SkinProperty.search(skinUUID.toUpperCase());
                    if (skinProperty == null) {
                        uuid = UUID.fromString(skinUUID);
                    } else {
                        npc = new NPC(this, FakePlayerPacket.createEntityPlayer(
                                location.getWorld(),
                                location,
                                skinProperty,
                                colorName
                        ));
                        break;
                    }
                }
                npc = new NPC(this, FakePlayerPacket.createEntityPlayer(
                        location.getWorld(),
                        location,
                        uuid,
                        colorName
                ));
                break;
            default:
                npc = new NPC(this, EntityPacket.createEntity(location, colorName, entityType));
                break;
        }
        return npc;
    }

}
