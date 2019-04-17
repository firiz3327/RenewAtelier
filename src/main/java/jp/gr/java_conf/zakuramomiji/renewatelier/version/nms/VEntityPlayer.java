/*
 * VEntityPlayer.java
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

import java.lang.reflect.Field;
import java.util.UUID;
import jp.gr.java_conf.zakuramomiji.renewatelier.utils.Chore;
import jp.gr.java_conf.zakuramomiji.renewatelier.version.VersionUtils;
import org.bukkit.Location;
import org.bukkit.World;

/**
 *
 * @author firiz
 */
public class VEntityPlayer {

    private final Object entityPlayer; // EntityPlayer class
    private final int id;
    private final UUID uuid;
    private final String name;
    private final Location location;

    public VEntityPlayer(Object entityPlayer, int id, UUID uuid, String name, Location location) {
        this.entityPlayer = entityPlayer;
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.location = location;
    }

    public Object getEntityPlayer() {
        return entityPlayer;
    }

    public int getId() {
        return id;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public World getWorld() {
        return location.getWorld();
    }

    public void setListName(final String listName) {
        try {
            final Field listNameField = entityPlayer.getClass().getField("listName");
            listNameField.set(entityPlayer, VersionUtils.createChatMessage(listName));
        } catch (SecurityException | IllegalArgumentException | NoSuchFieldException | IllegalAccessException ex) {
            Chore.logWarning(ex);
        }
    }

}
