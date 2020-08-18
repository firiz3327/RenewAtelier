package net.firiz.renewatelier.alchemy.catalyst;

import java.util.List;

import net.firiz.renewatelier.utils.chores.ItemUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

/**
 * @author firiz
 */
public class Catalyst {

    private final List<CatalystBonus> bonus;
    private final int[] mainCS;
    private static DefaultCatalyst defaultCatalyst;

    public Catalyst(List<CatalystBonus> bonus) {
        this.bonus = bonus;
        this.mainCS = createAllCS();
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

    public void setInv(Inventory inv, boolean kettle) {
        final int size = bonus.get(0).getCS().length;
        switch (size) {
            case 36: // 0 6x6
                inv.setItem(0, ItemUtils.ci(Material.DIAMOND_AXE, kettle ? 1511 : 1561, "", null));
                break;
            case 25: // 1 5x5
                inv.setItem(0, ItemUtils.ci(Material.DIAMOND_AXE, kettle ? 1510 : 1523, "", null));
                break;
            case 16: // 2 4x4
                inv.setItem(0, ItemUtils.ci(Material.DIAMOND_AXE, kettle ? 1509 : 1524, "", null));
                break;
            default:
                throw new IllegalStateException("catalyst bonus size number is not supported.");
        }
        inv.setItem(45, ItemUtils.ci(Material.DIAMOND_AXE, kettle ? 1512 : 1562, "", null));

        final int defSlot = (size == 36 || size == 25 ? 3 : 13);
        bonus.forEach(b -> {
            int slot = defSlot;
            for (int c : b.getCS()) {
                short cmd = getCustomModelData(c);
                if (cmd != -1) {
                    inv.setItem(slot, ItemUtils.ci(
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
