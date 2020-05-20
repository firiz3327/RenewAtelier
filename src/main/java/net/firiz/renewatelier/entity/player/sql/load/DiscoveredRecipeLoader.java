package net.firiz.renewatelier.entity.player.sql.load;

import java.util.List;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.firiz.renewatelier.version.minecraft.MinecraftRecipeSaveType;
import net.firiz.renewatelier.sql.SQLManager;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author firiz
 */
class DiscoveredRecipeLoader implements StatusLoader<List<MinecraftRecipeSaveType>> {

    @NotNull
    @Override
    public List<MinecraftRecipeSaveType> load(int id) {
        final List<List<Object>> saveTypesObj = SQLManager.INSTANCE.select(
                "discoveredRecipes",
                new String[]{"userId", "itemId"},
                new Object[]{id}
        );
        final List<MinecraftRecipeSaveType> saveTypes = new ObjectArrayList<>();
        saveTypesObj.forEach(datas -> {
            final MinecraftRecipeSaveType type = MinecraftRecipeSaveType.search((String) datas.get(1));
            if (type != null) {
                saveTypes.add(type);
            }
        });
        return saveTypes;
    }

}
