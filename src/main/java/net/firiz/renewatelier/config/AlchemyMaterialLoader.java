/*
 * AlchemyMaterialLoader.java
 *
 * Copyright (c) 2018 firiz.
 *
 * This file is part of Expression program is undefined on line 6, column 40 in Templates/Licenses/license-licence-gplv3.txt..
 *
 * Expression program is undefined on line 8, column 19 in Templates/Licenses/license-licence-gplv3.txt. is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Expression program is undefined on line 13, column 19 in Templates/Licenses/license-licence-gplv3.txt. is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Expression program is undefined on line 19, column 30 in Templates/Licenses/license-licence-gplv3.txt..  If not, see <http ://www.gnu.org/licenses/>.
 */
package net.firiz.renewatelier.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import net.firiz.renewatelier.AtelierPlugin;
import net.firiz.renewatelier.alchemy.catalyst.Catalyst;
import net.firiz.renewatelier.alchemy.catalyst.CatalystBonus;
import net.firiz.renewatelier.alchemy.catalyst.CatalystBonusData;
import net.firiz.renewatelier.alchemy.material.*;
import net.firiz.renewatelier.characteristic.Characteristic;
import net.firiz.renewatelier.characteristic.CharacteristicTemplate;
import net.firiz.renewatelier.utils.Chore;
import net.firiz.renewatelier.utils.CustomConfig;
import net.firiz.renewatelier.utils.doubledata.DoubleData;
import net.firiz.renewatelier.utils.chores.CollectionUtils;
import net.firiz.renewatelier.utils.doubledata.FinalDoubleData;
import net.firiz.renewatelier.version.LanguageItemUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

/**
 * @author firiz
 */
public class AlchemyMaterialLoader extends ConfigLoader<AlchemyMaterial> {

    private static final String PREFIX = "MaterialLoader: ";
    private static final String KEY_MATERIAL = "material";
    private static final String KEY_QUALITY_MIN = "quality_min";
    private static final String KEY_QUALITY_MAX = "quality_max";
    private static final String KEY_CATEGORYS = "categorys";
    private static final String KEY_INGREDIENTS = "ingredients";

    AlchemyMaterialLoader() {
        super(new File(AtelierPlugin.getPlugin().getDataFolder(), "materials"), true);
    }

    @Override
    protected void loadConfig(final FileConfiguration config) {
        int errorCount = 0;
        final Set<String> keys = config.getKeys(false);
        for (final String key : keys) {
            final List<String> notFounds = new ArrayList<>();
            try {
                final ConfigurationSection item = config.getConfigurationSection(key);
                assert item != null;

                // *アイテムのマテリアルを取得
                if (!item.contains(KEY_MATERIAL)) {
                    notFounds.add(KEY_MATERIAL);
                }
                final String mat_str = item.getString(KEY_MATERIAL);
                final FinalDoubleData<Material, Integer> mat;
                if (!mat_str.contains(",")) {
                    if (mat_str.equalsIgnoreCase("XXX")) {
                        Chore.logWhiteWarning(PREFIX.concat(key).concat(" -> No customModelData value has been set for XXX."));
                    }
                    mat = new FinalDoubleData<>(Chore.getMaterial(mat_str), 0);
                } else {
                    final String[] matSplit = mat_str.split(",");
                    mat = new FinalDoubleData<>(Chore.getMaterial(matSplit[0]), Integer.parseInt(matSplit[1]));
                }
                // デフォルト名優先
                final boolean default_name = item.contains("default_name") && item.getBoolean("default_name");
                // *名前取得
                final String name;
                if (default_name) {
                    name = ChatColor.RESET + LanguageItemUtil.getLocalizeName(new ItemStack(mat.getLeft())); // クエストブック用に名前を設定しておく
                } else {
                    if (!item.contains("name")) {
                        notFounds.add("name");
                    }
                    name = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(item.getString("name")));
                }
                // *品質<最大・最小>
                if (!item.contains(KEY_QUALITY_MIN)) {
                    notFounds.add(KEY_QUALITY_MIN);
                }
                if (!item.contains(KEY_QUALITY_MAX)) {
                    notFounds.add(KEY_QUALITY_MAX);
                }
                final int quality_min = item.getInt(KEY_QUALITY_MIN);
                final int quality_max = item.getInt(KEY_QUALITY_MAX);
                // 売価
                final int price = item.contains("price") ? item.getInt("price") : 1;
                // *カテゴリ取得
                if (!item.contains(KEY_CATEGORYS)) {
                    notFounds.add(KEY_CATEGORYS);
                }
                final List<String> categorysStr = CollectionUtils.castList(item.getList(KEY_CATEGORYS));
                final List<Category> categorys = new ArrayList<>();
                categorysStr.forEach(cStr -> categorys.add(Category.searchName(cStr)));
                // *錬金成分取得
                if (!item.contains(KEY_INGREDIENTS)) {
                    notFounds.add(KEY_INGREDIENTS);
                }
                final List<String> ingsStr = CollectionUtils.castList(item.getList(KEY_INGREDIENTS));
                final List<FinalDoubleData<AlchemyIngredients, Integer>> ingredients = new ArrayList<>();
                if (ingsStr != null) {
                    ingsStr.forEach(ing -> {
                        final String[] ingData = ing.split(",");
                        ingredients.add(new FinalDoubleData<>(
                                AlchemyIngredients.searchName(ingData[0].trim()),
                                Integer.parseInt(ingData[1].trim())
                        ));
                    });
                }
                // サイズ取得
                if (!item.contains("size")) {
                    notFounds.add("size");
                }
                final MaterialSizeTemplate sizeTemplate = MaterialSizeTemplate.valueOf("TYPE" + item.getInt("size"));
                // 特性取得
                final List<String> charasStr = CollectionUtils.castList(item.getList("characteristics"));
                final List<Object> charas = new ArrayList<>();
                if (charasStr != null) {
                    charasStr.forEach(cStr -> {
                        if (cStr.contains(",")) {
                            final String[] strs = cStr.split(",");
                            final String id = strs[0].trim().toUpperCase();
                            Characteristic c;
                            try {
                                c = Characteristic.getCharacteristic(id);
                            } catch (IllegalArgumentException e) {
                                c = Characteristic.search(id);
                            }
                            charas.add(new DoubleData<>(c, Integer.parseInt(strs[1].trim())));
                        } else {
                            charas.add(CharacteristicTemplate.valueOf(cStr.toUpperCase()));
                        }
                    });
                }
                // 触媒取得
                Catalyst catalyst = null;
                if (item.contains("catalyst")) {
                    final ConfigurationSection catalystConfig = item.getConfigurationSection("catalyst");
                    final List<CatalystBonus> bonus = new ArrayList<>();
                    assert catalystConfig != null;
                    catalystConfig.getKeys(false).stream()
                            .filter(cKey -> (cKey.startsWith("bonus")))
                            .map(catalystConfig::getConfigurationSection)
                            .filter(Objects::nonNull)
                            .forEachOrdered(sec -> {
                                final List<Integer> size = sec.getIntegerList("size");
                                bonus.add(new CatalystBonus(
                                        CollectionUtils.parseInts(size),
                                        new CatalystBonusData(
                                                CatalystBonusData.BonusType.valueOf(sec.getString("type")),
                                                sec.contains("x") ? sec.getInt("x") : 0,
                                                sec.contains("y") ? sec.getString("y") : null
                                        )
                                ));
                            });
                    catalyst = new Catalyst(bonus);
                }
                // スクリプト取得
                final String script = item.getString("script");
                //unbreaking & hide系取得
                final boolean unbreaking = item.contains("unbreaking") && item.getBoolean("unbreaking");
                final boolean hideAttribute = item.contains("hideAttribute") && item.getBoolean("hideAttribute");
                final boolean hideDestroy = item.contains("hideDestroy") && item.getBoolean("hideDestroy");
                final boolean hideEnchant = item.contains("hideEnchant") && item.getBoolean("hideEnchant");
                final boolean hidePlacedOn = item.contains("hidePlacedOn") && item.getBoolean("hidePlacedOn");
                final boolean hidePotionEffect = item.contains("hidePotionEffect") && item.getBoolean("hidePotionEffect");
                final boolean hideUnbreaking = item.contains("hideUnbreaking") && item.getBoolean("hideUnbreaking");
                // リストへ追加
                if (notFounds.isEmpty()) {
                    add(new AlchemyMaterial(
                            key,
                            name,
                            default_name,
                            mat,
                            quality_min,
                            quality_max,
                            price,
                            categorys,
                            ingredients,
                            sizeTemplate,
                            charas,
                            catalyst,
                            script,
                            unbreaking,
                            hideAttribute,
                            hideDestroy,
                            hideEnchant,
                            hidePlacedOn,
                            hidePotionEffect,
                            hideUnbreaking
                    ));
                }
            } catch (Exception ex) {
                Chore.logWarning(PREFIX.concat(key).concat(" -> "), ex);
                errorCount++;
            } finally {
                if (!notFounds.isEmpty()) {
                    Chore.logWarning(PREFIX.concat(key).concat(" -> Not found columns for ").concat(notFounds.toString()).concat("."));
                    errorCount++;
                }
            }
        }
        if (errorCount != 0) {
            Chore.logWhiteWarning("error founded.");
        }
        final String fileName = ((CustomConfig.CConfiguration) config).getConfigFile().getName();
        Chore.log(PREFIX + fileName + " - " + getList().size() + " loaded and " + errorCount + " errors found.");
    }

}
