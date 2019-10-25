package net.firiz.renewatelier.player;

import net.firiz.renewatelier.buff.Buff;
import net.firiz.renewatelier.buff.BuffType;
import net.firiz.renewatelier.characteristic.Characteristic;
import net.firiz.renewatelier.constants.GameConstants;
import net.firiz.renewatelier.item.AlchemyItemStatus;
import net.firiz.renewatelier.utils.TRunnable;
import net.firiz.renewatelier.utils.doubledata.FinalDoubleData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class CharStats {

    private final Player player;
    private int level;
    private long exp;
    private int alchemyLevel;
    private int alchemyExp;
    private int maxHp;
    private int hp;
    private int maxMp;
    private int mp;
    private int atk;
    private int def;
    private int speed;
    private List<Buff> buffs;

    private static final Map<StatusType, FinalDoubleData<TRunnable<CharStats, Integer>, BuffType[]>> buffTypes = new EnumMap<>(StatusType.class);
    private static final Map<StatusType, Characteristic.CharacteristicType[]> characteristicTypes = new EnumMap<>(StatusType.class);

    static {
        buffTypes.put(StatusType.HP, new FinalDoubleData<>(o -> o.maxHp, new BuffType[]{BuffType.STATS_HP, BuffType.STATS_HP_FIXED}));
        buffTypes.put(StatusType.MP, new FinalDoubleData<>(o -> o.maxMp, new BuffType[]{BuffType.STATS_MP, BuffType.STATS_MP_FIXED}));
        buffTypes.put(StatusType.ATK, new FinalDoubleData<>(o -> o.atk, new BuffType[]{BuffType.STATS_ATK, BuffType.STATS_ATK_FIXED}));
        buffTypes.put(StatusType.DEF, new FinalDoubleData<>(o -> o.def, new BuffType[]{BuffType.STATS_DEF, BuffType.STATS_DEF_FIXED}));
        buffTypes.put(StatusType.SPEED, new FinalDoubleData<>(o -> o.speed, new BuffType[]{BuffType.STATS_SPD, BuffType.STATS_SPD_FIXED}));
        characteristicTypes.put(StatusType.HP, new Characteristic.CharacteristicType[]{Characteristic.CharacteristicType.STATS_HP, Characteristic.CharacteristicType.STATS_HP_FIXED});
        characteristicTypes.put(StatusType.MP, new Characteristic.CharacteristicType[]{Characteristic.CharacteristicType.STATS_MP, Characteristic.CharacteristicType.STATS_MP_FIXED});
        characteristicTypes.put(StatusType.ATK, new Characteristic.CharacteristicType[]{Characteristic.CharacteristicType.STATS_ATK, Characteristic.CharacteristicType.STATS_ATK_FIXED});
        characteristicTypes.put(StatusType.DEF, new Characteristic.CharacteristicType[]{Characteristic.CharacteristicType.STATS_DEF, Characteristic.CharacteristicType.STATS_DEF_FIXED});
        characteristicTypes.put(StatusType.SPEED, new Characteristic.CharacteristicType[]{Characteristic.CharacteristicType.STATS_SPD, Characteristic.CharacteristicType.STATS_SPD_FIXED});
    }

    CharStats(Player player, int level, long exp, int alchemyLevel, int alchemyExp, int maxHp, int hp, int maxMp, int mp, int atk, int def, int speed, List<Buff> buffs) {
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
    }

    public int getLevel() {
        return level;
    }

    public long getExp() {
        return exp;
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
    }

    public int getAlchemyLevel() {
        return alchemyLevel;
    }

    public int getAlchemyExp() {
        return alchemyExp;
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

    public int getMaxHp() {
        return getStatus(StatusType.HP);
    }

    public int getHp() {
        return hp;
    }

    public int getMaxMp() {
        return getStatus(StatusType.MP);
    }

    public int getMp() {
        return mp;
    }

    public int getAtk() {
        return getStatus(StatusType.ATK);
    }

    public int getDef() {
        return getStatus(StatusType.DEF);
    }

    public int getSpeed() {
        return getStatus(StatusType.SPEED);
    }

    /**
     * バフ、装備(オフハンドを含まない)のステータス上昇を含めたステータス値を返します
     *
     * @param type StatusType 取得したいステータスのタイプ
     * @return int バフ、装備(オフハンドを含まない)のステータス特性を含めたステータスの値
     */
    private int getStatus(@NotNull StatusType type) {
        // バフ計算
        final FinalDoubleData<TRunnable<CharStats, Integer>, BuffType[]> buffType = buffTypes.get(type);
        final BuffType[] checkBuffTypes = buffType.getRight();
        final int defStatus = buffType.getLeft().run(this);
        int status = defStatus;
        for (final Buff buff : buffs) {
            if (buff.getType() == checkBuffTypes[0]) { // percent
                status += defStatus * ((buff.getX() * 0.01) + 1);
            } else if (buff.getType() == checkBuffTypes[1]) { // fixed
                status += buff.getX();
            }
        }
        // 装備(オフハンドを含まない)計算
        final PlayerInventory inv = player.getInventory();
        final Characteristic.CharacteristicType[] characteristicType = characteristicTypes.get(type);
        final List<ItemStack> checkItems = Arrays.asList(inv.getArmorContents());
        checkItems.add(inv.getItemInMainHand());
        for (final ItemStack item : checkItems) {
            final AlchemyItemStatus itemStatus = AlchemyItemStatus.load(item);
            if (itemStatus != null) {
                for (final Characteristic c : itemStatus.getCharacteristics()) {
                    if (c.hasData(characteristicType[0])) { // percent
                        status += defStatus * ((((int) c.getData(characteristicType[0])) * 0.01) + 1);
                    } else if (c.hasData(characteristicType[1])) { // fixed
                        status += (int) c.getData(characteristicType[1]);
                    }
                }
            }
        }
        return status;
    }

    private enum StatusType {
        HP, MP, ATK, DEF, SPEED
    }

}