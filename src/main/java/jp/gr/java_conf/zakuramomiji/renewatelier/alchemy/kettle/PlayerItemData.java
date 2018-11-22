package jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.kettle;

import org.bukkit.inventory.ItemStack;

/**
 *
 * @author kanzakiayaka
 */
public class PlayerItemData {

    private final ItemStack item;
//    private boolean remove;
    private int bonus;

    public PlayerItemData(ItemStack item) {
        this.item = item;
//        this.remove = false;
        this.bonus = 0;
    }

    public ItemStack getItem() {
        return item;
    }

//    public void setRemove(boolean remove) {
//        this.remove = remove;
//    }
//
//    public boolean isRemove() {
//        return remove;
//    }

    public void setBonus(int bonus) {
        this.bonus = bonus;
    }

    public int getBonus() {
        return bonus;
    }

}
