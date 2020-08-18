package net.firiz.renewatelier.skill.effect;

import net.firiz.renewatelier.utils.ParticleData;
import org.bukkit.Location;

import java.util.Arrays;
import java.util.List;

public class RangeEffect implements IEffect {

    final List<ParticleData> particleDataList;

    public RangeEffect(ParticleData... particleData) {
        particleDataList = Arrays.asList(particleData);
    }

    @Override
    public void effect(Location location) {
        particleDataList.get(0).spawnParticle(location);
    }

    public void hit(Location location) {
        if (particleDataList.size() >= 2) {
            particleDataList.get(1).spawnParticle(location);
        }
    }

}
