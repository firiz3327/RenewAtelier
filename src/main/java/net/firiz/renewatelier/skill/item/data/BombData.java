package net.firiz.renewatelier.skill.item.data;

import net.firiz.renewatelier.damage.AttackAttribute;
import net.firiz.renewatelier.skill.effect.BombSkillEffect;

public class BombData implements ItemSkillData {

    private final BombSkillEffect effect;
    private final double radius;
    private final int mobCount;
    private final AttackAttribute attackAttribute;

    public BombData(BombSkillEffect effect, double radius, int mobCount, AttackAttribute attackAttribute) {
        this.effect = effect;
        this.radius = radius;
        this.mobCount = mobCount;
        this.attackAttribute = attackAttribute;
    }

    public BombSkillEffect getEffect() {
        return effect;
    }

    public double getRadius() {
        return radius;
    }

    public int getMobCount() {
        return mobCount;
    }

    public AttackAttribute getAttackAttribute() {
        return attackAttribute;
    }
}
