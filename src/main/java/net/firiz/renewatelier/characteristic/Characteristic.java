package net.firiz.renewatelier.characteristic;

import net.firiz.renewatelier.alchemy.material.AlchemyMaterial;
import net.firiz.renewatelier.alchemy.material.AlchemyMaterialCategory;
import net.firiz.renewatelier.characteristic.datas.CharacteristicArray;
import net.firiz.renewatelier.characteristic.datas.CharacteristicData;
import net.firiz.renewatelier.characteristic.datas.CharacteristicInt;
import net.firiz.renewatelier.config.ConfigManager;
import net.firiz.renewatelier.config.CharacteristicLoader;
import net.firiz.renewatelier.item.json.AlchemyItemStatus;
import net.firiz.renewatelier.utils.CommonUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public final class Characteristic {

    private static final ConfigManager CONFIG_MANAGER = ConfigManager.INSTANCE;
    private final String id;
    private final int lv;
    private final String name;
    private final String desc;
    private final CharacteristicCategory[] categories;
    private final List<List<String>> reqIds;
    private final Map<CharacteristicType, CharacteristicData> datas;

    public Characteristic(String id, int lv, String name, String desc, CharacteristicCategory[] categories, List<List<String>> reqIds, Map<CharacteristicType, CharacteristicData> datas) {
        this.id = id;
        this.lv = lv;
        this.name = name;
        this.desc = desc;
        this.categories = categories;
        this.reqIds = reqIds;
        this.datas = datas;
    }

    public boolean hasCategory(final AlchemyItemStatus itemStatus) {
        return hasCategory(itemStatus.getAlchemyMaterial().getMaterialCategory());
    }

    public boolean hasCategory(final AlchemyMaterial material) {
        return hasCategory(material.getMaterialCategory());
    }

    public boolean hasCategory(AlchemyMaterialCategory category) {
        CommonUtils.log(Arrays.toString(categories) + " " + category);
        return Arrays.stream(categories).anyMatch(c -> c.c(category));
    }

    public String getId() {
        return id;
    }

    public int getLevel() {
        return lv;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public List<List<String>> getReqIds() {
        return reqIds;
    }

    public CharacteristicCategory[] getCategories() {
        return categories;
    }

    public boolean hasData(CharacteristicType type) {
        return datas.containsKey(type);
    }

    @Nullable
    public CharacteristicData getData(CharacteristicType type) {
        return datas.get(type);
    }

    public int getIntData(CharacteristicType type) {
        final CharacteristicData data = datas.get(type);
        if (data instanceof CharacteristicInt) {
            return ((CharacteristicInt) data).getX();
        }
        throw new IllegalStateException("not characteristicInt class.");
    }

    /**
     *
     * @deprecated 全ての特性タイプに対してクラスを用意するため
     * @param type
     * @return data.x
     */
    @Deprecated(forRemoval = true)
    public String[] getArrayData(CharacteristicType type) {
        final CharacteristicData data = datas.get(type);
        if (data instanceof CharacteristicArray) {
            return ((CharacteristicArray) data).getX();
        }
        throw new IllegalStateException("not characteristicArray class.");
    }

    public Collection<CharacteristicType> getTypes() {
        return datas.keySet();
    }

    @NotNull
    public static Characteristic getCharacteristic(@NotNull final String id) {
        for (final Characteristic characteristic : CONFIG_MANAGER.getList(CharacteristicLoader.class, Characteristic.class)) {
            if (characteristic.getId().equalsIgnoreCase(id)) {
                return characteristic;
            }
        }
        throw new IllegalArgumentException(id.concat(" not found."));
    }

    @NotNull
    public static Characteristic search(String name) {
        for (final Characteristic characteristic : CONFIG_MANAGER.getList(CharacteristicLoader.class, Characteristic.class)) {
            if (characteristic.getName().equals(name)) {
                return characteristic;
            }
        }
        return getCharacteristic(name);
    }

    protected static final CharacteristicType[] alchemyKettleBonusTypes = {
            CharacteristicType.QUALITY,
            CharacteristicType.USECOUNT,
            CharacteristicType.SIZE
    };

    public enum Debuff {
        ALL,
        SLEEP,
        POISON,
        SLOW,
        CURSE,
        DARKNESS,
        WEAKNESS,
        DISABLE_HEAL,
        SLOW_SKILL,
        BREAK_DAMAGE
    }

}
