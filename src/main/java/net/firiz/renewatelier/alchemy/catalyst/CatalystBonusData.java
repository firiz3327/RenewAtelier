/*
 * CatalystBonusData.java
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
package net.firiz.renewatelier.alchemy.catalyst;

import java.util.ArrayList;
import java.util.List;

import net.firiz.renewatelier.alchemy.material.AlchemyAttribute;
import net.firiz.renewatelier.characteristic.Characteristic;
import net.firiz.renewatelier.utils.TRunnable;
import net.firiz.renewatelier.utils.doubledata.FinalDoubleData;
import net.md_5.bungee.api.ChatColor;

/**
 * @author firiz
 */
public class CatalystBonusData {

    private static final String ERROR_NAME = "ERROR";
    private final BonusType type;
    private final int x;
    private final Object y;

    public CatalystBonusData(BonusType type, int x) {
        this.type = type;
        this.x = x;
        this.y = null;
    }

    public CatalystBonusData(BonusType type, int x, String y) {
        this.type = type;
        this.x = x;
        this.y = type.yConversionToObject(y);
    }

    public String getName() {
        return type.name
                .replace("$x", (x >= 0 ? "+" : "-") + x)
                .replace("$y", y != null ? (String) type.yConversionToString(y) : ERROR_NAME);
    }

    public List<String> getDesc() {
        final String[] desc = type.desc
                .replace("$x", String.valueOf(x))
                .replace("$y", y != null ? (String) type.yConversionToString(y) : ERROR_NAME)
                .replace("$z", type.descRepletion != null ? type.descRepletion.run(x) : ERROR_NAME)
                .split("\n");
        final List<String> result = new ArrayList<>();
        for (final String str : desc) {
            result.add(ChatColor.GRAY + str);
        }
        return result;
    }

    public BonusType getType() {
        return type;
    }

    public int getX() {
        return x;
    }

    public Object getY() {
        return y;
    }

    public enum BonusType {
        INHERITING(
                "特性引継ぎ可能数$x",
                "調合終了時に引き\n継がれる特性の数が\n$xつ$zます",
                false,
                null,
                o -> o >= 0 ? "増え" : "減り"
        ), // ok
        QUALITY(
                "品質$x",
                "できあがるアイテムの\n最終的な品質が\n$z$xされます",
                false,
                null,
                o -> o >= 0 ? "+" : "-"
        ), // ok
        QUALITY_PERCENT(
                "品質$x%",
                "できあがるアイテムの\n最終的な品質が\n$x%$zします",
                false,
                null,
                o -> o >= 0 ? "％増加" : "％減少"
        ), // ok
        AMOUNT(
                "作成数$x",
                "",
                false,
                null,
                o -> o >= 0 ? "増加" : "減少"
        ), // ok
        USECOUNT(
                "使用回数$x",
                "できあがるアイテムが\n使用できる場合、使用\n回数が$x回$zます",
                false,
                null,
                o -> o >= 0 ? "増え" : "減り"
        ), // 使用回数ありのアイテムを作っていない
        STARLEVEL(
                "効果レベル$x・$y",
                "$y色で示されている\n効果のレベルを全て\n$x段階$zさせます",
                false,
                new FinalDoubleData<>(
                        AlchemyAttribute::valueOf,
                        o -> ((AlchemyAttribute) o).getName()
                ),
                o -> o >= 0 ? "アップ" : "ダウン"
        ), // ok
        SIZE(
                "サイズ$x",
                "できあがるアイテムの\n最終的なサイズが\n$x段階$zなります",
                false,
                null,
                o -> o >= 0 ? "大きく" : "小さく"
        ),
        INGREDIENT_AMOUNT_PERCENT(
                "錬金成分量$x%",
                "次に投入する材料の\n錬金成分量が通常より\n$x%$zします",
                true,
                null,
                o -> o >= 0 ? "増加" : "減少"
        ), // ok
        CHARACTERISTIC("$y付与",
                "できあがるアイテムに\n「$y」の特性が\n追加されます",
                false,
                new FinalDoubleData<>(
                        id -> {
                            Characteristic c;
                            try {
                                c = Characteristic.getCharacteristic(id);
                            } catch (IllegalArgumentException e) {
                                c = Characteristic.search(id);
                            }
                            return c;
                        },
                        o -> ((Characteristic) o).getName()
                )
        ); // ok

        private final String name;
        private final String desc;
        private final boolean once; // 使い切りであるかどうか
        private final FinalDoubleData<TRunnable<String, Object>, TRunnable<Object, String>> yParse; // FinalDoubleData<文字列から特定のデータへ変換用, オブジェクトからデータ取得用>
        private final TRunnable<Integer, String> descRepletion;

        BonusType(final String name, final String desc, final boolean once, final FinalDoubleData<TRunnable<String, Object>, TRunnable<Object, String>> yParse) {
            this.name = name;
            this.desc = desc;
            this.once = once;
            this.yParse = yParse;
            this.descRepletion = null;
        }

        BonusType(final String name, final String desc, final boolean once, final FinalDoubleData<TRunnable<String, Object>, TRunnable<Object, String>> yParse, final TRunnable<Integer, String> descRepletion) {
            this.name = name;
            this.desc = desc;
            this.once = once;
            this.yParse = yParse;
            this.descRepletion = descRepletion;
        }

        public boolean isOnce() {
            return once;
        }

        /**
         * 文字列から特定のオブジェクトに変換します
         *
         * @param y
         * @return
         */
        private Object yConversionToObject(final String y) {
            if (yParse != null) {
                return yParse.getLeft().run(y);
            }
            return y;
        }

        /**
         * 特定のオブジェクトから特定の文字列に変換します
         *
         * @param y
         * @return
         */
        private Object yConversionToString(final Object y) {
            if (yParse != null) {
                return yParse.getRight().run(y);
            }
            return y;
        }
    }

}
