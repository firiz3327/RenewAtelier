package net.firiz.renewatelier.inventory.item.drop;

import net.firiz.renewatelier.loop.AnimatedDropManager;
import net.firiz.renewatelier.utils.minecraft.ItemUtils;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author firiz
 */
public abstract class AnimatedDrop {

    private static final AnimatedDropManager manager = AnimatedDropManager.INSTANCE;
    protected final Location loc;
    private final ItemStack itemstack;
    protected Item item = null;
    protected int tick;
    private boolean isGet;

    public AnimatedDrop(Location loc, ItemStack itemstack, int tick) {
        this.loc = loc;
        this.itemstack = itemstack;
        this.tick = tick;
        this.isGet = true;
    }

    public final void start() {
        addLoop();
    }

    protected final void spawn() {
        item = ItemUtils.drop(loc, itemstack);
    }

    protected final void setGet(boolean isGet) {
        this.isGet = isGet;
    }

    public final boolean isGet() {
        return isGet;
    }

    public final void anim() {
        if (tick > 0) {
            run();
            tick -= 1;
        } else {
            manager.removeAnimatedDrop(this);
            end();
        }
    }

    public final Item getDrop() {
        return item;
    }

    private void addLoop() {
        manager.addAnimatedDrop(this);
    }

    protected abstract void run();

    protected abstract void end();

}
