package net.firiz.renewatelier.alchemy.recipe;

import net.firiz.renewatelier.alchemy.material.AlchemyIngredients;
import net.firiz.renewatelier.alchemy.material.Category;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;

/**
 * @author firiz
 */
public class StarEffect {

    public enum StarEffectType {
        NAME,
        INGREDIENT,
        CATEGORY,
        ENCHANT
    }

    private final StarEffectType type;
    private final String name;
    private final AlchemyIngredients ingredient;
    private final Category category;
    private final EnchantEffect enchantEffect;

    public StarEffect(final String name) {
        this.type = StarEffectType.NAME;
        this.name = name;
        this.ingredient = null;
        this.category = null;
        this.enchantEffect = null;
    }

    public StarEffect(final AlchemyIngredients ingredient) {
        this.type = StarEffectType.INGREDIENT;
        this.name = ingredient.getName();
        this.ingredient = ingredient;
        this.category = null;
        this.enchantEffect = null;
    }

    public StarEffect(final Category category) {
        this.type = StarEffectType.CATEGORY;
        this.name = category.getName();
        this.ingredient = null;
        this.category = category;
        this.enchantEffect = null;
    }

    public StarEffect(final EnchantEffect effect) {
        this.type = StarEffectType.ENCHANT;
        this.name = effect.name;
        this.ingredient = null;
        this.category = null;
        this.enchantEffect = effect;
    }

    public StarEffectType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public AlchemyIngredients getIngredient() {
        return ingredient;
    }

    public Category getCategory() {
        return category;
    }

    public EnchantEffect getEnchantEffect() {
        return enchantEffect;
    }

    public static class EnchantEffect {
        @NotNull
        private final Enchantment enchant;
        private final int level;
        @NotNull
        private final String name;

        public EnchantEffect(@NotNull final Enchantment enchant, final int level, @NotNull final String name) {
            this.enchant = enchant;
            this.level = level;
            this.name = name;
        }

        @NotNull
        public Enchantment getEnchant() {
            return enchant;
        }

        public int getLevel() {
            return level;
        }

        @NotNull
        public String getName() {
            return name;
        }
    }

}
