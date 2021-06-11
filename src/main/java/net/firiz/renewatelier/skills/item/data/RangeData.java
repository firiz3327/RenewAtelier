package net.firiz.renewatelier.skills.item.data;

import net.firiz.renewatelier.damage.AttackAttribute;
import net.firiz.renewatelier.skills.effect.RangeSkillEffect;

public class RangeData implements ItemSkillData {

    private final RangeSkillEffect effect;
    private final double radius;
    private final float height;
    private final int mobCount;
    private final boolean heal;
    private final AttackAttribute attackAttribute;

    public RangeData(RangeSkillEffect effect, double radius, float height, int mobCount, AttackAttribute attackAttribute) {
        this.effect = effect;
        this.radius = radius;
        this.height = height;
        this.mobCount = mobCount;
        this.heal = attackAttribute == AttackAttribute.HEAL;
        this.attackAttribute = attackAttribute;
    }

    public RangeSkillEffect getEffect() {
        return effect;
    }

    public float getHeight() {
        return height;
    }

    public double getRadius() {
        return radius;
    }

    public int getMobCount() {
        return mobCount;
    }

    public boolean isHeal() {
        return heal;
    }

    public AttackAttribute getAttackAttribute() {
        return attackAttribute;
    }
}

