package net.firiz.renewatelier.alchemy.recipe.idea.ifs;

import net.firiz.renewatelier.entity.player.Char;
import org.jetbrains.annotations.NotNull;

public class RequireIf {

    @NotNull
    private final RIF rif;

    public RequireIf(String str) {
        final String[] datas = str.split(":");
        switch (datas[0].toLowerCase()) {
            case "alchemylevel":
                rif = new RIFAlchemyLevel(Integer.parseInt(datas[1]));
                break;
            default:
                throw new IllegalArgumentException(String.format("if-%s not found.", datas[0]));
        }
    }

    public boolean isAvailable(Char character) {
        return rif.isAvailable(character);
    }

}
