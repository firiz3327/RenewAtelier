package net.firiz.renewatelier.alchemy.catalyst;

import java.util.List;

import net.firiz.renewatelier.utils.minecraft.ItemUtils;
import net.kyori.adventure.text.Component;
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
            case 36 -> // 0 6x6
                    inv.setItem(0, ItemUtils.ci(Material.DIAMOND_AXE, kettle ? 1511 : 1561, Component.empty(), null));
            case 25 -> // 1 5x5
                    inv.setItem(0, ItemUtils.ci(Material.DIAMOND_AXE, kettle ? 1510 : 1523, Component.empty(), null));
            case 16 -> // 2 4x4
                    inv.setItem(0, ItemUtils.ci(Material.DIAMOND_AXE, kettle ? 1509 : 1524, Component.empty(), null));
            default -> throw new IllegalStateException("catalyst bonus size number is not supported.");
        }
        inv.setItem(45, ItemUtils.ci(Material.DIAMOND_AXE, kettle ? 1512 : 1562, Component.empty(), null));

        final int defSlot = (size == 36 || size == 25 ? 3 : 13);
        bonus.forEach(b -> {
            int slot = defSlot;
            for (int c : b.getCS()) {
                short cmd = getCustomModelData(c);
                if (cmd != -1) {
                    inv.setItem(slot, ItemUtils.ci(
                            Material.DIAMOND_AXE,
                            cmd,
                            b.getData().getName(),
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
        return (short) switch (d) {
            case 1 -> 1527;
            case 2 -> 1534;
            case 3 -> 1541;
            case 4 -> 1548;
            case 5 -> 1555;
            case 6 -> 1514;
            default -> -1;
        };
    }

    public static int nextSlot(int slot, int size) {
        switch (size) {
            case 16:
                if (slot == 16 || slot == 25 || slot == 34 || slot == 43) {
                    return slot + 6;
                }
                break;
            case 25:
                if (slot == 7 || slot == 16 || slot == 25 || slot == 34 || slot == 43) {
                    return slot + 5;
                }
                break;
            case 36:
                if (slot == 8 || slot == 17 || slot == 26 || slot == 35 || slot == 44 || slot == 53) {
                    return slot + 4;
                }
                break;
            default:
                break;
        }
        return slot + 1;
    }
}
