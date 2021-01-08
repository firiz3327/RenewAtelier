package net.firiz.renewatelier.entity.player.sql;

import net.firiz.renewatelier.alchemy.recipe.AlchemyRecipe;
import net.firiz.renewatelier.alchemy.recipe.RecipeStatus;
import net.firiz.renewatelier.alchemy.recipe.idea.IncreaseIdea;
import net.firiz.renewatelier.alchemy.recipe.idea.RecipeIdea;
import net.firiz.renewatelier.alchemy.recipe.result.ARecipeResult;
import net.firiz.renewatelier.alchemy.recipe.result.AlchemyMaterialRecipeResult;
import net.firiz.renewatelier.alchemy.recipe.result.MinecraftMaterialRecipeResult;
import net.firiz.renewatelier.constants.GameConstants;
import net.firiz.renewatelier.notification.Notification;
import net.firiz.renewatelier.sql.SQLManager;
import net.firiz.renewatelier.utils.java.ArrayUtils;
import net.firiz.renewatelier.version.LanguageItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class RecipeSQL {

    private static final String TABLE_RECIPE_LEVELS = "recipeLevels";
    private static final String[] COLUMNS = {
            "userId",
            "recipeId",
            "acquired",
            "level",
            "exp",
            "idea"
    };

    private final int id;
    @NotNull
    private final List<RecipeStatus> recipeStatuses;

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
        if (addRecipeExp(recipe, exp) && view) {
            notification(player, recipe);
        }
    }

    private boolean addRecipeExp(final AlchemyRecipe recipe, final int exp) {
        final String recipeId = recipe.getId();
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
        if (status == null) { // コマンドの場合のみ(基本的にアイデアで開放、若しくはその他要素で開放)
            status = addRecipe(recipe);
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
        insert(status);
        return first;
    }

    public void increaseIdea(@Nullable final Player player, @NotNull IncreaseIdea item) {
        Objects.requireNonNull(item);
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
                    if (!recipeStatus.isAcquired()
                            && recipeStatus.getIdea() != null
                            && recipeStatus.getIdea().increaseIdea(item)) {
                        recipeStatus.setAcquired(true); // レシピを有効にする
                        insert(recipeStatus);
                        if (player != null) {
                            notification(player, ideaRecipe);
                        }
                    }
                });
    }

    private void insert(final RecipeStatus recipeStatus) {
        final RecipeIdea idea = recipeStatus.getIdea();
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

        String name = "unknown";
        final ARecipeResult<?> resultData = recipe.getResult();
        if (resultData instanceof AlchemyMaterialRecipeResult) {
            name = ((AlchemyMaterialRecipeResult) resultData).getResult().getName();
        } else if (resultData instanceof MinecraftMaterialRecipeResult) { // 基本想定しない
            final Material material = ((MinecraftMaterialRecipeResult) resultData).getResult();
            name = LanguageItemUtil.getLocalizeName(new ItemStack(material));
        }
        player.sendMessage("レシピ【" + ChatColor.GREEN + name + ChatColor.RESET + "】を開放しました。");
    }

}
