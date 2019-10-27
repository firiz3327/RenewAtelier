/*
 * DiscoveredRecipeLoader.java
 * 
 * Copyright (c) 2019 firiz.
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
package net.firiz.renewatelier.entity.player.loadsqls;

import java.util.ArrayList;
import java.util.List;
import net.firiz.renewatelier.entity.player.minecraft.MinecraftRecipeSaveType;
import net.firiz.renewatelier.sql.SQLManager;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author firiz
 */
public class DiscoveredRecipeLoader implements StatusLoader<List<MinecraftRecipeSaveType>> {

    @NotNull
    @Override
    public List<MinecraftRecipeSaveType> load(int id) {
        final List<List<Object>> saveTypesObj = SQLManager.INSTANCE.select(
                "discoveredRecipes",
                new String[]{"user_id", "item_id"},
                new Object[]{id}
        );
        final List<MinecraftRecipeSaveType> saveTypes = new ArrayList<>();
        saveTypesObj.forEach(datas -> {
            final MinecraftRecipeSaveType type = MinecraftRecipeSaveType.search((String) datas.get(1));
            if (type != null) {
                saveTypes.add(type);
            }
        });
        return saveTypes;
    }

}
