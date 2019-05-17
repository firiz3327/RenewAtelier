/*
 * RecipeLevelEffect.java
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
package net.firiz.renewatelier.alchemy.recipe;

/**
 *
 * @author firiz
 */
public class RecipeLevelEffect {
    private final RecipeLEType type;
    private final int count;

    public RecipeLevelEffect(RecipeLEType type, int count) {
        this.type = type;
        this.count = count;
    }
    
    public int getCount(RecipeLEType type) {
        if(type == this.type) {
            return count;
        }
        return 0;
    }
    
    public int getCount() {
        return count;
    }
    
    public RecipeLEType getType() {
        return type;
    }

    public enum RecipeLEType {
        ADD_INHERITING("特性引継ぎ可能数", true), // 特性引継ぎ可能数増加 +x
        ADD_QUALITY("品質", true), // 品質増加 +x
        ADD_AMOUNT("作成数", true), // 作成数増加 +x
        ADD_USECOUNT("使用回数", true), // 使用回数増加 +x
        ADD_VERTICAL_ROTATION("上下反転", false), // 上下反転
        ADD_HORIZONTAL_ROTATION("左右反転", false), // 左右反転
        ADD_ROTATION("回転", false) // 回転
        ;
        
        private final String name;
        private final boolean viewnumber;
        
        RecipeLEType(String name, boolean viewnumber) {
            this.name = name;
            this.viewnumber = viewnumber;
        }
        
        public String getName() {
            return name;
        }
        
        public boolean isViewNumber() {
            return viewnumber;
        }
    }

}
