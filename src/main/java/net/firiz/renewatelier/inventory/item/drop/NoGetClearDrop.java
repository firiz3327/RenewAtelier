package net.firiz.renewatelier.inventory.item.drop;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author firiz
 */
public class NoGetClearDrop extends AnimatedDrop {
    private Runnable runnable = null;

    public NoGetClearDrop(Location loc, ItemStack itemstack, int tick) {
        super(loc, itemstack, tick);
        setGet(false);
        spawn();
    }

    public void setRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    protected void run() {
        if(runnable != null) {
            runnable.run();
        }
    }

    @Override
    protected void end() {
        item.remove();
    }

}
