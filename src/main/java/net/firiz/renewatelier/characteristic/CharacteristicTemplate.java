package net.firiz.renewatelier.characteristic;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIntImmutablePair;

import java.util.List;


/**
 * @author firiz
 */
public enum CharacteristicTemplate implements ICharacteristic {
    ITEM_LV1(
//            new DoubleData<>(Characteristic.SELL_DOWN_1, 10),
//            new DoubleData<>(Characteristic.SELL_DOWN_2, 5),
//            new DoubleData<>(Characteristic.SELL_DOWN_3, 1),
//            new DoubleData<>(Characteristic.SELL_UP_1, 10),
//            new DoubleData<>(Characteristic.SELL_UP_2, 5),
//            new DoubleData<>(Characteristic.SELL_UP_3, 1),
//            new DoubleData<>(Characteristic.QUALITY_1, 10),
//            new DoubleData<>(Characteristic.QUALITY_2, 5),
//            new DoubleData<>(Characteristic.QUALITY_3, 1),
//            new DoubleData<>(Characteristic.ITEM_DAMAGE_1, 10),
//            new DoubleData<>(Characteristic.ITEM_DAMAGE_2, 5),
//            new DoubleData<>(Characteristic.ITEM_DAMAGE_3, 1),
//            new DoubleData<>(Characteristic.ITEM_DAMAGE_FIXED_1, 10),
//            new DoubleData<>(Characteristic.ITEM_DAMAGE_FIXED_2, 4),
//            new DoubleData<>(Characteristic.ITEM_DAMAGE_FIXED_3, 1),
//            new DoubleData<>(Characteristic.WEAPON_ADD_DAMAGE_1, 1),
//            new DoubleData<>(Characteristic.ITEM_HEAL_1, 10),
//            new DoubleData<>(Characteristic.ITEM_HEAL_2, 5),
//            new DoubleData<>(Characteristic.ITEM_HEAL_3, 1),
//            new DoubleData<>(Characteristic.ITEM_HEAL_FIXED_1, 8),
//            new DoubleData<>(Characteristic.ITEM_HEAL_FIXED_2, 4),
//            new DoubleData<>(Characteristic.ITEM_HEAL_FIXED_3, 1),
//            new DoubleData<>(Characteristic.ITEM_CRITICAL_1, 10),
//            new DoubleData<>(Characteristic.ITEM_CRITICAL_2, 5),
//            new DoubleData<>(Characteristic.ITEM_CRITICAL_3, 1),
//            new DoubleData<>(Characteristic.ITEM_RANDOM_1, 10),
//            new DoubleData<>(Characteristic.ITEM_RANDOM_2, 5),
//            new DoubleData<>(Characteristic.ITEM_RANDOM_3, 1),
//            new DoubleData<>(Characteristic.ITEM_USE_COUNT_1, 10),
//            new DoubleData<>(Characteristic.ITEM_USE_COUNT_2, 5),
//            new DoubleData<>(Characteristic.ITEM_USE_COUNT_3, 1),
//            new DoubleData<>(Characteristic.ITEM_USE_COUNT_DOWN_1, 10),
//            new DoubleData<>(Characteristic.ITEM_USE_COUNT_DOWN_2, 5),
//            new DoubleData<>(Characteristic.ITEM_USE_COUNT_DOWN_3, 1),
//            new DoubleData<>(Characteristic.UP_HP_1, 10),
//            new DoubleData<>(Characteristic.UP_HP_2, 5),
//            new DoubleData<>(Characteristic.UP_HP_3, 1),
//            new DoubleData<>(Characteristic.UP_MP_1, 10),
//            new DoubleData<>(Characteristic.UP_MP_2, 5),
//            new DoubleData<>(Characteristic.UP_MP_3, 1),
//            new DoubleData<>(Characteristic.UP_HP_MP_1, 3),
//            new DoubleData<>(Characteristic.UP_HP_MP_2, 1),
//            new DoubleData<>(Characteristic.UP_PARAMETER_1, 5),
//            new DoubleData<>(Characteristic.UP_PARAMETER_2, 3),
//            new DoubleData<>(Characteristic.UP_PARAMETER_3, 1),
//            new DoubleData<>(Characteristic.UP_ATTACK_1, 10),
//            new DoubleData<>(Characteristic.UP_ATTACK_2, 5),
//            new DoubleData<>(Characteristic.UP_ATTACK_3, 1),
//            new DoubleData<>(Characteristic.UP_DEFENSE_1, 10),
//            new DoubleData<>(Characteristic.UP_DEFENSE_2, 5),
//            new DoubleData<>(Characteristic.UP_DEFENSE_3, 1),
//            new DoubleData<>(Characteristic.UP_SPEED_1, 10),
//            new DoubleData<>(Characteristic.UP_SPEED_2, 5),
//            new DoubleData<>(Characteristic.UP_SPEED_3, 1),
//            new DoubleData<>(Characteristic.UP_ATTACK_DEFENSE_1, 3),
//            new DoubleData<>(Characteristic.UP_ATTACK_SPEED_1, 3),
//            new DoubleData<>(Characteristic.UP_DEFENSE_SPEED_1, 3),
//            new DoubleData<>(Characteristic.UP_ALL_1, 3),
//            new DoubleData<>(Characteristic.UP_ALL_2, 1),
//            new DoubleData<>(Characteristic.DOWN_CONSUME_MP_1, 3),
//            new DoubleData<>(Characteristic.DOWN_CONSUME_MP_2, 1),
//            new DoubleData<>(Characteristic.UP_SKILL_1, 10),
//            new DoubleData<>(Characteristic.UP_SKILL_2, 5),
//            new DoubleData<>(Characteristic.UP_SKILL_3, 1),
//            new DoubleData<>(Characteristic.UP_SKILL_DOWN_MP_1, 1)
    );

    @SafeVarargs
    CharacteristicTemplate(ObjectIntImmutablePair<String>... cs) {
        this.cs = cs;
    }

    private List<ObjectIntImmutablePair<Characteristic>> getCs() {
        if (loaded == null) {
            loaded = new ObjectArrayList<>(cs.length);
            for (ObjectIntImmutablePair<String> fdd : cs) {
                loaded.add(new ObjectIntImmutablePair<>(Characteristic.getCharacteristic(fdd.left()), fdd.rightInt()));
            }
        }
        return loaded;
    }

    private final ObjectIntImmutablePair<String>[] cs;
    private List<ObjectIntImmutablePair<Characteristic>> loaded;

    @Override
    public void add(List<ObjectIntImmutablePair<Characteristic>> cs) {
        cs.addAll(getCs());
    }
}
