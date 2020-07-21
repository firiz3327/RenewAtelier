package net.firiz.renewatelier.alchemy.recipe;

import java.util.*;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.firiz.renewatelier.alchemy.RequireAmountMaterial;
import net.firiz.renewatelier.alchemy.RequireMaterial;
import net.firiz.renewatelier.alchemy.material.AlchemyIngredients;
import net.firiz.renewatelier.alchemy.recipe.idea.IncreaseIdea;
import net.firiz.renewatelier.alchemy.recipe.idea.RecipeIdea;
import net.firiz.renewatelier.config.ConfigManager;
import net.firiz.renewatelier.config.AlchemyRecipeLoader;
import org.jetbrains.annotations.NotNull;

/**
 * @author firiz
 */
public class AlchemyRecipe {

    private static final ConfigManager CONFIG_MANAGER = ConfigManager.INSTANCE;
    private final String id;
    private final String result;
    private final int amount;
    private final List<RequireAmountMaterial> reqMaterial;
    private final int reqAlchemyLevel;
    private final List<AlchemyIngredients> defaultIngredients;
    private final int reqBar;
    private final List<RecipeEffect> effects;
    private final Int2ObjectMap<List<RecipeLevelEffect>> levels;
    private final List<RequireMaterial> catalystCategories;
    private final List<RequireAmountMaterial> ideaRequires;

    public AlchemyRecipe(
            final String id,
            final String result,
            final int amount,
            final List<RequireAmountMaterial> requireMaterials,
            final int reqAlchemyLevel,
            final List<AlchemyIngredients> defaultIngredients,
            final int reqBar,
            final List<RecipeEffect> effects,
            final Int2ObjectMap<List<RecipeLevelEffect>> levels,
            final List<RequireMaterial> catalystCategories,
            final List<RequireAmountMaterial> ideaRequires
    ) {
        this.id = id;
        this.result = result;
        this.amount = amount;
        this.reqMaterial = requireMaterials;
        this.reqAlchemyLevel = reqAlchemyLevel;
        this.defaultIngredients = defaultIngredients;
        this.reqBar = reqBar;
        this.effects = effects;
        this.levels = levels;
        this.catalystCategories = catalystCategories;
        this.ideaRequires = ideaRequires;
    }

    public static AlchemyRecipe search(@NotNull final String id) {
        for (final AlchemyRecipe recipe : CONFIG_MANAGER.getList(AlchemyRecipeLoader.class, AlchemyRecipe.class)) {
            if (id.equals(recipe.getId())) {
                return recipe;
            }
        }
        return null;
    }

    public static List<AlchemyRecipe> getIdeaRecipeList() {
        return ((AlchemyRecipeLoader) CONFIG_MANAGER.getLoader(AlchemyRecipeLoader.class, AlchemyRecipe.class)).getIdeaRecipes();
    }

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public String getResult() {
        return result;
    }

    public int getAmount() {
        return amount;
    }

    @NotNull
    public List<RequireAmountMaterial> getReqMaterial() {
        return new ObjectArrayList<>(reqMaterial);
    }

    public int getReqAlchemyLevel() {
        return reqAlchemyLevel;
    }

    @NotNull
    public List<AlchemyIngredients> getDefaultIngredients() {
        return new ObjectArrayList<>(defaultIngredients);
    }

    public int getReqBar() {
        return reqBar;
    }

    @NotNull
    public List<RecipeEffect> getEffects() {
        return new ObjectArrayList<>(effects);
    }

    @NotNull
    public Int2ObjectMap<List<RecipeLevelEffect>> getLevels() {
        return new Int2ObjectOpenHashMap<>(levels);
    }

    @NotNull
    public List<RequireMaterial> getCatalystCategories() {
        return new ObjectArrayList<>(catalystCategories);
    }

    @NotNull
    public List<RequireAmountMaterial> getIdeaRequires() {
        return ideaRequires;
    }

    public boolean isIdeaRequired() {
        return !ideaRequires.isEmpty();
    }

    public boolean hasIdeaRequire(@NotNull IncreaseIdea idea) {
        if (!isIdeaRequired()) {
            throw new IllegalStateException("There are no ideas for this recipe.");
        }
        Objects.requireNonNull(idea);
        if (idea.getMaterial() != null) {
            for (final RequireAmountMaterial requireMaterial : ideaRequires) {
                if ((
                        requireMaterial.getType() == RequireMaterial.RequireType.MATERIAL
                                && requireMaterial.getMaterial() == idea.getMaterial()
                ) || (
                        requireMaterial.getType() == RequireMaterial.RequireType.CATEGORY
                                && idea.getMaterial().getCategories().contains(requireMaterial.getCategory())
                )) {
                    return true;
                }
            }
        } else if (idea.getRecipe() != null) {
            for (final RequireAmountMaterial requireMaterial : ideaRequires) {
                if (requireMaterial.getType() == RequireMaterial.RequireType.RECIPE
                        && requireMaterial.getRecipe() == idea.getRecipe()) {
                    return true;
                }
            }
        }
        return false;
    }

}
