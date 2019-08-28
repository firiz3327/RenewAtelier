package net.firiz.renewatelier.script.template.balls;

import org.bukkit.Location;
import org.bukkit.Particle;

public class STBallEffect {

    private final String id;
    private final int count;

    public STBallEffect(String id, int count) {
        this.id = id;
        this.count = count;
    }

    public void effect(Location loc) {
        loc.getWorld().spawnParticle(Particle.valueOf(id), loc, count);
    }

}
