package net.firiz.renewatelier.skill.item.data;

import net.firiz.renewatelier.damage.AttackAttribute;
import net.firiz.renewatelier.skill.effect.RangeEffect;
import org.jetbrains.annotations.Nullable;

public class RangeData implements ItemSkillData {

    private final RangeEffect effect;
    private final double radius;
    private final float height;
    private final int mobCount;
    private final boolean heal;

    @Nullable
    private final AttackAttribute attackAttribute;

    public RangeData(RangeEffect effect, double radius, float height, int mobCount) {
        this.effect = effect;
        this.radius = radius;
        this.height = height;
        this.mobCount = mobCount;
        this.heal = true;
        this.attackAttribute = null;
    }

    public RangeData(RangeEffect effect, double radius, float height, int mobCount, AttackAttribute attackAttribute) {
        this.effect = effect;
        this.radius = radius;
        this.height = height;
        this.mobCount = mobCount;
        this.heal = false;
        this.attackAttribute = attackAttribute;
    }

    public RangeEffect getEffect() {
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

    @Nullable
    public AttackAttribute getAttackAttribute() {
        return attackAttribute;
    }
}

