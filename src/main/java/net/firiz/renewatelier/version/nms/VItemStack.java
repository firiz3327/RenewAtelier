/*
 * VItemStack.java
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
package net.firiz.renewatelier.version.nms;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.firiz.renewatelier.utils.Chore;
import net.firiz.renewatelier.version.VersionUtils;
import org.bukkit.inventory.ItemStack;

/**
 * @author firiz
 */
public class VItemStack {

    private final Object nmsItem;

    public VItemStack(Object nmsItem) {
        this.nmsItem = nmsItem;
    }

    public Object getNmsItem() {
        return nmsItem;
    }

    /**
     * @return Example: "block.minecraft.stone"
     */
    public String getLocalizationId() {
        return (String) getMethodItem("getItem", "getName");
    }

    /**
     * @return Example: "{id:"minecraft:stone",Count:1b}"
     */
    public String getMinecraftJson() {
        final Object nbtTagComponent = VersionUtils.createNBTTagCompound();
        return getMethodItem(
                new String[]{"save"},
                new Class<?>[][]{new Class<?>[]{nbtTagComponent.getClass()}},
                new Object[][]{new Object[]{nbtTagComponent}}
        ).toString();
    }

    /**
     * @return Example: "minecraft:stone"
     */
    public String getMinecraftId() {
        String json = getMinecraftJson();
        json = json.substring(json.indexOf("id:\"") + 4);
        return json.substring(0, json.indexOf("\""));
    }

    public Object getMethodItem(final String... methodNames) {
        Object obj = nmsItem;
        try {
            for (final String name : methodNames) {
                final Method method = obj.getClass().getMethod(name);
                obj = method.invoke(obj);
            }
            return obj;
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Chore.logWarning(ex);
        }
        return obj;
    }

    public Object getMethodItem(final String[] methodNames, final Object[][] values) {
        final Class<?>[][] clasz = new Class<?>[values.length][];
        for (int i = 0; i < values.length; i++) {
            clasz[i] = new Class<?>[values[i].length];
            for (int j = 0; j < values[i].length; j++) {
                clasz[i][j] = values[i][j].getClass();
            }
        }
        return getMethodItem(methodNames, clasz, values);
    }

    public Object getMethodItem(final String[] methodNames, final Class<?>[][] clasz, final Object[][] values) {
        Object obj = nmsItem;
        try {
            for (int i = 0; i < methodNames.length; i++) {
                final Method method = obj.getClass().getMethod(methodNames[i], clasz[i]);
                obj = method.invoke(obj, values[i]);
            }
            return obj;
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Chore.logWarning(ex);
        }
        return obj;
    }

    public void setString(final String key, final Object value) {
        final Object tag = VersionUtils.getField(nmsItem, "tag");
        VersionUtils.invoke(
                tag,
                "setString",
                new Class<?>[]{String.class, String.class},
                new Object[]{key, value}
        );
    }

    public String getString(final String key) {
        final Object tag = VersionUtils.getField(nmsItem, "tag");
        return (String) VersionUtils.invoke(
                tag,
                "getString",
                new Class<?>[]{String.class},
                new Object[]{key}
        );
    }

    public boolean hasKey(final String key) {
        final Object tag = VersionUtils.getField(nmsItem, "tag");
        return (boolean) VersionUtils.invoke(
                tag,
                "hasKey",
                new Class<?>[]{String.class},
                new Object[]{key}
        );
    }

    public ItemStack getItem() {
        return VersionUtils.asItem(this);
    }

}
