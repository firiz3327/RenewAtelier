package net.firiz.renewatelier.config;

import java.io.File;
import java.util.*;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.firiz.renewatelier.AtelierPlugin;
import net.firiz.renewatelier.alchemy.RequireAmountMaterial;
import net.firiz.renewatelier.alchemy.RequireMaterial;
import net.firiz.renewatelier.alchemy.material.AlchemyAttribute;
import net.firiz.renewatelier.alchemy.material.AlchemyIngredients;
import net.firiz.renewatelier.alchemy.material.AlchemyMaterial;
import net.firiz.renewatelier.alchemy.material.Category;
import net.firiz.renewatelier.alchemy.recipe.AlchemyRecipe;
import net.firiz.renewatelier.alchemy.recipe.RecipeEffect;
import net.firiz.renewatelier.alchemy.recipe.RecipeLevelEffect;
import net.firiz.renewatelier.alchemy.recipe.StarEffect;
import net.firiz.renewatelier.alchemy.recipe.result.ARecipeResult;
import net.firiz.renewatelier.alchemy.recipe.result.AlchemyMaterialRecipeResult;
import net.firiz.renewatelier.alchemy.recipe.result.MinecraftMaterialRecipeResult;
import net.firiz.renewatelier.item.CustomModelMaterial;
import net.firiz.renewatelier.item.json.itemeffect.AlchemyItemEffect;
import net.firiz.renewatelier.utils.minecraft.ItemUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * @author firiz
 */
public class AlchemyRecipeLoader extends ConfigLoader<AlchemyRecipe> {

    private final List<AlchemyRecipe> ideaRecipes = new ObjectArrayList<>();

    AlchemyRecipeLoader() {
        super(new File(AtelierPlugin.getPlugin().getDataFolder(), "recipes"), true);
    }

    @Override
    protected void initClear() {
        ideaRecipes.clear();
    }

    @Override
    protected void loadConfig(final FileConfiguration config) {
        config.getKeys(false).forEach(key -> {
            final ConfigurationSection item = config.getConfigurationSection(key);
            assert item != null;
            // リザルトマテリアル (material: AlchemyMaterial, なし: Material) *
            final ARecipeResult<?> result = getResult(item);
            // リザルト・初期アイテム数 *
            final int amount = item.getInt("amount");
            // 必要素材 *
            final List<RequireAmountMaterial> reqMaterials = RequireAmountMaterial.loadAmount(item.getStringList("req_materials"));
            // 必要錬金レベル
            final int requireAlchemyLevel = item.contains("req_alchemylevel") ? item.getInt("req_alchemylevel") : 1;
            // 初期錬金属性 *
            final List<String> defaultIngredientsStr = item.getStringList("default_ingredients");
            final List<AlchemyIngredients> defaultIngredients = new ObjectArrayList<>();
            defaultIngredientsStr.forEach(str -> defaultIngredients.add(AlchemyIngredients.searchName(str)));
            // 効果-必要ゲージ数 *
            final int req_bar = item.getInt("req_bar");
            // 効果
            final List<RecipeEffect> effects = new ObjectArrayList<>();
            if (item.contains("effects")) {
                final ConfigurationSection effectsItem = item.getConfigurationSection("effects");
                effectsItem.getKeys(false).stream().map(effectsItem::getConfigurationSection).filter(Objects::nonNull).forEachOrdered(esec -> {
                    StarEffect defaultSE = null;
                    if (esec.contains("default_star_effect")) {
                        defaultSE = getStarEffect(Objects.requireNonNull(esec.getConfigurationSection("default_star_effect")));
                    }
                    final AlchemyAttribute attribute = AlchemyAttribute.valueOf(esec.getString("attribute"));
                    final List<Integer> star = esec.getIntegerList("star");
                    final List<StarEffect> starEffects = new ObjectArrayList<>();
                    star.stream()
                            .filter(s -> (s != 0))
                            .map(s -> esec.getConfigurationSection("star_effect_" + s))
                            .filter(Objects::nonNull)
                            .map(this::getStarEffect)
                            .filter(Objects::nonNull)
                            .forEachOrdered(starEffects::add);
                    effects.add(new RecipeEffect(
                            attribute,
                            star,
                            starEffects,
                            defaultSE
                    ));
                });
            }
            // 熟練度 *
            final Int2ObjectMap<List<RecipeLevelEffect>> levels = new Int2ObjectOpenHashMap<>();
            final ConfigurationSection levelsec = item.getConfigurationSection("levels");
            for (int i = 1; i <= 4; i++) {
                final List<String> levelEffectStr = levelsec.getStringList("level_" + i);
                final List<RecipeLevelEffect> levelEffects = new ObjectArrayList<>();
                levelEffectStr.stream()
                        .map(effect -> effect.split(","))
                        .forEachOrdered(effectSplit -> levelEffects.add(new RecipeLevelEffect(
                                RecipeLevelEffect.RecipeLEType.valueOf(effectSplit[0].trim()),
                                Integer.parseInt(effectSplit[1].trim())
                        )));
                levels.put(i, levelEffects);
            }
            // 使用可能触媒 *
            final List<RequireMaterial> catalystCategories = RequireMaterial.load(item.getStringList("usable_catalysts_categories"));
            // アイデア
            final List<RequireAmountMaterial> ideaRequires;
            if (item.contains("idea")) {
                ideaRequires = RequireAmountMaterial.loadAmount(item.getStringList("idea"));
            } else {
                ideaRequires = Collections.emptyList();
            }
            // リストに追加
            final AlchemyRecipe recipe = new AlchemyRecipe(key, result, amount, reqMaterials, requireAlchemyLevel, defaultIngredients, req_bar, effects, levels, catalystCategories, ideaRequires);
            add(recipe);
            if (!ideaRequires.isEmpty()) {
                ideaRecipes.add(recipe);
            }
        });
    }

    private ARecipeResult<?> getResult(ConfigurationSection item) {
        final String resultData = item.getString("result");
        if (resultData != null) {
            if (resultData.startsWith("material:")) {
                return new AlchemyMaterialRecipeResult(AlchemyMaterial.getMaterial(resultData.substring(9)));
            } else if (resultData.startsWith("minecraft:")) {
                ItemUtils.getMaterial(resultData);
                final String[] result = resultData.contains(",") ? resultData.split(",") : new String[]{resultData};
                return new MinecraftMaterialRecipeResult(new CustomModelMaterial(ItemUtils.getMaterial(result[0].substring(10)), result.length > 1 ? Integer.parseInt(result[1]) : 0));
            }
        }
        throw new IllegalStateException("The value of the result could not load. " + resultData);
    }

    public List<AlchemyRecipe> getIdeaRecipes() {
        return ideaRecipes;
    }

    private StarEffect getStarEffect(ConfigurationSection starEffect) {
        StarEffect se = null;
        if (starEffect.contains("ingredient")) {
            se = new StarEffect(AlchemyIngredients.searchName(Objects.requireNonNull(starEffect.getString("ingredient"))));
        } else if (starEffect.contains("category")) {
            se = new StarEffect(Category.searchName(Objects.requireNonNull(starEffect.getString("category"))));
        } else if (starEffect.contains("effect")) {
            se = new StarEffect(AlchemyItemEffect.search(Objects.requireNonNull(starEffect.getString("effect"))));
        }
        return se;
    }
}
