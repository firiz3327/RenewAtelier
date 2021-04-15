package net.firiz.renewatelier.alchemy;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.firiz.renewatelier.alchemy.material.AlchemyMaterial;
import net.firiz.renewatelier.alchemy.material.Category;
import net.firiz.renewatelier.alchemy.recipe.AlchemyRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class RequireMaterial {

    private final RequireType type;
    private final AlchemyMaterial material;
    private final Category category;
    private final AlchemyRecipe recipe;

    public RequireMaterial(AlchemyMaterial material) {
        this.type = RequireType.MATERIAL;
        this.material = material;
        this.category = null;
        this.recipe = null;
    }

    public RequireMaterial(Category category) {
        this.type = RequireType.CATEGORY;
        this.material = null;
        this.category = category;
        this.recipe = null;
    }

    public RequireMaterial(AlchemyRecipe recipe) {
        this.type = RequireType.RECIPE;
        this.material = null;
        this.category = null;
        this.recipe = recipe;
    }

    public static List<RequireMaterial> load(@NotNull final List<String> reqs) {
        Objects.requireNonNull(reqs);
        if (reqs.isEmpty()) {
            throw new IllegalStateException("reqs is Empty.");
        }
        final List<RequireMaterial> list = new ObjectArrayList<>();
        for (String req : reqs) {
            list.add(load(req));
        }
        return list;
    }

    public static RequireMaterial load(@NotNull final String req) {
        RequireMaterial material = null;
        if (req.startsWith("material:")) {
            material = new RequireMaterial(AlchemyMaterial.getMaterial(req.substring(9)));
        } else if (req.startsWith("category:")) {
            material = new RequireMaterial(Category.searchName(req.substring(9)));
        } else if (req.startsWith("recipe:")) {
            material = new RequireMaterial(AlchemyRecipe.search(req.substring(7)));
        }
        if (material == null) {
            throw new IllegalStateException("null load.");
        } else {
            return material;
        }
    }

    public RequireType getType() {
        return type;
    }

    public AlchemyMaterial getMaterial() {
        return material;
    }

    public Category getCategory() {
        return category;
    }

    public AlchemyRecipe getRecipe() {
        return recipe;
    }

    public enum RequireType {
        MATERIAL,
        CATEGORY,
        RECIPE
    }
}
