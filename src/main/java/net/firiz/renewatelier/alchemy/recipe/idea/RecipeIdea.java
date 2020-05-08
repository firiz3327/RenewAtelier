package net.firiz.renewatelier.alchemy.recipe.idea;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.firiz.renewatelier.alchemy.RequireAmountMaterial;
import net.firiz.renewatelier.alchemy.RequireMaterial;
import net.firiz.renewatelier.alchemy.material.AlchemyMaterial;
import net.firiz.renewatelier.alchemy.material.Category;
import net.firiz.renewatelier.alchemy.recipe.AlchemyRecipe;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class RecipeIdea {

    private final AlchemyRecipe recipe;
    private final Requires requires;

    public RecipeIdea(@NotNull AlchemyRecipe recipe) {
        this.recipe = Objects.requireNonNull(recipe);
        this.requires = new Requires();
        for (final RequireAmountMaterial requireMaterial : recipe.getIdeaRequires()) {
            this.requires.put(requireMaterial, 0);
        }
    }

    public RecipeIdea(@NotNull AlchemyRecipe recipe, @NotNull int[] ideaData) {
        this.recipe = Objects.requireNonNull(recipe);
        this.requires = new Requires();
        final List<RequireAmountMaterial> ideaRequires = recipe.getIdeaRequires();
        Objects.requireNonNull(ideaData);
        if (ideaData.length != ideaRequires.size()) {
            throw new IllegalArgumentException("The length of ideaData and the size of idea required for the recipe are different.");
        }
        for (int i = 0; i < ideaData.length; i++) {
            this.requires.put(ideaRequires.get(i), ideaData[i]);
        }
    }

    public AlchemyRecipe getRecipe() {
        return recipe;
    }

    public int[] getRequires() {
        final int[] result = new int[requires.size()];
        int i = 0;
        for (final int value : requires.values()) {
            result[i] = value;
            i++;
        }
        return result;
    }

    public boolean increaseIdea(final IncreaseIdea idea) {
        for (final RequireAmountMaterial require : requires.keySet()) {
            boolean check = false;
            switch (require.getType()) {
                case CATEGORY:
                case MATERIAL:
                    check = checkAlchemyMaterial(idea, require);
                    break;
                case RECIPE:
                    check = idea.recipe == require.getRecipe();
                    break;
                default: // 想定しない
                    break;
            }
            if (check) {
                return requires.increaseIdea(require);
            }
        }
        return false;
    }

    private boolean checkAlchemyMaterial(final IncreaseIdea idea, final RequireMaterial require) {
        final AlchemyMaterial material = idea.material;
        boolean result = false;
        if (material != null) {
            if (require.getType() == RequireMaterial.RequireType.CATEGORY) {
                for (final Category category : material.getCategories()) {
                    if (category == require.getCategory()) {
                        result = true;
                        break;
                    }
                }
            } else if (material == require.getMaterial()) { // alchemyMaterial only
                result = true;
            }
        }
        return result;
    }

    private static class Requires extends Object2IntOpenHashMap<RequireAmountMaterial> {

        public boolean increaseIdea(final RequireAmountMaterial require) {
            final int after = getInt(require) + 1;
            put(require, after);
            return after >= require.getAmount();
        }

    }

    public static class IncreaseIdea {
        private final AlchemyMaterial material;
        private final ItemStack item;
        private final AlchemyRecipe recipe;

        public IncreaseIdea(ItemStack item) {
            this.material = AlchemyMaterial.getMaterialOrNull(item);
            this.item = material == null ? item : null;
            this.recipe = null;
        }

        public IncreaseIdea(AlchemyRecipe recipe) {
            this.material = null;
            this.item = null;
            this.recipe = recipe;
        }

        public AlchemyMaterial getMaterial() {
            return material;
        }

        public ItemStack getItem() {
            return item;
        }

        public AlchemyRecipe getRecipe() {
            return recipe;
        }
    }

}
