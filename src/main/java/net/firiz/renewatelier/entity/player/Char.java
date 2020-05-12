package net.firiz.renewatelier.entity.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.script.ScriptEngine;

import net.firiz.renewatelier.alchemy.material.AlchemyMaterial;
import net.firiz.renewatelier.alchemy.recipe.AlchemyRecipe;
import net.firiz.renewatelier.alchemy.recipe.RecipeStatus;
import net.firiz.renewatelier.alchemy.recipe.idea.RecipeIdea;
import net.firiz.renewatelier.entity.player.sql.RecipeSQL;
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
import org.bukkit.Bukkit;
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

    private static final String COLUMN_USER_ID = "userId";

    @NotNull
    private final UUID uuid;
    private final int id;
    @Nullable
    private final String email;
    @Nullable
    private final String password;
    @NotNull
    private final CharStats charStats;
    @NotNull
    private final RecipeSQL recipeSQL;
    @NotNull
    private final List<QuestStatus> questStatuses;
    @NotNull
    private final List<MinecraftRecipeSaveType> saveTypes;
    @NotNull
    private final CharSettings settings;
    private boolean isEnginesUsable;
    @Nullable
    private ScriptEngine jsEngine;
    @Nullable
    private ScriptEngine py3Engine;
    @NotNull
    private final Runnable autoSave;

    public Char(
            @NotNull UUID uuid,
            final int id,
            @Nullable final String email,
            @Nullable final String password,
            @NotNull final CharStats charStats,
            @NotNull final List<RecipeStatus> recipeStatuses,
            @NotNull final List<QuestStatus> questStatuses,
            @NotNull final List<MinecraftRecipeSaveType> saveTypes,
            @NotNull final CharSettings settings
    ) {
        this.uuid = uuid;
        this.id = id;
        this.email = email;
        this.password = password;
        this.charStats = charStats;
        this.recipeSQL = new RecipeSQL(id, recipeStatuses);
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
        charStats.heal(charStats.getMaxHp());
    }

    //<editor-fold defaultstate="collapsed" desc="alchemy recipe">
    @NotNull
    public List<RecipeStatus> getRecipeStatusList() {
        return recipeSQL.getRecipeStatusList();
    }

    @Nullable
    public RecipeStatus getRecipeStatus(final String id) {
        return recipeSQL.getRecipeStatus(id);
    }

    public void setRecipeStatus(final RecipeStatus status) {
        recipeSQL.setRecipeStatus(status);
    }

    public void addRecipeExp(final Player player, final boolean view, final AlchemyRecipe recipe, final int exp) {
        recipeSQL.addRecipeExp(player, view, recipe, exp);
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
                    gainMoneyCompulsion(money);
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

    public int getMoney() {
        return charStats.getMoney();
    }

    public boolean hasMoney(int money) {
        final boolean result = charStats.hasMoney(money);
        if (!result) {
            charStats.getPlayer().sendMessage("所持金が足りません。");
        }
        return result;
    }

    public void increaseIdea(@NotNull final ItemStack item) {
        Objects.requireNonNull(item);
        recipeSQL.increaseIdea(Bukkit.getPlayer(uuid), new RecipeIdea.IncreaseIdea(item));
    }

    public void increaseIdea(@NotNull final AlchemyRecipe recipe) {
        Objects.requireNonNull(recipe);
        recipeSQL.increaseIdea(Bukkit.getPlayer(uuid), new RecipeIdea.IncreaseIdea(recipe));
    }


    public void gainMoney(int money) {
        if (!charStats.gainMoney(money, false)) { // 0以下になることは想定しない
            charStats.getPlayer().sendMessage("所持金が上限を超えるため受け取れませんでした。");
        }
    }

    public void gainMoneyCompulsion(int money) {
        charStats.gainMoney(money, true);
    }

    @NotNull
    public CharStats getCharStats() {
        return charStats;
    }

    @NotNull
    public CharSettings getSettings() {
        return settings;
    }

    public boolean isEnginesUsable() {
        return isEnginesUsable;
    }

    public void setEnginesUsable(boolean enginesUsable) {
        isEnginesUsable = enginesUsable;
    }

    @Nullable
    public ScriptEngine getJsEngine() {
        return jsEngine;
    }

    public void setJsEngine(@Nullable ScriptEngine jsEngine) {
        this.jsEngine = jsEngine;
    }

    @Nullable
    public ScriptEngine getPy3Engine() {
        return py3Engine;
    }

    public void setPy3Engine(@Nullable ScriptEngine py3Engine) {
        this.py3Engine = py3Engine;
    }

}
