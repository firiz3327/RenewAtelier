package net.firiz.renewatelier.alchemy;

import net.firiz.renewatelier.alchemy.material.AlchemyMaterial;
import net.firiz.renewatelier.alchemy.material.Category;
import net.firiz.renewatelier.alchemy.recipe.AlchemyRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RequireAmountMaterial extends RequireMaterial {

    private final int amount;

    public static List<RequireAmountMaterial> loadAmount(@NotNull final List<String> reqs) {
        Objects.requireNonNull(reqs);
        if (reqs.isEmpty()) {
            throw new IllegalStateException("reqs is Empty.");
        }
        final List<RequireAmountMaterial> list = new ArrayList<>();
        for (String req : reqs) {
            final String[] data = req.split(",");
            final int requireAmount = Integer.parseInt(data[1]);

            RequireAmountMaterial material = null;
            if (data[0].startsWith("material:")) {
                material = new RequireAmountMaterial(
                        AlchemyMaterial.getMaterial(data[0].substring(9)),
                        requireAmount
                );
            } else if (data[0].startsWith("category:")) {
                material = new RequireAmountMaterial(
                        Category.valueOf(data[0].substring(9)),
                        requireAmount
                );
            } else if (data[0].startsWith("recipe:")) {
                material = new RequireAmountMaterial(
                        AlchemyRecipe.search(data[0].substring(7)),
                        requireAmount
                );
            }
            if (material == null) {
                throw new IllegalStateException("null load.");
            } else {
                list.add(material);
            }
        }
        return list;
    }

    public RequireAmountMaterial(AlchemyMaterial material, int amount) {
        super(material);
        this.amount = amount;
    }

    public RequireAmountMaterial(Category category, int amount) {
        super(category);
        this.amount = amount;
    }

    public RequireAmountMaterial(AlchemyRecipe recipe, int amount) {
        super(recipe);
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

}
