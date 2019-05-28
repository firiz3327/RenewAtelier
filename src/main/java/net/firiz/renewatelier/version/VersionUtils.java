/*
 * VersionUtil.java
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
package net.firiz.renewatelier.version;

import net.firiz.renewatelier.version.nms.VItemStack;
import net.minecraft.server.v1_14_R1.ChatMessage;
import net.minecraft.server.v1_14_R1.NBTTagCompound;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *
 * @author firiz
 */
public class VersionUtils {

    public static Object createNBTTagCompound() {
        return new NBTTagCompound();
    }

    public static Object createChatMessage(final String msg) {
        return new ChatMessage(msg);
    }

    public static VItemStack asVItemCopy(final ItemStack item) {
        return new VItemStack(CraftItemStack.asNMSCopy(item));
    }

    public static ItemStack asItem(final VItemStack item) {
        final net.minecraft.server.v1_14_R1.ItemStack nms = (net.minecraft.server.v1_14_R1.ItemStack) item.getNmsItem();
        return CraftItemStack.asBukkitCopy(nms);
    }

    public static Field getField(Object obj, String name) {
        try {
            final Field field = obj.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setField(Object obj, String name, Object value) {
        try {
            getField(obj, name).set(obj, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static Object invoke(Object obj, String name, Class<?>[] params, Object[] args) {
        try {
            final Method method = obj.getClass().getMethod(name, params);
            method.setAccessible(true);
            return method.invoke(obj, args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

}