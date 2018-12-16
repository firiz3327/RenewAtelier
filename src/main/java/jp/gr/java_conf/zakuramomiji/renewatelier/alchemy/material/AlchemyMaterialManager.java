/*
 * AlchemyMaterialManager.java
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
package jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.material;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import jp.gr.java_conf.zakuramomiji.renewatelier.AtelierPlugin;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.catalyst.Catalyst;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.catalyst.CatalystBonus;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.catalyst.CatalystBonusData;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.catalyst.CatalystBonusData.BonusType;
import jp.gr.java_conf.zakuramomiji.renewatelier.characteristic.Characteristic;
import jp.gr.java_conf.zakuramomiji.renewatelier.characteristic.CharacteristicTemplate;
import jp.gr.java_conf.zakuramomiji.renewatelier.item.AlchemyItemStatus;
import jp.gr.java_conf.zakuramomiji.renewatelier.utils.Chore;
import jp.gr.java_conf.zakuramomiji.renewatelier.utils.CustomConfig;
import jp.gr.java_conf.zakuramomiji.renewatelier.utils.DoubleData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author firiz
 */
public enum AlchemyMaterialManager {
    INSTANCE; // enum singleton style
    
    private final List<AlchemyMaterial> materials;

    private AlchemyMaterialManager() {
        materials = new ArrayList<>();
    }

    public void loadConfig() {
        final AtelierPlugin plugin = AtelierPlugin.getPlugin();
        final File[] files = new File(plugin.getDataFolder(), "materials").listFiles();
        if (files != null) {
            materials.clear();
            for (final File f : files) {
                if(!(f.getName().endsWith(".yml") || f.getName().endsWith(".yaml") || f.getName().endsWith(".YML") || f.getName().endsWith(".YAML"))) {
                    continue;
                }
                final FileConfiguration config = new CustomConfig(plugin, "materials", f.getName()).getConfig();
                config.getKeys(false).forEach((key) -> {
                    final ConfigurationSection item = config.getConfigurationSection(key);
                    if (item != null) {
                        // 名前取得
                        final String name = ChatColor.translateAlternateColorCodes('&', item.getString("name"));
                        // デフォルト名優先
                        final boolean default_name = item.contains("default_name") ? item.getBoolean("default_name") : false;
                        // アイテムのマテリアルを取得
                        final String mat_str = item.getString("material");
                        final DoubleData<Material, Short> mat;
                        if (!mat_str.contains(",")) {
                            mat = new DoubleData<>(Material.getMaterial(mat_str), (short) 0);
                        } else {
                            final String[] mat_split = mat_str.split(",");
                            mat = new DoubleData<>(Material.getMaterial(mat_split[0]), Short.parseShort(mat_split[1]));
                        }
                        // 品質<最大・最小>
                        final int quality_min = item.getInt("quality_min");
                        final int quality_max = item.getInt("quality_max");
                        // カテゴリ取得
                        final List<String> categorys_str = (List<String>) item.getList("categorys");
                        final List<Category> categorys = new ArrayList<>();
                        categorys_str.forEach((c_str) -> {
                            categorys.add(Category.valueOf(c_str.toUpperCase()));
                        });
                        // 錬金成分取得
                        final List<String> ings_str = (List<String>) item.getList("ingredients");
                        final List<DoubleData<Ingredients, Integer>> ingredients = new ArrayList<>();
                        if (ings_str != null) {
                            ings_str.forEach((ing) -> {
                                final String ingdata[] = ing.split(",");
                                ingredients.add(new DoubleData<>(
                                        AlchemyIngredients.valueOf(ingdata[0].trim().toUpperCase()),
                                        Integer.parseInt(ingdata[1].trim())
                                ));
                            });
                        }
                        // サイズ取得
                        final List<String> sizes_str = (List<String>) item.getList("sizes");
                        // 特性取得
                        final List<MaterialSizeData> sizes = new ArrayList<>();
                        sizes_str.forEach((s_str) -> {
                            final String strs[] = s_str.split(",");
                            sizes.add(new MaterialSizeData(
                                    MaterialSize.valueOf(strs[0].trim().toUpperCase()),
                                    Integer.parseInt(strs[1].trim()))
                            );
                        });
                        // 特性取得
                        final List<String> charas_str = (List<String>) item.getList("characteristics");
                        final List<Object> charas = new ArrayList<>();
                        if (charas_str != null) {
                            charas_str.forEach((c_str) -> {
                                if (c_str.contains(",")) {
                                    final String strs[] = c_str.split(",");
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
                            final Category category = Category.valueOf(catalystConfig.getString("category").toUpperCase());
                            final List<CatalystBonus> bonus = new ArrayList<>();
                            catalystConfig.getKeys(false).stream()
                                    .filter((c_key) -> (!c_key.equals("category")))
                                    .map((c_key) -> catalystConfig.getConfigurationSection(c_key))
                                    .forEachOrdered((sec) -> {
                                        //final CatalystBonusType type = CatalystBonusType.valueOf(sec.getString("type"));
                                        final List<Integer> size = sec.getIntegerList("size");
                                        bonus.add(new CatalystBonus(
                                                Chore.parseInts(size),
                                                new CatalystBonusData(
                                                        BonusType.valueOf(sec.getString("type")),
                                                        sec.contains("x") ? sec.getInt("x") : 0,
                                                        sec.contains("y") ? sec.getString("y") : null
                                                )
                                        ));
                                    });
                            catalyst = new Catalyst(category, bonus);
                        }
                        // スクリプト取得
                        final String script = item.getString("script");
                        //unbreaking & hide系取得
                        final boolean unbreaking = item.contains("unbreaking") ? item.getBoolean("unbreaking") : false;
                        final boolean hideAttribute = item.contains("hideAttribute") ? item.getBoolean("hideAttribute") : false;
                        final boolean hideDestroy = item.contains("hideDestroy") ? item.getBoolean("hideDestroy") : false;
                        final boolean hideEnchant = item.contains("hideEnchant") ? item.getBoolean("hideEnchant") : false;
                        final boolean hidePlacedOn = item.contains("hidePlacedOn") ? item.getBoolean("hidePlacedOn") : false;
                        final boolean hidePotionEffect = item.contains("hidePotionEffect") ? item.getBoolean("hidePotionEffect") : false;
                        final boolean hideUnbreaking = item.contains("hideUnbreaking") ? item.getBoolean("hideUnbreaking") : false;
                        // リストへ追加
                        materials.add(new AlchemyMaterial(
                                key,
                                name,
                                default_name,
                                mat,
                                quality_min,
                                quality_max,
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
                    }
                });
            }
        }
    }

    public AlchemyMaterial getMaterial(final String id) {
        for (final AlchemyMaterial am : materials) {
            if (am.getId().equalsIgnoreCase(id)) {
                return am;
            }
        }
        return null;
    }

    public AlchemyMaterial getMaterial(final ItemStack item) {
        final List<String> lores = AlchemyItemStatus.getLores(AlchemyItemStatus.ID, item);
        if (!lores.isEmpty()) {
            return getMaterial(
                    Chore.getStridColor(lores.get(0).replaceAll(
                            AlchemyItemStatus.ID.getCheck(),
                            ""
                    ))
            );
        }
        return null;
    }
}
