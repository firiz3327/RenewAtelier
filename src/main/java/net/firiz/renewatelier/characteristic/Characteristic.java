package net.firiz.renewatelier.characteristic;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.firiz.renewatelier.alchemy.material.AlchemyMaterial;
import net.firiz.renewatelier.alchemy.material.AlchemyMaterialCategory;
import net.firiz.renewatelier.characteristic.datas.ChData;
import net.firiz.renewatelier.characteristic.datas.ChInt;
import net.firiz.renewatelier.config.ConfigManager;
import net.firiz.renewatelier.config.CharacteristicLoader;
import net.firiz.renewatelier.inventory.item.json.AlchemyItemStatus;
import net.firiz.renewatelier.utils.CommonUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class Characteristic {

    private static final ConfigManager CONFIG_MANAGER = ConfigManager.INSTANCE;
    private final String id;
    private final int lv;
    private final String name;
    private final String desc;
    private final CharacteristicCategory[] categories;
    private final List<List<String>> combineRequireIds;
    private final List<List<Characteristic>> combineRequireCs;
    private final Map<CharacteristicType, ChData> dataMap;

    public Characteristic(String id, int lv, String name, String desc, CharacteristicCategory[] categories, List<List<String>> combineRequireIds, Map<CharacteristicType, ChData> dataMap) {
        this.id = id;
        this.lv = lv;
        this.name = name;
        this.desc = desc;
        this.categories = categories;
        this.combineRequireIds = combineRequireIds;
        this.combineRequireCs = new ObjectArrayList<>();
        this.dataMap = dataMap;
    }

    public void loadCombine() {
        combineRequireCs.clear();
        for (final List<String> ids : this.getCombineRequireIds()) {
            final List<Characteristic> cs = new ObjectArrayList<>();
            for (final String recipeId : ids) {
                final Characteristic c;
                try {
                    c = Characteristic.getCharacteristic(recipeId);
                } catch (IllegalArgumentException e) {
                    CommonUtils.logWarning(e);
                    continue;
                }
                cs.add(c);
            }
            combineRequireCs.add(cs);
        }
    }

    public static ObjectSet<Characteristic> combine(final AlchemyMaterialCategory category, final List<Characteristic> characteristics) {
        final List<Characteristic> tempCharacteristics = new ArrayList<>(characteristics);
        final ObjectLinkedOpenHashSet<Characteristic> combined = new ObjectLinkedOpenHashSet<>();
        for (int i = 0; i < 10; i++) {
            final ObjectLinkedOpenHashSet<Characteristic> combine = new ObjectLinkedOpenHashSet<>();
            for (final Characteristic c : CONFIG_MANAGER.getList(CharacteristicLoader.class, Characteristic.class)) {
                if (!c.combineRequireCs.isEmpty()) {
                    combine(c, tempCharacteristics, combine);
                }
            }
            if (combine.isEmpty()) {
                break;
            } else {
                tempCharacteristics.addAll(combine);
            }
        }
        tempCharacteristics.stream().filter(c -> c.hasCategory(category)).forEach(combined::add);
        return combined;
    }

    private static void combine(Characteristic c, List<Characteristic> characteristics, Set<Characteristic> combined) {
        final Set<Characteristic> temp = new HashSet<>();
        for (final Characteristic s1 : new ArrayList<>(characteristics)) {
            for (final List<Characteristic> reqs : c.combineRequireCs) {
                if (reqs.contains(s1) && !combined.contains(c) && !temp.contains(s1)) {
                    temp.add(s1);
                    if (new HashSet<>(reqs).equals(temp)) {
                        temp.forEach(characteristics::remove);
                        temp.clear();
                        combined.add(c);
                    }
                }
            }
        }
    }

    public boolean hasCategory(final AlchemyItemStatus itemStatus) {
        return hasCategory(itemStatus.getAlchemyMaterial().getMaterialCategory());
    }

    public boolean hasCategory(final AlchemyMaterial material) {
        return hasCategory(material.getMaterialCategory());
    }

    public boolean hasCategory(AlchemyMaterialCategory category) {
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

    public List<List<String>> getCombineRequireIds() {
        return combineRequireIds;
    }

    public CharacteristicCategory[] getCategories() {
        return categories;
    }

    public boolean hasData(CharacteristicType type) {
        return dataMap.containsKey(type);
    }

    @Nullable
    public ChData getData(CharacteristicType type) {
        return dataMap.get(type);
    }

    public int getIntData(CharacteristicType type) {
        final ChData data = dataMap.get(type);
        if (data instanceof ChInt) {
            return ((ChInt) data).getX();
        }
        throw new IllegalStateException("not characteristicInt class. " + data.getClass());
    }

    public <T> T getData(CharacteristicType type, Class<T> clasz) {
        return CommonUtils.cast(dataMap.get(type));
    }

//    /**
//     *
//     * @deprecated 全ての特性タイプに対してクラスを用意するため
//     * @param type
//     * @return data.x
//     */
//    @Deprecated(forRemoval = true)
//    public String[] getArrayData(CharacteristicType type) {
//        final ChData data = datas.get(type);
//        if (data instanceof ChArray) {
//            return ((ChArray) data).getX();
//        }
//        throw new IllegalStateException("not characteristicArray class. " + data.getClass());
//    }

    public Collection<CharacteristicType> getTypes() {
        return dataMap.keySet();
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
