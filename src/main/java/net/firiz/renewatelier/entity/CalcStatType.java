package net.firiz.renewatelier.entity;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.firiz.renewatelier.alchemy.material.Category;
import net.firiz.renewatelier.buff.IBuff;
import net.firiz.renewatelier.buff.BuffType;
import net.firiz.renewatelier.characteristic.Characteristic;
import net.firiz.renewatelier.characteristic.CharacteristicType;
import net.firiz.renewatelier.constants.GameConstants;
import net.firiz.renewatelier.entity.monster.MonsterStats;
import net.firiz.renewatelier.entity.player.stats.CharStats;
import net.firiz.renewatelier.inventory.item.json.AlchemyItemStatus;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.ToIntFunction;

public enum CalcStatType {
    LEVEL(true, false),
    HP(true, true, AlchemyItemStatus::getHp),
    MP(true, true, AlchemyItemStatus::getMp),
    ATK(true, true, AlchemyItemStatus::getAtk),
    DEF(true, true, AlchemyItemStatus::getDef),
    SPEED(true, true, AlchemyItemStatus::getSpeed),
    ACC(false, true),
    AVO(false, true);

    private final boolean buff;
    private final boolean characteristic;
    private final ToIntFunction<AlchemyItemStatus> runGetEquipStats;

    private static final Map<CalcStatType, BuffType[]> buffTypes = new Object2ObjectOpenHashMap<>();
    private static final Map<CalcStatType, CharacteristicType[]> characteristicTypes = new Object2ObjectOpenHashMap<>();

    static {
        buffTypes.put(CalcStatType.LEVEL, new BuffType[]{BuffType.STATS_LEVEL, BuffType.STATS_LEVEL_FIXED});
        buffTypes.put(CalcStatType.HP, new BuffType[]{BuffType.STATS_HP, BuffType.STATS_HP_FIXED});
        buffTypes.put(CalcStatType.MP, new BuffType[]{BuffType.STATS_MP, BuffType.STATS_MP_FIXED});
        buffTypes.put(CalcStatType.ATK, new BuffType[]{BuffType.STATS_ATK, BuffType.STATS_ATK_FIXED});
        buffTypes.put(CalcStatType.DEF, new BuffType[]{BuffType.STATS_DEF, BuffType.STATS_DEF_FIXED});
        buffTypes.put(CalcStatType.SPEED, new BuffType[]{BuffType.STATS_SPD, BuffType.STATS_SPD_FIXED});
        characteristicTypes.put(CalcStatType.HP, new CharacteristicType[]{CharacteristicType.STATS_HP, CharacteristicType.STATS_HP_FIXED, CharacteristicType.STATS_HP_QUALITY});
        characteristicTypes.put(CalcStatType.MP, new CharacteristicType[]{CharacteristicType.STATS_MP, CharacteristicType.STATS_MP_FIXED, CharacteristicType.STATS_MP_QUALITY});
        characteristicTypes.put(CalcStatType.ATK, new CharacteristicType[]{CharacteristicType.STATS_ATK, CharacteristicType.STATS_ATK_FIXED, CharacteristicType.STATS_ATK_QUALITY});
        characteristicTypes.put(CalcStatType.DEF, new CharacteristicType[]{CharacteristicType.STATS_DEF, CharacteristicType.STATS_DEF_FIXED, CharacteristicType.STATS_DEF_QUALITY});
        characteristicTypes.put(CalcStatType.SPEED, new CharacteristicType[]{CharacteristicType.STATS_SPD, CharacteristicType.STATS_SPD_FIXED, CharacteristicType.STATS_SPD_QUALITY});
        characteristicTypes.put(CalcStatType.ACC, new CharacteristicType[]{CharacteristicType.STATS_ACC, CharacteristicType.STATS_ACC_FIXED, CharacteristicType.STATS_ACC_QUALITY});
        characteristicTypes.put(CalcStatType.AVO, new CharacteristicType[]{CharacteristicType.STATS_AVO, CharacteristicType.STATS_AVO_FIXED, CharacteristicType.STATS_AVO_QUALITY});
    }

    CalcStatType(boolean buff, boolean characteristic) {
        this(buff, characteristic, null);
    }

    CalcStatType(boolean buff, boolean characteristic, ToIntFunction<AlchemyItemStatus> getEquipStats) {
        this.buff = buff;
        this.characteristic = characteristic;
        this.runGetEquipStats = getEquipStats;
    }

    /**
     * バフのステータス上昇を含めたステータスの増加分を返します
     *
     * @param charStats CharStats 取得したいキャラクターのステータス
     * @return int バフを含めたステータスの増加値
     */
    public int getCharBuffStats(@NotNull CharStats charStats, int defStatus) {
        final AtomicInteger status = new AtomicInteger(0);

        // バフ計算
        if (buff) {
            getBuff(charStats.getBuffs(), defStatus, status);
            getBuff(charStats.getPassiveBuffs(), defStatus, status);
        }

        return status.intValue();
    }

    /**
     * バフのステータス上昇を含めたステータスの増加分を返します
     *
     * @param monsterStats MonsterStats 取得したいモンスターのステータス
     * @return int バフを含めたステータスの増加値
     */
    public int getMobBuffStats(@NotNull MonsterStats monsterStats, int defStatus) {
        final AtomicInteger status = new AtomicInteger(0);

        // バフ計算
        if (buff && this != MP) {
            getBuff(monsterStats.getBuffs(), defStatus, status);
            getBuff(monsterStats.getPassiveBuffs(), defStatus, status);
        }

        return status.intValue();
    }

    private void getBuff(@NotNull final Collection<IBuff> buffs, final int defStatus, @NotNull final AtomicInteger status) {
        final BuffType[] checkBuffTypes = buffTypes.get(this);
        for (final IBuff b : buffs) {
            if (b.getType() == checkBuffTypes[0]) { // percent
                status.addAndGet((int) (defStatus * (b.getX() * 0.01)));
            } else if (b.getType() == checkBuffTypes[1]) { // fixed
                status.addAndGet(b.getX());
            }
        }
    }

    /**
     * 装備のステータス上昇を含めたステータスの増加分を返します
     *
     * @param charStats CharStats 取得したいキャラクターのステータス
     * @param weapon    boolean 装備または武器のステータスを参照
     * @return int 装備のステータス特性を含めたステータスの増加値
     */
    public int getEquipStats(@NotNull CharStats charStats, ItemStack item, int defStatus, boolean weapon) {
        final AtomicInteger status = new AtomicInteger(0);

        // 装備特性計算
        if (characteristic) {
            final PlayerInventory inv = charStats.getPlayer().getInventory();
            if (weapon) {
                final AlchemyItemStatus itemStatus = AlchemyItemStatus.load(item);
                if (itemStatus != null && itemStatus.getAlchemyMaterial().categories().contains(Category.WEAPON)) {
                    getStats(charStats, defStatus, status, item);
                }
            } else {
                getStats(charStats, defStatus, status, inv.getArmorContents());
            }
        }

        return status.intValue();
    }

    private void getStats(@NotNull final CharStats charStats, final int defStatus, @NotNull final AtomicInteger status, @NotNull final ItemStack... checkItems) {
        final CharacteristicType[] characteristicType = characteristicTypes.get(this);
        for (final ItemStack item : checkItems) {
            if (item != null) {
                final AlchemyItemStatus itemStatus = AlchemyItemStatus.load(item);
                if (itemStatus == null) { // バニラ装備を想定
                    addVanilla(status, item);
                } else {
                    addStatus(status, charStats, defStatus, itemStatus, characteristicType);
                }
            }
        }
    }

    private void addVanilla(@NotNull final AtomicInteger status, @NotNull final ItemStack item) {
        switch (this) {
            case DEF:
                status.addAndGet(GameConstants.getItemDefense(item.getType()));
                break;
            default: // 想定しない
                break;
        }
    }

    private void addStatus(@NotNull final AtomicInteger status, @NotNull final CharStats charStats, final int defStatus, @NotNull final AlchemyItemStatus itemStatus, @NotNull final CharacteristicType[] characteristicType) {
        if (runGetEquipStats != null) {
            status.addAndGet(runGetEquipStats.applyAsInt(itemStatus)); // getEquipItemStats
        }
        for (final Characteristic c : itemStatus.getCharacteristics()) {
            if (c.hasData(characteristicType[0])) { // percent
                status.addAndGet((int) (defStatus * ((c.getIntData(characteristicType[0])) * 0.01)));
            } else if (c.hasData(characteristicType[1])) { // fixed
                status.addAndGet(c.getIntData(characteristicType[1]));
            } else if (c.hasData(characteristicType[2])) { // quality
                status.addAndGet((int) (c.getIntData(characteristicType[2]) + Math.round(itemStatus.getQuality() / 50D)));
            } else if (c.hasData(CharacteristicType.LEVEL_STATS_UP)) {
                status.addAndGet(charStats.getLevel() * c.getIntData(CharacteristicType.LEVEL_STATS_UP));
            }
        }
    }
}
