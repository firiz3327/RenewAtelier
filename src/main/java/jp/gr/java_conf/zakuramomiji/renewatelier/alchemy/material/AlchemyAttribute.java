/*
 * AlchemyAttribute.java
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

package jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.material;

import net.md_5.bungee.api.ChatColor;

/**
 *
 * @author firiz
 */
public enum AlchemyAttribute {
    RED(ChatColor.RED, "赤"),
    BLUE(ChatColor.BLUE, "青"),
    GREEN(ChatColor.GREEN, "緑"),
    YELLOW(ChatColor.YELLOW, "黄"),
    PURPLE(ChatColor.DARK_PURPLE, "紫")
    ;
    private final String color;
    private final String name;
    
    AlchemyAttribute(ChatColor color, String name) {
        this.color = color.toString();
        this.name = name;
    }
    
    public String getColor() {
        return color;
    }
    
    public String getName() {
        return name;
    }
    
    public static AlchemyAttribute searchColor(String color) {
        for(AlchemyAttribute type : AlchemyAttribute.values()) {
            if(type.getColor().equals(color)) {
                return type;
            }
        }
        return null;
    }
}
