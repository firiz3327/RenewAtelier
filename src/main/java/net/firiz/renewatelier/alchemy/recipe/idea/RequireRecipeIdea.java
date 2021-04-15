package net.firiz.renewatelier.alchemy.recipe.idea;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.firiz.renewatelier.alchemy.RequireAmountMaterial;
import net.firiz.renewatelier.alchemy.RequireMaterial;
import net.firiz.renewatelier.alchemy.recipe.idea.ifs.RequireIf;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RequireRecipeIdea {

    private final List<RequireIf> requireIfs = new ObjectArrayList<>();
    private final List<RequireAmountMaterial> requireMaterials;

    public RequireRecipeIdea(List<String> ideaData) {
        this.requireMaterials = RequireAmountMaterial.loadAmountIgnored(
                ideaData,
                ignore -> requireIfs.add(new RequireIf(ignore))
        );
    }

    public boolean isEmpty() {
        return requireMaterials.isEmpty() && requireIfs.isEmpty();
    }

    public List<RequireAmountMaterial> getRequireMaterials() {
        return requireMaterials;
    }

    public List<RequireIf> getRequireIfs() {
        return requireIfs;
    }

    public boolean hasIdeaRequire(@NotNull final IncreaseIdea idea) {
        if (idea.getMaterial() != null) {
            for (final RequireAmountMaterial requireMaterial : requireMaterials) {
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
            for (final RequireAmountMaterial requireMaterial : requireMaterials) {
                if (requireMaterial.getType() == RequireMaterial.RequireType.RECIPE
                        && requireMaterial.getRecipe() == idea.getRecipe()) {
                    return true;
                }
            }
        }
        return false;
    }

}
