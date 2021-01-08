package net.firiz.renewatelier.skill.item.data;

import net.firiz.renewatelier.damage.AttackAttribute;
import net.firiz.renewatelier.skill.effect.ISkillEffect;

public class InteractData implements ItemSkillData {

    private final ISkillEffect effect;
    private final boolean heal;
    private final AttackAttribute attackAttribute;
    private final Type type;

    public InteractData(ISkillEffect effect, AttackAttribute attackAttribute, Type type) {
        this.effect = effect;
        this.heal = attackAttribute == AttackAttribute.HEAL;
        this.attackAttribute = attackAttribute;
        this.type = type;
    }

    public ISkillEffect getEffect() {
        return effect;
    }

    public boolean isHeal() {
        return heal;
    }

    public AttackAttribute getAttackAttribute() {
        return attackAttribute;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        INTERACT,
        INTERACT_ENTITY,
        TARGET_ENTITY,
        INTERACT_PLAYER,
        TARGET_PLAYER
    }

}
