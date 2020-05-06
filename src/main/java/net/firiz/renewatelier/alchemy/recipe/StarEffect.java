/*
 * StarEffect.java
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
