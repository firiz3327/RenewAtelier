package net.firiz.renewatelier.characteristic;

import net.firiz.renewatelier.config.ConfigManager;
import net.firiz.renewatelier.config.CharacteristicLoader;
import net.firiz.renewatelier.utils.Chore;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Characteristic {

    private static final ConfigManager CONFIG_MANAGER = ConfigManager.INSTANCE;
    private final String id;
    private final int lv;
    private final String name;
    private final String desc;
    private final CharacteristicCategory[] categorys;
    private final List<List<String>> reqIds;
    private final Map<CharacteristicType, Object> datas;

    public Characteristic(String id, int lv, String name, String desc, CharacteristicCategory[] categorys, List<List<String>> reqIds, Map<CharacteristicType, Object> datas) {
        this.id = id;
        this.lv = lv;
        this.name = name;
        this.desc = desc;
        this.categorys = categorys;
        this.reqIds = reqIds;
        this.datas = datas;
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

    public CharacteristicCategory[] getCategorys() {
        return categorys;
    }

    public boolean hasData(CharacteristicType type) {
        return datas.containsKey(type);
    }

    @Nullable
    public Object getData(CharacteristicType type) {
        return datas.get(type);
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
