package net.firiz.renewatelier.characteristic.datas;

import net.firiz.renewatelier.damage.AttackAttribute;
import org.jetbrains.annotations.Nullable;

public class ChResistanceAttribute implements ChData {

    private final boolean isPhysics;
    @Nullable
    private final AttackAttribute attackAttribute;
    private final int value;

    private ChResistanceAttribute(boolean isPhysics, @Nullable AttackAttribute attackAttribute, int value) {
        this.isPhysics = isPhysics;
        this.attackAttribute = attackAttribute;
        this.value = value;
    }

    public static ChData newInstance(String[] args) {
        final String value = args[0];
        if (value.equalsIgnoreCase("physics")) {
            return new ChResistanceAttribute(true, null, Integer.parseInt(args[1]));
        }
        return new ChResistanceAttribute(false, AttackAttribute.valueOf(value), Integer.parseInt(args[1]));
    }

    public boolean isPhysics() {
        return isPhysics;
    }

    @Nullable
    public AttackAttribute getAttackAttribute() {
        return attackAttribute;
    }

    public int getValue() {
        return value;
    }
}
