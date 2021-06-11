package net.firiz.renewatelier.version.entity.projectile.arrow.skill.item;

import net.firiz.renewatelier.version.entity.projectile.arrow.skill.ISkillProjectile;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public interface IBombProjectile extends ISkillProjectile {

    Location getLocation();

    void setLocation(Location location);

    void setVelocity(Vector velocity);

    Vector getVelocity();

}
