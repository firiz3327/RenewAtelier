package net.firiz.renewatelier.skill.effect;

import net.firiz.renewatelier.utils.Randomizer;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;

public class BombEffect implements IEffect {

    private final boolean large;

    public BombEffect(boolean large) {
        this.large = large;
    }

    @Override
    public void effect(Location location) {
        if (Randomizer.nextBoolean()) {
            location.getWorld().spawnParticle(Particle.REDSTONE, location, 2, new Particle.DustOptions(Color.RED, 3));
        }
    }

    public void hit(Location location) {
        vanillaSound(location);
        final Particle particle;
        final int i;
        final double range;
        if (large) {
            particle = Particle.EXPLOSION_HUGE;
            i = 1;
            range = 0.5;
        } else {
            particle = Particle.EXPLOSION_LARGE;
            i = 3;
            range = 0.2;
        }
        location.getWorld().spawnParticle(particle, location, i, range, range, range);
    }

    private void vanillaSound(Location location) {
        final float a = Randomizer.nextFloat();
        final float b = Randomizer.nextFloat();
        location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 4f, (1.0F + (a - b) * 0.2F) * 0.7F);
    }

}
