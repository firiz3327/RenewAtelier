package net.firiz.renewatelier.entity.player.stats;

import net.firiz.renewatelier.buff.Buff;
import net.firiz.renewatelier.buff.BuffValueType;
import net.firiz.renewatelier.constants.GameConstants;
import net.firiz.renewatelier.entity.CalcStatType;
import net.firiz.renewatelier.entity.EntityStatus;
import net.firiz.renewatelier.entity.player.PlayerSaveManager;
import net.firiz.renewatelier.item.AlchemyItemStatus;
import net.firiz.renewatelier.sql.SQLManager;
import net.firiz.renewatelier.utils.Chore;
import net.firiz.renewatelier.version.VersionUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.EntityEffect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CharStats extends EntityStatus {

    private final Player player;
    private int level;
    private long exp;
    private int alchemyLevel;
    private int alchemyExp;
    private int maxMp;
    private int mp;

    private int breakGage; // ブレイク値
    private int acc; // 命中率
    private int avo; // 回避率

    private final EquipStats equipStats; // 装備・ハンド更新時、更新
    private final BuffedStats buffedStats; // バフ更新時、更新

    public CharStats(Player player, int level, long exp, int alchemyLevel, int alchemyExp, int maxHp, int hp, int maxMp, int mp, int atk, int def, int speed, List<Buff> buffs) {
        super(player, level, maxHp, hp, atk, def, speed);
        this.player = player;
        this.level = level;
        this.exp = exp;
        this.alchemyLevel = alchemyLevel;
        this.alchemyExp = alchemyExp;
        this.maxMp = maxMp;
        this.mp = mp;
        this.acc = 100;
        this.avo = 100;
        this.equipStats = new EquipStats(this, maxHp, maxMp, atk, def, speed, acc, avo);
        this.buffedStats = new BuffedStats(this, equipStats, level);
        for (final Buff buff : buffs) {
            buff.startTimer();
        }

        viewHp();
        viewMp();
    }

    public void save(int id) {
        SQLManager.INSTANCE.insert(
                "accounts",
                new String[]{"id", "uuid", "level", "exp", "alchemyLevel", "alchemyExp", "maxHp", "hp", "maxMp", "mp", "atk", "def", "speed"},
                new Object[]{id, player.getUniqueId().toString(), level, exp, alchemyLevel, alchemyExp, maxHp, hp, maxMp, mp, atk, def, speed}
        );
    }

    public void updateEquip() {
        equipStats.updateEquip();
        buffedStats.update();
    }

    public void updateWeapon() {
        equipStats.updateWeapon(-1);
        buffedStats.update();
    }

    public void updateWeapon(int slot) {
        equipStats.updateWeapon(slot);
        buffedStats.update();
    }

    public void updateBuffs() {
        buffedStats.update();
    }

    public void updateStats() {
        equipStats.update(maxHp, maxMp, atk, def, speed, level, level);
        buffedStats.update();
    }

    @Nullable
    public AlchemyItemStatus getWeapon() {
        return AlchemyItemStatus.load(player.getInventory().getItemInMainHand());
    }

    @NotNull
    public ItemStack getWeaponItem() {
        return player.getInventory().getItemInMainHand();
    }

    @NotNull
    public List<AlchemyItemStatus> getEquips() {
        final List<AlchemyItemStatus> itemStatuses = new ArrayList<>();
        for (final ItemStack armor : player.getInventory().getArmorContents()) {
            final AlchemyItemStatus itemStatus = AlchemyItemStatus.load(armor);
            if (itemStatus != null) {
                itemStatuses.add(itemStatus);
            }
        }
        return itemStatuses;
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
        hp = Math.max(0, Math.min(getHp() - damage, getMaxHp()));
        if (hp == 0) {
            final Inventory inv = player.getInventory();
            if (inv.contains(Material.TOTEM_OF_UNDYING)) {
                damageHp(-getMaxHp());
                player.playEffect(EntityEffect.TOTEM_RESURRECT);
                Chore.gainItem(inv, Material.TOTEM_OF_UNDYING, 1);
            } else {
                player.setHealth(0D);
            }
            return;
        }
        viewHp();
    }

    public void viewHp() {
        player.setHealth(Math.max(1, 20 * (hp / getMaxHp())));
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

    public void clearBuffs() {
        buffs.clear();
        refreshBuffStats();
    }

    @Override
    protected void refreshBuffStats() {
        buffedStats.update();
    }
}