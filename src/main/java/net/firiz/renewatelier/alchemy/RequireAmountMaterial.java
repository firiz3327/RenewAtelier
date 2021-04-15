package net.firiz.renewatelier.alchemy;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.firiz.renewatelier.alchemy.material.AlchemyMaterial;
import net.firiz.renewatelier.alchemy.material.Category;
import net.firiz.renewatelier.alchemy.recipe.AlchemyRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class RequireAmountMaterial extends RequireMaterial {

    private final int amount;

    public static List<RequireAmountMaterial> loadAmount(@NotNull final List<String> reqs) {
        return loadAmountIgnored(reqs, data -> {
            throw new IllegalStateException("null load.");
        });
    }

    public static List<RequireAmountMaterial> loadAmountIgnored(@NotNull final List<String> reqs, @NotNull final Consumer<String> consumer) {
        Objects.requireNonNull(reqs);
//        if (reqs.isEmpty()) {
//            throw new IllegalStateException("reqs is Empty.");
//        }
        final List<RequireAmountMaterial> list = new ObjectArrayList<>();
        for (final String req : reqs) {
            final String[] data = req.split(",");
            RequireAmountMaterial material = null;
            if (data.length > 1) {
                final int requireAmount = Integer.parseInt(data[1]);
                if (data[0].startsWith("material:")) {
                    material = new RequireAmountMaterial(
                            AlchemyMaterial.getMaterial(data[0].substring(9)),
                            requireAmount
                    );
                } else if (data[0].startsWith("category:")) {
                    material = new RequireAmountMaterial(
                            Category.searchName(data[0].substring(9)),
                            requireAmount
                    );
                } else if (data[0].startsWith("recipe:")) {
                    material = new RequireAmountMaterial(
                            AlchemyRecipe.search(data[0].substring(7)),
                            requireAmount
                    );
                }
            }
            if (material == null) {
                consumer.accept(req);
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
