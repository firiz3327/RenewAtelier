/*
 * PlayerSaveManager.java
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
package jp.gr.java_conf.zakuramomiji.renewatelier.player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.recipe.RecipeStatus;
import jp.gr.java_conf.zakuramomiji.renewatelier.sql.SQLManager;
import jp.gr.java_conf.zakuramomiji.renewatelier.utils.Chore;

/**
 *
 * @author firiz
 */
public enum PlayerSaveManager {
    INSTANCE;
    
    private final SQLManager sql = SQLManager.INSTANCE;
    private final Map<UUID, PlayerStatus> statusList = new HashMap<>();

    public PlayerStatus getStatus(final UUID uuid) {
        PlayerStatus status;
        if (!statusList.containsKey(uuid)) {
            final List<List<Object>> select = sql.select(
                    "accounts",
                    new String[]{"uuid", "id"},
                    new Object[]{uuid.toString()}
            );
            Chore.log(select);
            final int id = (int) select.get(0).get(1);
            status = new PlayerStatus(id);
            final List<List<Object>> recipe_statuses_obj = sql.select(
                    "recipe_levels",
                    new String[]{"user_id", "recipe_id", "level", "exp"},
                    new Object[]{id}
            );
            recipe_statuses_obj.forEach((datas) -> {
                status.addRecipe(new RecipeStatus(
                        (String) datas.get(1), // id
                        (int) datas.get(2), // level
                        (int) datas.get(3) // exp
                ));
            });
            statusList.put(uuid, status);
        } else {
            status = statusList.get(uuid);
        }
        return status;
    }

    public void unloadStatus(final UUID uuid) {
        statusList.remove(uuid);
    }

}
