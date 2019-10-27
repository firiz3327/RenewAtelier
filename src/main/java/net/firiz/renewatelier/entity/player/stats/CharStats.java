package net.firiz.renewatelier.entity.player.stats;

import net.firiz.renewatelier.buff.Buff;
import net.firiz.renewatelier.constants.GameConstants;
import net.firiz.renewatelier.version.VersionUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.*;

public class CharStats {

    private final Player player;
    private int level;
    private long exp;
    private int alchemyLevel;
    private int alchemyExp;
    private int maxHp;
    private double hp;
    private int maxMp;
    private int mp;
    private int atk;
    private int def;
    private int speed;
    private List<Buff> buffs;

    private int breakGage; // ブレイク値
    private int acc; // 命中率
    private int avo; // 回避率

    private final CharacteristicStats equipStats; // 装備・ハンド更新時、更新
    private final BuffedStats buffedStats; // バフ更新時、更新

    public CharStats(Player player, int level, long exp, int alchemyLevel, int alchemyExp, int maxHp, int hp, int maxMp, int mp, int atk, int def, int speed, List<Buff> buffs) {
        this.player = player;
        this.level = level;
        this.exp = exp;
        this.alchemyLevel = alchemyLevel;
        this.alchemyExp = alchemyExp;
        this.maxHp = maxHp;
        this.hp = hp;
        this.maxMp = maxMp;
        this.mp = mp;
        this.atk = atk;
        this.def = def;
        this.speed = speed;
        this.buffs = buffs;
        this.acc = 100;
        this.avo = 100;
        this.equipStats = new CharacteristicStats(this, maxHp, maxMp, atk, def, speed, acc, avo);
        this.buffedStats = new BuffedStats(this, equipStats, level);
        for (final Buff buff : buffs) {
            buff.startTimer();
        }
    }

    public void updateEquip() {
        equipStats.updateEquip();
    }

    public void updateWeapon() {
        equipStats.updateWeapon();
    }

    public void updateBuffs() {
        buffedStats.update();
    }

    public void updateStats() {
        equipStats.update(maxHp, maxMp, atk, def, speed, level, level);
        buffedStats.update();
    }

    public int getLevel() {
        return buffedStats.getLevel();
    }

    public long getExp() {
        return exp;
    }

    public int getAlchemyLevel() {
        return alchemyLevel;
    }

    public int getAlchemyExp() {
        return alchemyExp;
    }

    public Player getPlayer() {
        return player;
    }

    public int getMaxHp() {
        return buffedStats.getMaxHp();
    }

    public double getHp() {
        return hp;
    }

    public int getMaxMp() {
        return buffedStats.getMaxMp();
    }

    public int getMp() {
        return mp;
    }

    public int getAtk() {
        return buffedStats.getAtk();
    }

    public int getDef() {
        return buffedStats.getDef();
    }

    public int getSpeed() {
        return buffedStats.getSpeed();
    }

    public int getAcc() {
        return equipStats.getAcc();
    }

    public int getAvo() {
        return equipStats.getAvo();
    }

    public List<Buff> getBuffs() {
        return buffs;
    }

    /**
     * 経験値を加算し、もしレベルアップ回数が1回以上あった場合、trueを返します。
     *
     * @param exp long 増加させる経験値量
     * @return boolean レベルアップしたかどうか
     */
    public boolean addExp(long exp) {
        if (GameConstants.PLAYER_LEVEL_CAP <= level) {
            return false;
        }
        this.exp += exp;
        boolean levelUp = false;
        while (true) {
            final long reqExp = GameConstants.PLAYER_REQ_EXPS[level];
            if (GameConstants.PLAYER_LEVEL_CAP <= level || reqExp > exp) {
                break;
            }
            this.exp += exp - reqExp;
            levelUp();
            levelUp = true;
        }
        return levelUp;
    }

    private void levelUp() {
        this.level++;
        final int up = level % 10 == 0 ? 2 : 0;
        final int atkUp = level % 2 == 0 ? 1 : 0;
        final int defUp = level % 3 == 0 ? 1 : 0;
        final int speedUp = level % 12 == 0 ? 1 : 0;
        this.maxHp += 1 + up;
        this.hp = maxHp;
        this.maxMp += 1 + up;
        this.mp = maxMp;
        this.atk += atkUp;
        this.def += defUp;
        this.speed += speedUp;

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.1f, 1);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, VersionUtils.createTextComponent("LEVEL UP"));
        updateStats();
    }

    /**
     * 錬金経験値を加算し、もしレベルアップ回数が1回以上あった場合、trueを返します。
     *
     * @param alchemyExp int 増加させる経験値量
     * @return boolean レベルアップしたかどうか
     */
    public boolean addAlchemyExp(final int alchemyExp) {
        if (GameConstants.ALCHEMY_LEVEL_CAP <= alchemyLevel) {
            return false;
        }
        this.alchemyExp += alchemyExp;
        boolean levelUp = false;
        while (true) {
            final int reqExp = GameConstants.ALCHEMY_REQ_EXPS[alchemyLevel];
            if (GameConstants.ALCHEMY_LEVEL_CAP <= alchemyLevel || reqExp > alchemyExp) {
                break;
            }
            this.alchemyExp += alchemyExp - reqExp;
            this.alchemyLevel++;
            levelUp = true;
        }
        return levelUp;
    }

    public void damageHp(double damage) {
        hp -= damage;
        hp = Math.max(0, Math.min(hp, getMaxHp()));
        if (hp <= 0) {
            player.setHealth(0D);
            hp = getMaxHp();
            return;
        }
        viewHp();
    }

    public void viewHp() {
        final double newHp = 20 * (hp / getMaxHp());
        player.setHealth(newHp);
    }

    public void damageMp(double damage) {
        mp -= damage;
        mp = Math.max(0, Math.min(mp, getMaxMp()));
        viewMp();
    }

    public void viewMp() {
        final double newMp = 30 * ((double) mp / getMaxMp());
        final StringBuilder sb = new StringBuilder();
        sb.append("MP ").append(mp).append(" / ").append(getMaxMp()).append(' ').append(ChatColor.BLUE.toString());
        for (int i = 0; i < 30; i++) {
            if (i >= newMp) {
                sb.append(ChatColor.GRAY);
            }
            sb.append('|');
        }
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, VersionUtils.createTextComponent(sb.toString()));
    }
}