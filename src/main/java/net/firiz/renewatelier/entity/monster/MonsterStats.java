package net.firiz.renewatelier.entity.monster;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.firiz.renewatelier.buff.IBuff;
import net.firiz.renewatelier.buff.BuffType;
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

    private boolean defenseBreaking = false;

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

    public MonsterStats(Entity entity, Race race, int level, int maxHp, double hp, int atk, int def, int speed, int exp, boolean isBoss, Object2ObjectMap<AttackAttribute, AttackResistance> resistances) {
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

    public boolean isEmptyResistances() {
        return resistances.isEmpty();
    }

    public Object2ObjectMap<AttackAttribute, AttackResistance> getResistances() {
        return resistances;
    }

    public Object2ObjectMap<AttackAttribute, AttackResistance> getBuffResistances() {
        final Object2ObjectMap<AttackAttribute, AttackResistance> result = new Object2ObjectOpenHashMap<>(resistances);
        for (final IBuff buff : buffs) {
            if (buff.getType() == BuffType.RESISTANCE) {
                final AttackAttribute attribute = AttackAttribute.valueOf(buff.getY());
                AttackResistance resultResistance = result.getOrDefault(attribute, AttackResistance.NONE);
                if (buff.getLevel() >= 0) { // plus
                    for (int i = 0; i < buff.getLevel(); i++) {
                        resultResistance = resultResistance.up();
                    }
                } else { // minus
                    final int loops = buff.getLevel() * -1;
                    for (int i = 0; i < loops; i++) {
                        resultResistance = resultResistance.down();
                    }
                }
                result.put(attribute, resultResistance);
            }
        }
        return result;
    }

    public void setDefenseBreaking(boolean defenseBreaking) {
        this.defenseBreaking = defenseBreaking;
    }

    public boolean isDefenseBreaking() {
        return defenseBreaking;
    }
}
