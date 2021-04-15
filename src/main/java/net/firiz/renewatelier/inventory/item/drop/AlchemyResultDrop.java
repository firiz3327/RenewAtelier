package net.firiz.renewatelier.inventory.item.drop;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

/**
 *
 * @author firiz
 */
public class AlchemyResultDrop extends AnimatedDrop {
    private final World world;

    public AlchemyResultDrop(Location loc, ItemStack item) {
        super(loc, item, 1000);
        world = loc.getWorld();
        world.playSound(loc, Sound.BLOCK_PORTAL_TRAVEL, 0.1f, 1);
    }

    @Override
    protected void run() {
        if (item != null) {
            if (!item.isDead()) {
                if (tick >= 800) {
                    item.setVelocity(new Vector().setY(0.005));
                }
            } else {
                tick = 0;
            }
        } else if (tick <= 900) {
            spawn();
            item.setGravity(false);
            item.setVelocity(new Vector());
            world.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
            world.spawnParticle(Particle.EXPLOSION_LARGE, loc, 1);
            world.spawnParticle(Particle.CRIT, loc, 50);
            world.spawnParticle(Particle.CRIT_MAGIC, loc, 50);
            world.spawnParticle(Particle.CLOUD, loc, 10);
        } else if(tick >= 950) {
            world.spawnParticle(Particle.PORTAL, loc, 3);
            world.spawnParticle(Particle.ENCHANTMENT_TABLE, loc, 3);
        }
    }

    @Override
    protected void end() {
        if (!item.isDead()) {
            item.setGravity(true);
        }
    }

}
