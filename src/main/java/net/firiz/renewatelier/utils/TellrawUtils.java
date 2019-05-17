/*
 * TellrawUtils.java
 * 
 * Copyright (c) 2019 firiz.
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
package net.firiz.renewatelier.utils;

import net.firiz.renewatelier.version.VersionUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author firiz
 */
public class TellrawUtils {

    public static ClickEvent createClickEvent(final ClickEvent.Action action, final String value) {
        return new ClickEvent(
                action,
                value
        );
    }

    public static HoverEvent createHoverEvent(final String text) {
        return new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(text).create()
        );
    }

    public static HoverEvent createHoverEvent(final ItemStack item) {
        return new HoverEvent(
                HoverEvent.Action.SHOW_ITEM,
                parseItemComponents(item)
        );
    }

    private static BaseComponent[] parseItemComponents(final ItemStack item) {
        return new BaseComponent[]{
            new TextComponent(VersionUtils.asVItemCopy(item).getMinecraftJson())
        };
    }
}
