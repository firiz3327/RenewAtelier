/*
 * StartEffect.java
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
package jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.recipe;

import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.material.AlchemyIngredients;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.material.Category;

/**
 *
 * @author firiz
 */
public class StarEffect {
    
    private final int type;
    private final String name;
    private final AlchemyIngredients ingredient;
    private final Category category;
    
    public StarEffect(final String name) {
        this.type = 0;
        this.name = name;
        this.ingredient = null;
        this.category = null;
    }
    
    public StarEffect(final AlchemyIngredients ingredient) {
        this.type = 1;
        this.name = ingredient.getName();
        this.ingredient = ingredient;
        this.category = null;
    }

    public StarEffect(final Category category) {
        this.type = 2;
        this.name = category.getName();
        this.ingredient = null;
        this.category = category;
    }
    
    public int getType() {
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
    
}
