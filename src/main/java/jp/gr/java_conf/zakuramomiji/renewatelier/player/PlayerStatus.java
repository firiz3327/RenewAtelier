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
import javax.script.ScriptEngine;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.recipe.AlchemyRecipe;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.recipe.RecipeStatus;
import jp.gr.java_conf.zakuramomiji.renewatelier.sql.SQLManager;
import jp.gr.java_conf.zakuramomiji.renewatelier.constants.GameConstants;
import jp.gr.java_conf.zakuramomiji.renewatelier.nodification.Nodification;
import jp.gr.java_conf.zakuramomiji.renewatelier.player.minecraft.MinecraftRecipeSaveType;
import jp.gr.java_conf.zakuramomiji.renewatelier.quest.Quest;
import jp.gr.java_conf.zakuramomiji.renewatelier.quest.QuestItem;
import jp.gr.java_conf.zakuramomiji.renewatelier.quest.QuestStatus;
import jp.gr.java_conf.zakuramomiji.renewatelier.quest.result.ItemQuestResult;
import jp.gr.java_conf.zakuramomiji.renewatelier.quest.result.MoneyQuestResult;
import jp.gr.java_conf.zakuramomiji.renewatelier.quest.result.RecipeQuestResult;
import jp.gr.java_conf.zakuramomiji.renewatelier.utils.Chore;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author firiz
 */
public final class PlayerStatus {

    private final int id;
    private final List<RecipeStatus> recipe_statuses;
    private final List<QuestStatus> quest_statuses;
    private final List<MinecraftRecipeSaveType> saveTypes;
    private final ScriptEngine jsEngine;
    private final ScriptEngine pyEngine;

    public PlayerStatus(final int id, final List<RecipeStatus> recipe_statuses, final List<QuestStatus> quest_statuses, final List<MinecraftRecipeSaveType> saveTypes, final ScriptEngine jsEngine, final ScriptEngine pyEngine) {
        this.id = id;
        this.recipe_statuses = recipe_statuses;
        this.quest_statuses = quest_statuses;
        this.saveTypes = saveTypes;
        this.jsEngine = jsEngine;
        this.pyEngine = pyEngine;
    }

    //<editor-fold defaultstate="collapsed" desc="alchemy recipe">
    private void addRecipe(final RecipeStatus recipe_status) {
        recipe_statuses.add(recipe_status);
        SQLManager.INSTANCE.insert(
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

    public void addRecipeExp(final Player player, final boolean view, final AlchemyRecipe recipe, final int exp) {
        if (addRecipeExp(recipe.getId(), exp) && view) {
            Nodification.recipeNodification(player, Material.CAULDRON);
            player.sendMessage("レシピ【" + ChatColor.GREEN + recipe.getResult() + ChatColor.RESET + "】を開放しました。");
        }
    }

    private boolean addRecipeExp(final String recipe_id, final int exp) {
        RecipeStatus status = null;
        for (final RecipeStatus rs : getRecipeStatusList()) {
            if (rs.getId().equals(recipe_id)) {
                if (rs.getLevel() > 3) {
                    return false;
                } else {
                    status = rs;
                    break;
                }
            }
        }

        boolean first = false;
        if (status == null) {
            status = new RecipeStatus(recipe_id);
            addRecipe(status);
            first = true;
        }

        status.setExp(status.getExp() + exp);
        while (true) {
            final int level = status.getLevel();
            final int req_exp = GameConstants.RECIPE_REQLEVELS[level];
            if (status.getExp() >= req_exp) {
                status.setLevel(level + 1);
                status.setExp(status.getExp() - req_exp);
                continue;
            }
            break;
        }
        SQLManager.INSTANCE.insert(
                "recipe_levels",
                new String[]{"user_id", "recipe_id", "level", "exp"},
                new Object[]{id, status.getId(), status.getLevel(), status.getExp()}
        );
        return first;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="quest">
    public void addQuest(final QuestStatus quest_status) {
        quest_statuses.add(quest_status);
        SQLManager.INSTANCE.insert(
                "questDatas",
                new String[]{"user_id", "quest_id", "clear"},
                new Object[]{id, quest_status.getId(), quest_status.isClear() ? 1 : 0}
        );
    }

    public void addQuest(final Player player, final String questId) {
        addQuest(new QuestStatus(questId));
        player.sendMessage("クエスト「" + ChatColor.GREEN + Quest.getQuest(questId).getName() + ChatColor.RESET + "」を受注しました。");
    }

    public List<QuestStatus> getQuestStatusList() {
        return new ArrayList<>(quest_statuses);
    }

    public QuestStatus getQuestStatus(final String id) {
        for (final QuestStatus rs : quest_statuses) {
            if (rs.getId().equals(id)) {
                return rs;
            }
        }
        return null;
    }

    public void questClear(final Player player, final String id, final boolean view) {
        questClear(player, getQuestStatus(id), view);
    }

    public void questClear(final Player player, final QuestStatus quest_status, final boolean view) {
        SQLManager.INSTANCE.insert(
                "questDatas",
                new String[]{"user_id", "quest_id", "clear"},
                new Object[]{id, quest_status.getId(), 1}
        );
        quest_status.clear();
        final Quest quest = Quest.getQuest(quest_status.getId());
        if (view) {
            player.sendMessage("クエスト「" + ChatColor.GREEN + quest.getName() + ChatColor.RESET + "」を完了しました。");
        }
        if (quest.getNextQuestId() != null) {
            addQuest(player, quest.getNextQuestId());
        }
        if (quest.getResults() != null) {
            quest.getResults().forEach((result) -> {
                if (result instanceof ItemQuestResult) {
                    final QuestItem item = ((ItemQuestResult) result).getResult();
                    Chore.addItem(player, item.getItem());
                } else if (result instanceof RecipeQuestResult) {
                    final AlchemyRecipe recipe = ((RecipeQuestResult) result).getResult();
                    addRecipeExp(player, true, recipe, 0);
                } else if (result instanceof MoneyQuestResult) {
                    final int money = ((MoneyQuestResult) result).getResult();
                    Chore.addItem(player, new ItemStack(Material.EMERALD, money));
                }
            });
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="minecraft recipe">
    public boolean discoveredRecipe(final MinecraftRecipeSaveType type) {
        return saveTypes.contains(type);
    }

    public void discoverRecipe(final String item_id) {
        final MinecraftRecipeSaveType type = MinecraftRecipeSaveType.search(item_id);
        if (type != null) {
            saveTypes.add(type);
            SQLManager.INSTANCE.insert(
                    "discoveredRecipes",
                    new String[]{"user_id", "item_id"},
                    new Object[]{id, item_id}
            );
        }
    }

    /**
     * 基本レシピの喪失は起こりえないので、使用しない
     *
     * @param item_id
     */
    public void undiscoverRecipe(final String item_id) {
        final MinecraftRecipeSaveType type = MinecraftRecipeSaveType.search(item_id);
        if (type != null) {
            saveTypes.remove(type);
            SQLManager.INSTANCE.delete(
                    "discoveredRecipes",
                    new String[]{"user_id", "item_id"},
                    new Object[]{id, item_id}
            );
        }
    }
    //</editor-fold>

    public ScriptEngine getJsEngine() {
        return jsEngine;
    }

    public ScriptEngine getPyEngine() {
        return pyEngine;
    }

}
