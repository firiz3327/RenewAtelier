package net.firiz.renewatelier.config;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.firiz.renewatelier.AtelierPlugin;
import net.firiz.renewatelier.characteristic.Characteristic;
import net.firiz.renewatelier.characteristic.CharacteristicCategory;
import net.firiz.renewatelier.characteristic.CharacteristicType;
import net.firiz.renewatelier.characteristic.datas.*;
import net.firiz.renewatelier.utils.CommonUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
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
            final List<String> categoriesStr = item.getStringList("categories");
            final CharacteristicCategory[] categories = new CharacteristicCategory[categoriesStr.size()];
            for (int i = 0; i < categoriesStr.size(); i++) {
                categories[i] = CharacteristicCategory.valueOf(categoriesStr.get(i));
            }
            final List<List<String>> reqs = getReqs(item);
            final List<String> itemStringList = item.getStringList("datas");
            final Map<CharacteristicType, ChData> dataMap = new Object2ObjectOpenHashMap<>();
            itemStringList.forEach(str -> {
                if (str.contains(",")) {
                    final int i = str.indexOf(',');
                    final CharacteristicType type = CharacteristicType.valueOf(str.substring(0, i));
                    final String dataStr = str.substring(i + 1).trim();
                    final String[] split = dataStr.split(",");
                    for (int j = 0; j < split.length; j++) {
                        split[j] = split[j].trim();
                    }
                    try {
                        dataMap.put(type, type.newInstance(split));
                    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                        CommonUtils.logWarning(str, e);
                    }
                } else {
                    final CharacteristicType type = CharacteristicType.valueOf(str);
                    if (type.isType(ChNone.class)) {
                        dataMap.put(type, ChNone.newInstance(null));
                    } else {
                        CommonUtils.logWarning(type + " is not ChNone class.");
                    }
                }
            });
            add(new Characteristic(key, lv, name, desc, categories, reqs, dataMap));
        });
    }

    @Override
    protected void loadEnd() {
        getList().forEach(Characteristic::loadCombine);
    }

    @NotNull
    private List<List<String>> getReqs(@NotNull ConfigurationSection item) {
        List<?> list = item.getList("reqs");
        if (list == null) {
            return Collections.emptyList();
        }

        final List<List<String>> result = new ObjectArrayList<>();
        if (list.get(0) instanceof ArrayList) {
            list.stream().<List<String>>map(CommonUtils::cast).forEach(result::add);
        } else if (list.get(0) instanceof String) {
            result.add(convertStringList(list));
        }
        return result;
    }

    @NotNull
    private List<String> convertStringList(List<?> list) {
        final List<String> result = new ObjectArrayList<>();
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
