/*
 * FakeEntity.java
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
package net.firiz.renewatelier.version.packet;

import java.util.UUID;
import net.firiz.renewatelier.utils.DoubleData;
import net.minecraft.server.v1_14_R1.EntityTypes;
import org.bukkit.entity.EntityType;

/**
 *
 * @author firiz
 */
public class FakeEntity {

    private static int lastId = -1;

    private final int entityId;
    private final UUID uniqueId;
    private final FakeEntityType type;
    private final int typeId;
    private final boolean object;
    private final int objectData;

    public FakeEntity(int entityId, FakeEntityType type, int objectData) {
        this.entityId = entityId;
        this.uniqueId = UUID.randomUUID();
        this.type = type;
        this.objectData = objectData;

        final DoubleData<Boolean, Integer> check = ObjectChecker.check(type.type);
        this.typeId = check.getRight();
        this.object = check.getLeft();
    }

    public FakeEntity(FakeEntityType type, int objectData) {
        lastId--;
        this.entityId = lastId;
        this.uniqueId = UUID.randomUUID();
        this.type = type;
        this.objectData = objectData;

        final DoubleData<Boolean, Integer> check = ObjectChecker.check(type.type);
        this.typeId = check.getRight();
        this.object = check.getLeft();
    }

    public FakeEntity(FakeEntityType type) {
        this(type, 0);
    }

    public static int getLastId() {
        return lastId;
    }

    public int getEntityId() {
        return entityId;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public FakeEntityType getType() {
        return type;
    }

    public int getTypeId() {
        return typeId;
    }

    public boolean isObject() {
        return object;
    }

    public int getObjectData() {
        return objectData;
    }

    public enum FakeEntityType {
        ARMOR_STAND(EntityType.ARMOR_STAND, EntityTypes.ARMOR_STAND);

        private final EntityType type;
        private final EntityTypes<?> typeNms;

        FakeEntityType(EntityType type, EntityTypes<?> typeNms) {
            this.type = type;
            this.typeNms = typeNms;
        }

        public EntityType getType() {
            return type;
        }

        public EntityTypes<?> getTypeNms() {
            return typeNms;
        }
    }

    private enum ObjectChecker {
        BOAT(EntityType.BOAT, 1),
        DROPPED_ITEM(EntityType.DROPPED_ITEM, 2),
        AREA_EFFECT_CLOUD(EntityType.AREA_EFFECT_CLOUD, 3),
        MINECART(EntityType.MINECART, 10),
        PRIMED_TNT(EntityType.PRIMED_TNT, 50),
        ENDER_CRYSTAL(EntityType.ENDER_CRYSTAL, 51),
//        TIPPED_ARROW(EntityType.TIPPED_ARROW, 60), 1.14 deleted
        SNOWBALL(EntityType.SNOWBALL, 61),
        EGG(EntityType.EGG, 62),
        FIREBALL(EntityType.FIREBALL, 63),
        SMALL_FIREBALL(EntityType.SMALL_FIREBALL, 64),
        ENDER_PEARL(EntityType.ENDER_PEARL, 65),
        WITHER_SKULL(EntityType.WITHER_SKULL, 66),
        SHULKER_BULLET(EntityType.SHULKER_BULLET, 67),
        FALLING_BLOCK(EntityType.FALLING_BLOCK, 70),
        ITEM_FRAME(EntityType.ITEM_FRAME, 71),
        ENDER_SIGNAL(EntityType.ENDER_SIGNAL, 72),
        SPLASH_POTION(EntityType.SPLASH_POTION, 73),
        THROWN_EXP_BOTTLE(EntityType.THROWN_EXP_BOTTLE, 75),
        FIREWORK(EntityType.FIREWORK, 76),
        LEASH_HITCH(EntityType.LEASH_HITCH, 77),
        ARMOR_STAND(EntityType.ARMOR_STAND, 78),
        FISHING_HOOK(EntityType.FISHING_HOOK, 90),
        SPECTRAL_ARROW(EntityType.SPECTRAL_ARROW, 91),
        DRAGON_FIREBALL(EntityType.DRAGON_FIREBALL, 93);

        private final EntityType type;
        private final int value;

        ObjectChecker(EntityType type, int value) {
            this.type = type;
            this.value = value;
        }

        public static DoubleData<Boolean, Integer> check(final EntityType type) {
            for (final ObjectChecker c : values()) {
                if (c.type == type) {
                    return new DoubleData<>(true, c.value);
                }
            }
            return new DoubleData<>(false, type.ordinal());
        }

    }
}