package net.firiz.renewatelier.loop;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.firiz.ateliercommonapi.loop.LoopManager;
import net.firiz.renewatelier.inventory.item.drop.AnimatedDrop;

import java.util.List;

public enum AnimatedDropManager {
    INSTANCE;

    private final LoopManager loop = LoopManager.INSTANCE;
    private final List<AnimatedDrop> animDrops = new ObjectArrayList<>();

    public void start() {
        loop.addTicks(() -> new ObjectArrayList<>(animDrops).forEach(AnimatedDrop::anim));
    }

    public void addAnimatedDrop(AnimatedDrop drop) {
        animDrops.add(drop);
    }

    public void removeAnimatedDrop(AnimatedDrop drop) {
        animDrops.remove(drop);
    }

    public List<AnimatedDrop> getAnimDrops() {
        return new ObjectArrayList<>(animDrops);
    }

}
