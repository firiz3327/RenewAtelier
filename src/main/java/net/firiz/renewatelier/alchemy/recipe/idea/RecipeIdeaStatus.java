package net.firiz.renewatelier.alchemy.recipe.idea;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.firiz.renewatelier.alchemy.RequireAmountMaterial;
import net.firiz.renewatelier.alchemy.RequireMaterial;
import net.firiz.renewatelier.alchemy.material.AlchemyMaterial;
import net.firiz.renewatelier.alchemy.material.Category;
import net.firiz.renewatelier.entity.player.Char;
import net.firiz.renewatelier.utils.java.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class RecipeIdeaStatus {

    private final RequireRecipeIdea recipeIdea;
    private final Requires requires;

    public RecipeIdeaStatus(@NotNull RequireRecipeIdea recipeIdea) {
        this.recipeIdea = Objects.requireNonNull(recipeIdea);
        this.requires = new Requires();
        for (final RequireAmountMaterial requireMaterial : recipeIdea.getRequireMaterials()) {
            this.requires.put(requireMaterial, 0);
        }
    }

    public RecipeIdeaStatus(@NotNull RequireRecipeIdea recipeIdea, @NotNull String ideaData) {
        this.recipeIdea = Objects.requireNonNull(recipeIdea);
        this.requires = new Requires();
        Objects.requireNonNull(ideaData);
        convertRequires(ideaData);
    }

    private void convertRequires(String ideaData) {
        final List<RequireAmountMaterial> ideaRequires = recipeIdea.getRequireMaterials();
        final int[] reqs = ArrayUtils.convertToInt(ideaData.split(","));
        for (int i = 0; i < reqs.length; i++) {
            this.requires.put(ideaRequires.get(i), reqs[i]);
        }
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

    public boolean isAvailable(@NotNull final Char character) {
        for (final RequireAmountMaterial require : requires.keySet()) {
            if (!requires.isAvailable(require)) {
                return false;
            }
        }
        return recipeIdea.getRequireIfs().stream().allMatch(requireIf -> requireIf.isAvailable(character));
    }

    public void increaseIdea(final IncreaseIdea idea) {
        for (final RequireAmountMaterial require : requires.keySet()) {
            boolean check = false;
            switch (require.getType()) {
                case CATEGORY:
                case MATERIAL:
                    check = checkAlchemyMaterial(idea, require);
                    break;
                case RECIPE:
                    check = idea.getRecipe() == require.getRecipe();
                    break;
                default: // 想定しない
                    break;
            }
            if (check) {
                requires.increaseIdea(require);
            }
        }
    }

    private boolean checkAlchemyMaterial(final IncreaseIdea idea, final RequireMaterial require) {
        final AlchemyMaterial material = idea.getMaterial();
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

        void increaseIdea(final RequireAmountMaterial require) {
            final int after = getInt(require) + 1;
            put(require, after);
        }

        boolean isAvailable(final RequireAmountMaterial require) {
            return getInt(require) >= require.getAmount();
        }

    }

}
