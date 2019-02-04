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
package jp.gr.java_conf.zakuramomiji.renewatelier.version.nms;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.gr.java_conf.zakuramomiji.renewatelier.utils.Chore;
import jp.gr.java_conf.zakuramomiji.renewatelier.version.VersionUtils;

/**
 *
 * @author firiz
 */
public class VItemStack {

    private final Object nmsItem;

    public VItemStack(Object nmsItem) {
        this.nmsItem = nmsItem;
    }

    public String getItemName() {
        return (String) getMethodItem("getItem", "getName");
    }

    public String getMinecraftId() {
        return getMethodItem(
                new String[]{"save"},
                new String[][]{new String[]{"NBTTagCompound"}},
                new Object[][]{
                    new Object[]{VersionUtils.createNBTTagCompound()}
                }
        ).toString();
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
            Logger.getLogger(VItemStack.class.getName()).log(Level.SEVERE, null, ex);
        }
        return obj;
    }

    public Object getMethodItem(final String[] methodNames, final Class[][] clasz, final Object[][] values) {
        Object obj = nmsItem;
        try {
            for (int i = 0; i < methodNames.length; i++) {
                final Method method = obj.getClass().getMethod(methodNames[i], clasz[i]);
                obj = method.invoke(obj, values[i]);
            }
            return obj;
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(VItemStack.class.getName()).log(Level.SEVERE, null, ex);
        }
        return obj;
    }

    public Object getMethodItem(final String[] methodNames, final String[][] clasz, final Object[][] values) {
        Object obj = nmsItem;
        try {
            for (int i = 0; i < methodNames.length; i++) {
                check_methods:
                for (final Method method : obj.getClass().getMethods()) {
                    final Class<?>[] parameterTypes = method.getParameterTypes();
                    if (parameterTypes.length == clasz[i].length) {
                        for (int j = 0; j < parameterTypes.length; j++) {
                            final Class<?> type = parameterTypes[j];
                            if (!type.getName().equalsIgnoreCase(clasz[i][j])) {
                                continue check_methods;
                            }
                        }
                    }
                    obj = method.invoke(obj, values[i]);
                    break;
                }
            }
            return obj;
        } catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Chore.log(ex);
        }
        return obj;
    }

}
