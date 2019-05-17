/*
 * Characteristic.java
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

package net.firiz.renewatelier.characteristic;

import java.util.Arrays;
import java.util.List;
import net.firiz.renewatelier.utils.DoubleData;

/**
 *
 * @author firiz
 */
public enum Characteristic {
    SELL_DOWN_1(
            "安値",
            "お店の買い取り価格が少し下がる。",
            new CharacteristicCategory[]{CharacteristicCategory.ALL},
            null,
            new DoubleData<>(CharacteristicType.SELL_DOWN, 10)
    ),
    SELL_DOWN_2(
            "安値+",
            "お店の買い取り価格が下がる。",
            new CharacteristicCategory[]{CharacteristicCategory.ALL},
            null,
            new DoubleData<>(CharacteristicType.SELL_DOWN, 20)
    ),
    SELL_DOWN_3(
            "安値++",
            "お店の買い取り価格がかなり下がる。",
            new CharacteristicCategory[]{CharacteristicCategory.ALL},
            null,
            new DoubleData<>(CharacteristicType.SELL_DOWN, 30)
    ),
    SELL_DOWN_4(
            "量産品",
            "お店の買い取り価格がかなり下がる。",
            new CharacteristicCategory[]{CharacteristicCategory.ALL},
            new DoubleData<>(Characteristic.SELL_DOWN_1, Characteristic.SELL_DOWN_2),
            new DoubleData<>(CharacteristicType.SELL_DOWN, 30)
    ),
    SELL_DOWN_5(
            "大量生産品",
            "お店の買い取り価格が大きく下がる。",
            new CharacteristicCategory[]{CharacteristicCategory.ALL},
            new DoubleData<>(Characteristic.SELL_DOWN_2, Characteristic.SELL_DOWN_3),
            new DoubleData<>(CharacteristicType.SELL_DOWN, 40)
    ),
    SELL_DOWN_6(
            "プライスレス",
            "お店の買い取り価格が激減する。",
            new CharacteristicCategory[]{CharacteristicCategory.ALL},
            new DoubleData<>(Characteristic.SELL_DOWN_4, Characteristic.SELL_DOWN_5),
            new DoubleData<>(CharacteristicType.SELL_DOWN, 50)
    ),
    SELL_UP_1(
            "高値",
            "お店の買い取り価格が少し上がる。",
            new CharacteristicCategory[]{CharacteristicCategory.ALL},
            null,
            new DoubleData<>(CharacteristicType.SELL_UP, 10)
    ),
    SELL_UP_2(
            "高値+",
            "お店の買い取り価格が上がる。",
            new CharacteristicCategory[]{CharacteristicCategory.ALL},
            null,
            new DoubleData<>(CharacteristicType.SELL_UP, 20)
    ),
    SELL_UP_3(
            "高値++",
            "お店の買い取り価格がかなり上がる。",
            new CharacteristicCategory[]{CharacteristicCategory.ALL},
            null,
            new DoubleData<>(CharacteristicType.SELL_UP, 30)
    ),
    SELL_UP_4(
            "高級品",
            "お店の買い取り価格がかなり上がる。",
            new CharacteristicCategory[]{CharacteristicCategory.ALL},
            new DoubleData<>(Characteristic.SELL_UP_1, Characteristic.SELL_UP_2),
            new DoubleData<>(CharacteristicType.SELL_UP, 30)
    ),
    SELL_UP_5(
            "希少な逸品",
            "お店の買い取り価格が大幅に上がる。",
            new CharacteristicCategory[]{CharacteristicCategory.ALL},
            new DoubleData<>(Characteristic.SELL_UP_2, Characteristic.SELL_UP_3),
            new DoubleData<>(CharacteristicType.SELL_UP, 40)
    ),
    SELL_UP_6(
            "プレミア価格",
            "お店の買い取り価格がはね上がる。",
            new CharacteristicCategory[]{CharacteristicCategory.ALL},
            new DoubleData<>(Characteristic.SELL_UP_4, Characteristic.SELL_UP_5),
            new DoubleData<>(CharacteristicType.SELL_UP, 50)
    ),
    QUALITY_1(
            "品質上昇",
            "特性引継ぎ時に選択するとアイテムの品質を10%上昇させる。",
            new CharacteristicCategory[]{CharacteristicCategory.ALL},
            null,
            new DoubleData<>(CharacteristicType.QUALITY, 10)
    ),
    QUALITY_2(
            "品質上昇+",
            "特性引継ぎ時に選択するとアイテムの品質を15%上昇させる。",
            new CharacteristicCategory[]{CharacteristicCategory.ALL},
            null,
            new DoubleData<>(CharacteristicType.QUALITY, 15)
    ),
    QUALITY_3(
            "品質上昇++",
            "特性引継ぎ時に選択するとアイテムの品質を25%上昇させる。",
            new CharacteristicCategory[]{CharacteristicCategory.ALL},
            null,
            new DoubleData<>(CharacteristicType.QUALITY, 25)
    ),
    QUALITY_4(
            "出来が良い",
            "特性引継ぎ時に選択するとアイテムの品質を20%上昇させる。",
            new CharacteristicCategory[]{CharacteristicCategory.ALL},
            new DoubleData<>(Characteristic.QUALITY_1, Characteristic.QUALITY_2),
            new DoubleData<>(CharacteristicType.QUALITY, 20)
    ),
    QUALITY_5(
            "プロの完成度",
            "特性引継ぎ時に選択するとアイテムの品質を35%上昇させる。",
            new CharacteristicCategory[]{CharacteristicCategory.ALL},
            new DoubleData<>(Characteristic.QUALITY_2, Characteristic.QUALITY_3),
            new DoubleData<>(CharacteristicType.QUALITY, 35)
    ),
    QUALITY_6(
            "超クオリティ",
            "特性引継ぎ時に選択するとアイテムの品質を50%上昇させる。",
            new CharacteristicCategory[]{CharacteristicCategory.ALL},
            new DoubleData<>(Characteristic.QUALITY_4, Characteristic.QUALITY_5),
            new DoubleData<>(CharacteristicType.QUALITY, 50)
    ),
    ITEM_DAMAGE_1(
            "破壊力増加",
            "攻撃アイテムの効果が10%上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.DAMAGE},
            null,
            new DoubleData<>(CharacteristicType.ITEM_ATTACK, 10)
    ),
    ITEM_DAMAGE_2(
            "破壊力増加+",
            "攻撃アイテムの効果が12%上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.DAMAGE},
            null,
            new DoubleData<>(CharacteristicType.ITEM_ATTACK, 12)
    ),
    ITEM_DAMAGE_3(
            "破壊力増加++",
            "攻撃アイテムの効果が15%上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.DAMAGE},
            null,
            new DoubleData<>(CharacteristicType.ITEM_ATTACK, 15)
    ),
    ITEM_DAMAGE_4(
            "大きな破壊力",
            "攻撃アイテムの効果が17%上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.DAMAGE},
            new DoubleData<>(Characteristic.ITEM_DAMAGE_1, Characteristic.ITEM_DAMAGE_2),
            new DoubleData<>(CharacteristicType.ITEM_ATTACK, 17)
    ),
    ITEM_DAMAGE_5(
            "強烈な破壊力",
            "攻撃アイテムの効果が22%上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.DAMAGE},
            new DoubleData<>(Characteristic.ITEM_DAMAGE_2, Characteristic.ITEM_DAMAGE_3),
            new DoubleData<>(CharacteristicType.ITEM_ATTACK, 22)
    ),
    ITEM_DAMAGE_6(
            "究極の破壊力",
            "攻撃アイテムの効果が17%上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.DAMAGE},
            new DoubleData<>(Characteristic.ITEM_DAMAGE_4, Characteristic.ITEM_DAMAGE_5),
            new DoubleData<>(CharacteristicType.ITEM_ATTACK, 30)
    ),
    ITEM_DAMAGE_FIXED_1(
            "威力固定強化",
            "攻撃アイテムが、固定値でやや強化される。威力が弱いほど強くなる。",
            new CharacteristicCategory[]{CharacteristicCategory.DAMAGE},
            null,
            new DoubleData<>(CharacteristicType.ITEM_ATTACK_FIXED, new DoubleData<>(10, 20))
    ),
    ITEM_DAMAGE_FIXED_2(
            "威力固定強化+",
            "攻撃アイテムが、固定値で強化される。威力が弱いほど強くなる。",
            new CharacteristicCategory[]{CharacteristicCategory.DAMAGE},
            null,
            new DoubleData<>(CharacteristicType.ITEM_ATTACK_FIXED, new DoubleData<>(20, 30))
    ),
    ITEM_DAMAGE_FIXED_3(
            "威力固定強化++",
            "攻撃アイテムが、固定値で大きく強化される。威力が弱いほど強くなる。",
            new CharacteristicCategory[]{CharacteristicCategory.DAMAGE},
            null,
            new DoubleData<>(CharacteristicType.ITEM_ATTACK_FIXED, new DoubleData<>(30, 40))
    ),
    ITEM_DAMAGE_FIXED_4(
            "実数で痛くなる",
            "アイテムが固定値で強化される。威力が弱いほど強くなる。",
            new CharacteristicCategory[]{CharacteristicCategory.DAMAGE},
            new DoubleData<>(Characteristic.ITEM_DAMAGE_FIXED_1, Characteristic.ITEM_DAMAGE_2),
            new DoubleData<>(CharacteristicType.ITEM_ATTACK_FIXED, new DoubleData<>(30, 40))
    ),
    ITEM_DAMAGE_FIXED_5(
            "実数でダメージ増加",
            "アイテムが固定値で大きく強化される。威力が弱いほど強くなる。",
            new CharacteristicCategory[]{CharacteristicCategory.DAMAGE},
            new DoubleData<>(Characteristic.ITEM_DAMAGE_FIXED_2, Characteristic.ITEM_DAMAGE_3),
            new DoubleData<>(CharacteristicType.ITEM_ATTACK_FIXED, new DoubleData<>(40, 60))
    ),
    ITEM_DAMAGE_FIXED_6(
            "実数で大ダメージ",
            "アイテム固定値で大幅に強化される。威力が弱いほど強くなる。",
            new CharacteristicCategory[]{CharacteristicCategory.DAMAGE},
            new DoubleData<>(Characteristic.ITEM_DAMAGE_FIXED_4, Characteristic.ITEM_DAMAGE_5),
            new DoubleData<>(CharacteristicType.ITEM_ATTACK_FIXED, new DoubleData<>(70, 90))
    ),
    WEAPON_ADD_DAMAGE_1(
            "鋭さが増す",
            "攻撃に15%の追加ダメージが発生。",
            new CharacteristicCategory[]{CharacteristicCategory.WEAPON},
            null,
            new DoubleData<>(CharacteristicType.WEAPON_ADD_DAMAGE, 15)
    ),
    WEAPON_ADD_DAMAGE_2(
            "切れ味が増す",
            "攻撃に20%の追加ダメージが発生。",
            new CharacteristicCategory[]{CharacteristicCategory.WEAPON},
            null,
            new DoubleData<>(CharacteristicType.WEAPON_ADD_DAMAGE, 20)
    ),
    WEAPON_ADD_DAMAGE_3(
            "攻撃性能が増す",
            "攻撃に30%の追加ダメージが発生。",
            new CharacteristicCategory[]{CharacteristicCategory.WEAPON},
            null,
            new DoubleData<>(CharacteristicType.WEAPON_ADD_DAMAGE, 30)
    ),
    ITEM_HEAL_1(
            "回復力増加",
            "回復アイテムの効果が10%上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.HEAL},
            null,
            new DoubleData<>(CharacteristicType.ITEM_HEAL, 10)
    ),
    ITEM_HEAL_2(
            "回復力増加+",
            "回復アイテムの効果が12%上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.HEAL},
            null,
            new DoubleData<>(CharacteristicType.ITEM_HEAL, 12)
    ),
    ITEM_HEAL_3(
            "回復力増加++",
            "回復アイテムの効果が15%上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.HEAL},
            null,
            new DoubleData<>(CharacteristicType.ITEM_HEAL, 15)
    ),
    ITEM_HEAL_4(
            "大きな回復力",
            "回復アイテムの効果が17%上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.HEAL},
            new DoubleData<>(Characteristic.ITEM_HEAL_1, Characteristic.ITEM_HEAL_2),
            new DoubleData<>(CharacteristicType.ITEM_HEAL, 17)
    ),
    ITEM_HEAL_5(
            "強烈な回復力",
            "回復アイテムの効果が22%上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.HEAL},
            new DoubleData<>(Characteristic.ITEM_HEAL_2, Characteristic.ITEM_HEAL_3),
            new DoubleData<>(CharacteristicType.ITEM_HEAL, 22)
    ),
    ITEM_HEAL_6(
            "究極の回復力",
            "回復アイテムの効果が17%上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.HEAL},
            new DoubleData<>(Characteristic.ITEM_HEAL_4, Characteristic.ITEM_HEAL_5),
            new DoubleData<>(CharacteristicType.ITEM_HEAL, 30)
    ),
    ITEM_HEAL_FIXED_1(
            "回復固定強化",
            "回復アイテムが、固定値でやや強化される。元の威力が弱いほど効果は高い。",
            new CharacteristicCategory[]{CharacteristicCategory.HEAL},
            null,
            new DoubleData<>(CharacteristicType.ITEM_HEAL_FIXED, new DoubleData<>(10, 20))
    ),
    ITEM_HEAL_FIXED_2(
            "回復固定強化+",
            "回復アイテムが、固定値で強化される。元の威力が弱いほど効果は高い。",
            new CharacteristicCategory[]{CharacteristicCategory.HEAL},
            null,
            new DoubleData<>(CharacteristicType.ITEM_HEAL_FIXED, new DoubleData<>(20, 30))
    ),
    ITEM_HEAL_FIXED_3(
            "回復固定強化++",
            "回復アイテムが、固定値で大きく強化される。元の威力が弱いほど効果は高い。",
            new CharacteristicCategory[]{CharacteristicCategory.HEAL},
            null,
            new DoubleData<>(CharacteristicType.ITEM_HEAL_FIXED, new DoubleData<>(30, 40))
    ),
    ITEM_HEAL_FIXED_4(
            "薬を強化",
            "アイテムが固定値で強化される。元の威力が弱いほど効果は高い。",
            new CharacteristicCategory[]{CharacteristicCategory.HEAL},
            new DoubleData<>(Characteristic.ITEM_HEAL_FIXED_1, Characteristic.ITEM_HEAL_2),
            new DoubleData<>(CharacteristicType.ITEM_HEAL_FIXED, new DoubleData<>(30, 40))
    ),
    ITEM_HEAL_FIXED_5(
            "薬を大きく強化",
            "アイテムが固定値で大きく強化される。元の威力が弱いほど効果は高い。",
            new CharacteristicCategory[]{CharacteristicCategory.HEAL},
            new DoubleData<>(Characteristic.ITEM_HEAL_FIXED_2, Characteristic.ITEM_HEAL_3),
            new DoubleData<>(CharacteristicType.ITEM_HEAL_FIXED, new DoubleData<>(40, 60))
    ),
    ITEM_HEAL_FIXED_6(
            "回復固定超強化",
            "アイテム固定値で大幅に強化される。元の威力が弱いほど効果は高い。",
            new CharacteristicCategory[]{CharacteristicCategory.HEAL},
            new DoubleData<>(Characteristic.ITEM_HEAL_FIXED_4, Characteristic.ITEM_HEAL_5),
            new DoubleData<>(CharacteristicType.ITEM_HEAL_FIXED, new DoubleData<>(70, 90))
    ),
    ITEM_CRITICAL_1(
            "クリティカル",
            "アイテムが10%の確率でクリティカルする。",
            new CharacteristicCategory[]{CharacteristicCategory.DAMAGE, CharacteristicCategory.HEAL},
            null,
            new DoubleData<>(CharacteristicType.ITEM_CRITICAL, 10)
    ),
    ITEM_CRITICAL_2(
            "クリティカル+",
            "アイテムが20%の確率でクリティカルする。",
            new CharacteristicCategory[]{CharacteristicCategory.DAMAGE, CharacteristicCategory.HEAL},
            null,
            new DoubleData<>(CharacteristicType.ITEM_CRITICAL, 20)
    ),
    ITEM_CRITICAL_3(
            "クリティカル++",
            "アイテムが30%の確率でクリティカルする。",
            new CharacteristicCategory[]{CharacteristicCategory.DAMAGE, CharacteristicCategory.HEAL},
            null,
            new DoubleData<>(CharacteristicType.ITEM_CRITICAL, 30)
    ),
    ITEM_CRITICAL_4(
            "会心の出来",
            "アイテムが25%の確率でクリティカルする。",
            new CharacteristicCategory[]{CharacteristicCategory.DAMAGE, CharacteristicCategory.HEAL},
            new DoubleData<>(Characteristic.ITEM_CRITICAL_1, Characteristic.ITEM_CRITICAL_2),
            new DoubleData<>(CharacteristicType.ITEM_CRITICAL, 25)
    ),
    ITEM_CRITICAL_5(
            "半分クリティカル",
            "アイテムが50%の確率でクリティカルする。",
            new CharacteristicCategory[]{CharacteristicCategory.DAMAGE, CharacteristicCategory.HEAL},
            new DoubleData<>(Characteristic.ITEM_CRITICAL_2, Characteristic.ITEM_CRITICAL_3),
            new DoubleData<>(CharacteristicType.ITEM_CRITICAL, 50)
    ),
    ITEM_CRITICAL_6(
            "一撃必殺",
            "対象を戦闘不能にし(ボス無効)、アイテムが66%の確率でクリティカルする。",
            new CharacteristicCategory[]{CharacteristicCategory.DAMAGE, CharacteristicCategory.HEAL},
            new DoubleData<>(Characteristic.ITEM_CRITICAL_1, Characteristic.ITEM_CRITICAL_2),
            new DoubleData<>(CharacteristicType.ITEM_CRITICAL, 66),
            new DoubleData<>(CharacteristicType.DEATH_DAMAGE, 100)
    ),
    ITEM_RANDOM_1(
            "効果安定",
            "アイテムの乱数の幅がやや小さくなり、高い値が出やすくなる。",
            new CharacteristicCategory[]{CharacteristicCategory.DAMAGE, CharacteristicCategory.HEAL},
            null,
            new DoubleData<>(CharacteristicType.ITEM_RANDOM, 10)
    ),
    ITEM_RANDOM_2(
            "効果安定+",
            "アイテムの乱数の幅が少し小さくなり、高い値が出やすくなる。",
            new CharacteristicCategory[]{CharacteristicCategory.DAMAGE, CharacteristicCategory.HEAL},
            null,
            new DoubleData<>(CharacteristicType.ITEM_RANDOM, 20)
    ),
    ITEM_RANDOM_3(
            "効果安定++",
            "アイテムの乱数の幅が小さくなり、高い値が出やすくなる。",
            new CharacteristicCategory[]{CharacteristicCategory.DAMAGE, CharacteristicCategory.HEAL},
            null,
            new DoubleData<>(CharacteristicType.ITEM_RANDOM, 30)
    ),
    ITEM_RANDOM_4(
            "安定度重視",
            "アイテムの乱数の幅が小さくなり、高い値が出やすくなる。",
            new CharacteristicCategory[]{CharacteristicCategory.DAMAGE, CharacteristicCategory.HEAL},
            new DoubleData<>(Characteristic.ITEM_RANDOM_1, Characteristic.ITEM_RANDOM_2),
            new DoubleData<>(CharacteristicType.ITEM_RANDOM, 35)
    ),
    ITEM_RANDOM_5(
            "鉄板の効果",
            "アイテムの乱数の幅が半分になり、高い値が出やすくなる。",
            new CharacteristicCategory[]{CharacteristicCategory.DAMAGE, CharacteristicCategory.HEAL},
            new DoubleData<>(Characteristic.ITEM_RANDOM_2, Characteristic.ITEM_RANDOM_3),
            new DoubleData<>(CharacteristicType.ITEM_RANDOM, 50)
    ),
    ITEM_RANDOM_6(
            "期待値大増幅",
            "アイテムの乱数の幅が安定し、高い値が非常に出やすくなる。",
            new CharacteristicCategory[]{CharacteristicCategory.DAMAGE, CharacteristicCategory.HEAL},
            new DoubleData<>(Characteristic.ITEM_RANDOM_4, Characteristic.ITEM_RANDOM_5),
            new DoubleData<>(CharacteristicType.ITEM_RANDOM, 80)
    ),
    ITEM_USE_COUNT_1(
            "使用回数+1",
            "アイテムの使用回数が、1回分増加する。その代わり、威力が少し落ちる。",
            new CharacteristicCategory[]{CharacteristicCategory.DAMAGE, CharacteristicCategory.HEAL},
            null,
            new DoubleData<>(CharacteristicType.ITEM_USE_COUNT, 1),
            new DoubleData<>(CharacteristicType.ITEM_ATTACK, -20),
            new DoubleData<>(CharacteristicType.ITEM_HEAL, -20)
    ),
    ITEM_USE_COUNT_2(
            "使用回数+2",
            "アイテムの使用回数が、2回分増加する。その代わり、威力がやや落ちる。",
            new CharacteristicCategory[]{CharacteristicCategory.DAMAGE, CharacteristicCategory.HEAL},
            null,
            new DoubleData<>(CharacteristicType.ITEM_USE_COUNT, 2),
            new DoubleData<>(CharacteristicType.ITEM_ATTACK, -10),
            new DoubleData<>(CharacteristicType.ITEM_HEAL, -10)
    ),
    ITEM_USE_COUNT_3(
            "増殖",
            "アイテムの使用回数が、3回分増加する。",
            new CharacteristicCategory[]{CharacteristicCategory.DAMAGE, CharacteristicCategory.HEAL},
            new DoubleData<>(Characteristic.ITEM_USE_COUNT_1, Characteristic.ITEM_USE_COUNT_2),
            new DoubleData<>(CharacteristicType.ITEM_USE_COUNT, 3)
    ),
    ITEM_USE_COUNT_DOWN_1(
            "使用回数-1",
            "アイテムの使用回数が、1回分低下する。その代わり、威力が少し上がる。",
            new CharacteristicCategory[]{CharacteristicCategory.DAMAGE, CharacteristicCategory.HEAL},
            null,
            new DoubleData<>(CharacteristicType.ITEM_USE_COUNT, -1),
            new DoubleData<>(CharacteristicType.ITEM_ATTACK, 20),
            new DoubleData<>(CharacteristicType.ITEM_HEAL, 20)
    ),
    ITEM_USE_COUNT_DOWN_2(
            "使用回数-2",
            "アイテムの使用回数が、2回分低下する。その代わり、威力が上がる。",
            new CharacteristicCategory[]{CharacteristicCategory.DAMAGE, CharacteristicCategory.HEAL},
            null,
            new DoubleData<>(CharacteristicType.ITEM_USE_COUNT, -2),
            new DoubleData<>(CharacteristicType.ITEM_ATTACK, 40),
            new DoubleData<>(CharacteristicType.ITEM_HEAL, 40)
    ),
    ITEM_USE_COUNT_DOWN_3(
            "収縮",
            "アイテムの使用回数が、5回分低下する。その代わり、威力が大幅に上がる。",
            new CharacteristicCategory[]{CharacteristicCategory.DAMAGE, CharacteristicCategory.HEAL},
            new DoubleData<>(Characteristic.ITEM_USE_COUNT_DOWN_1, Characteristic.ITEM_USE_COUNT_DOWN_2),
            new DoubleData<>(CharacteristicType.ITEM_USE_COUNT, -5),
            new DoubleData<>(CharacteristicType.ITEM_ATTACK, 100),
            new DoubleData<>(CharacteristicType.ITEM_HEAL, 100)
    ),
    UP_HP_1(
            "HP強化",
            "最大HPが10上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.ARMOR, CharacteristicCategory.DECORATION},
            null,
            new DoubleData<>(CharacteristicType.UP_HP, 10)
    ),
    UP_HP_2(
            "HP大増量",
            "最大HPが15上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.ARMOR, CharacteristicCategory.DECORATION},
            null,
            new DoubleData<>(CharacteristicType.UP_HP, 15)
    ),
    UP_HP_3(
            "HP超強化",
            "最大HPが25上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.ARMOR, CharacteristicCategory.DECORATION},
            null,
            new DoubleData<>(CharacteristicType.UP_HP, 25)
    ),
    UP_HP_4(
            "みなぎる力",
            "最大HPが20上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.ARMOR, CharacteristicCategory.DECORATION},
            new DoubleData<>(Characteristic.UP_HP_1, Characteristic.UP_HP_2),
            new DoubleData<>(CharacteristicType.UP_HP, 20)
    ),
    UP_HP_5(
            "あふれる力",
            "最大HPが35上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.ARMOR, CharacteristicCategory.DECORATION},
            new DoubleData<>(Characteristic.UP_HP_2, Characteristic.UP_HP_3),
            new DoubleData<>(CharacteristicType.UP_HP, 35)
    ),
    UP_HP_6(
            "生命の力",
            "最大HPが50上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.ARMOR, CharacteristicCategory.DECORATION},
            new DoubleData<>(Characteristic.UP_HP_4, Characteristic.UP_HP_5),
            new DoubleData<>(CharacteristicType.UP_HP, 50)
    ),
    UP_MP_1(
            "MP強化",
            "最大MPが10上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.ARMOR, CharacteristicCategory.DECORATION},
            null,
            new DoubleData<>(CharacteristicType.UP_MP, 10)
    ),
    UP_MP_2(
            "MP大増量",
            "最大MPが15上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.ARMOR, CharacteristicCategory.DECORATION},
            null,
            new DoubleData<>(CharacteristicType.UP_MP, 15)
    ),
    UP_MP_3(
            "MP超強化",
            "最大MPが25上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.ARMOR, CharacteristicCategory.DECORATION},
            null,
            new DoubleData<>(CharacteristicType.UP_MP, 25)
    ),
    UP_MP_4(
            "魔法使いの知恵",
            "最大MPが20上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.ARMOR, CharacteristicCategory.DECORATION},
            new DoubleData<>(Characteristic.UP_MP_1, Characteristic.UP_MP_2),
            new DoubleData<>(CharacteristicType.UP_MP, 20)
    ),
    UP_MP_5(
            "大魔法使いの知恵",
            "最大MPが35上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.ARMOR, CharacteristicCategory.DECORATION},
            new DoubleData<>(Characteristic.UP_MP_2, Characteristic.UP_MP_3),
            new DoubleData<>(CharacteristicType.UP_MP, 35)
    ),
    UP_MP_6(
            "神々の知恵",
            "最大MPが50上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.ARMOR, CharacteristicCategory.DECORATION},
            new DoubleData<>(Characteristic.UP_MP_4, Characteristic.UP_MP_5),
            new DoubleData<>(CharacteristicType.UP_MP, 50)
    ),
    UP_HP_MP_1(
            "HPMP強化",
            "最大HPとMPが10上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.ARMOR, CharacteristicCategory.DECORATION},
            new DoubleData<>(Characteristic.UP_HP_4, Characteristic.UP_MP_4),
            new DoubleData<>(CharacteristicType.UP_HP, 10),
            new DoubleData<>(CharacteristicType.UP_MP, 10)
    ),
    UP_HP_MP_2(
            "HPMP大増量",
            "最大HPとMPが20上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.ARMOR, CharacteristicCategory.DECORATION},
            new DoubleData<>(Characteristic.UP_HP_5, Characteristic.UP_MP_5),
            new DoubleData<>(CharacteristicType.UP_HP, 20),
            new DoubleData<>(CharacteristicType.UP_MP, 20)
    ),
    UP_HP_MP_3(
            "HPMP超強化",
            "最大HPとMPが35上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.ARMOR, CharacteristicCategory.DECORATION},
            new DoubleData<>(Characteristic.UP_HP_6, Characteristic.UP_MP_6),
            new DoubleData<>(CharacteristicType.UP_HP, 35),
            new DoubleData<>(CharacteristicType.UP_MP, 35)
    ),
    UP_PARAMETER_1(
            "パラメータ+10%",
            "素の能力値を10%上昇させる。",
            new CharacteristicCategory[]{CharacteristicCategory.ARMOR},
            null,
            new DoubleData<>(CharacteristicType.UP_PARAMETER, 10)
    ),
    UP_PARAMETER_2(
            "パラメータ+12%",
            "素の能力値を12%上昇させる。",
            new CharacteristicCategory[]{CharacteristicCategory.ARMOR},
            null,
            new DoubleData<>(CharacteristicType.UP_PARAMETER, 12)
    ),
    UP_PARAMETER_3(
            "パラメータ+15%",
            "素の能力値を15%上昇させる。",
            new CharacteristicCategory[]{CharacteristicCategory.ARMOR},
            null,
            new DoubleData<>(CharacteristicType.UP_PARAMETER, 15)
    ),
    UP_PARAMETER_4(
            "肉体を強化する",
            "素の能力値を15%上昇させる。",
            new CharacteristicCategory[]{CharacteristicCategory.ARMOR},
            new DoubleData<>(Characteristic.UP_PARAMETER_1, Characteristic.UP_PARAMETER_2),
            new DoubleData<>(CharacteristicType.UP_PARAMETER, 15)
    ),
    UP_PARAMETER_5(
            "身体の力を引き出す",
            "素の能力値を20%上昇させる。",
            new CharacteristicCategory[]{CharacteristicCategory.ARMOR},
            new DoubleData<>(Characteristic.UP_PARAMETER_2, Characteristic.UP_PARAMETER_3),
            new DoubleData<>(CharacteristicType.UP_PARAMETER, 20)
    ),
    UP_PARAMETER_6(
            "肉体を超進化させる",
            "素の能力値を25%上昇させる。",
            new CharacteristicCategory[]{CharacteristicCategory.ARMOR},
            new DoubleData<>(Characteristic.UP_PARAMETER_4, Characteristic.UP_PARAMETER_5),
            new DoubleData<>(CharacteristicType.UP_PARAMETER, 25)
    ),
    UP_ATTACK_1(
            "攻撃強化",
            "攻撃力が3上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.WEAPON},
            null,
            new DoubleData<>(CharacteristicType.UP_ATTACK, 3)
    ),
    UP_ATTACK_2(
            "攻撃ブースト",
            "攻撃力が5上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.WEAPON},
            null,
            new DoubleData<>(CharacteristicType.UP_ATTACK, 5)
    ),
    UP_ATTACK_3(
            "攻撃超強化",
            "攻撃力が7上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.WEAPON},
            null,
            new DoubleData<>(CharacteristicType.UP_ATTACK, 7)
    ),
    UP_ATTACK_4(
            "猛獣の力",
            "攻撃力が6上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.WEAPON},
            new DoubleData<>(Characteristic.UP_ATTACK_1, Characteristic.UP_ATTACK_2),
            new DoubleData<>(CharacteristicType.UP_ATTACK, 6)
    ),
    UP_ATTACK_5(
            "破壊の力",
            "攻撃力が10上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.WEAPON},
            new DoubleData<>(Characteristic.UP_ATTACK_2, Characteristic.UP_ATTACK_3),
            new DoubleData<>(CharacteristicType.UP_ATTACK, 10)
    ),
    UP_ATTACK_6(
            "軍神の力",
            "攻撃力が15上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.WEAPON},
            new DoubleData<>(Characteristic.UP_ATTACK_4, Characteristic.UP_ATTACK_5),
            new DoubleData<>(CharacteristicType.UP_ATTACK, 15)
    ),
    UP_DEFENSE_1(
            "防御強化",
            "防御力が3上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.ARMOR},
            null,
            new DoubleData<>(CharacteristicType.UP_DEFENSE, 3)
    ),
    UP_DEFENSE_2(
            "防御ブースト",
            "防御力が5上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.ARMOR},
            null,
            new DoubleData<>(CharacteristicType.UP_DEFENSE, 5)
    ),
    UP_DEFENSE_3(
            "防御超強化",
            "防御力が7上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.ARMOR},
            null,
            new DoubleData<>(CharacteristicType.UP_DEFENSE, 7)
    ),
    UP_DEFENSE_4(
            "鋼鉄の守り",
            "防御力が6上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.ARMOR},
            new DoubleData<>(Characteristic.UP_DEFENSE_1, Characteristic.UP_DEFENSE_2),
            new DoubleData<>(CharacteristicType.UP_DEFENSE, 6)
    ),
    UP_DEFENSE_5(
            "輝石の守り",
            "防御力が10上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.ARMOR},
            new DoubleData<>(Characteristic.UP_DEFENSE_2, Characteristic.UP_DEFENSE_3),
            new DoubleData<>(CharacteristicType.UP_DEFENSE, 10)
    ),
    UP_DEFENSE_6(
            "竜鱗の守り",
            "防御力が15上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.ARMOR},
            new DoubleData<>(Characteristic.UP_DEFENSE_4, Characteristic.UP_DEFENSE_5),
            new DoubleData<>(CharacteristicType.UP_DEFENSE, 15)
    ),
    UP_SPEED_1(
            "素早さ強化",
            "素早さが3上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.DECORATION},
            null,
            new DoubleData<>(CharacteristicType.UP_SPEED, 3)
    ),
    UP_SPEED_2(
            "素早さブースト",
            "素早さが5上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.DECORATION},
            null,
            new DoubleData<>(CharacteristicType.UP_SPEED, 5)
    ),
    UP_SPEED_3(
            "素早さ超強化",
            "素早さが7上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.DECORATION},
            null,
            new DoubleData<>(CharacteristicType.UP_SPEED, 7)
    ),
    UP_SPEED_4(
            "韋駄天の脚力",
            "素早さが6上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.DECORATION},
            new DoubleData<>(Characteristic.UP_SPEED_1, Characteristic.UP_SPEED_2),
            new DoubleData<>(CharacteristicType.UP_SPEED, 6)
    ),
    UP_SPEED_5(
            "神速の脚力",
            "素早さが10上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.DECORATION},
            new DoubleData<>(Characteristic.UP_SPEED_2, Characteristic.UP_SPEED_3),
            new DoubleData<>(CharacteristicType.UP_SPEED, 10)
    ),
    UP_SPEED_6(
            "光速の脚力",
            "素早さが15上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.DECORATION},
            new DoubleData<>(Characteristic.UP_SPEED_4, Characteristic.UP_SPEED_5),
            new DoubleData<>(CharacteristicType.UP_SPEED, 15)
    ),
    UP_ATTACK_DEFENSE_1(
            "攻防強化",
            "攻撃力・防御力が3上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.WEAPON, CharacteristicCategory.ARMOR},
            new DoubleData<>(Characteristic.UP_ATTACK_1, Characteristic.UP_DEFENSE_1),
            new DoubleData<>(CharacteristicType.UP_ATTACK, 3),
            new DoubleData<>(CharacteristicType.UP_DEFENSE, 3)
    ),
    UP_ATTACK_DEFENSE_2(
            "攻防ブースト",
            "攻撃力・防御力が5上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.WEAPON, CharacteristicCategory.ARMOR},
            new DoubleData<>(Characteristic.UP_ATTACK_2, Characteristic.UP_DEFENSE_2),
            new DoubleData<>(CharacteristicType.UP_ATTACK, 5),
            new DoubleData<>(CharacteristicType.UP_DEFENSE, 5)
    ),
    UP_ATTACK_SPEED_1(
            "攻速強化",
            "攻撃力・素早さが3上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.WEAPON, CharacteristicCategory.DECORATION},
            new DoubleData<>(Characteristic.UP_ATTACK_1, Characteristic.UP_SPEED_1),
            new DoubleData<>(CharacteristicType.UP_ATTACK, 3),
            new DoubleData<>(CharacteristicType.UP_SPEED, 3)
    ),
    UP_ATTACK_SPEED_2(
            "攻速ブースト",
            "攻撃力・素早さが5上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.WEAPON, CharacteristicCategory.DECORATION},
            new DoubleData<>(Characteristic.UP_ATTACK_2, Characteristic.UP_SPEED_2),
            new DoubleData<>(CharacteristicType.UP_ATTACK, 5),
            new DoubleData<>(CharacteristicType.UP_SPEED, 5)
    ),
    UP_DEFENSE_SPEED_1(
            "防速強化",
            "防御力・素早さが3上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.ARMOR, CharacteristicCategory.DECORATION},
            new DoubleData<>(Characteristic.UP_DEFENSE_1, Characteristic.UP_SPEED_1),
            new DoubleData<>(CharacteristicType.UP_DEFENSE, 3),
            new DoubleData<>(CharacteristicType.UP_SPEED, 3)
    ),
    UP_DEFENSE_SPEED_2(
            "防速ブースト",
            "防御力・素早さが5上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.ARMOR, CharacteristicCategory.DECORATION},
            new DoubleData<>(Characteristic.UP_DEFENSE_2, Characteristic.UP_SPEED_2),
            new DoubleData<>(CharacteristicType.UP_DEFENSE, 5),
            new DoubleData<>(CharacteristicType.UP_SPEED, 5)
    ),
    UP_ALL_1(
            "全能力強化",
            "全ての能力が3上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.WEAPON, CharacteristicCategory.ARMOR, CharacteristicCategory.DECORATION},
            new DoubleData<>(Characteristic.UP_ATTACK_DEFENSE_1, Characteristic.UP_ATTACK_SPEED_1),
            new DoubleData<>(CharacteristicType.UP_ATTACK, 3),
            new DoubleData<>(CharacteristicType.UP_DEFENSE, 3),
            new DoubleData<>(CharacteristicType.UP_SPEED, 3)
    ),
    UP_ALL_2(
            "全能力ブースト",
            "全ての能力が5上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.WEAPON, CharacteristicCategory.ARMOR, CharacteristicCategory.DECORATION},
            new DoubleData<>(Characteristic.UP_ATTACK_DEFENSE_2, Characteristic.UP_DEFENSE_SPEED_2),
            new DoubleData<>(CharacteristicType.UP_ATTACK, 5),
            new DoubleData<>(CharacteristicType.UP_DEFENSE, 5),
            new DoubleData<>(CharacteristicType.UP_SPEED, 5)
    ),
    UP_ALL_3(
            "全能力超強化",
            "全ての能力が7上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.WEAPON, CharacteristicCategory.ARMOR, CharacteristicCategory.DECORATION},
            new DoubleData<>(Characteristic.UP_ALL_1, Characteristic.UP_ALL_2),
            new DoubleData<>(CharacteristicType.UP_ATTACK, 7),
            new DoubleData<>(CharacteristicType.UP_DEFENSE, 7),
            new DoubleData<>(CharacteristicType.UP_SPEED, 7)
    ),
    UP_ALL_4(
            "全能の力",
            "全ての能力が10上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.WEAPON, CharacteristicCategory.ARMOR, CharacteristicCategory.DECORATION},
            new DoubleData<>(Characteristic.UP_ALL_2, Characteristic.UP_ALL_3),
            new DoubleData<>(CharacteristicType.UP_ATTACK, 10),
            new DoubleData<>(CharacteristicType.UP_DEFENSE, 10),
            new DoubleData<>(CharacteristicType.UP_SPEED, 10)
    ),
    DOWN_CONSUME_MP_1(
            "消費MP-10%",
            "スキルの消費MPを10%軽減する。",
            new CharacteristicCategory[]{CharacteristicCategory.WEAPON, CharacteristicCategory.DECORATION},
            null,
            new DoubleData<>(CharacteristicType.DOWN_CONSUME_MP, 10)
    ),
    DOWN_CONSUME_MP_2(
            "消費MP-15%",
            "スキルの消費MPを15%軽減する。",
            new CharacteristicCategory[]{CharacteristicCategory.WEAPON, CharacteristicCategory.DECORATION},
            null,
            new DoubleData<>(CharacteristicType.DOWN_CONSUME_MP, 15)
    ),
    DOWN_CONSUME_MP_3(
            "消費MP-20%",
            "スキルの消費MPを20%軽減する。",
            new CharacteristicCategory[]{CharacteristicCategory.WEAPON, CharacteristicCategory.DECORATION},
            null,
            new DoubleData<>(CharacteristicType.DOWN_CONSUME_MP, 20)
    ),
    DOWN_CONSUME_MP_4(
            "消費MP削減",
            "スキルの消費MPを20%軽減する。",
            new CharacteristicCategory[]{CharacteristicCategory.WEAPON, CharacteristicCategory.DECORATION},
            new DoubleData<>(Characteristic.DOWN_CONSUME_MP_1, Characteristic.DOWN_CONSUME_MP_2),
            new DoubleData<>(CharacteristicType.DOWN_CONSUME_MP, 20)
    ),
    DOWN_CONSUME_MP_5(
            "消費MP圧縮",
            "スキルの消費MPを30%軽減する。",
            new CharacteristicCategory[]{CharacteristicCategory.WEAPON, CharacteristicCategory.DECORATION},
            new DoubleData<>(Characteristic.DOWN_CONSUME_MP_2, Characteristic.DOWN_CONSUME_MP_3),
            new DoubleData<>(CharacteristicType.DOWN_CONSUME_MP, 30)
    ),
    DOWN_CONSUME_MP_6(
            "消費MP半減",
            "スキルの消費MPを50%軽減する。",
            new CharacteristicCategory[]{CharacteristicCategory.WEAPON, CharacteristicCategory.DECORATION},
            new DoubleData<>(Characteristic.DOWN_CONSUME_MP_4, Characteristic.DOWN_CONSUME_MP_5),
            new DoubleData<>(CharacteristicType.DOWN_CONSUME_MP, 50)
    ),
    UP_SKILL_1(
            "スキル威力+10%",
            "スキルの威力と消費MPを10%上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.WEAPON},
            null,
            new DoubleData<>(CharacteristicType.UP_SKILL, 10),
            new DoubleData<>(CharacteristicType.DOWN_CONSUME_MP, -10)
    ),
    UP_SKILL_2(
            "スキル威力+12%",
            "スキルの威力と消費MPを12%上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.WEAPON},
            null,
            new DoubleData<>(CharacteristicType.UP_SKILL, 12),
            new DoubleData<>(CharacteristicType.DOWN_CONSUME_MP, -12)
    ),
    UP_SKILL_3(
            "スキル威力+15%",
            "スキルの威力と消費MPを15%上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.WEAPON},
            null,
            new DoubleData<>(CharacteristicType.UP_SKILL, 15),
            new DoubleData<>(CharacteristicType.DOWN_CONSUME_MP, -15)
    ),
    UP_SKILL_4(
            "スキル強化",
            "スキルの威力と消費MPを15%上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.WEAPON},
            new DoubleData<>(Characteristic.UP_SKILL_1, Characteristic.UP_SKILL_2),
            new DoubleData<>(CharacteristicType.UP_SKILL, 15),
            new DoubleData<>(CharacteristicType.DOWN_CONSUME_MP, -15)
    ),
    UP_SKILL_5(
            "スキルブースト",
            "スキルの威力と消費MPを20%上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.WEAPON},
            new DoubleData<>(Characteristic.UP_SKILL_2, Characteristic.UP_SKILL_3),
            new DoubleData<>(CharacteristicType.UP_SKILL, 20),
            new DoubleData<>(CharacteristicType.DOWN_CONSUME_MP, -20)
    ),
    UP_SKILL_6(
            "スキル超強化",
            "スキルの威力と消費MPを30%上昇する。",
            new CharacteristicCategory[]{CharacteristicCategory.WEAPON},
            new DoubleData<>(Characteristic.UP_SKILL_4, Characteristic.UP_SKILL_5),
            new DoubleData<>(CharacteristicType.UP_SKILL, 30),
            new DoubleData<>(CharacteristicType.DOWN_CONSUME_MP, -30)
    ),
    UP_SKILL_DOWN_MP_1(
            "エコスキル",
            "スキルの威力を7%上昇させ、消費MPを7%減少させる。",
            new CharacteristicCategory[]{CharacteristicCategory.WEAPON},
            new DoubleData<>(Characteristic.UP_SKILL_4, Characteristic.DOWN_CONSUME_MP_4),
            new DoubleData<>(CharacteristicType.UP_SKILL, 7),
            new DoubleData<>(CharacteristicType.DOWN_CONSUME_MP, 7)
    ),
    UP_SKILL_DOWN_MP_2(
            "コスト圧縮スキル",
            "スキルの威力を10%上昇させ、消費MPを10%減少させる。",
            new CharacteristicCategory[]{CharacteristicCategory.WEAPON},
            new DoubleData<>(Characteristic.UP_SKILL_5, Characteristic.DOWN_CONSUME_MP_5),
            new DoubleData<>(CharacteristicType.UP_SKILL, 10),
            new DoubleData<>(CharacteristicType.DOWN_CONSUME_MP, 10)
    ),
    UP_SKILL_DOWN_MP_3(
            "セービングスキル",
            "スキルの威力を15%上昇させ、消費MPを15%減少させる。",
            new CharacteristicCategory[]{CharacteristicCategory.WEAPON},
            new DoubleData<>(Characteristic.UP_SKILL_6, Characteristic.DOWN_CONSUME_MP_6),
            new DoubleData<>(CharacteristicType.UP_SKILL, 15),
            new DoubleData<>(CharacteristicType.DOWN_CONSUME_MP, 15)
    ),
//    BUFF_LEVEL(
//            "魂宿り",
//            "対象のレベルを大きく上昇させる。",
//            new CharacteristicCategory[]{CharacteristicCategory.HEAL},
//            null,
//            new DoubleData<>(CharacteristicType.UP_SKILL, 15)
//    ),
    BUFF_DAMAGE(
            "剛力の祝福",
            "対象の攻撃力を大きく上昇させる。",
            new CharacteristicCategory[]{CharacteristicCategory.HEAL},
            null,
            new DoubleData<>(CharacteristicType.BUFF_DAMAGE, new DoubleData<>(10, 10))
    ),
    BUFF_DEFENSE(
            "守護の祝福",
            "対象の防御力を大きく上昇させる。",
            new CharacteristicCategory[]{CharacteristicCategory.HEAL},
            null,
            new DoubleData<>(CharacteristicType.BUFF_DEFENSE, new DoubleData<>(10, 10))
    ),
    BUFF_SPEED(
            "韋駄天の祝福",
            "対象の素早さを大きく上昇させる。",
            new CharacteristicCategory[]{CharacteristicCategory.HEAL},
            null,
            new DoubleData<>(CharacteristicType.BUFF_SPEED, new DoubleData<>(10, 10))
    ),
    ABNORMALITY_SLEEP(
            "眠り付与",
            "ダメージを与えた時、確率で眠り状態にする。",
            new CharacteristicCategory[]{CharacteristicCategory.DAMAGE, CharacteristicCategory.WEAPON},
            null,
            new DoubleData<>(CharacteristicType.ABNORMALITY, new DoubleData<>(AbnormalityType.SLEEP, 30))
    ),
    ABNORMALITY_POISON(
            "毒付与",
            "ダメージを与えた時、確率で毒状態にする。",
            new CharacteristicCategory[]{CharacteristicCategory.DAMAGE, CharacteristicCategory.WEAPON},
            null,
            new DoubleData<>(CharacteristicType.ABNORMALITY, new DoubleData<>(AbnormalityType.POISON, 30))
    ),
    ABNORMALITY_SLOW(
            "スロウ付与",
            "ダメージを与えた時、確率でスロウ状態にする。",
            new CharacteristicCategory[]{CharacteristicCategory.DAMAGE, CharacteristicCategory.WEAPON},
            null,
            new DoubleData<>(CharacteristicType.ABNORMALITY, new DoubleData<>(AbnormalityType.SLOW, 30))
    ),
    ABNORMALITY_CURSE(
            "呪い付与",
            "ダメージを与えた時、確率で呪い状態にする。",
            new CharacteristicCategory[]{CharacteristicCategory.DAMAGE, CharacteristicCategory.WEAPON},
            null,
            new DoubleData<>(CharacteristicType.ABNORMALITY, new DoubleData<>(AbnormalityType.CURSE, 30))
    ),
    ABNORMALITY_DARKNESS(
            "暗黒付与",
            "ダメージを与えた時、確率で暗黒状態にする。",
            new CharacteristicCategory[]{CharacteristicCategory.DAMAGE, CharacteristicCategory.WEAPON},
            null,
            new DoubleData<>(CharacteristicType.ABNORMALITY, new DoubleData<>(AbnormalityType.DARKNESS, 30))
    ),
    ABNORMALITY_WEAK(
            "弱体化付与",
            "ダメージを与えた時、確率で弱体化状態にする。",
            new CharacteristicCategory[]{CharacteristicCategory.DAMAGE, CharacteristicCategory.WEAPON},
            null,
            new DoubleData<>(CharacteristicType.ABNORMALITY, new DoubleData<>(AbnormalityType.WEAK, 30))
    ),
    ABNORMALITY_NOHEAL(
            "回復無効付与",
            "ダメージを与えた時、確率で回復無効状態にする。",
            new CharacteristicCategory[]{CharacteristicCategory.DAMAGE, CharacteristicCategory.WEAPON},
            null,
            new DoubleData<>(CharacteristicType.ABNORMALITY, new DoubleData<>(AbnormalityType.NOHEAL, 30))
    ),
    ABNORMALITY_SEALED(
            "封印付与",
            "ダメージを与えた時、確率で封印状態にする。",
            new CharacteristicCategory[]{CharacteristicCategory.DAMAGE, CharacteristicCategory.WEAPON},
            null,
            new DoubleData<>(CharacteristicType.ABNORMALITY, new DoubleData<>(AbnormalityType.SEALED, 30))
    ),
//    ATTACK_LEVEL(
//            "魂宿り",
//            "対象のレベルを大きく上昇させる。",
//            new CharacteristicCategory[]{CharacteristicCategory.HEAL},
//            null,
//            new DoubleData<>(CharacteristicType.UP_SKILL, 15)
//    ),
    ATTACK_DAMAGE(
            "無力の呪詛",
            "対象の攻撃力を大きく低下させる。",
            new CharacteristicCategory[]{CharacteristicCategory.DAMAGE},
            null,
            new DoubleData<>(CharacteristicType.BUFF_DAMAGE, new DoubleData<>(10, 10))
    ),
    ATTACK_DEFENSE(
            "無守の呪詛",
            "対象の防御力を大きく低下させる。",
            new CharacteristicCategory[]{CharacteristicCategory.DAMAGE},
            null,
            new DoubleData<>(CharacteristicType.BUFF_DEFENSE, new DoubleData<>(10, 10))
    ),
    ATTACK_SPEED(
            "無速の呪詛",
            "対象の素早さを大きく低下させる。",
            new CharacteristicCategory[]{CharacteristicCategory.DAMAGE},
            null,
            new DoubleData<>(CharacteristicType.BUFF_SPEED, new DoubleData<>(10, 10))
    ),
    ATTACK_HEAL_1(
            "ダメージ還元",
            "攻撃で与えたダメージの10%だけ、自分のHPが回復する。",
            new CharacteristicCategory[]{CharacteristicCategory.WEAPON},
            null,
            new DoubleData<>(CharacteristicType.HEAL_ATTACK, 10)
    ),
    ATTACK_HEAL_2(
            "ダメージ還元+",
            "攻撃で与えたダメージの12%だけ、自分のHPが回復する。",
            new CharacteristicCategory[]{CharacteristicCategory.WEAPON},
            null,
            new DoubleData<>(CharacteristicType.HEAL_ATTACK, 12)
    ),
    ATTACK_HEAL_3(
            "ダメージ還元++",
            "攻撃で与えたダメージの15%だけ、自分のHPが回復する。",
            new CharacteristicCategory[]{CharacteristicCategory.WEAPON},
            null,
            new DoubleData<>(CharacteristicType.HEAL_ATTACK, 15)
    ),
    ATTACK_HEAL_4(
            "HP吸収",
            "攻撃で与えたダメージの15%だけ、自分のHPが回復する。",
            new CharacteristicCategory[]{CharacteristicCategory.WEAPON},
            new DoubleData<>(Characteristic.ATTACK_HEAL_1, Characteristic.ATTACK_HEAL_2),
            new DoubleData<>(CharacteristicType.HEAL_ATTACK, 15)
    ),
    ATTACK_HEAL_5(
            "生命力奪取",
            "攻撃で与えたダメージの20%だけ、自分のHPが回復する。",
            new CharacteristicCategory[]{CharacteristicCategory.WEAPON},
            new DoubleData<>(Characteristic.ATTACK_HEAL_2, Characteristic.ATTACK_HEAL_3),
            new DoubleData<>(CharacteristicType.HEAL_ATTACK, 20)
    ),
    ATTACK_HEAL_6(
            "魂を吸収する",
            "攻撃で与えたダメージの25%だけ、自分のHPが回復する。",
            new CharacteristicCategory[]{CharacteristicCategory.WEAPON},
            new DoubleData<>(Characteristic.ATTACK_HEAL_4, Characteristic.ATTACK_HEAL_5),
            new DoubleData<>(CharacteristicType.HEAL_ATTACK, 25)
    ),
    ATTACK_DAMAGE_HEAL(
            "オーバーパワー",
            "攻撃力が20上昇し、通常攻撃で与えたダメージの15%を自分のHPに還元する。",
            new CharacteristicCategory[]{CharacteristicCategory.WEAPON},
            null,
            new DoubleData<>(CharacteristicType.UP_ATTACK, 20),
            new DoubleData<>(CharacteristicType.HEAL_ATTACK, 15)
    ),
    // 追い討ち強化 実装なし
    CHARACTERISTIC_1(
            "特性強化",
            "アイテムについている特性の強さに応じて効果が強化される。",
            new CharacteristicCategory[]{CharacteristicCategory.DAMAGE, CharacteristicCategory.HEAL},
            null,
            new DoubleData<>(CharacteristicType.CHARACTERISTIC_UP, 0)
    ),
    CHARACTERISTIC_2(
            "特性強化+",
            "アイテムについている特性の強さに応じて効果が大きく強化される。",
            new CharacteristicCategory[]{CharacteristicCategory.DAMAGE, CharacteristicCategory.HEAL},
            null,
            new DoubleData<>(CharacteristicType.CHARACTERISTIC_UP, 1)
    ),
    CHARACTERISTIC_3(
            "コストボーナス",
            "アイテムについている特性の強さに応じて効果が大幅に強化される。",
            new CharacteristicCategory[]{CharacteristicCategory.DAMAGE, CharacteristicCategory.HEAL},
            null,
            new DoubleData<>(CharacteristicType.CHARACTERISTIC_UP, 2)
    ),
    CHARACTERISTIC_4(
            "特性で超強化",
            "アイテムについている特性の強さに応じて効果が大きく強化される。強い特性がついているほど超絶に強化される。",
            new CharacteristicCategory[]{CharacteristicCategory.DAMAGE, CharacteristicCategory.HEAL},
            new DoubleData<>(Characteristic.CHARACTERISTIC_1, Characteristic.CHARACTERISTIC_2),
            new DoubleData<>(CharacteristicType.CHARACTERISTIC_UP, 3)
    ),
    RANGE_BONUS_1(
            "範囲ボーナス",
            "効果範囲の対象×5%、威力がアップする。",
            new CharacteristicCategory[]{CharacteristicCategory.DAMAGE, CharacteristicCategory.HEAL},
            null,
            new DoubleData<>(CharacteristicType.RANGE_BONUS, 5)
    ),
    RANGE_BONUS_2(
            "範囲ボーナス+",
            "効果範囲の対象×7%、威力がアップする。",
            new CharacteristicCategory[]{CharacteristicCategory.DAMAGE, CharacteristicCategory.HEAL},
            null,
            new DoubleData<>(CharacteristicType.RANGE_BONUS, 7)
    ),
    RANGE_BONUS_3(
            "複数に効果増大",
            "効果範囲の対象×10%、威力がアップする。",
            new CharacteristicCategory[]{CharacteristicCategory.DAMAGE, CharacteristicCategory.HEAL},
            new DoubleData<>(Characteristic.RANGE_BONUS_1, Characteristic.RANGE_BONUS_2),
            new DoubleData<>(CharacteristicType.RANGE_BONUS, 10)
    ),
    RANGE_BONUS_4(
            "マルチボーナス",
            "効果範囲の対象×12%、威力がアップする。",
            new CharacteristicCategory[]{CharacteristicCategory.DAMAGE, CharacteristicCategory.HEAL},
            null,
            new DoubleData<>(CharacteristicType.RANGE_BONUS, 12)
    ),
//    LITTLE_BONUS_1(
//            "範囲ボーナス",
//            "効果範囲の対象×5%、威力がアップする。",
//            new CharacteristicCategory[]{CharacteristicCategory.DAMAGE, CharacteristicCategory.HEAL},
//            null,
//            new DoubleData<>(CharacteristicType.RANGE_BONUS, 5)
//    ),
    
    ;

    /*
    private Characteristic(String name, String msg, CharacteristicCategory[] categorys, DoubleData<Characteristic, Characteristic> req, DoubleData<CharacteristicType, Object> data) {
        this.name = name;
        this.msg = msg;
        this.categorys = Arrays.asList(categorys);
        this.req = req;
        this.datas = new DoubleData[]{data};
    }
    */

    Characteristic(String name, String msg, CharacteristicCategory[] categorys, DoubleData<Characteristic, Characteristic> req, DoubleData<CharacteristicType, Object>... datas) {
        this.name = name;
        this.msg = msg;
        this.categorys = Arrays.asList(categorys);
        this.req = req;
        this.datas = datas;
    }

    public String getName() {
        return name;
    }

    public String getMsg() {
        return msg;
    }

    public DoubleData<Characteristic, Characteristic> getReq() {
        return req;
    }

    public boolean isCategory(CharacteristicCategory cc) {
        return categorys.contains(cc);
    }

    public DoubleData<CharacteristicType, Object>[] getDatas() {
        return datas;
    }
    
    public static Characteristic search(final String str) {
        for(final Characteristic c : values()) {
            if(c.name.equals(str)) {
                return c;
            }
        }
        return null;
    }

    private final String name;
    private final String msg;
    private final List<CharacteristicCategory> categorys;
    private final DoubleData<Characteristic, Characteristic> req;
    private final DoubleData<CharacteristicType, Object>[] datas;

    public enum CharacteristicType {
        SELL_DOWN, // 安価%
        SELL_UP, // 高価%
        QUALITY, // 品質上昇%
        ITEM_ATTACK, // 攻撃アイテム効果%
        ITEM_ATTACK_FIXED, // 攻撃アイテム固定強化        DoubleData<Integer, Integer>
        WEAPON_ADD_DAMAGE, // 武器ダメージ追加ダメージ%
        ITEM_HEAL, // 回復アイテム効果%
        ITEM_HEAL_FIXED, // 回復アイテム固定強化        DoubleData<Integer, Integer>
        ITEM_CRITICAL, // アイテムのクリティカル率%
        ITEM_RANDOM, // アイテムの乱数の最低値を上げる
        ITEM_USE_COUNT, // アイテムの使用回数変更
        DEATH_DAMAGE, // ボス以外戦闘不能率%
        UP_HP, // 最大HP上昇
        UP_MP, // 最大MP上昇
        UP_PARAMETER, // 能力値上昇%
        UP_ATTACK, // 攻撃力上昇
        UP_DEFENSE, // 防御力上昇
        UP_SPEED, // 素早さ上昇
        DOWN_CONSUME_MP, //MP消費減少%
        UP_SKILL, // スキルの威力上昇%
        BUFF_DAMAGE, // <右スロット>秒間 <左スロット>攻撃力上昇        DoubleData<Integer, Integer>
        BUFF_DEFENSE, // <右スロット>秒間 <左スロット>防御力上昇        DoubleData<Integer, Integer>
        BUFF_SPEED, // <右スロット>秒間 <左スロット>素早さ力上昇        DoubleData<Integer, Integer>
        ABNORMALITY, // 状態異常%       DoubleData<AbnormalityType, Integer>
        HEAL_ATTACK, // ダメージをHPへ還元
        CHARACTERISTIC_UP, // 特性強化
        RANGE_BONUS, // 範囲ボーナス
        ;
    }
    
    public enum AbnormalityType {
        SLEEP, // 眠り
        POISON, // 毒
        SLOW, // スロウ
        CURSE, // 呪い
        DARKNESS, // 暗黒
        WEAK, // 弱体化
        NOHEAL, // 回復無効
        SEALED // 封印
    }
}
