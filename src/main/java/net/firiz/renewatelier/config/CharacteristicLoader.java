package net.firiz.renewatelier.config;

import net.firiz.renewatelier.AtelierPlugin;
import net.firiz.renewatelier.characteristic.Characteristic;
import net.firiz.renewatelier.characteristic.CharacteristicCategory;
import net.firiz.renewatelier.utils.Chore;
import net.firiz.renewatelier.utils.chores.CollectionUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

public class CharacteristicLoader extends ConfigLoader<Characteristic> {

    CharacteristicLoader() {
        super(new File(AtelierPlugin.getPlugin().getDataFolder(), "characteristics.yml"), false);
    }

    @Override
    protected void loadConfig(FileConfiguration config) {
        config.getKeys(false).forEach(key -> {
            final ConfigurationSection item = config.getConfigurationSection(key);
            assert item != null;

            final int lv = item.getInt("lv");
            final String name = item.getString("name");
            final String desc = item.getString("desc");
            final List<String> categorysStr = item.getStringList("categorys");
            final CharacteristicCategory[] categorys = new CharacteristicCategory[categorysStr.size()];
            for (int i = 0; i < categorysStr.size(); i++) {
                categorys[i] = CharacteristicCategory.valueOf(categorysStr.get(i));
            }
            final List<List<String>> reqs = getReqs(item);
            final List<String> datasStr = item.getStringList("datas");
            final Map<Characteristic.CharacteristicType, Object> datas = new EnumMap<>(Characteristic.CharacteristicType.class);
            datasStr.forEach(str -> {
                final int i = str.indexOf(',');
                final String type = str.substring(0, i);
                final String dataStr = str.substring(i + 1);
                final Object data = Chore.isNumMatch(dataStr) ? Integer.parseInt(dataStr) : dataStr;
                datas.put(Characteristic.CharacteristicType.valueOf(type), data);
            });
            add(new Characteristic(key, lv, name, desc, categorys, reqs, datas));
        });
    }

    @Override
    protected void loadEnd() {
        getList().stream().flatMap(c -> c.getReqIds().stream()).flatMap(Collection::stream).forEach(r -> {
            try {
                Characteristic.getCharacteristic(r);
            } catch (IllegalArgumentException e) {
                Chore.logWarning(e);
            }
        });
    }

    @NotNull
    private List<List<String>> getReqs(@NotNull ConfigurationSection item) {
        List<?> list = item.getList("reqs");
        if (list == null) {
            return new ArrayList<>(0);
        }

        final List<List<String>> result = new ArrayList<>();
        if (list.get(0) instanceof ArrayList) {
            list.stream().<List<String>>map(CollectionUtils::castList).forEach(result::add);
        } else if (list.get(0) instanceof String) {
            result.add(convertStringList(list));
        }
        return result;
    }

    @NotNull
    private List<String> convertStringList(List<?> list) {
        final List<String> result = new ArrayList<>();
        final Iterator iterator = list.iterator();
        while (true) {
            Object object;
            do {
                if (!iterator.hasNext()) {
                    return result;
                }
                object = iterator.next();
            } while (!(object instanceof String) && !this.isPrimitiveWrapper(object));
            result.add(String.valueOf(object));
        }
    }

    private boolean isPrimitiveWrapper(@Nullable Object input) {
        return input instanceof Integer || input instanceof Boolean || input instanceof Character || input instanceof Byte || input instanceof Short || input instanceof Double || input instanceof Long || input instanceof Float;
    }
}
