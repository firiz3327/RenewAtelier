package net.firiz.renewatelier.entity.player.sql;

import net.firiz.ateliercommonapi.adventure.text.C;
import net.firiz.ateliercommonapi.adventure.text.Text;
import net.firiz.renewatelier.alchemy.recipe.AlchemyRecipe;
import net.firiz.renewatelier.alchemy.recipe.RecipeStatus;
import net.firiz.renewatelier.alchemy.recipe.idea.IncreaseIdea;
import net.firiz.renewatelier.alchemy.recipe.idea.RecipeIdeaStatus;
import net.firiz.renewatelier.alchemy.recipe.result.ARecipeResult;
import net.firiz.renewatelier.alchemy.recipe.result.AlchemyMaterialRecipeResult;
import net.firiz.renewatelier.alchemy.recipe.result.MinecraftMaterialRecipeResult;
import net.firiz.renewatelier.constants.GameConstants;
import net.firiz.renewatelier.entity.player.Char;
import net.firiz.renewatelier.entity.player.sql.load.PlayerSaveManager;
import net.firiz.renewatelier.notification.Notification;
import net.firiz.renewatelier.sql.SQLManager;
import net.firiz.renewatelier.utils.java.ArrayUtils;
import net.firiz.renewatelier.version.LanguageItemUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public record RecipeSQL(int id, @NotNull List<RecipeStatus> recipeStatuses) {

    private static final String TABLE_RECIPE_LEVELS = "recipeLevels";
    private static final String[] COLUMNS = {
            "userId",
            "recipeId",
            "acquired",
            "level",
            "exp",
            "idea"
    };

    public RecipeSQL(final int id, @NotNull List<RecipeStatus> recipeStatuses) {
        this.id = id;
        this.recipeStatuses = recipeStatuses;
    }

    public RecipeStatus addRecipe(final AlchemyRecipe recipe) {
        final RecipeStatus recipeStatus = new RecipeStatus(recipe, true);
        addRecipe(recipeStatus);
        return recipeStatus;
    }

    private void addRecipe(final RecipeStatus recipeStatus) {
        recipeStatuses.add(recipeStatus);
        insert(recipeStatus);
    }

    public boolean hasRecipe(@NotNull final String id) {
        return getRecipeStatus(id) != null;
    }

    public boolean hasRecipe(@NotNull final AlchemyRecipe recipe) {
        return getRecipeStatus(recipe) != null;
    }

    @NotNull
    public List<RecipeStatus> getRecipeStatusList() {
        return Collections.unmodifiableList(recipeStatuses);
    }

    @Nullable
    public RecipeStatus getRecipeStatus(final String id) {
        for (final RecipeStatus rs : getRecipeStatusList()) {
            if (rs.getId().equals(id)) {
                return rs;
            }
        }
        return null;
    }

    @Nullable
    public RecipeStatus getRecipeStatus(final AlchemyRecipe recipe) {
        for (final RecipeStatus rs : getRecipeStatusList()) {
            if (rs.getRecipe() == recipe) {
                return rs;
            }
        }
        return null;
    }

    public void setRecipeStatus(final RecipeStatus status) {
        // recipeStatusListに同idが存在する場合は、リスト内のRecipeStatusを参照する
        RecipeStatus recipeStatus = getRecipeStatus(status.getId());
        if (recipeStatus == null) {
            recipeStatus = status;
            recipeStatuses.add(recipeStatus);
        }
        insert(recipeStatus);
    }

    public void addRecipeExp(final Player player, final boolean view, final AlchemyRecipe recipe, final int exp) {
        addRecipeExp(player, view, recipe, exp, false);
    }

    public void addRecipeExp(final Player player, final boolean view, final AlchemyRecipe recipe, final int exp, final boolean enableAcquired) {
        if (addRecipeExp(recipe, exp, enableAcquired) && view) {
            notification(player, recipe);
        }
    }

    private boolean addRecipeExp(final AlchemyRecipe recipe, final int exp, final boolean enableAcquired) {
        final String recipeId = recipe.getId();
        RecipeStatus status = getRecipeStatus(recipeId);
        if (status != null && status.getLevel() > 3) {
            return false;
        }

        boolean first = false;
        if (status == null) { // スキルもしくはコマンドの場合のみ(基本的にアイデアで開放、若しくはその他要素で開放)
            status = addRecipe(recipe);
            first = true;
        }
        if (enableAcquired) {
            status.setAcquired(true);
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
        insert(status);
        return first;
    }

    public void increaseIdea(@NotNull final Player player, @NotNull IncreaseIdea item) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(item);
        final Char character = PlayerSaveManager.INSTANCE.getChar(player);
        AlchemyRecipe.getIdeaRecipeList().stream()
                .filter(ideaRecipe -> ideaRecipe.hasIdeaRequire(item))
                .forEach(ideaRecipe -> {
                    RecipeStatus recipeStatus = null;
                    for (final RecipeStatus tempRecipeStatus : getRecipeStatusList()) {
                        if (tempRecipeStatus.getRecipe() == ideaRecipe) {
                            recipeStatus = tempRecipeStatus;
                        }
                    }
                    if (recipeStatus == null) {
                        recipeStatus = new RecipeStatus(ideaRecipe);
                        addRecipe(recipeStatus); // レシピを無効状態で追加する
                    }
                    final RecipeIdeaStatus ideaStatus = recipeStatus.getIdea();
                    if (!recipeStatus.isAcquired() && ideaStatus != null) {
                        ideaStatus.increaseIdea(item);
                        if (ideaStatus.isAvailable(character)) {
                            recipeStatus.setAcquired(true); // レシピを有効にする
                            insert(recipeStatus);
                            notification(player, ideaRecipe);
                        }
                    }
                });
    }

    private void insert(final RecipeStatus recipeStatus) {
        final RecipeIdeaStatus idea = recipeStatus.getIdea();
        String ideaString = null;
        if (!recipeStatus.isAcquired() && idea != null) {
            ideaString = ArrayUtils.splitString(idea.getRequires());
        }
        SQLManager.INSTANCE.insert(
                TABLE_RECIPE_LEVELS,
                COLUMNS,
                new Object[]{
                        id,
                        recipeStatus.getId(),
                        recipeStatus.isAcquired() ? 1 : 0,
                        recipeStatus.getLevel(),
                        recipeStatus.getExp(),
                        ideaString
                }
        );
    }

    private void notification(@NotNull final Player player, @NotNull final AlchemyRecipe recipe) {
        Notification.recipeNotification(player, Material.CAULDRON);

        Component name = Component.text("unknown");
        final ARecipeResult<?> resultData = recipe.getResult();
        if (resultData instanceof AlchemyMaterialRecipeResult) {
            name = ((AlchemyMaterialRecipeResult) resultData).getResult().getName();
        } else if (resultData instanceof MinecraftMaterialRecipeResult) { // 基本想定しない
            final Material material = ((MinecraftMaterialRecipeResult) resultData).getResult();
            name = Component.text(LanguageItemUtil.getLocalizeName(new ItemStack(material)));
        }
        player.sendMessage(Text.of("レシピ【").append(name).color(C.GREEN).append("】を開放しました。").color(C.WHITE));
    }

}
