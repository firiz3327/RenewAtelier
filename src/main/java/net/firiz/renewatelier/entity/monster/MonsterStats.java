package net.firiz.renewatelier.entity.monster;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.firiz.renewatelier.damage.AttackAttribute;
import net.firiz.renewatelier.damage.AttackResistance;
import net.firiz.renewatelier.entity.CalcStatType;
import net.firiz.renewatelier.entity.EntityStatus;
import net.firiz.renewatelier.entity.Race;
import org.bukkit.entity.Entity;

public class MonsterStats extends EntityStatus {

    private final Race race;
    private final int exp;
    private final boolean isBoss;
    private int buffLevel;
    private int buffHp;
    private int buffAtk;
    private int buffDef;
    private int buffSpeed;

    private final Object2ObjectMap<AttackAttribute, AttackResistance> resistances;

    public MonsterStats(Entity entity, Race race, int level, int maxHp, double hp, int atk, int def, int speed) {
        this(entity, race, level, maxHp, hp, atk, def, speed, 0, false);
    }

    public MonsterStats(Entity entity, Race race, int level, int maxHp, double hp, int atk, int def, int speed, boolean isBoss) {
        this(entity, race, level, maxHp, hp, atk, def, speed, 0, isBoss);
    }

    public MonsterStats(Entity entity, Race race, int level, int maxHp, double hp, int atk, int def, int speed, int exp) {
        this(entity, race, level, maxHp, hp, atk, def, speed, exp, false);
    }

    public MonsterStats(Entity entity, Race race, int level, int maxHp, double hp, int atk, int def, int speed, int exp, boolean isBoss) {
        this(entity, race, level, maxHp, hp, atk, def, speed, exp, isBoss, new Object2ObjectOpenHashMap<>());
    }

    public MonsterStats(Entity entity, Race race, int level, int maxHp, double hp, int atk, int def, int speed, int exp, boolean isBoss, Object2ObjectOpenHashMap<AttackAttribute, AttackResistance> resistances) {
        super(entity, level, maxHp, hp, atk, def, speed);
        this.race = race;
        this.isBoss = isBoss;
        this.exp = exp;
        this.resistances = resistances;
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

    public int getExp() {
        return exp;
    }

    public boolean isBoss() {
        return isBoss;
    }
}
