package net.firiz.renewatelier.utils;

import org.bukkit.Location;
import org.bukkit.Particle;

public class ParticleData {
    private final Particle particle;
    private final int count;
    private final double offsetX;
    private final double offsetY;
    private final double offsetZ;
    private final double extra;
    private final Object data;

    public ParticleData(Particle particle, int count, double offsetX, double offsetY, double offsetZ, double extra, Object data) {
        this.particle = particle;
        this.count = count;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.extra = extra;
        this.data = data;
    }

    public ParticleData(Particle particle) {
        this(particle, 1);
    }

    public ParticleData(Particle particle, int count) {
        this(particle, count, null);
    }

    public ParticleData(Particle particle, int count, Object data) {
        this(particle, count, 0, 0, 0, 1, data);
    }

    public ParticleData(Particle particle, int count, double offsetX, double offsetY, double offsetZ) {
        this(particle, count, offsetX, offsetY, offsetZ, 1, null);
    }

    public ParticleData(Particle particle, int count, double offsetX, double offsetY, double offsetZ, Object data) {
        this(particle, count, offsetX, offsetY, offsetZ, 1, data);
    }

    public ParticleData(Particle particle, int count, double offsetX, double offsetY, double offsetZ, double extra) {
        this(particle, count, offsetX, offsetY, offsetZ, extra, null);
    }

    public ParticleData(Particle particle, int count, double radius) {
        this(particle, count, radius, 1, null);
    }

    public ParticleData(Particle particle, int count, double radius, Object data) {
        this(particle, count, radius, 1, data);
    }

    public ParticleData(Particle particle, int count, double radius, double extra) {
        this(particle, count, radius, extra, null);
    }

    public ParticleData(Particle particle, int count, double radius, double extra, Object data) {
        this(particle, count, radius, radius, radius, extra, data);
    }

    public void spawnParticle(Location location) {
        // forceは使わんやろたぶん
        location.getWorld().spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, extra, data, false);
    }
}
