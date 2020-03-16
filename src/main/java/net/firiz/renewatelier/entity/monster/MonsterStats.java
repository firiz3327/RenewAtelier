package net.firiz.renewatelier.entity.monster;

import net.firiz.renewatelier.buff.Buff;
import net.firiz.renewatelier.buff.BuffValueType;
import net.firiz.renewatelier.characteristic.Characteristic;
import net.firiz.renewatelier.entity.CalcStatType;
import net.firiz.renewatelier.entity.Race;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class MonsterStats {

    private final Race race;
    private final int level;
    private final int maxHp;
    private double hp;
    private final int atk;
    private final int def;
    private final int speed;
    private int buffLevel;
    private int buffHp;
    private int buffAtk;
    private int buffDef;
    private int buffSpeed;
    private final Set<Buff> buffs = Collections.synchronizedSet(new HashSet<>());

    private final boolean isBoss;

    public MonsterStats(Race race, int level, int maxHp, double hp, int atk, int def, int speed) {
        this.race = race;
        this.level = level;
        this.maxHp = maxHp;
        this.hp = hp;
        this.atk = atk;
        this.def = def;
        this.speed = speed;
        this.isBoss = false;
    }

    public MonsterStats(Race race, int level, int maxHp, double hp, int atk, int def, int speed, boolean isBoss) {
        this.race = race;
        this.level = level;
        this.maxHp = maxHp;
        this.hp = hp;
        this.atk = atk;
        this.def = def;
        this.speed = speed;
        this.isBoss = isBoss;
    }

    synchronized public boolean addBuff(@NotNull Buff buff) {
        if (buffs.contains(buff)) {
            return false;
        }
        byte add = 0;
        for (final Buff b : buffs) {
            if (b.getBuffValueType() == BuffValueType.CHARACTERISTIC && (b.getType() == buff.getType() || b.getValue().equals(buff.getValue()))) {
                if (b.getLevel() <= buff.getLevel()) { // よりレベルの高いバフへ上書き
                    b.stopTimer();
                    buffs.remove(b);
                    add = 1;
                } else if(add == 0) {
                    add = -1;
                }
            }
        }
        final boolean checkAdd = add == 0 || add == 1;
        if (checkAdd) {
            buff.setEndHandler(() -> {
                buffs.remove(buff);
                refreshBuffStats();
            });
            buff.startTimer();
            buffs.add(buff);
        }
        return checkAdd;
    }

    public Set<Buff> getBuffs() {
        return new HashSet<>(buffs);
    }

    private void refreshBuffStats() {
        this.buffLevel = CalcStatType.LEVEL.getMobBuffStats(this, level);
        this.buffHp = CalcStatType.HP.getMobBuffStats(this, maxHp);
        this.buffAtk = CalcStatType.ATK.getMobBuffStats(this, atk);
        this.buffDef = CalcStatType.DEF.getMobBuffStats(this, def);
        this.buffSpeed = CalcStatType.SPEED.getMobBuffStats(this, speed);
    }

    public int getLevel() {
        return level + buffLevel;
    }

    public int getMaxHp() {
        return maxHp + buffHp;
    }

    public double getHp() {
        return hp;
    }

    public void setHp(double hp) {
        this.hp = hp;
    }

    public int getAtk() {
        return atk + buffAtk;
    }

    public int getDef() {
        return def + buffDef;
    }

    public int getSpeed() {
        return speed + buffSpeed;
    }

    public boolean isBoss() {
        return isBoss;
    }
}
