package net.firiz.renewatelier.entity.player.stats;

import net.firiz.renewatelier.entity.CalcStatType;
import org.bukkit.inventory.ItemStack;

public class EquipStats {
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


    EquipStats(CharStats charStats, int maxHp, int maxMp, int atk, int def, int speed, int acc, int avo) {
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
        this.updateWeapon(-1);
    }

    void updateEquip() {
        this.maxHpE = CalcStatType.HP.getEquipStats(charStats, null, maxHp, false);
        this.maxMpE = CalcStatType.MP.getEquipStats(charStats, null, maxMp, false);
        this.atkE = CalcStatType.ATK.getEquipStats(charStats, null, atk, false);
        this.defE = CalcStatType.DEF.getEquipStats(charStats, null, def, false);
        this.speedE = CalcStatType.SPEED.getEquipStats(charStats, null, speed, false);
        this.accE = CalcStatType.ACC.getEquipStats(charStats, null, acc, false);
        this.avoE = CalcStatType.AVO.getEquipStats(charStats, null, avo, false);
    }

    void updateWeapon(int slot) {
        final ItemStack item;
        if (slot == -1) {
            item = charStats.getPlayer().getInventory().getItemInMainHand();
        } else {
            item = charStats.getPlayer().getInventory().getItem(slot);
        }
        this.maxHpW = CalcStatType.HP.getEquipStats(charStats, item, maxHp, true);
        this.maxMpW = CalcStatType.MP.getEquipStats(charStats, item, maxMp, true);
        this.atkW = CalcStatType.ATK.getEquipStats(charStats, item, atk, true);
        this.defW = CalcStatType.DEF.getEquipStats(charStats, item, def, true);
        this.speedW = CalcStatType.SPEED.getEquipStats(charStats, item, speed, true);
        this.accW = CalcStatType.ACC.getEquipStats(charStats, item, acc, true);
        this.avoW = CalcStatType.AVO.getEquipStats(charStats, item, avo, true);
    }

    public int getMaxHp() {
        return maxHp + getMaxHpEW();
    }

    public int getMaxMp() {
        return maxMp + getMaxMpEW();
    }

    public int getAtk() {
        return atk + getAtkEW();
    }

    public int getDef() {
        return def + getDefEW();
    }

    public int getSpeed() {
        return speed + getSpeedEW();
    }

    public int getAcc() {
        return acc + getAccEW();
    }

    public int getAvo() {
        return avo + getAvoEW();
    }

    public int getMaxHpEW() {
        return maxHpE + maxHpW;
    }

    public int getMaxMpEW() {
        return maxMpE + maxMpW;
    }

    public int getAtkEW() {
        return atkE + atkW;
    }

    public int getDefEW() {
        return defE + defW;
    }

    public int getSpeedEW() {
        return speedE + speedW;
    }

    public int getAccEW() {
        return accE + accW;
    }

    public int getAvoEW() {
        return avoE + avoW;
    }
}
