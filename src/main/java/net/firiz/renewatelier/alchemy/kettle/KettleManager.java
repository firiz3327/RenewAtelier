package net.firiz.renewatelier.alchemy.kettle;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.firiz.renewatelier.alchemy.recipe.AlchemyRecipe;
import net.firiz.renewatelier.utils.minecraft.ItemUtils;
import net.firiz.renewatelier.utils.java.CObjects;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public enum KettleManager {
    INSTANCE;

    final Object2ObjectMap<UUID, KettleUserData> userMap = new Object2ObjectOpenHashMap<>();

    public void create(@NotNull UUID uuid, @NotNull Location location, @NotNull AlchemyRecipe recipe) {
        userMap.put(uuid, new KettleUserData(location, recipe));
    }

    public KettleUserData getUserData(@NotNull UUID uuid) {
        return userMap.get(uuid);
    }

    @Nullable
    public KettleUserData remove(@NotNull Player player, boolean resetContents) {
        final UUID uuid = player.getUniqueId();
        if (userMap.containsKey(uuid)) {
            final KettleUserData kettleUserData = userMap.remove(uuid);
            CObjects.nonNullConsumer(
                    kettleUserData.getContents(),
                    contents -> {
                        player.getInventory().setContents(Objects.requireNonNull(contents));
                        player.updateInventory();
                    }
            );
            if (!resetContents) {
                kettleUserData.getPageItems().forEach(list -> list.forEach(item -> ItemUtils.addItem(player, item)));
            }
            return kettleUserData;
        }
        return null;
    }

}
