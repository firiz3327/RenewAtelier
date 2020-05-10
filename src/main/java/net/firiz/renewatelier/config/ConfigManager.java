package net.firiz.renewatelier.config;

import java.util.List;

import net.firiz.renewatelier.utils.Chore;
import org.jetbrains.annotations.NotNull;

/**
 * @author firiz
 */
public enum ConfigManager {
    INSTANCE;

    private final ConfigLoader<?>[] loaders = new ConfigLoader<?>[]{
            new CharacteristicLoader(),
            new AlchemyMaterialLoader(),
            new AlchemyRecipeLoader(),
            new QuestLoader()
    };

    public void reloadConfigs() {
        for (final ConfigLoader<?> loader : loaders) {
            loader.load();
        }
    }

    @NotNull
    public <T> ConfigLoader<T> getLoader(@NotNull final Class<?> clasz, @NotNull final Class<T> tClass) {
        for (final ConfigLoader<?> loader : loaders) {
            if (loader.getClass() == clasz) {
                return Chore.cast(loader);
            }
        }
        throw new IllegalStateException(clasz.getName().concat(" not found."));
    }

    @NotNull
    public <T> List<T> getList(@NotNull final Class<?> clasz, @NotNull final Class<T> tClass) {
        for (final ConfigLoader<?> loader : loaders) {
            if (loader.getClass() == clasz) {
                return Chore.cast(loader.getList());
            }
        }
        throw new IllegalStateException(clasz.getName().concat(" not found."));
    }

}
