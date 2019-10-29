package net.firiz.renewatelier.entity.player.stats;

import net.firiz.renewatelier.alchemy.material.Category;
import net.firiz.renewatelier.buff.Buff;
import net.firiz.renewatelier.buff.BuffType;
import net.firiz.renewatelier.characteristic.Characteristic;
import net.firiz.renewatelier.characteristic.CharacteristicType;
import net.firiz.renewatelier.item.AlchemyItemStatus;
import net.firiz.renewatelier.utils.TRunnable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public enum CharStatType {
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
    private final TRunnable<AlchemyItemStatus, Integer> runGetEquipStats;

    private static final Map<CharStatType, BuffType[]> buffTypes = new EnumMap<>(CharStatType.class);
    private static final Map<CharStatType, CharacteristicType[]> characteristicTypes = new EnumMap<>(CharStatType.class);

    static {
        buffTypes.put(CharStatType.LEVEL, new BuffType[]{BuffType.STATS_LEVEL, BuffType.STATS_LEVEL_FIXED});
        buffTypes.put(CharStatType.HP, new BuffType[]{BuffType.STATS_HP, BuffType.STATS_HP_FIXED});
        buffTypes.put(CharStatType.MP, new BuffType[]{BuffType.STATS_MP, BuffType.STATS_MP_FIXED});
        buffTypes.put(CharStatType.ATK, new BuffType[]{BuffType.STATS_ATK, BuffType.STATS_ATK_FIXED});
        buffTypes.put(CharStatType.DEF, new BuffType[]{BuffType.STATS_DEF, BuffType.STATS_DEF_FIXED});
        buffTypes.put(CharStatType.SPEED, new BuffType[]{BuffType.STATS_SPD, BuffType.STATS_SPD_FIXED});
        characteristicTypes.put(CharStatType.HP, new CharacteristicType[]{CharacteristicType.STATS_HP, CharacteristicType.STATS_HP_FIXED, CharacteristicType.STATS_HP_QUALITY});
        characteristicTypes.put(CharStatType.MP, new CharacteristicType[]{CharacteristicType.STATS_MP, CharacteristicType.STATS_MP_FIXED, CharacteristicType.STATS_MP_QUALITY});
        characteristicTypes.put(CharStatType.ATK, new CharacteristicType[]{CharacteristicType.STATS_ATK, CharacteristicType.STATS_ATK_FIXED, CharacteristicType.STATS_ATK_QUALITY});
        characteristicTypes.put(CharStatType.DEF, new CharacteristicType[]{CharacteristicType.STATS_DEF, CharacteristicType.STATS_DEF_FIXED, CharacteristicType.STATS_DEF_QUALITY});
        characteristicTypes.put(CharStatType.SPEED, new CharacteristicType[]{CharacteristicType.STATS_SPD, CharacteristicType.STATS_SPD_FIXED, CharacteristicType.STATS_SPD_QUALITY});
        characteristicTypes.put(CharStatType.ACC, new CharacteristicType[]{CharacteristicType.STATS_ACC, CharacteristicType.STATS_ACC_FIXED, CharacteristicType.STATS_ACC_QUALITY});
        characteristicTypes.put(CharStatType.AVO, new CharacteristicType[]{CharacteristicType.STATS_AVO, CharacteristicType.STATS_AVO_FIXED, CharacteristicType.STATS_AVO_QUALITY});
    }

    CharStatType(boolean buff, boolean characteristic) {
        this.buff = buff;
        this.characteristic = characteristic;
        this.runGetEquipStats = null;
    }

    CharStatType(boolean buff, boolean characteristic, TRunnable<AlchemyItemStatus, Integer> getEquipStats) {
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
    protected int getBuffStats(@NotNull CharStats charStats, int defStatus) {
        final AtomicInteger status = new AtomicInteger(0);

        // バフ計算
        if (buff) {
            getBuff(charStats, defStatus, status);
        }

        return status.intValue();
    }

    private void getBuff(final CharStats charStats, final int defStatus, final AtomicInteger status) {
        final BuffType[] checkBuffTypes = buffTypes.get(this);
        for (final Buff b : charStats.getBuffs()) {
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
    protected int getEquipStats(CharStats charStats, ItemStack item, int defStatus, boolean weapon) {
        final AtomicInteger status = new AtomicInteger(0);

        // 装備特性計算
        if (characteristic) {
            final PlayerInventory inv = charStats.getPlayer().getInventory();
            if (weapon) {
                final AlchemyItemStatus itemStatus = AlchemyItemStatus.load(item);
                if (itemStatus != null && itemStatus.getAlchemyMaterial().getCategorys().contains(Category.WEAPON)) {
                    getStats(charStats, defStatus, status, item);
                }
            } else {
                getStats(charStats, defStatus, status, inv.getArmorContents());
            }
        }

        return status.intValue();
    }

    private void getStats(final CharStats charStats, final int defStatus, final AtomicInteger status, final ItemStack... checkItems) {
        final CharacteristicType[] characteristicType = characteristicTypes.get(this);
        for (final ItemStack item : checkItems) {
            final AlchemyItemStatus itemStatus = AlchemyItemStatus.load(item);
            if (itemStatus != null) {
                if (runGetEquipStats != null) {
                    status.addAndGet(runGetEquipStats.run(itemStatus)); // getEquipItemStats
                }
                for (final Characteristic c : itemStatus.getCharacteristics()) {
                    if (c.hasData(characteristicType[0])) { // percent
                        status.addAndGet((int) (defStatus * (((int) c.getData(characteristicType[0])) * 0.01)));
                    } else if (c.hasData(characteristicType[1])) { // fixed
                        status.addAndGet((int) c.getData(characteristicType[1]));
                    } else if (c.hasData(characteristicType[2])) { // quality
                        status.addAndGet((int) ((int) c.getData(characteristicType[2]) + Math.round(itemStatus.getQuality() / 50D)));
                    } else if (c.hasData(CharacteristicType.LEVEL_STATS_UP)) {
                        status.addAndGet(charStats.getLevel() * (int) c.getData(CharacteristicType.LEVEL_STATS_UP));
                    }
                }
            }
        }
    }
}
