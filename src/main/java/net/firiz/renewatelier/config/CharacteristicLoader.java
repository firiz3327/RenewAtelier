package net.firiz.renewatelier.config;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.firiz.renewatelier.AtelierPlugin;
import net.firiz.renewatelier.buff.BuffType;
import net.firiz.renewatelier.characteristic.Characteristic;
import net.firiz.renewatelier.characteristic.CharacteristicCategory;
import net.firiz.renewatelier.characteristic.CharacteristicType;
import net.firiz.renewatelier.characteristic.datas.CharacteristicBuff;
import net.firiz.renewatelier.characteristic.datas.CharacteristicData;
import net.firiz.renewatelier.characteristic.datas.CharacteristicInt;
import net.firiz.renewatelier.characteristic.datas.addattack.AddAttackData;
import net.firiz.renewatelier.characteristic.datas.CharacteristicArray;
import net.firiz.renewatelier.characteristic.datas.addattack.AddAttackType;
import net.firiz.renewatelier.utils.Chore;
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
            final List<String> categoriesStr = item.getStringList("categories");
            final CharacteristicCategory[] categories = new CharacteristicCategory[categoriesStr.size()];
            for (int i = 0; i < categoriesStr.size(); i++) {
                categories[i] = CharacteristicCategory.valueOf(categoriesStr.get(i));
            }
            final List<List<String>> reqs = getReqs(item);
            final List<String> itemStringList = item.getStringList("datas");
            final Map<CharacteristicType, CharacteristicData> datas = new Object2ObjectOpenHashMap<>();
            itemStringList.forEach(str -> {
                if (str.contains(",")) {
                    final int i = str.indexOf(',');
                    final CharacteristicType type = CharacteristicType.valueOf(str.substring(0, i));
                    final String dataStr = str.substring(i + 1).trim();
                    final CharacteristicData data;
                    if (Chore.isNumMatch(dataStr)) {
                        data = new CharacteristicInt(Integer.parseInt(dataStr));
                    } else {
                        final String[] split = dataStr.split(",");
                        for (int j = 0; j < split.length; j++) {
                            split[j] = split[j].trim();
                        }
                        switch (type) {
                            case BUFF:
                                data = new CharacteristicBuff(BuffType.valueOf(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));
                                break;
                            case ADD_ATTACK:
                                final AddAttackType addAttackType = AddAttackType.valueOf(split[0]);
                                data = new AddAttackData(addAttackType, Integer.parseInt(split[1]), AddAttackData.AttackCategory.search(Integer.parseInt(split[2])), addAttackType.createAddAttackX(split));
                                break;
                            default:
                                data = new CharacteristicArray(split);
                                break;
                        }
                    }
                    datas.put(type, data);
                } else {
                    datas.put(CharacteristicType.valueOf(str), null);
                }
            });
            add(new Characteristic(key, lv, name, desc, categories, reqs, datas));
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
            return Collections.emptyList();
        }

        final List<List<String>> result = new ObjectArrayList<>();
        if (list.get(0) instanceof ArrayList) {
            list.stream().<List<String>>map(Chore::cast).forEach(result::add);
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
