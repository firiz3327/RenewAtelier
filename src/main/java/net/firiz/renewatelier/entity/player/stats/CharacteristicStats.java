package net.firiz.renewatelier.entity.player.stats;

public class CharacteristicStats {
    private final CharStats charStats;

    // 基礎ステータス
    private int maxHp;
    private int maxMp;
    private int atk;
    private int def;
    private int speed;
    private int acc; // 命中率
    private int avo; // 回避率

    // 装備ステータス増加値
    private int maxHpE;
    private int maxMpE;
    private int atkE;
    private int defE;
    private int speedE;
    private int accE; // 命中率
    private int avoE; // 回避率

    // 武器ステータス増加値
    private int maxHpW;
    private int maxMpW;
    private int atkW;
    private int defW;
    private int speedW;
    private int accW; // 命中率
    private int avoW; // 回避率


    CharacteristicStats(CharStats charStats, int maxHp, int maxMp, int atk, int def, int speed, int acc, int avo) {
        this.charStats = charStats;
        this.update(maxHp, maxMp, atk, def, speed, acc, avo);
    }

    void update(int maxHp, int maxMp, int atk, int def, int speed, int acc, int avo) {
        this.maxHp = maxHp;
        this.maxMp = maxMp;
        this.atk = atk;
        this.def = def;
        this.speed = speed;
        this.acc = acc;
        this.avo = avo;
        this.updateEquip();
        this.updateWeapon();
    }

    void updateEquip() {
        this.maxHpE = CharStatType.HP.getEquipStats(charStats, maxHp, false);
        this.maxMpE = CharStatType.MP.getEquipStats(charStats, maxMp, false);
        this.atkE = CharStatType.ATK.getEquipStats(charStats, atk, false);
        this.defE = CharStatType.DEF.getEquipStats(charStats, def, false);
        this.speedE = CharStatType.SPEED.getEquipStats(charStats, speed, false);
        this.accE = CharStatType.ACC.getEquipStats(charStats, acc, false);
        this.avoE = CharStatType.AVO.getEquipStats(charStats, avo, false);
    }

    void updateWeapon() {
        this.maxHpW = CharStatType.HP.getEquipStats(charStats, maxHp, false);
        this.maxMpW = CharStatType.MP.getEquipStats(charStats, maxMp, false);
        this.atkW = CharStatType.ATK.getEquipStats(charStats, atk, false);
        this.defW = CharStatType.DEF.getEquipStats(charStats, def, false);
        this.speedW = CharStatType.SPEED.getEquipStats(charStats, speed, false);
        this.accW = CharStatType.ACC.getEquipStats(charStats, acc, false);
        this.avoW = CharStatType.AVO.getEquipStats(charStats, avo, false);
    }

    public int getMaxHp() {
        return maxHp + maxHpE + maxHpW;
    }

    public int getMaxMp() {
        return maxMp + maxMpE + maxMpW;
    }

    public int getAtk() {
        return atk + atkE + atkW;
    }

    public int getDef() {
        return def + defE + defW;
    }

    public int getSpeed() {
        return speed + speedE + speedW;
    }

    public int getAcc() {
        return acc + accE + accW;
    }

    public int getAvo() {
        return avo + avoE + avoW;
    }

}
