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
        location.getWorld().spawnParticle(Particle.REDSTONE, location, 2, new Particle.DustOptions(Color.RED, 3));
    }

    public void hit(Location location) {
        vanillaSound(location);
        location.getWorld().spawnParticle(large ? Particle.EXPLOSION_LARGE : Particle.EXPLOSION_NORMAL, location, 1);
    }

    private void vanillaSound(Location location) {
        final float a = Randomizer.nextFloat();
        final float b = Randomizer.nextFloat();
        location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 4f, (1.0F + (a - b) * 0.2F) * 0.7F);
    }

}