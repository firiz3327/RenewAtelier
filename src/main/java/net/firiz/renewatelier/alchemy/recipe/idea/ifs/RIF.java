package net.firiz.renewatelier.alchemy.recipe.idea.ifs;

import net.firiz.renewatelier.entity.player.Char;
import org.jetbrains.annotations.NotNull;

public interface RIF {

    boolean isAvailable(@NotNull Char character);

}
