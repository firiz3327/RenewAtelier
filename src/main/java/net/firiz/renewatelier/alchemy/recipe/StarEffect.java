package net.firiz.renewatelier.alchemy.recipe;

import net.firiz.renewatelier.alchemy.material.AlchemyIngredients;
import net.firiz.renewatelier.alchemy.material.Category;
import net.firiz.renewatelier.inventory.item.json.itemeffect.AlchemyItemEffect;
import net.kyori.adventure.text.Component;
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
    private final Component name;
    private final AlchemyItemEffect effect;
    private final AlchemyIngredients ingredient;
    private final Category category;

    public StarEffect(final AlchemyItemEffect effect) {
        this.type = StarEffectType.ITEM_EFFECT;
        this.name = effect.getNameComponent();
        this.effect = effect;
        this.ingredient = null;
        this.category = null;
    }

    public StarEffect(final AlchemyIngredients ingredient) {
        this.type = StarEffectType.INGREDIENT;
        this.name = ingredient.getNameComponent();
        this.effect = null;
        this.ingredient = ingredient;
        this.category = null;
    }

    public StarEffect(final Category category) {
        this.type = StarEffectType.CATEGORY;
        this.name = category.getNameComponent();
        this.effect = null;
        this.ingredient = null;
        this.category = category;
    }

    public StarEffectType getType() {
        return type;
    }

    public Component getName() {
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

    public record EnchantEffect(@NotNull Enchantment enchant, int level) {
        public EnchantEffect(@NotNull final Enchantment enchant, final int level) {
            this.enchant = enchant;
            this.level = level;
        }
    }

}
