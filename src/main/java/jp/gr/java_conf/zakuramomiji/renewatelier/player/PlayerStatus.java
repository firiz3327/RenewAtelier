/*
 * PlayerStatus.java
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

import java.util.ArrayList;
import java.util.List;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.recipe.RecipeStatus;
import jp.gr.java_conf.zakuramomiji.renewatelier.sql.SQLManager;
import jp.gr.java_conf.zakuramomiji.renewatelier.utils.GameContents;

/**
 *
 * @author firiz
 */
public final class PlayerStatus {

    private final int id;
    private final List<RecipeStatus> recipe_statuses;

    public PlayerStatus(final int id) {
        this.id = id;
        recipe_statuses = new ArrayList<>();
    }

    public void addRecipes(final List<RecipeStatus> recipe_statuses) {
        this.recipe_statuses.addAll(recipe_statuses);
        recipe_statuses.forEach((rs) -> {
            SQLManager.getInstance().insert(
                    "recipe_levels",
                    new String[]{"user_id", "recipe_id", "level", "exp"},
                    new Object[]{id, rs.getId(), rs.getLevel(), rs.getExp()}
            );
        });
    }

    public void addRecipe(final RecipeStatus recipe_status) {
        recipe_statuses.add(recipe_status);
        SQLManager.getInstance().insert(
                "recipe_levels",
                new String[]{"user_id", "recipe_id", "level", "exp"},
                new Object[]{id, recipe_status.getId(), recipe_status.getLevel(), recipe_status.getExp()}
        );
    }

    public List<RecipeStatus> getRecipeStatusList() {
        return new ArrayList<>(recipe_statuses);
    }

    public RecipeStatus getRecipeStatus(final String id) {
        for (final RecipeStatus rs : recipe_statuses) {
            if (rs.getId().equals(id)) {
                return rs;
            }
        }
        return null;
    }

    public void addRecipeExp(final String recipe_id, final int exp) {
        RecipeStatus status = null;
        for (final RecipeStatus rs : getRecipeStatusList()) {
            if (rs.getId().equals(recipe_id)) {
                if (rs.getLevel() > 3) {
                    return;
                } else {
                    status = rs;
                    break;
                }
            }
        }
        if (status == null) {
            status = new RecipeStatus(recipe_id, 0, 0);
            addRecipe(status);
        }

        status.setExp(status.getExp() + exp);
        while (true) {
            final int level = status.getLevel();
            final int req_exp = GameContents.RECIPE_REQLEVELS[level];
            if (status.getExp() >= req_exp) {
                status.setLevel(level + 1);
                status.setExp(status.getExp() - req_exp);
                continue;
            }
            break;
        }
        SQLManager.getInstance().insert(
                "recipe_levels",
                new String[]{"user_id", "recipe_id", "level", "exp"},
                new Object[]{id, status.getId(), status.getLevel(), status.getExp()}
        );
    }

}
