package net.firiz.renewatelier.alchemy.kettle;

import net.firiz.renewatelier.utils.minecraft.ItemUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

/**
 * @author kuro
 */
public enum AlchemyCircle {
    //    WHITE_CIRCLE(1526, 10),
    // White
    WHITE(0, 0, 1527, 110),
    WHITE_B_CIRCLE_PURPLE(0, 5, 1528, 16),
    WHITE_B_CIRCLE_RED(0, 1, 1529, 12),
    WHITE_B_CIRCLE_BLUE(0, 2, 1530, 13),
    WHITE_B_CIRCLE_GREEN(0, 3, 1531, 14),
    WHITE_B_CIRCLE_YELLOW(0, 4, 1532, 15),
    // Red
    RED_CIRCLE(1, 1533, 20),
    RED(1, 1, 1534, 220),
    RED_B_CIRCLE_PURPLE(1, 5, 1535, 26),
    RED_B_CIRCLE_RED(1, 1, 1536, 22),
    RED_B_CIRCLE_BLUE(1, 2, 1537, 23),
    RED_B_CIRCLE_GREEN(1, 3, 1538, 24),
    RED_B_CIRCLE_YELLOW(1, 4, 1539, 25),
    // Blue
    BLUE_CIRCLE(2, 1540, 30),
    BLUE(2, 2, 1541, 330),
    BLUE_B_CIRCLE_PURPLE(2, 5, 1542, 36),
    BLUE_B_CIRCLE_RED(2, 1, 1543, 32),
    BLUE_B_CIRCLE_BLUE(2, 2, 1544, 33),
    BLUE_B_CIRCLE_GREEN(2, 3, 1545, 34),
    BLUE_B_CIRCLE_YELLOW(2, 4, 1546, 35),
    // Green
    GREEN_CIRCLE(3, 1547, 40),
    GREEN(3, 3, 1548, 440),
    GREEN_B_CIRCLE_PURPLE(3, 5, 1549, 46),
    GREEN_B_CIRCLE_RED(3, 1, 1550, 42),
    GREEN_B_CIRCLE_BLUE(3, 2, 1551, 43),
    GREEN_B_CIRCLE_GREEN(3, 3, 1552, 44),
    GREEN_B_CIRCLE_YELLOW(3, 4, 1553, 45),
    // Yellow
    YELLOW_CIRCLE(4, 1554, 50),
    YELLOW(4, 4, 1555, 550),
    YELLOW_B_CIRCLE_PURPLE(4, 5, 1556, 56),
    YELLOW_B_CIRCLE_RED(4, 1, 1557, 52),
    YELLOW_B_CIRCLE_BLUE(4, 2, 1558, 53),
    YELLOW_B_CIRCLE_GREEN(4, 3, 1559, 54),
    YELLOW_B_CIRCLE_YELLOW(4, 4, 1560, 55),
    // Purple
    PURPLE_CIRCLE(5, 1513, 60),
    PURPLE(5, 5, 1514, 660),
    PURPLE_B_CIRCLE_PURPLE(5, 5, 1515, 66),
    PURPLE_B_CIRCLE_RED(5, 1, 1516, 62),
    PURPLE_B_CIRCLE_BLUE(5, 2, 1517, 63),
    PURPLE_B_CIRCLE_GREEN(5, 3, 1518, 64),
    PURPLE_B_CIRCLE_YELLOW(5, 4, 1519, 65);

    private final int type;
    private final int type2;
    private final int data;
    private final int value;

    AlchemyCircle(final int type, final int data, final int value) {
        this.type = type;
        this.type2 = -1;
        this.data = data;
        this.value = value;
    }

    AlchemyCircle(final int type, final int type2, final int data, final int value) {
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

    @NotNull
    private static AlchemyCircle searchValue(final int value) {
        for (final AlchemyCircle ai : AlchemyCircle.values()) {
            if (ai.value == value) {
                return ai;
            }
        }
        throw new IllegalArgumentException(value + " not found.");
    }

    public static AlchemyCircle searchData(final int data) {
        for (final AlchemyCircle ai : AlchemyCircle.values()) {
            if (ai.data == data) {
                return ai;
            }
        }
        return null;
    }

    public static ItemStack getCircle(final int value, final ItemStack sItem) {
        if (ItemUtils.getType(sItem) == Material.DIAMOND_AXE) {
            final AlchemyCircle circle = searchData(ItemUtils.getCustomModelData(sItem));
            if (circle != null && String.valueOf(circle.value).charAt(1) != '0') {
                return i(value, Integer.parseInt(String.valueOf(circle.value).substring(0, 1)), sItem);
            }
        }
        return i(0, value, sItem);
    }

    private static ItemStack i(final int v1, final int v2, final ItemStack sItem) {
        final ItemStack item = new ItemStack(Material.DIAMOND_AXE, 1);
        final int cmd = AlchemyCircle.searchValue(Integer.parseInt(v2 + "" + v1)).data;
        final ItemMeta meta = item.getItemMeta();
        ItemUtils.setCustomModelData(meta, cmd);
        if (sItem != null) {
            final ItemMeta changeMeta = sItem.getItemMeta();
            meta.displayName(changeMeta.displayName());
            meta.lore(changeMeta.lore());
        } else {
            meta.displayName(Component.text("-"));
        }
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

}

/*
 1526 触媒・白 輪
 1527 触媒・白 芯
 1528 触媒・白 芯輪白
 1529 触媒・白 芯輪赤
 1530 触媒・白 芯輪青
 1531 触媒・白 芯輪緑
 1532 触媒・白 芯輪黄

 1533 触媒・赤 輪
 1534 触媒・赤 芯
 1535 触媒・赤 芯輪白
 1536 触媒・赤 芯輪赤
 1537 触媒・赤 芯輪青
 1538 触媒・赤 芯輪緑
 1539 触媒・赤 芯輪黄

 1540 触媒・青 輪
 1541 触媒・青 芯
 1542 触媒・青 芯輪白
 1543 触媒・青 芯輪赤
 1544 触媒・青 芯輪青
 1545 触媒・青 芯輪緑
 1546 触媒・青 芯輪黄

 1547 触媒・緑 輪
 1548 触媒・緑 芯
 1549 触媒・緑 芯輪白
 1550 触媒・緑 芯輪赤
 1551 触媒・緑 芯輪青
 1552 触媒・緑 芯輪緑
 1553 触媒・緑 芯輪黄

 1554 触媒・黄 輪
 1555 触媒・黄 芯
 1556 触媒・黄 芯輪白
 1557 触媒・黄 芯輪赤
 1558 触媒・黄 芯輪青
 1559 触媒・黄 芯輪緑
 1560 触媒・黄 芯輪黄
 */
