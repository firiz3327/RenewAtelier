package net.firiz.renewatelier.version.entity.living.horse;

import com.google.common.collect.Maps;
import net.firiz.renewatelier.version.VersionUtils;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_15_R1.util.CraftVector;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class CaneHorse extends EntityHorseMule {

    private final Player player;
    private final EntityPlayer entityPlayer;

    public CaneHorse(org.bukkit.World world, org.bukkit.entity.Player player) {
        super(EntityTypes.MULE, ((CraftWorld) world).getHandle());
        this.player = player;
        this.entityPlayer = ((CraftPlayer) player).getHandle();
        this.setLocation(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), 0, 0);
        this.setOwnerUUID(player.getUniqueId());
        this.setTamed(true);
        this.setInvulnerable(true);
        this.setSilent(true);
        this.inventoryChest.setItem(0, new ItemStack(Items.SADDLE));
        this.world.addEntity(this);
        entityPlayer.startRiding(this);
    }

    @Override
    public void tick() {
        VersionUtils.superInvoke("tick", this, EntityInsentient.class, void.class, new HashMap<>());
        if (!isInWater() && player.isOnline() && getPassengers().contains(entityPlayer)) {
            if (onGround) {
                setNoGravity(false);
            } else {
                setNoGravity(true);
                final Vector direction = player.getLocation().getDirection();
                final Vector unitVector = new Vector(
                        direction.getX(),
                        direction.getY(),
                        direction.getZ()
                ).normalize();

                final Vec3D vec3d = CraftVector.toNMS(unitVector.multiply(0.7));
                setMot(vec3d);
                float f2 = MathHelper.sqrt(b(vec3d));
                yaw = (float) (MathHelper.d(vec3d.x, vec3d.z) * 57.2957763671875D);
                pitch = (float) (MathHelper.d(vec3d.y, f2) * 57.2957763671875D);
                lastYaw = yaw;
                lastPitch = pitch;
            }
        } else {
            die();
        }
    }

}
