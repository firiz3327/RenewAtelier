package net.firiz.renewatelier.entity;

import net.firiz.renewatelier.buff.*;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public abstract class EntityStatus {

    private final Entity entity;
    protected final Set<TimerBuff> buffs;
    protected final Set<PassiveBuff> passiveBuffs;
    protected int level;
    protected int maxHp;
    protected double hp;
    protected int atk;
    protected int def;
    protected int speed;

    public EntityStatus(Entity entity, int level, int maxHp, double hp, int atk, int def, int speed) {
        this(entity, level, maxHp, hp, atk, def, speed, new HashSet<>());
    }

    public EntityStatus(Entity entity, int level, int maxHp, double hp, int atk, int def, int speed, final Set<TimerBuff> buffs) {
        this(entity, level, maxHp, hp, atk, def, speed, buffs, new HashSet<>());
    }

    public EntityStatus(Entity entity, int level, int maxHp, double hp, int atk, int def, int speed, final Set<TimerBuff> buffs, final Set<PassiveBuff> passiveBuffs) {
        this.entity = entity;
        this.level = level;
        this.maxHp = maxHp;
        this.hp = hp;
        this.atk = atk;
        this.def = def;
        this.speed = speed;
        this.buffs = buffs;
        this.passiveBuffs = passiveBuffs;
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

    public boolean addBuff(@NotNull final TimerBuff buff) {
        if (buffs.contains(buff)) {
            return false;
        }
        byte add = 0;
        for (final TimerBuff b : getBuffTimers()) {
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

    public Set<IBuff> getBuffs() {
        return Set.copyOf(buffs);
    }

    public Set<TimerBuff> getBuffTimers() {
        return Set.copyOf(buffs);
    }

    public boolean addPassiveBuff(@NotNull final PassiveBuff passive) {
        if (passiveBuffs.contains(passive)) {
            return false;
        }
        return passiveBuffs.add(passive);
    }

    public Set<IBuff> getPassiveBuffs() {
        return Set.copyOf(passiveBuffs);
    }

    public void resetPassiveBuff() {
        passiveBuffs.clear();
    }

    public boolean hasAntiHeal() {
        return getBuffs().stream().anyMatch(buff -> buff.getType() == BuffType.ANTI_HEAL);
    }

}
