package net.firiz.renewatelier.entity;

import net.firiz.renewatelier.buff.Buff;
import net.firiz.renewatelier.buff.BuffData;
import net.firiz.renewatelier.buff.BuffType;
import net.firiz.renewatelier.buff.BuffValueType;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public abstract class EntityStatus {

    private final Entity entity;
    protected final Set<Buff> buffs;
    protected int level;
    protected int maxHp;
    protected double hp;
    protected int atk;
    protected int def;
    protected int speed;

    public EntityStatus(Entity entity, int level, int maxHp, double hp, int atk, int def, int speed, final Set<Buff> buffs) {
        this.entity = entity;
        this.level = level;
        this.maxHp = maxHp;
        this.hp = hp;
        this.atk = atk;
        this.def = def;
        this.speed = speed;
        this.buffs = buffs;
    }

    public EntityStatus(Entity entity, int level, int maxHp, double hp, int atk, int def, int speed) {
        this.entity = entity;
        this.level = level;
        this.maxHp = maxHp;
        this.hp = hp;
        this.atk = atk;
        this.def = def;
        this.speed = speed;
        this.buffs = new HashSet<>();
    }

    public Entity getEntity() {
        return entity;
    }

    protected abstract void refreshBuffStats();

    public abstract int getLevel();

    public abstract int getMaxHp();

    public abstract double getHp();

    public abstract int getAtk();

    public abstract int getDef();

    public abstract int getSpeed();

    public boolean addBuff(@NotNull final Buff buff) {
        if (buffs.contains(buff)) {
            return false;
        }
        byte add = 0;
        for (final Buff b : getBuffs()) {
            if (b.getBuffValueType() != BuffValueType.NONE && b.getType() == buff.getType()) {
                if (/*b.getLevel() <= buff.getLevel() || */b.getX() <= buff.getX()) { // より効果の高いバフへ上書き
                    b.stopTimer();
                    buffs.remove(b);
                    add = 1;
                } else if (add == 0) {
                    add = -1;
                }
            }
        }
        final boolean checkAdd = add == 0 || add == 1;
        if (checkAdd) {
            buffs.add(buff);
            buff.setEndHandler(() -> {
                buffs.remove(buff);
                refreshBuffStats();
            });
            buff.startTimer();
        }
        return checkAdd;
    }

    public Set<Buff> getBuffs() {
        return Collections.unmodifiableSet(new HashSet<>(buffs));
    }

    public boolean hasAntiHeal() {
        return getBuffs().stream().anyMatch(buff -> buff.getType() == BuffType.ANTI_HEAL);
    }

}
