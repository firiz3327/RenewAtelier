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
package net.firiz.renewatelier.config.loader;

import java.util.ArrayList;
import java.util.List;
import net.firiz.renewatelier.alchemy.catalyst.Catalyst;
import net.firiz.renewatelier.alchemy.catalyst.CatalystBonus;
import net.firiz.renewatelier.alchemy.catalyst.CatalystBonusData;
import net.firiz.renewatelier.alchemy.material.AlchemyIngredients;
import net.firiz.renewatelier.alchemy.material.AlchemyMaterial;
import net.firiz.renewatelier.alchemy.material.Category;
import net.firiz.renewatelier.alchemy.material.Ingredients;
import net.firiz.renewatelier.alchemy.material.MaterialSize;
import net.firiz.renewatelier.alchemy.material.MaterialSizeData;
import net.firiz.renewatelier.characteristic.Characteristic;
import net.firiz.renewatelier.characteristic.CharacteristicTemplate;
import net.firiz.renewatelier.utils.Chore;
import net.firiz.renewatelier.utils.DoubleData;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

/**
 *
 * @author firiz
 */
public class AlchemyMaterialLoader extends ConfigLoader<AlchemyMaterial> {

    public AlchemyMaterialLoader() {
        super("materials");
    }

    @Override
    protected void loadConfig(final FileConfiguration config) {
        config.getKeys(false).forEach((key) -> {
            final List<String> notFounds = new ArrayList<>();
            try {
                final ConfigurationSection item = config.getConfigurationSection(key);
                // *名前取得
                if(!item.contains("name")) {
                    notFounds.add("name");
                }
                final String name = ChatColor.translateAlternateColorCodes('&', item.getString("name"));
                // デフォルト名優先
                final boolean default_name = item.contains("default_name") && item.getBoolean("default_name");
                // *アイテムのマテリアルを取得
                if(!item.contains("material")) {
                    notFounds.add("material");
                }
                final String mat_str = item.getString("material");
                final DoubleData<Material, Short> mat;
                if (!mat_str.contains(",")) {
                    if(mat_str.equalsIgnoreCase("XXX")) {
                        Chore.logSLightWarning("MaterialLoader: " + key + " -> No damage value has been set for XXX.");
                    }
                    mat = new DoubleData<>(Chore.getMaterial(mat_str), (short) 0);
                } else {
                    final String[] mat_split = mat_str.split(",");
                    mat = new DoubleData<>(Chore.getMaterial(mat_split[0]), Short.parseShort(mat_split[1]));
                }
                // *品質<最大・最小>
                if(!item.contains("quality_min")) {
                    notFounds.add("quality_min");
                }
                if(!item.contains("quality_max")) {
                    notFounds.add("quality_max");
                }
                final int quality_min = item.getInt("quality_min");
                final int quality_max = item.getInt("quality_max");
                // 売価
                final int price = item.contains("price") ? item.getInt("price") : 1;
                // *カテゴリ取得
                if(!item.contains("categorys")) {
                    notFounds.add("categorys");
                }
                final List<String> categorys_str = (List<String>) item.getList("categorys");
                final List<Category> categorys = new ArrayList<>();
                categorys_str.forEach((c_str) -> categorys.add(Category.searchName(c_str)));
                // *錬金成分取得
                if(!item.contains("ingredients")) {
                    notFounds.add("ingredients");
                }
                final List<String> ings_str = (List<String>) item.getList("ingredients");
                final List<DoubleData<Ingredients, Integer>> ingredients = new ArrayList<>();
                if (ings_str != null) {
                    ings_str.forEach((ing) -> {
                        final String[] ingData = ing.split(",");
                        ingredients.add(new DoubleData<>(
                                AlchemyIngredients.searchName(ingData[0].trim()),
                                Integer.parseInt(ingData[1].trim())
                        ));
                    });
                }
                // サイズ取得
                if(!item.contains("sizes")) {
                    notFounds.add("sizes");
                }
                final List<String> sizes_str = (List<String>) item.getList("sizes");
                final List<MaterialSizeData> sizes = new ArrayList<>();
                sizes_str.forEach((s_str) -> {
                    final String[] strs = s_str.split(",");
                    final String size_id = strs[0].trim();
                    final MaterialSize materialSize = MaterialSize.valueOf(size_id.toUpperCase());
                    if (materialSize == MaterialSize.S3_3) {
                        Chore.logLightWarning("MaterialLoader: " + key + " -> " + size_id + " is deprecated.");
                    }
                    sizes.add(new MaterialSizeData(
                            materialSize,
                            Integer.parseInt(strs[1].trim())
                    ));
                });
                // 特性取得
                final List<String> charas_str = (List<String>) item.getList("characteristics");
                final List<Object> charas = new ArrayList<>();
                if (charas_str != null) {
                    charas_str.forEach((c_str) -> {
                        if (c_str.contains(",")) {
                            final String[] strs = c_str.split(",");
                            charas.add(new DoubleData<>(
                                    Characteristic.valueOf(strs[0].trim().toUpperCase()),
                                    Integer.parseInt(strs[1].trim())
                            ));
                        } else {
                            charas.add(CharacteristicTemplate.valueOf(c_str.toUpperCase()));
                        }
                    });
                }
                // 触媒取得
                Catalyst catalyst = null;
                if (item.contains("catalyst")) {
                    final ConfigurationSection catalystConfig = item.getConfigurationSection("catalyst");
                    final List<CatalystBonus> bonus = new ArrayList<>();
                    catalystConfig.getKeys(false).stream()
                            .filter((c_key) -> (c_key.startsWith("bonus")))
                            .map(catalystConfig::getConfigurationSection)
                            .forEachOrdered((sec) -> {
                                final List<Integer> size = sec.getIntegerList("size");
                                bonus.add(new CatalystBonus(
                                        Chore.parseInts(size),
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
                        sizes,
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
            } catch(Exception ex) {
                Chore.logWarning("MaterialLoader: " + key + " -> ", ex);
            } finally {
                if (!notFounds.isEmpty()) {
                    Chore.logWarning("MaterialLoader: " + key + " -> Not found columns for " + notFounds.toString() + ".");
                }
            }
        });
    }

}
