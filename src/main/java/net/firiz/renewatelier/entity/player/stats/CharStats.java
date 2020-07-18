package net.firiz.renewatelier.entity.player.stats;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.firiz.renewatelier.AtelierPlugin;
import net.firiz.renewatelier.buff.Buff;
import net.firiz.renewatelier.constants.GameConstants;
import net.firiz.renewatelier.entity.EntityStatus;
import net.firiz.renewatelier.item.json.AlchemyItemStatus;
import net.firiz.renewatelier.sql.SQLManager;
import net.firiz.renewatelier.utils.ItemUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.EntityEffect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class CharStats extends EntityStatus {

    private final Player player;
    private long exp;
    private int alchemyLevel;
    private int alchemyExp;
    private long money;
    private int maxMp;
    private int mp;

    private int breakGage; // ブレイク値
    private int acc; // 命中率
    private int avo; // 回避率

    private final EquipStats equipStats; // 装備・ハンド更新時、更新
    private final BuffedStats buffedStats; // バフ更新時、更新

    private long lastAttackTime = 0;

    public CharStats(Player player, int level, long exp, int alchemyLevel, int alchemyExp, long money, int maxHp, int hp, int maxMp, int mp, int atk, int def, int speed, List<Buff> buffs) {
        super(player, level, maxHp, hp, atk, def, speed);
        this.player = player;
        this.exp = exp;
        this.alchemyLevel = alchemyLevel;
        this.alchemyExp = alchemyExp;
        this.money = money;
        this.maxMp = maxMp;
        this.mp = mp;
        this.acc = 100;
        this.avo = 100;
        this.equipStats = new EquipStats(this, maxHp, maxMp, atk, def, speed, acc, avo);
        this.buffedStats = new BuffedStats(this, equipStats, level);
        for (final Buff buff : buffs) {
            buff.setStatus(this);
            buff.startTimer();
        }

        updateHp();
        updateMp();
    }

    public void save(int id) {
        SQLManager.INSTANCE.insert(
                "accounts",
                new String[]{"id", "uuid", "level", "exp", "alchemyLevel", "alchemyExp", "maxHp", "hp", "maxMp", "mp", "atk", "def", "speed", "money"},
                new Object[]{id, player.getUniqueId().toString(), level, exp, alchemyLevel, alchemyExp, maxHp, hp, maxMp, mp, atk, def, speed, money}
        );
    }

    public void updateLastAttack() {
        this.lastAttackTime = System.currentTimeMillis();
    }

    public long getAttackSpeed() {
        return getAttackSpeed(player.getInventory().getItemInMainHand().getType());
    }

    public long getAttackSpeed(@NotNull final Material material) {
        return Math.max(250, GameConstants.getCoolTimeMillis(material) - (long) getSpeed()); // 素手より早くなることはない
    }

    public boolean attack(@NotNull final Material material) {
        final long coolTimeMillis = getAttackSpeed(material);
        final boolean canAttack = System.currentTimeMillis() - lastAttackTime >= coolTimeMillis;
        if (canAttack) {
            updateLastAttack();
            player.setCooldown(material, (int) (coolTimeMillis * 0.02)); // 速度の違い武器がわからりにくくなるため使用しない
        }
        return canAttack;
    }

    public void updateEquip() {
        update(() -> {
            equipStats.updateEquip();
            buffedStats.update();
        });
    }

    public void updateWeapon() {
        update(() -> {
            equipStats.updateWeapon(-1);
            buffedStats.update();
        });
    }

    public void updateWeapon(int slot) {
        update(() -> {
            equipStats.updateWeapon(slot);
            buffedStats.update();
        });
    }

    public void updateBuffs() {
        update(buffedStats::update);
    }

    public void updateStats() {
        update(() -> {
            equipStats.update(maxHp, maxMp, atk, def, speed, level, level);
            buffedStats.update(this.level);
            player.setLevel(buffedStats.getLevel());
        });
    }

    private void update(@NotNull final Runnable updater) {
        final int oldMaxHp = getMaxHp();
        final int oldMaxMp = getMaxMp();
        updater.run();
        updateHp(oldMaxHp);
        updateMp(oldMaxMp);
        AtelierPlugin.getPlugin().getTabList().update(player);
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
        final List<AlchemyItemStatus> itemStatuses = new ObjectArrayList<>();
        for (final ItemStack armor : player.getInventory().getArmorContents()) {
            final AlchemyItemStatus itemStatus = AlchemyItemStatus.load(armor);
            if (itemStatus != null) {
                itemStatuses.add(itemStatus);
            }
        }
        return itemStatuses;
    }

    public Player getPlayer() {
        return player;
    }

    public int getNaturalLevel() {
        return level;
    }

    public int getLevel() {
        return buffedStats.getLevel();
    }

    public long getRequiredExp() {
        return GameConstants.PLAYER_REQ_EXPS[getNaturalLevel()];
    }

    public long getExp() {
        return exp;
    }

    public int getAlchemyLevel() {
        return alchemyLevel;
    }

    public long getRequiredAlchemyExp() {
        return GameConstants.ALCHEMY_REQ_EXPS[getAlchemyLevel()];
    }

    public int getAlchemyExp() {
        return alchemyExp;
    }

    public long getMoney() {
        return money;
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

    public int getPlusLevel() {
        return buffedStats.getPlusLevel();
    }

    public int getPlusMaxHp() {
        return buffedStats.getPlusMaxHp();
    }

    public int getPlusMaxMp() {
        return buffedStats.getPlusMaxMp();
    }

    public int getPlusAtk() {
        return buffedStats.getPlusAtk();
    }

    public int getPlusDef() {
        return buffedStats.getPlusDef();
    }

    public int getPlusSpeed() {
        return buffedStats.getPlusSpeed();
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
        player.sendActionBar(ChatColor.GREEN + "xp " + exp);
        this.exp += exp;
        boolean levelUp = false;
        while (true) {
            final long reqExp = GameConstants.PLAYER_REQ_EXPS[level];
            if (GameConstants.PLAYER_LEVEL_CAP <= level || reqExp > this.exp) {
                break;
            }
            this.exp -= reqExp;
            levelUp();
            levelUp = true;
        }
        final BigDecimal decimal = BigDecimal.valueOf(this.exp)
                .divide(BigDecimal.valueOf(GameConstants.PLAYER_REQ_EXPS[level]), 2, RoundingMode.DOWN);
        player.setExp(decimal.floatValue());
        return levelUp;
    }

    private void levelUp() {
        this.level++;
        final int up = this.level % 10 == 0 ? 2 : 0;
        final int atkUp = this.level % 2 == 0 ? 1 : 0;
        final int defUp = this.level % 3 == 0 ? 1 : 0;
        final int speedUp = this.level % 12 == 0 ? 1 : 0;
        this.maxHp += 1 + up;
        this.hp = this.maxHp;
        this.maxMp += 1 + up;
        this.mp = this.maxMp;
        this.atk += atkUp;
        this.def += defUp;
        this.speed += speedUp;

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.1f, 1);
        player.sendTitle(
                ChatColor.GREEN + "LEVEL UP",
                "Level: " + this.level,
                10,
                70,
                20
        );
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

    public void setMoney(long money) {
        this.money = money;
    }

    public boolean hasMoney(long money) {
        return this.money >= money;
    }

    /**
     * @param money      お金
     * @param compulsion 強制的にお金を所持制限最大もしくは最低になるまで変動させるかどうか
     * @return 無事に所持金を変動させられたか
     */
    public boolean gainMoney(long money, boolean compulsion) {
        final long tempMoney = (this.money + money);
        if (compulsion) {
            this.money = Math.max(0, Math.min(tempMoney, GameConstants.PLAYER_MONEY_CAP));
        } else {
            if (tempMoney > GameConstants.PLAYER_MONEY_CAP || tempMoney < 0) {
                return false;
            }
            this.money += money;
        }
        return true;
    }

    public void heal(double heal) {
        damageHp(-heal);
    }

    public void damageHp(double damage) {
        hp = Math.max(0, Math.min(getHp() - damage, getMaxHp()));
        if (hp == 0) {
            final Inventory inv = player.getInventory();
            if (inv.contains(Material.TOTEM_OF_UNDYING)) {
                heal(getMaxHp());
                player.playEffect(EntityEffect.TOTEM_RESURRECT);
                ItemUtils.gainItem(inv, Material.TOTEM_OF_UNDYING, 1);
            } else {
                player.setHealth(0D);
            }
            return;
        }
        updateHp();
    }

    private void updateHp(int oldMaxHp) {
        final int currentMaxHp = getMaxHp();
        hp = Math.min(hp * ((double) currentMaxHp / oldMaxHp), currentMaxHp);
        viewHp();
    }

    private void updateHp() {
        hp = Math.min(hp, getMaxHp());
        viewHp();
    }

    private void viewHp() {
        player.setHealth(Math.max(1, 20 * (hp / getMaxHp())));
    }

    public void damageMp(double damage) {
        mp -= damage;
        mp = Math.max(0, Math.min(mp, getMaxMp()));
        updateMp();
    }

    public void updateMp(int oldMaxMp) {
        final int currentMaxMp = getMaxMp();
        mp = (int) Math.min(mp * ((double) currentMaxMp / oldMaxMp), currentMaxMp);
        viewMp();
    }

    public void updateMp() {
        mp = Math.min(mp, getMaxMp());
        viewMp();
    }

    private void viewMp() {
        final double mpGage = 30 * ((double) mp / getMaxMp());
        final StringBuilder sb = new StringBuilder();
        sb.append("MP ").append(mp).append(" / ").append(getMaxMp()).append(' ').append(ChatColor.BLUE.toString());
        for (int i = 0; i < 30; i++) {
            if (i >= mpGage) {
                sb.append(ChatColor.GRAY);
            }
            sb.append('|');
        }
        player.sendActionBar(sb.toString());
    }

    public void clearBuffs() {
        buffs.clear();
        refreshBuffStats();
    }

    @Override
    protected void refreshBuffStats() {
        updateBuffs();
    }
}