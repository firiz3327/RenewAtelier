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

import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.material.AlchemyAttribute;
import jp.gr.java_conf.zakuramomiji.renewatelier.characteristic.Characteristic;
import jp.gr.java_conf.zakuramomiji.renewatelier.utils.ReturnObjectRunnable;

/**
 *
 * @author kanzakiayaka
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
        INHERITING("特性引継ぎ可能数 $x"), // ok
        QUALITY("品質 $x"), // ok
        QUALITY_PERCENT("品質 $x%"), // ok
        AMOUNT("作成数 $x"), // ok
        USECOUNT("使用回数 $x"),
        STARLEVEL("効果レベル $x・$y", (Object... objs) -> { // ok type: STARLEVEL, x: level, y: AlchemyAttribute_ID
            if(!(Boolean) objs[1]) {
                return AlchemyAttribute.valueOf((String) objs[0]);
            }
            return ((AlchemyAttribute) objs[0]).getName();
        }),
        SIZE("サイズ $x"),
        INGREDIENT_AMOUNT_PERCENT("錬金成分量 $x%"), // ok
        CHARACTERISTIC("$y 付与", (Object... objs) -> { // ok type: CHARACTERISTIC, y: Characteristic_ID
            if (!(Boolean) objs[1]) {
                return Characteristic.valueOf((String) objs[0]);
            }
            return ((Characteristic) objs[0]).getName();
        });

        private final String name;
        private final ReturnObjectRunnable y_parse;

        private BonusType(final String name) {
            this.name = name;
            this.y_parse = null;
        }

        private BonusType(final String name, final ReturnObjectRunnable y_parse) {
            this.name = name;
            this.y_parse = y_parse;
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
