package net.firiz.renewatelier.entity.player.sql.load;

import java.util.List;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.firiz.renewatelier.alchemy.recipe.RecipeStatus;
import net.firiz.renewatelier.sql.SQLManager;
import net.firiz.renewatelier.utils.java.ArrayUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author firiz
 */
class RecipeStatusLoader implements StatusLoader<List<RecipeStatus>> {

    @NotNull
    @Override
    public List<RecipeStatus> load(int id) {
        final List<List<Object>> recipeStatusesObj = SQLManager.INSTANCE.select(
                "recipeLevels",
                new String[]{"userId", "recipeId", "acquired", "level", "exp", "idea"},
                new Object[]{id}
        );
        final List<RecipeStatus> recipeStatuses = new ObjectArrayList<>();
        recipeStatusesObj.forEach(objectList -> {
            final boolean acquired = (boolean) objectList.get(2);
            if (acquired) {
                recipeStatuses.add(new RecipeStatus(
                        (String) objectList.get(1), // recipe_id
                        (int) objectList.get(3), // level
                        (int) objectList.get(4) // exp
                ));
            } else {
                final Object ideaObject = objectList.get(5);
                recipeStatuses.add(new RecipeStatus(
                        (String) objectList.get(1), // recipe_id
                        (int) objectList.get(3), // level
                        (int) objectList.get(4), // exp
                        (String) ideaObject
                ));
            }
        });
        return recipeStatuses;
    }

}
