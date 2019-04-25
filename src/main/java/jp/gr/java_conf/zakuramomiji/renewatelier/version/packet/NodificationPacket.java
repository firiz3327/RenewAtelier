/*
 * RecipePacket.java
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
package jp.gr.java_conf.zakuramomiji.renewatelier.version.packet;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.server.v1_14_R1.AdvancementProgress;
import net.minecraft.server.v1_14_R1.CriterionProgress;
import net.minecraft.server.v1_14_R1.MinecraftKey;
import net.minecraft.server.v1_14_R1.PacketPlayOutAdvancements;
import net.minecraft.server.v1_14_R1.PacketPlayOutRecipes;
import org.bukkit.entity.Player;

/**
 *
 * @author firiz
 */
public class NodificationPacket {

    public static void sendRecipe(final Player player, final String id, final boolean remove) {
        final ArrayList<MinecraftKey> var1 = new ArrayList<MinecraftKey>() {
            {
                add(new MinecraftKey(id));
            }
        };
        final PacketPlayOutRecipes packet = new PacketPlayOutRecipes(
                remove ? PacketPlayOutRecipes.Action.REMOVE : PacketPlayOutRecipes.Action.ADD,
                var1,
                new ArrayList<>(),
                false,
                false,
                false,
                false
        );
        PacketUtils.sendPacket(player, packet);
    }

    @Deprecated
    public static void sendAdvancement(final Player player, final String id, final boolean remove) {
        final HashMap<MinecraftKey, AdvancementProgress> var3 = new HashMap<MinecraftKey, AdvancementProgress>() {
            {
                final AdvancementProgress progress = new AdvancementProgress();
                if (!remove) {
                    try {
                        final Field a = progress.getClass().getDeclaredField("a");
                        a.setAccessible(true);
                        a.set(progress, new HashMap<String, CriterionProgress>() {
                            {
                                final CriterionProgress cp = new CriterionProgress();
                                final Field b2 = cp.getClass().getDeclaredField("b");
                                b2.setAccessible(true);
                                b2.set(cp, new Date());
                                put("traded", cp);
                            }
                        });

                        final Field b = progress.getClass().getDeclaredField("b");
                        b.setAccessible(true);
                        b.set(progress, new String[][]{new String[]{"traded"}});
                    } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
                        Logger.getLogger(NodificationPacket.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                put(new MinecraftKey(id), progress);
            }
        };
        final PacketPlayOutAdvancements packet = new PacketPlayOutAdvancements(
                false,
                new ArrayList<>(),
                new LinkedHashSet<>(),
                var3
        );
        PacketUtils.sendPacket(player, packet);
    }

}
