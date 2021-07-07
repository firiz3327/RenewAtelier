package net.firiz.renewatelier.entity.player;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.firiz.ateliercommonapi.adventure.text.C;
import net.firiz.ateliercommonapi.adventure.text.Text;
import net.firiz.ateliercommonapi.loop.LoopManager;
import net.firiz.ateliercommonapi.loop.TickRunnable;
import net.firiz.renewatelier.alchemy.recipe.AlchemyRecipe;
import net.firiz.renewatelier.alchemy.recipe.RecipeStatus;
import net.firiz.renewatelier.alchemy.recipe.idea.IncreaseIdea;
import net.firiz.renewatelier.entity.player.sql.RecipeSQL;
import net.firiz.renewatelier.entity.player.stats.CharStats;
import net.firiz.renewatelier.inventory.item.json.AlchemyItemBag;
import net.firiz.renewatelier.npc.MessageObjectRun;
import net.firiz.renewatelier.server.discord.DiscordStatus;
import net.firiz.renewatelier.sql.SQLManager;
import net.firiz.renewatelier.utils.CommonUtils;
import net.firiz.renewatelier.utils.minecraft.ItemUtils;
import net.firiz.renewatelier.quest.Quest;
import net.firiz.renewatelier.quest.QuestItem;
import net.firiz.renewatelier.quest.QuestStatus;
import net.firiz.renewatelier.quest.result.ItemQuestResult;
import net.firiz.renewatelier.quest.result.MoneyQuestResult;
import net.firiz.renewatelier.quest.result.RecipeQuestResult;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
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
    @NotNull
    private final EngineManager engineManager;
    @NotNull
    private final TickRunnable autoSave;
    @NotNull
    private final MessageObjectRun messageObjectRun;
    @NotNull
    private final AlchemyItemBag bag;
    private final int taskIdMinute;
    private final int taskIdTick;
    private final int taskIdSec;

    private Component lastChatComponent;
    private int chatCount;
    private long chatBanTime;

    @NotNull
    private final DiscordStatus discordStatus;

    public Char(
            @NotNull Player player,
            @NotNull UUID uuid,
            final int id,
            @Nullable final String email,
            @Nullable final String password,
            @NotNull final CharStats charStats,
            @NotNull final List<RecipeStatus> recipeStatuses,
            @NotNull final List<QuestStatus> questStatuses,
            @NotNull final CharSettings settings,
            @NotNull final AlchemyItemBag bag,
            @NotNull final DiscordStatus discordStatus) {
        this.player = player;
        this.uuid = uuid;
        this.id = id;
        this.email = email;
        this.password = password;
        this.charStats = charStats;
        this.recipeSQL = new RecipeSQL(id, recipeStatuses);
        this.questStatuses = questStatuses;
        this.settings = settings;
        this.engineManager = new EngineManager();
        this.bag = bag;
        this.discordStatus = discordStatus;

        this.autoSave = this::save;
        final TickRunnable oneLoop = new TickRunnable() {
            int time = 0;

            @Override
            public void run() {
                if (time > 3) {
                    charStats.healMp(charStats.getMaxMp() / 20);
                    chatCount = 0;
                    time = 0;
                }
                time++;
            }
        };
        this.taskIdMinute = LoopManager.INSTANCE.addMinutes(this.autoSave);
        this.taskIdSec = LoopManager.INSTANCE.addSeconds(oneLoop);
        this.messageObjectRun = new MessageObjectRun();
        this.taskIdTick = LoopManager.INSTANCE.addTicks(messageObjectRun);
    }

    public void save() {
        this.charStats.save(this.id);
        this.settings.save(this.id);
        this.bag.save(this.id);
    }

    public void unload() {
        LoopManager.INSTANCE.removeMinutes(this.taskIdMinute);
        LoopManager.INSTANCE.removeSeconds(this.taskIdSec);
        LoopManager.INSTANCE.removeTicks(this.taskIdTick);
        autoSave.run();
    }

    public void respawn() {
        charStats.clearBuffs();
        charStats.heal(charStats.getMaxHp());
    }

    public int getId() {
        return id;
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

    @Nullable
    public RecipeStatus getRecipeStatus(final AlchemyRecipe recipe) {
        return recipeSQL.getRecipeStatus(recipe);
    }

    public void setRecipeStatus(final RecipeStatus status) {
        recipeSQL.setRecipeStatus(status);
    }

    public void addRecipeExp(final boolean view, final AlchemyRecipe recipe, final int exp) {
        recipeSQL.addRecipeExp(player, view, recipe, exp);
    }

    public void addRecipe(final boolean view, final AlchemyRecipe recipe, boolean enableAcquired) {
        recipeSQL.addRecipeExp(player, view, recipe, 0, enableAcquired);
    }

    public void addRecipe(final boolean view, final AlchemyRecipe recipe) {
        addRecipeExp(view, recipe, 0);
    }

    public boolean hasRecipe(@NotNull final String id) {
        return recipeSQL.hasRecipe(id);
    }

    public boolean hasRecipe(@NotNull final AlchemyRecipe recipe) {
        return recipeSQL.hasRecipe(recipe);
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
        player.sendMessage(Text.of("クエスト「").append(Quest.getQuest(questId).getName()).color(C.GREEN).append("」を受注しました。"));
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
            player.sendMessage(Text.of("クエスト「").append(quest.getName()).color(C.GREEN).append("」を完了しました。"));
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
                    addRecipe(true, recipe);
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
        recipeSQL.increaseIdea(Objects.requireNonNull(Bukkit.getPlayer(uuid)), new IncreaseIdea(item));
    }

    public void increaseIdea(@NotNull final AlchemyRecipe recipe) {
        Objects.requireNonNull(recipe);
        recipeSQL.increaseIdea(Objects.requireNonNull(Bukkit.getPlayer(uuid)), new IncreaseIdea(recipe));
    }

    public boolean gainMoney(long money) {
        final int result = charStats.gainMoney(money, false);
        switch (result) {
            case 0:
                charStats.getPlayer().sendMessage("所持金が上限を超えるため受け取れませんでした。");
                return false;
            case 1:
                charStats.getPlayer().sendMessage("所持金が足りません。");
                return false;
            default:
                return true;
        }
    }

    public void gainMoneyCompulsion(long money) {
        charStats.gainMoney(money, true);
    }

    public void completionAlchemyKettleAdvancement(@NotNull final Location location) {
        location.getWorld().spawnParticle(Particle.SPELL, location.add(0.5, 1, 0.5), 20, 0.2, 0.2, 0.2);
        completionAdvancement("root");
    }

    public void completionAdvancement(@NotNull final String key) {
        final Advancement advancement = Bukkit.getAdvancement(CommonUtils.createKey(key));
        if (advancement != null) {
            final AdvancementProgress progress = player.getAdvancementProgress(Objects.requireNonNull(advancement));
            if (!progress.isDone()) {
                progress.awardCriteria("none");
            }
        }
    }

    @NotNull
    public EngineManager getEngineManager() {
        return engineManager;
    }

    @NotNull
    public CharStats getCharStats() {
        return charStats;
    }

    @NotNull
    public CharSettings getSettings() {
        return settings;
    }

    @NotNull
    public MessageObjectRun getMessageObjectRun() {
        return messageObjectRun;
    }

    @NotNull
    public AlchemyItemBag getBag() {
        return bag;
    }

    public void incrementChatCount() {
        chatCount += 1;
    }

    public int getChatCount() {
        return chatCount;
    }

    public Component getLastChatComponent() {
        return lastChatComponent;
    }

    public void setLastChatComponent(Component lastChatComponent) {
        this.lastChatComponent = lastChatComponent;
    }

    public void chatBan() {
        chatBanTime = System.currentTimeMillis();
    }

    public long getChatBanTime() {
        return chatBanTime;
    }

    @NotNull
    public DiscordStatus getDiscordStatus() {
        return discordStatus;
    }
}
