package net.firiz.renewatelier.skill.data;

import net.firiz.renewatelier.damage.AttackAttribute;
import net.firiz.renewatelier.skill.effect.BombEffect;

public class BombData implements ItemSkillData {
    private final BombEffect effect;
    private final float height;
    private final float particles;
    private final double radius;
    private final AttackAttribute attackAttribute;

    public BombData(BombEffect effect, float height, float particles, double radius, AttackAttribute attackAttribute) {
        this.effect = effect;
        this.height = height;
        this.particles = particles;
        this.radius = radius;
        this.attackAttribute = attackAttribute;
    }

    public BombEffect getEffect() {
        return effect;
    }

    public float getHeight() {
        return height;
    }

    public float getParticles() {
        return particles;
    }

    public double getRadius() {
        return radius;
    }

    public AttackAttribute getAttackAttribute() {
        return attackAttribute;
    }
}
