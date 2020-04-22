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
package net.firiz.renewatelier.entity.player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.script.ScriptEngine;

import net.firiz.renewatelier.alchemy.recipe.AlchemyRecipe;
import net.firiz.renewatelier.alchemy.recipe.RecipeStatus;
import net.firiz.renewatelier.entity.player.stats.CharStats;
import net.firiz.renewatelier.loop.LoopManager;
import net.firiz.renewatelier.sql.SQLManager;
import net.firiz.renewatelier.constants.GameConstants;
import net.firiz.renewatelier.notification.Notification;
import net.firiz.renewatelier.version.minecraft.MinecraftRecipeSaveType;
import net.firiz.renewatelier.quest.Quest;
import net.firiz.renewatelier.quest.QuestItem;
import net.firiz.renewatelier.quest.QuestStatus;
import net.firiz.renewatelier.quest.result.ItemQuestResult;
import net.firiz.renewatelier.quest.result.MoneyQuestResult;
import net.firiz.renewatelier.quest.result.RecipeQuestResult;
import net.firiz.renewatelier.utils.Chore;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author firiz
 */
public final class Char {

    private static final String COLUMN_RECIPE_LEVELS = "recipeLevels";
    private static final String COLUMN_USER_ID = "userId";
    private static final String COLUMN_RECIPE_ID = "recipeId";
    private static final String COLUMN_LEVEL = "level";

    private final UUID uuid;
    private final int id;
    private final String email;
    private final String password;
    private final CharStats charStats;
    private final List<RecipeStatus> recipeStatuses;
    private final List<QuestStatus> questStatuses;
    private final List<MinecraftRecipeSaveType> saveTypes;
    private final CharSettings settings;
    private boolean isEnginesUsable;
    private ScriptEngine jsEngine;
    private ScriptEngine py3Engine;
    private final Runnable autoSave;

    public Char(@NotNull UUID uuid, final int id, @Nullable String email, @Nullable String password, @NotNull CharStats charStats, @NotNull final List<RecipeStatus> recipeStatuses, @NotNull final List<QuestStatus> questStatuses, @NotNull final List<MinecraftRecipeSaveType> saveTypes, CharSettings settings) {
        this.uuid = uuid;
        this.id = id;
        this.email = email;
        this.password = password;
        this.charStats = charStats;
        this.recipeStatuses = recipeStatuses;
        this.questStatuses = questStatuses;
        this.saveTypes = saveTypes;
        this.settings = settings;

        this.autoSave = () -> {
            this.charStats.save(this.id);
            this.settings.save(this.id);
        };
        LoopManager.INSTANCE.addMinutes(this.autoSave);
    }

    public void unload() {
        LoopManager.INSTANCE.removeMinutes(this.autoSave);
        autoSave.run();
    }

    public void respawn() {
        charStats.clearBuffs();
        charStats.damageHp(-charStats.getMaxHp());
    }

    //<editor-fold defaultstate="collapsed" desc="alchemy recipe">
    private void addRecipe(final RecipeStatus recipeStatus) {
        recipeStatuses.add(recipeStatus);
        SQLManager.INSTANCE.insert(
                COLUMN_RECIPE_LEVELS,
                new String[]{COLUMN_USER_ID, COLUMN_RECIPE_ID, COLUMN_LEVEL, "exp"},
                new Object[]{id, recipeStatus.getId(), recipeStatus.getLevel(), recipeStatus.getExp()}
        );
    }

    @NotNull
    public List<RecipeStatus> getRecipeStatusList() {
        return new ArrayList<>(recipeStatuses);
    }

    @Nullable
    public RecipeStatus getRecipeStatus(final String id) {
        for (final RecipeStatus rs : recipeStatuses) {
            if (rs.getId().equals(id)) {
                return rs;
            }
        }
        return null;
    }

    public void setRecipeStatus(final RecipeStatus status) {
        RecipeStatus rs = getRecipeStatus(status.getId());
        if (rs == null) {
            rs = status;
            recipeStatuses.add(rs);
        }
        SQLManager.INSTANCE.insert(
                COLUMN_RECIPE_LEVELS,
                new String[]{COLUMN_USER_ID, COLUMN_RECIPE_ID, COLUMN_LEVEL, "exp"},
                new Object[]{id, rs.getId(), rs.getLevel(), rs.getExp()}
        );
    }

    public void addRecipeExp(final Player player, final boolean view, final AlchemyRecipe recipe, final int exp) {
        if (addRecipeExp(recipe.getId(), exp) && view) {
            Notification.recipeNotification(player, Material.CAULDRON);
            player.sendMessage("レシピ【" + ChatColor.GREEN + recipe.getResult() + ChatColor.RESET + "】を開放しました。");
        }
    }

    private boolean addRecipeExp(final String recipeId, final int exp) {
        RecipeStatus status = null;
        for (final RecipeStatus rs : getRecipeStatusList()) {
            if (rs.getId().equals(recipeId)) {
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
            status = new RecipeStatus(recipeId);
            addRecipe(status);
            first = true;
        }

        status.setExp(status.getExp() + exp);
        while (true) {
            final int level = status.getLevel();
            final int req_exp = GameConstants.RECIPE_REQ_EXPS[Math.min(level, GameConstants.RECIPE_REQ_EXPS.length - 1)];
            if (status.getExp() < req_exp) {
                break;
            }
            status.setLevel(level + 1);
            status.setExp(status.getExp() - req_exp);
        }
        SQLManager.INSTANCE.insert(
                COLUMN_RECIPE_LEVELS,
                new String[]{COLUMN_USER_ID, COLUMN_RECIPE_ID, COLUMN_LEVEL, "exp"},
                new Object[]{id, status.getId(), status.getLevel(), status.getExp()}
        );
        return first;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="quest">
    public void addQuest(final QuestStatus questStatus) {
        questStatuses.add(questStatus);
        SQLManager.INSTANCE.insert(
                "questDatas",
                new String[]{COLUMN_USER_ID, "questId", "clear"},
                new Object[]{id, questStatus.getId(), questStatus.isClear() ? 1 : 0}
        );
    }

    public void addQuest(final Player player, final String questId) {
        addQuest(new QuestStatus(questId));
        player.sendMessage("クエスト「" + ChatColor.GREEN + Quest.getQuest(questId).getName() + ChatColor.RESET + "」を受注しました。");
    }

    public List<QuestStatus> getQuestStatusList() {
        return new ArrayList<>(questStatuses);
    }

    @NotNull
    public QuestStatus getQuestStatus(final String id) {
        for (final QuestStatus rs : questStatuses) {
            if (rs.getId().equals(id)) {
                return rs;
            }
        }
        throw new IllegalStateException("not found questStatus for ".concat(id));
    }

    public void questClear(final Player player, final String id, final boolean view) {
        questClear(player, getQuestStatus(id), view);
    }

    public void questClear(final Player player, final QuestStatus questStatus, final boolean view) {
        SQLManager.INSTANCE.insert(
                "questDatas",
                new String[]{COLUMN_USER_ID, "questId", "clear"},
                new Object[]{id, questStatus.getId(), 1}
        );
        questStatus.clear();
        final Quest quest = Quest.getQuest(questStatus.getId());
        if (view) {
            player.sendMessage("クエスト「" + ChatColor.GREEN + quest.getName() + ChatColor.RESET + "」を完了しました。");
        }
        if (quest.getNextQuestId() != null) {
            addQuest(player, quest.getNextQuestId());
        }
        if (quest.getResults() != null) {
            quest.getResults().forEach(result -> {
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

    public void discoverRecipe(final String itemId) {
        final MinecraftRecipeSaveType type = MinecraftRecipeSaveType.search(itemId);
        if (type != null) {
            saveTypes.add(type);
            SQLManager.INSTANCE.insert(
                    "discoveredRecipes",
                    new String[]{COLUMN_USER_ID, "itemId"},
                    new Object[]{id, itemId}
            );
        }
    }

    /**
     * 基本レシピの喪失は起こりえないので、使用しない
     *
     * @param itemId
     */
    public void undiscoverRecipe(final String itemId) {
        final MinecraftRecipeSaveType type = MinecraftRecipeSaveType.search(itemId);
        if (type != null) {
            saveTypes.remove(type);
            SQLManager.INSTANCE.delete(
                    "discoveredRecipes",
                    new String[]{COLUMN_USER_ID, "itemId"},
                    new Object[]{id, itemId}
            );
        }
    }
    //</editor-fold>

    public CharStats getCharStats() {
        return charStats;
    }

    public CharSettings getSettings() {
        return settings;
    }

    public boolean isEnginesUsable() {
        return isEnginesUsable;
    }

    public void setEnginesUsable(boolean enginesUsable) {
        isEnginesUsable = enginesUsable;
    }

    public ScriptEngine getJsEngine() {
        return jsEngine;
    }

    public void setJsEngine(ScriptEngine jsEngine) {
        this.jsEngine = jsEngine;
    }

    public ScriptEngine getPy3Engine() {
        return py3Engine;
    }

    public void setPy3Engine(ScriptEngine py3Engine) {
        this.py3Engine = py3Engine;
    }


}
