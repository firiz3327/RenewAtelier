/*
 * QuestItem.java
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
package net.firiz.renewatelier.quest;

import java.util.List;

import net.firiz.renewatelier.alchemy.material.AlchemyIngredients;
import net.firiz.renewatelier.alchemy.material.AlchemyMaterial;
import net.firiz.renewatelier.characteristic.Characteristic;
import net.firiz.renewatelier.item.AlchemyItemStatus;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author firiz
 */
public class QuestItem {

    private final String material;
    private final String name;
    private final int amount;
    private final int quality;
    private final int usecount;
    private final List<AlchemyIngredients> ingredients;
    private final List<Characteristic> characteristics;

    public QuestItem(String material, String name, int amount, int quality, int usecount, List<AlchemyIngredients> ingredients, List<Characteristic> characteristics) {
        this.material = material;
        this.name = name;
        this.amount = amount;
        this.quality = quality;
        this.usecount = usecount;
        this.ingredients = ingredients;
        this.characteristics = characteristics;
    }

    public ItemStack getItem() {
        final ItemStack result = AlchemyItemStatus.getItem(
                AlchemyMaterial.getMaterial(material),
                ingredients,
                null,
                quality,
                null,
                characteristics,
                null,
                false
        );
        result.setAmount(amount);
        if (name != null) {
            final ItemMeta meta = result.getItemMeta();
            meta.setDisplayName(name);
            result.setItemMeta(meta);
        }
        return result;
    }

    public ItemStack getItem(final boolean[] flags) {
        final ItemStack result = AlchemyItemStatus.getItem(
                AlchemyMaterial.getMaterial(material),
                ingredients,
                null,
                quality,
                null,
                null,
                characteristics,
                null,
                flags
        );
        result.setAmount(amount);
        if (name != null) {
            final ItemMeta meta = result.getItemMeta();
            meta.setDisplayName(name);
            result.setItemMeta(meta);
        }
        return result;
    }

    public String getMaterial() {
        return material;
    }

    public String getName() {
        return name;
    }

    public int getAmount() {
        return amount;
    }

    public int getQuality() {
        return quality;
    }

    public int getUsecount() {
        return usecount;
    }

    public List<AlchemyIngredients> getIngredients() {
        return ingredients;
    }

    public List<Characteristic> getCharacteristics() {
        return characteristics;
    }

}
