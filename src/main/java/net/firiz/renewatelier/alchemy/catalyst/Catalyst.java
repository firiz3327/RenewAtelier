/*
 * Catalyst.java
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
package net.firiz.renewatelier.alchemy.catalyst;

import java.util.List;

import net.firiz.renewatelier.alchemy.recipe.AlchemyRecipe;
import net.firiz.renewatelier.utils.Chore;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * @author firiz
 */
public class Catalyst {

    private final List<CatalystBonus> bonus;
    private final int[] maincs;
    private static DefaultCatalyst defaultCatalyst;

    public Catalyst(List<CatalystBonus> bonus) {
        this.bonus = bonus;
        this.maincs = createAllCS();
    }

    public static DefaultCatalyst getDefaultCatalyst() {
        if (defaultCatalyst == null) {
            defaultCatalyst = new DefaultCatalyst();
        }
        return defaultCatalyst;
    }

    public List<CatalystBonus> getBonus() {
        return bonus;
    }

    public int[] getMainCS() {
        return maincs;
    }

    public void setInv(Inventory inv, AlchemyRecipe recipe, boolean kettle) {
        final int size = bonus.get(0).getCS().length;
        switch (size) {
            case 36: // 0 6x6
                inv.setItem(0, Chore.ci(Material.DIAMOND_AXE, kettle ? 1511 : 1561, "", null));
                break;
            case 25: // 1 5x5
                inv.setItem(0, Chore.ci(Material.DIAMOND_AXE, kettle ? 1510 : 1523, "", null));
                break;
            case 16: // 2 4x4
                inv.setItem(0, Chore.ci(Material.DIAMOND_AXE, kettle ? 1509 : 1524, "", null));
                break;
            default:
                throw new IllegalStateException("catalyst bonus size number is not supported.");
        }
        inv.setItem(45, Chore.ci(Material.DIAMOND_AXE, kettle ? 1512 : 1562, "", null));

        final int defslot = (size == 36 || size == 25 ? 3 : 13);
        bonus.forEach(b -> {
            int slot = defslot;
            for (int c : b.getCS()) {
                short cmd = getCustomModelData(c);
                if (cmd != -1) {
                    inv.setItem(slot, Chore.ci(
                            Material.DIAMOND_AXE,
                            cmd,
                            ChatColor.RESET + b.getData().getName(),
                            b.getData().getDesc()
                    ));
                }
                slot = nextSlot(slot, size);
            }
        });
    }

    private int[] createAllCS() {
        final int[] result = new int[bonus.get(0).getCS().length];
        for (CatalystBonus catalystBonus : bonus) {
            int[] cs = catalystBonus.getCS();
            for (int i = 0; i < cs.length; i++) {
                int slot = cs[i];
                if (slot != 0) {
                    result[i] = slot;
                }
            }
        }
        return result;
    }

    public ItemStack getSlotItem(int cslot) {
        final int size = bonus.get(0).getCS().length;
        final int defslot = (size == 36 || size == 25 ? 3 : 13);
        for (final CatalystBonus b : bonus) {
            int slot = defslot;
            for (int c : b.getCS()) {
                if (cslot == slot) {
                    short cmd = getCustomModelData(c);
                    if (cmd != -1) {
                        return Chore.ci(Material.DIAMOND_AXE, cmd, ChatColor.RESET + b.getData().getName(), null);
                    }
                }
                slot = nextSlot(slot, size);
            }
        }
        return null;
    }

    /**
     *
     * 1: White
     * 2: Red
     * 3: Blue
     * 4: Green
     * 5: Yellow
     * 6: Purple
     *
     * @param d
     * @return
     */
    public static short getCustomModelData(int d) {
        switch (d) {
            case 1:
                return 1527;
            case 2:
                return 1534;
            case 3:
                return 1541;
            case 4:
                return 1548;
            case 5:
                return 1555;
            case 6:
                return 1514;
            default:
                return -1;
        }
    }

    public static int nextSlot(int slot, int size) {
        switch (size) {
            case 16:
                switch (slot) {
                    case 16:
                    case 25:
                    case 34:
                    case 43:
                        return slot + 6;
                    default:
                        break;
                }
                break;
            case 25:
                switch (slot) {
                    case 7:
                    case 16:
                    case 25:
                    case 34:
                    case 43:
                        return slot + 5;
                    default:
                        break;
                }
                break;
            case 36:
                switch (slot) {
                    case 8:
                    case 17:
                    case 26:
                    case 35:
                    case 44:
                    case 53:
                        return slot + 4;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
        return slot + 1;
    }
}
