/*
 * ConfigManager.java
 *
 * Copyright (c) 2018 firiz.
 *
 * This file is part of Expression program is undefined on line 6, column 40 in Templates/Licenses/license-licence-gplv3.txt..
 *
 * Expression program is undefined on line 8, column 19 in Templates/Licenses/license-licence-gplv3.txt. is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Expression program is undefined on line 13, column 19 in Templates/Licenses/license-licence-gplv3.txt. is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Expression program is undefined on line 19, column 30 in Templates/Licenses/license-licence-gplv3.txt..  If not, see <http ://www.gnu.org/licenses/>.
 */
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
