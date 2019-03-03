/*
 * CharacteristicTemplate.java
 * 
 * Copyright (c) 2018 firiz.
 * 
 * This file is part of Expression program is undefined on line 6, column 40 in Templates/Licenses/license-licence-gplv3.txt..
 * 
 * Expression program is undefined on line 8, column 19 in Templates/Licenses/license-licence-gplv3.txt. is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Expression program is undefined on line 13, column 19 in Templates/Licenses/license-licence-gplv3.txt. is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Expression program is undefined on line 19, column 30 in Templates/Licenses/license-licence-gplv3.txt..  If not, see <http ://www.gnu.org/licenses/>.
 */

package jp.gr.java_conf.zakuramomiji.renewatelier.characteristic;

import jp.gr.java_conf.zakuramomiji.renewatelier.utils.DoubleData;


/**
 *
 * @author firiz
 */
public enum CharacteristicTemplate {
    ITEM_LV1(
            new DoubleData<>(Characteristic.SELL_DOWN_1, 10),
            new DoubleData<>(Characteristic.SELL_DOWN_2, 5),
            new DoubleData<>(Characteristic.SELL_DOWN_3, 1),
            new DoubleData<>(Characteristic.SELL_UP_1, 10),
            new DoubleData<>(Characteristic.SELL_UP_2, 5),
            new DoubleData<>(Characteristic.SELL_UP_3, 1),
            new DoubleData<>(Characteristic.QUALITY_1, 10),
            new DoubleData<>(Characteristic.QUALITY_2, 5),
            new DoubleData<>(Characteristic.QUALITY_3, 1),
            new DoubleData<>(Characteristic.ITEM_DAMAGE_1, 10),
            new DoubleData<>(Characteristic.ITEM_DAMAGE_2, 5),
            new DoubleData<>(Characteristic.ITEM_DAMAGE_3, 1),
            new DoubleData<>(Characteristic.ITEM_DAMAGE_FIXED_1, 10),
            new DoubleData<>(Characteristic.ITEM_DAMAGE_FIXED_2, 4),
            new DoubleData<>(Characteristic.ITEM_DAMAGE_FIXED_3, 1),
            new DoubleData<>(Characteristic.WEAPON_ADD_DAMAGE_1, 1),
            new DoubleData<>(Characteristic.ITEM_HEAL_1, 10),
            new DoubleData<>(Characteristic.ITEM_HEAL_2, 5),
            new DoubleData<>(Characteristic.ITEM_HEAL_3, 1),
            new DoubleData<>(Characteristic.ITEM_HEAL_FIXED_1, 8),
            new DoubleData<>(Characteristic.ITEM_HEAL_FIXED_2, 4),
            new DoubleData<>(Characteristic.ITEM_HEAL_FIXED_3, 1),
            new DoubleData<>(Characteristic.ITEM_CRITICAL_1, 10),
            new DoubleData<>(Characteristic.ITEM_CRITICAL_2, 5),
            new DoubleData<>(Characteristic.ITEM_CRITICAL_3, 1),
            new DoubleData<>(Characteristic.ITEM_RANDOM_1, 10),
            new DoubleData<>(Characteristic.ITEM_RANDOM_2, 5),
            new DoubleData<>(Characteristic.ITEM_RANDOM_3, 1),
            new DoubleData<>(Characteristic.ITEM_USE_COUNT_1, 10),
            new DoubleData<>(Characteristic.ITEM_USE_COUNT_2, 5),
            new DoubleData<>(Characteristic.ITEM_USE_COUNT_3, 1),
            new DoubleData<>(Characteristic.ITEM_USE_COUNT_DOWN_1, 10),
            new DoubleData<>(Characteristic.ITEM_USE_COUNT_DOWN_2, 5),
            new DoubleData<>(Characteristic.ITEM_USE_COUNT_DOWN_3, 1),
            new DoubleData<>(Characteristic.UP_HP_1, 10),
            new DoubleData<>(Characteristic.UP_HP_2, 5),
            new DoubleData<>(Characteristic.UP_HP_3, 1),
            new DoubleData<>(Characteristic.UP_MP_1, 10),
            new DoubleData<>(Characteristic.UP_MP_2, 5),
            new DoubleData<>(Characteristic.UP_MP_3, 1),
            new DoubleData<>(Characteristic.UP_HP_MP_1, 3),
            new DoubleData<>(Characteristic.UP_HP_MP_2, 1),
            new DoubleData<>(Characteristic.UP_PARAMETER_1, 5),
            new DoubleData<>(Characteristic.UP_PARAMETER_2, 3),
            new DoubleData<>(Characteristic.UP_PARAMETER_3, 1),
            new DoubleData<>(Characteristic.UP_ATTACK_1, 10),
            new DoubleData<>(Characteristic.UP_ATTACK_2, 5),
            new DoubleData<>(Characteristic.UP_ATTACK_3, 1),
            new DoubleData<>(Characteristic.UP_DEFENSE_1, 10),
            new DoubleData<>(Characteristic.UP_DEFENSE_2, 5),
            new DoubleData<>(Characteristic.UP_DEFENSE_3, 1),
            new DoubleData<>(Characteristic.UP_SPEED_1, 10),
            new DoubleData<>(Characteristic.UP_SPEED_2, 5),
            new DoubleData<>(Characteristic.UP_SPEED_3, 1),
            new DoubleData<>(Characteristic.UP_ATTACK_DEFENSE_1, 3),
            new DoubleData<>(Characteristic.UP_ATTACK_SPEED_1, 3),
            new DoubleData<>(Characteristic.UP_DEFENSE_SPEED_1, 3),
            new DoubleData<>(Characteristic.UP_ALL_1, 3),
            new DoubleData<>(Characteristic.UP_ALL_2, 1),
            new DoubleData<>(Characteristic.DOWN_CONSUME_MP_1, 3),
            new DoubleData<>(Characteristic.DOWN_CONSUME_MP_2, 1),
            new DoubleData<>(Characteristic.UP_SKILL_1, 10),
            new DoubleData<>(Characteristic.UP_SKILL_2, 5),
            new DoubleData<>(Characteristic.UP_SKILL_3, 1),
            new DoubleData<>(Characteristic.UP_SKILL_DOWN_MP_1, 1)
    )
    ;
        
    CharacteristicTemplate(DoubleData<Characteristic, Integer>... cs) {
        this.cs = cs;
    }

    public DoubleData<Characteristic, Integer>[] getCs() {
        return cs;
    }
    
    private final DoubleData<Characteristic, Integer>[] cs;
}
