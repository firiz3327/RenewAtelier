/*
 * AlchemyCircle.java
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
package jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.kettle;

import jp.gr.java_conf.zakuramomiji.renewatelier.utils.Chore;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author kuro
 */
public enum AlchemyCircle {
//    WHITE_CIRCLE(1525, 10),
    // White
    WHITE(0, 0, 1526, 110),
    WHITE_B_CIRCLE_PURPLE(0, 5, 1527, 16),
    WHITE_B_CIRCLE_RED(0, 1, 1528, 12),
    WHITE_B_CIRCLE_BLUE(0, 2, 1529, 13),
    WHITE_B_CIRCLE_GREEN(0, 3, 1530, 14),
    WHITE_B_CIRCLE_YELLOW(0, 4, 1531, 15),
    // Red
    RED_CIRCLE(1, 1532, 20),
    RED(1, 1, 1533, 220),
    RED_B_CIRCLE_PURPLE(1, 5, 1534, 26),
    RED_B_CIRCLE_RED(1, 1, 1535, 22),
    RED_B_CIRCLE_BLUE(1, 2, 1536, 23),
    RED_B_CIRCLE_GREEN(1, 3, 1537, 24),
    RED_B_CIRCLE_YELLOW(1, 4, 1538, 25),
    // Blue
    BLUE_CIRCLE(2, 1539, 30),
    BLUE(2, 2, 1540, 330),
    BLUE_B_CIRCLE_PURPLE(2, 5, 1541, 36),
    BLUE_B_CIRCLE_RED(2, 1, 1542, 32),
    BLUE_B_CIRCLE_BLUE(2, 2, 1543, 33),
    BLUE_B_CIRCLE_GREEN(2, 3, 1544, 34),
    BLUE_B_CIRCLE_YELLOW(2, 4, 1545, 35),
    // Green
    GREEN_CIRCLE(3, 1546, 40),
    GREEN(3, 3, 1547, 440),
    GREEN_B_CIRCLE_PURPLE(3, 5, 1548, 46),
    GREEN_B_CIRCLE_RED(3, 1, 1549, 42),
    GREEN_B_CIRCLE_BLUE(3, 2, 1550, 43),
    GREEN_B_CIRCLE_GREEN(3, 3, 1551, 44),
    GREEN_B_CIRCLE_YELLOW(3, 4, 1552, 45),
    // Yellow
    YELLOW_CIRCLE(4, 1553, 50),
    YELLOW(4, 4, 1554, 550),
    YELLOW_B_CIRCLE_PURPLE(4, 5, 1555, 56),
    YELLOW_B_CIRCLE_RED(4, 1, 1556, 52),
    YELLOW_B_CIRCLE_BLUE(4, 2, 1557, 53),
    YELLOW_B_CIRCLE_GREEN(4, 3, 1558, 54),
    YELLOW_B_CIRCLE_YELLOW(4, 4, 1559, 55),
    // Purple
    PURPLE_CIRCLE(5, 1512, 60),
    PURPLE(5, 5, 1513, 660),
    PURPLE_B_CIRCLE_PURPLE(5, 5, 1514, 66),
    PURPLE_B_CIRCLE_RED(5, 1, 1515, 62),
    PURPLE_B_CIRCLE_BLUE(5, 2, 1516, 63),
    PURPLE_B_CIRCLE_GREEN(5, 3, 1517, 64),
    PURPLE_B_CIRCLE_YELLOW(5, 4, 1518, 65),;

    private final int type;
    private final int type2;
    private final int data;
    private final int value;

    private AlchemyCircle(final int type, final int data, final int value) {
        this.type = type;
        this.type2 = -1;
        this.data = data;
        this.value = value;
    }

    private AlchemyCircle(final int type, final int type2, final int data, final int value) {
        this.type = type;
        this.type2 = type2;
        this.data = data;
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public int getCircleType() {
        return type2;
    }

    private static AlchemyCircle sertchValue(final int value) {
        for (final AlchemyCircle ai : AlchemyCircle.values()) {
            if (ai.value == value) {
                return ai;
            }
        }
        return null;
    }

    public static AlchemyCircle sertchData(final int data) {
        for (final AlchemyCircle ai : AlchemyCircle.values()) {
            if (ai.data == data) {
                return ai;
            }
        }
        return null;
    }

    public static ItemStack getCircle(final String value, final ItemStack sitem) {
        return getCircle(Chore.colorCint(value), sitem);
    }

    public static ItemStack getCircle(final int value, final ItemStack sitem) {
        if (Chore.getType(sitem) == Material.DIAMOND_HOE) {
            final AlchemyCircle ai = sertchData(Chore.getDamage(sitem));
            if (ai != null) {
                if (!String.valueOf(ai.value).substring(1, 2).equals("0")) {
                    final int v2 = Integer.parseInt(String.valueOf(ai.value).substring(0, 1));
                    return i(value, v2, sitem);
                }
            }
        }
        return i(0, value, sitem);
    }

    private static ItemStack i(final int v1, final int v2, final ItemStack sitem) {
        final ItemStack item = new ItemStack(Material.DIAMOND_HOE, 1);
        final int damage = AlchemyCircle.sertchValue(Integer.parseInt(v2 + "" + v1)).data;
        final ItemMeta meta = item.getItemMeta();
        Chore.setDamage(meta, damage);
        if (sitem != null) {
            final ItemMeta smeta = sitem.getItemMeta();
            meta.setDisplayName(smeta.getDisplayName());
            meta.setLore(smeta.getLore());
        } else {
            meta.setDisplayName("-");
        }
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

}

/*
 1525 触媒・白 輪
 1526 触媒・白 芯
 1527 触媒・白 芯輪白
 1528 触媒・白 芯輪赤
 1529 触媒・白 芯輪青
 1530 触媒・白 芯輪緑
 1531 触媒・白 芯輪黄

 1532 触媒・赤 輪
 1533 触媒・赤 芯
 1534 触媒・赤 芯輪白
 1535 触媒・赤 芯輪赤
 1536 触媒・赤 芯輪青
 1537 触媒・赤 芯輪緑
 1538 触媒・赤 芯輪黄

 1539 触媒・青 輪
 1540 触媒・青 芯
 1541 触媒・青 芯輪白
 1542 触媒・青 芯輪赤
 1543 触媒・青 芯輪青
 1544 触媒・青 芯輪緑
 1545 触媒・青 芯輪黄

 1546 触媒・緑 輪
 1547 触媒・緑 芯
 1548 触媒・緑 芯輪白
 1549 触媒・緑 芯輪赤
 1550 触媒・緑 芯輪青
 1551 触媒・緑 芯輪緑
 1552 触媒・緑 芯輪黄

 1553 触媒・黄 輪
 1554 触媒・黄 芯
 1555 触媒・黄 芯輪白
 1556 触媒・黄 芯輪赤
 1557 触媒・黄 芯輪青
 1558 触媒・黄 芯輪緑
 1559 触媒・黄 芯輪黄

 1560 錬金釜GUI・上
 1561 錬金釜GUI・下
 */
