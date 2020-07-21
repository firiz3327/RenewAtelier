package net.firiz.renewatelier.entity.player;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.script.ScriptEngine;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.firiz.renewatelier.alchemy.recipe.AlchemyRecipe;
import net.firiz.renewatelier.alchemy.recipe.RecipeStatus;
import net.firiz.renewatelier.alchemy.recipe.idea.IncreaseIdea;
import net.firiz.renewatelier.alchemy.recipe.idea.RecipeIdea;
import net.firiz.renewatelier.entity.player.sql.RecipeSQL;
import net.firiz.renewatelier.entity.player.stats.CharStats;
import net.firiz.renewatelier.loop.LoopManager;
import net.firiz.renewatelier.sql.SQLManager;
import net.firiz.renewatelier.utils.ItemUtils;
import net.firiz.renewatelier.quest.Quest;
import net.firiz.renewatelier.quest.QuestItem;
import net.firiz.renewatelier.quest.QuestStatus;
import net.firiz.renewatelier.quest.result.ItemQuestResult;
import net.firiz.renewatelier.quest.result.MoneyQuestResult;
import net.firiz.renewatelier.quest.result.RecipeQuestResult;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
    private final Player player;
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
    private final CharSettings settings;
    private boolean isEnginesUsable;
    @Nullable
    private ScriptEngine jsEngine;
    @Nullable
    private ScriptEngine py3Engine;
    @NotNull
    private final Runnable autoSave;

    public Char(
            @NotNull Player player,
            @NotNull UUID uuid,
            final int id,
            @Nullable final String email,
            @Nullable final String password,
            @NotNull final CharStats charStats,
            @NotNull final List<RecipeStatus> recipeStatuses,
            @NotNull final List<QuestStatus> questStatuses,
            @NotNull final CharSettings settings
    ) {
        this.player = player;
        this.uuid = uuid;
        this.id = id;
        this.email = email;
        this.password = password;
        this.charStats = charStats;
        this.recipeSQL = new RecipeSQL(id, recipeStatuses);
        this.questStatuses = questStatuses;
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

    @NotNull
    public Player getPlayer() {
        return player;
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

    public void addRecipeExp(final boolean view, final AlchemyRecipe recipe, final int exp) {
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

    public void addQuest(final String questId) {
        addQuest(new QuestStatus(questId));
        player.sendMessage("クエスト「" + ChatColor.GREEN + Quest.getQuest(questId).getName() + ChatColor.RESET + "」を受注しました。");
    }

    public List<QuestStatus> getQuestStatusList() {
        return new ObjectArrayList<>(questStatuses);
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

    public void questClear(final String id, final boolean view) {
        questClear(getQuestStatus(id), view);
    }

    public void questClear(final QuestStatus questStatus, final boolean view) {
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
            addQuest(quest.getNextQuestId());
        }
        if (quest.getResults() != null) {
            quest.getResults().forEach(result -> {
                if (result instanceof ItemQuestResult) {
                    final QuestItem item = ((ItemQuestResult) result).getResult();
                    ItemUtils.addItem(player, item.getItem());
                } else if (result instanceof RecipeQuestResult) {
                    final AlchemyRecipe recipe = ((RecipeQuestResult) result).getResult();
                    addRecipeExp(true, recipe, 0);
                } else if (result instanceof MoneyQuestResult) {
                    final int money = ((MoneyQuestResult) result).getResult();
                    gainMoneyCompulsion(money);
                }
            });
        }
    }
    //</editor-fold>

    public long getMoney() {
        return charStats.getMoney();
    }

    public boolean hasMoney(long money) {
        final boolean result = charStats.hasMoney(money);
        if (!result) {
            charStats.getPlayer().sendMessage("所持金が足りません。");
        }
        return result;
    }

    public void increaseIdea(@NotNull final ItemStack item) {
        Objects.requireNonNull(item);
        recipeSQL.increaseIdea(Bukkit.getPlayer(uuid), new IncreaseIdea(item));
    }

    public void increaseIdea(@NotNull final AlchemyRecipe recipe) {
        Objects.requireNonNull(recipe);
        recipeSQL.increaseIdea(Bukkit.getPlayer(uuid), new IncreaseIdea(recipe));
    }

    public void gainMoney(long money) {
        if (!charStats.gainMoney(money, false)) { // 0以下になることは想定しない
            charStats.getPlayer().sendMessage("所持金が上限を超えるため受け取れませんでした。");
        }
    }

    public void gainMoneyCompulsion(long money) {
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
