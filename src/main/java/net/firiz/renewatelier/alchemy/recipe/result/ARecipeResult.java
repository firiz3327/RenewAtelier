package net.firiz.renewatelier.alchemy.recipe.result;

import net.firiz.renewatelier.item.CustomModelMaterial;

public abstract class ARecipeResult<T> {

    private final T result;

    protected ARecipeResult(T result) {
        this.result = result;
    }

    public T getResult() {
        return result;
    }

    public abstract CustomModelMaterial getCustomModelMaterial();

}
