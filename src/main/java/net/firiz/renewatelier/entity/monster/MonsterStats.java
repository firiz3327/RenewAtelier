package net.firiz.renewatelier.entity.monster;

import net.firiz.renewatelier.entity.CalcStatType;
import net.firiz.renewatelier.entity.EntityStatus;
import net.firiz.renewatelier.entity.Race;
import org.bukkit.entity.Entity;

public class MonsterStats extends EntityStatus {

    private final Race race;
    private int buffLevel;
    private int buffHp;
    private int buffAtk;
    private int buffDef;
    private int buffSpeed;

    private final boolean isBoss;

    public MonsterStats(Entity entity, Race race, int level, int maxHp, double hp, int atk, int def, int speed) {
        super(entity, level, maxHp, hp, atk, def, speed);
        this.race = race;
        this.isBoss = false;
    }

    public MonsterStats(Entity entity, Race race, int level, int maxHp, double hp, int atk, int def, int speed, boolean isBoss) {
        super(entity, level, maxHp, hp, atk, def, speed);
        this.race = race;
        this.isBoss = isBoss;
    }

    @Override
    protected void refreshBuffStats() {
        this.buffLevel = CalcStatType.LEVEL.getMobBuffStats(this, level);
        this.buffHp = CalcStatType.HP.getMobBuffStats(this, maxHp);
        this.buffAtk = CalcStatType.ATK.getMobBuffStats(this, atk);
        this.buffDef = CalcStatType.DEF.getMobBuffStats(this, def);
        this.buffSpeed = CalcStatType.SPEED.getMobBuffStats(this, speed);
    }

    public Race getRace() {
        return race;
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
