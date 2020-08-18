package net.firiz.renewatelier.skill.item.data;

import net.firiz.renewatelier.damage.AttackAttribute;
import net.firiz.renewatelier.skill.effect.BombEffect;

public class BombData implements ItemSkillData {

    private final BombEffect effect;
    private final double radius;
    private final AttackAttribute attackAttribute;

    public BombData(BombEffect effect, double radius, AttackAttribute attackAttribute) {
        this.effect = effect;
        this.radius = radius;
        this.attackAttribute = attackAttribute;
    }

    public BombEffect getEffect() {
        return effect;
    }

    public double getRadius() {
        return radius;
    }

    public AttackAttribute getAttackAttribute() {
        return attackAttribute;
    }
}
