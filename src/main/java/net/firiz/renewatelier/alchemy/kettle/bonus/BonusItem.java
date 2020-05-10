package net.firiz.renewatelier.alchemy.kettle.bonus;

import org.bukkit.inventory.ItemStack;

/**
 *
 * @author firiz
 */
public class BonusItem {
    private final ItemStack item;
    private int bonus;

    public BonusItem(ItemStack item) {
        this.item = item;
    }

    public BonusItem(ItemStack item, int bonus) {
        this.item = item;
        this.bonus = bonus;
    }

    public ItemStack getItem() {
        return item;
    }

    public int getBonus() {
        return bonus;
    }

    public void setBonus(int bonus) {
        this.bonus = bonus;
    }
}
