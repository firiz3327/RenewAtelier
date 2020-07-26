package net.firiz.renewatelier.version.entity.projectile.arrow;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public interface IBombProjectile {

    Location getLocation();

    void setLocation(Location location);

    void setVelocity(Vector velocity);

    Vector getVelocity();

    boolean isGround();

    void remove();

}
