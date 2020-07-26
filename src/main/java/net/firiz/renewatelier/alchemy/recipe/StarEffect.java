package net.firiz.renewatelier.alchemy.recipe;

import net.firiz.renewatelier.alchemy.material.AlchemyIngredients;
import net.firiz.renewatelier.alchemy.material.Category;
import net.firiz.renewatelier.item.json.itemeffect.AlchemyItemEffect;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;

/**
 * @author firiz
 */
public class StarEffect {

    public enum StarEffectType {
        ITEM_EFFECT,
        INGREDIENT,
        CATEGORY
    }

    private final StarEffectType type;
    private final String name;
    private final AlchemyItemEffect effect;
    private final AlchemyIngredients ingredient;
    private final Category category;

    public StarEffect(final AlchemyItemEffect effect) {
        this.type = StarEffectType.ITEM_EFFECT;
        this.name = effect.getName();
        this.effect = effect;
        this.ingredient = null;
        this.category = null;
    }

    public StarEffect(final AlchemyIngredients ingredient) {
        this.type = StarEffectType.INGREDIENT;
        this.name = ingredient.getName();
        this.effect = null;
        this.ingredient = ingredient;
        this.category = null;
    }

    public StarEffect(final Category category) {
        this.type = StarEffectType.CATEGORY;
        this.name = category.getName();
        this.effect = null;
        this.ingredient = null;
        this.category = category;
    }

    public StarEffectType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public AlchemyItemEffect getEffect() {
        return effect;
    }

    public AlchemyIngredients getIngredient() {
        return ingredient;
    }

    public Category getCategory() {
        return category;
    }

    public static class EnchantEffect {
        @NotNull
        private final Enchantment enchant;
        private final int level;

        public EnchantEffect(@NotNull final Enchantment enchant, final int level) {
            this.enchant = enchant;
            this.level = level;
        }

        @NotNull
        public Enchantment getEnchant() {
            return enchant;
        }

        public int getLevel() {
            return level;
        }
    }

}
