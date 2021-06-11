package net.firiz.renewatelier.version.entity.projectile.arrow.skill;

import net.firiz.renewatelier.entity.player.Char;
import org.bukkit.Location;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.util.Vector;

public interface ISkillProjectile {

    default SkillProjectile spawn(Location location) {
        return spawn(location, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    SkillProjectile spawn(Location location, CreatureSpawnEvent.SpawnReason reason);

    void setLocation(Location location);

    Location getLocation();

    void setVelocity(Vector velocity);

    Vector getVelocity();

    void remove();

    Char getPlayer();

}
