package net.firiz.renewatelier.alchemy.recipe.idea.ifs;

import net.firiz.renewatelier.entity.player.Char;
import org.jetbrains.annotations.NotNull;

public class RIFAlchemyLevel implements RIF {

    private final int requireLevel;

    public RIFAlchemyLevel(int requireLevel) {
        this.requireLevel = requireLevel;
    }

    @Override
    public boolean isAvailable(@NotNull Char character) {
        return character.getCharStats().getAlchemyLevel() >= requireLevel;
    }
}
