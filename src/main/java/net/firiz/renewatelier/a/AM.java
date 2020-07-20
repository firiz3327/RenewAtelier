package net.firiz.renewatelier.a;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.firiz.renewatelier.alchemy.recipe.AlchemyRecipe;
import net.firiz.renewatelier.utils.ItemUtils;
import net.firiz.renewatelier.utils.chores.CObjects;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public enum AM {
    INSTANCE;

    final Object2ObjectMap<UUID, AData> userMap = new Object2ObjectOpenHashMap<>();

    public void create(@NotNull UUID uuid, @NotNull Location location, @NotNull AlchemyRecipe recipe) {
        userMap.put(uuid, new AData(location, recipe));
    }

    public AData getUserData(@NotNull UUID uuid) {
        return userMap.get(uuid);
    }

    @Nullable
    public AData remove(@NotNull Player player, boolean resetContents) {
        final UUID uuid = player.getUniqueId();
        if (userMap.containsKey(uuid)) {
            final AData aData = userMap.remove(uuid);
            CObjects.nonNullConsumer(
                    aData.getContents(),
                    contents -> {
                        player.getInventory().setContents(Objects.requireNonNull(contents));
                        player.updateInventory();
                    }
            );
            if (!resetContents) {
                aData.getPageItems().forEach(list -> list.forEach(item -> ItemUtils.addItem(player, item)));
            }

            return aData;
        }
        return null;
    }

}
