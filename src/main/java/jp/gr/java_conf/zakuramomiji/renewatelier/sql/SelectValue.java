/*
 * SelectConditions.java
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
package jp.gr.java_conf.zakuramomiji.renewatelier.sql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import jp.gr.java_conf.zakuramomiji.renewatelier.utils.DoubleData;

/**
 *
 * @author firiz
 */
public class SelectValue {

    private final String table;
    private final LinkedHashMap<KeyValue, DoubleData<ConditionType, Pipe>> vals;
    private final OrderByType order_type;
    private final List<String> order_bys;
    
    public SelectValue(String table, LinkedHashMap<KeyValue, DoubleData<ConditionType, Pipe>> vals) {
        this(table, vals, null);
    }

    public SelectValue(String table, LinkedHashMap<KeyValue, DoubleData<ConditionType, Pipe>> vals, OrderByType order_type, String... order_bys) {
        this.table = table;
        this.vals = vals;
        this.order_type = order_type;
        this.order_bys = new ArrayList<>(Arrays.asList(order_bys)) {
            @Override
            public String toString() {
                final String s = super.toString();
                return s.substring(1, s.length() - 1);
            }
        };
    }

    public String getTable() {
        return table;
    }

    public LinkedHashMap<KeyValue, DoubleData<ConditionType, Pipe>> getVals() {
        return vals;
    }
    
    public OrderByType getOrderType() {
        return order_type;
    }

    public List<String> getOrderBys() {
        return order_bys;
    }
    
    public static enum OrderByType {
        ASC,
        DESC
    }

    public static enum Pipe {
        NONE(""),
        AND("AND"),
        OR("OR");

        private final String word;

        private Pipe(String word) {
            this.word = word;
        }

        @Override
        public final String toString() {
            return word;
        }
    }

    public static enum ConditionType {
        EQUALS("="), // 等しい
        NOT_EQUALS("!="), // 等しくない
        MORE(">="), // 以上
        MORE_THAN(">"), // より大きい
        LESS("<="), // 以下
        LESS_THAN("<"), // より小さい
        BETWEEN("BETWEEN"), // $x以上 $y以下
        NOT_BETWEEN("NOT BETWEEN"), // $x以上 $y以下 を除いた部分
        LIKE("LIKE"), // 文字列を検索するための演算子
        IN("IN"), // 対象の値が指定した値のリストの中にあるか
        ;

        private final String word;

        private ConditionType(String word) {
            this.word = word;
        }

        @Override
        public final String toString() {
            return word;
        }
    }

    public static class KeyValue {

        private final String column;
        private final String value;

        public KeyValue(String column) {
            this.column = column;
            this.value = null;
        }

        public KeyValue(String column, Object value) {
            this.column = column;
            this.value = value instanceof String
                    ? "'".concat(replace((String) value)).concat("'")
                    : value == null ? null : replace(value.toString());
        }

        private String replace(final String val) {
            return val.replace("'", "\"").replace("\\", "/");
        }

        public String getColumn() {
            return column;
        }

        public String getValue() {
            return value;
        }
    }

    public static class BetweenValue extends KeyValue {

        public BetweenValue(String column, int min, int max) {
            super(column, new DoubleData<Integer, Integer>() {
                {
                    setLeft(min);
                    setRight(max);
                }

                @Override
                public String toString() {
                    return min + " AND " + max;
                }
            });
        }

    }

    public static class InValue extends KeyValue {

        public InValue(String column, int... val) {
            super(column, new ArrayList<>(Arrays.asList(val)) {
                @Override
                public String toString() {
                    final String str = super.toString();
                    return str.substring(1, str.length() - 1);
                }
            });
        }

    }

}
