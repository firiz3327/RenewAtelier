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
package jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.catalyst;

import java.util.ArrayList;
import java.util.List;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.material.AlchemyAttribute;
import jp.gr.java_conf.zakuramomiji.renewatelier.characteristic.Characteristic;
import jp.gr.java_conf.zakuramomiji.renewatelier.utils.ReturnObjectRunnable;
import net.md_5.bungee.api.ChatColor;

/**
 *
 * @author firiz
 */
public class CatalystBonusData {

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
        this.y = type.y_parse(y);
    }

    public String getName() {
        return type.name
                .replace("$x", (x >= 0 ? "+" : "-") + x)
                .replace("$y", y != null ? (String) type.y_parse(y, true) : "ERROR");
    }

    public List<String> getDesc() {
        final String[] desc = type.desc
                .replace("$x", String.valueOf(x))
                .replace("$y", y != null ? (String) type.y_parse(y, true) : "ERROR")
                .replace("$z", type.descRepletion != null ? type.descRepletion.run(x).toString() : "ERROR")
                .split("\n");
        final List<String> result = new ArrayList<>();
        for(final String str : desc) {
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
                (Object... objs) -> ((int) objs[0]) >= 0 ? "増え" : "減り"
        ), // ok
        QUALITY(
                "品質$x",
                "できあがるアイテムの\n最終的な品質が\n$z$xされます",
                false,
                null,
                (Object... objs) -> ((int) objs[0]) >= 0 ? "+" : "-"
        ), // ok
        QUALITY_PERCENT(
                "品質$x%",
                "できあがるアイテムの\n最終的な品質が\n$x%$zします",
                false,
                null,
                (Object... objs) -> ((int) objs[0]) >= 0 ? "増加" : "減少"
        ), // ok
        AMOUNT(
                "作成数$x",
                "",
                false,
                null,
                (Object... objs) -> ((int) objs[0]) >= 0 ? "増加" : "減少"
        ), // ok
        USECOUNT(
                "使用回数$x",
                "できあがるアイテムが\n使用できる場合、使用\n回数が$x回$zます",
                false,
                null,
                (Object... objs) -> ((int) objs[0]) >= 0 ? "増え" : "減り"
        ), // 使用回数ありのアイテムを作っていない
        STARLEVEL(
                "効果レベル$x・$y",
                "$y色で示されている\n効果のレベルを全て\n$x段階$zさせます",
                false,
                (Object... objs) -> { // ok type: STARLEVEL, x: level, y: AlchemyAttribute_ID
                    if (!(Boolean) objs[1]) {
                        return AlchemyAttribute.valueOf((String) objs[0]);
                    }
                    return ((AlchemyAttribute) objs[0]).getName();
                },
                (Object... objs) -> ((int) objs[0]) >= 0 ? "アップ" : "ダウン"
        ), // ok
        SIZE(
                "サイズ$x",
                "できあがるアイテムの\n最終的なサイズが\n$x段階$zなります",
                false,
                null,
                (Object... objs) -> ((int) objs[0]) >= 0 ? "大きく" : "小さく"
        ),
        INGREDIENT_AMOUNT_PERCENT(
                "錬金成分量$x%",
                "次に投入する材料の\n錬金成分量が通常より\n$x%$zします",
                true,
                null,
                (Object... objs) -> ((int) objs[0]) >= 0 ? "増加" : "減少"
        ), // ok
        CHARACTERISTIC("$y付与", "できあがるアイテムに\n「$y」の特性が\n追加されます", false, (Object... objs) -> { // ok type: CHARACTERISTIC, y: Characteristic_ID
            if (!(Boolean) objs[1]) {
                return Characteristic.valueOf((String) objs[0]);
            }
            return ((Characteristic) objs[0]).getName();
        }); // ok

        private final String name;
        private final String desc;
        private final boolean once; // 使い切りであるかどうか
        private final ReturnObjectRunnable y_parse;
        private final ReturnObjectRunnable descRepletion;

        BonusType(final String name, final String desc, final boolean once) {
            this.name = name;
            this.desc = desc;
            this.y_parse = null;
            this.once = once;
            this.descRepletion = null;
        }

        BonusType(final String name, final String desc, final boolean once, final ReturnObjectRunnable y_parse) {
            this.name = name;
            this.desc = desc;
            this.once = once;
            this.y_parse = y_parse;
            this.descRepletion = null;
        }

        BonusType(final String name, final String desc, final boolean once, final ReturnObjectRunnable y_parse, final ReturnObjectRunnable descRepletion) {
            this.name = name;
            this.desc = desc;
            this.once = once;
            this.y_parse = y_parse;
            this.descRepletion = descRepletion;
        }
        
        public boolean isOnce() {
            return once;
        }

        private Object y_parse(final String y_str) {
            return y_parse(y_str, false);
        }

        private Object y_parse(final Object y, final boolean visible) {
            if (y_parse != null) {
                return y_parse.run(y, visible);
            }
            return y;
        }
    }

}
