package net.firiz.renewatelier.skills.character.passive;

import net.firiz.renewatelier.entity.player.Char;
import net.firiz.renewatelier.entity.player.sql.load.PlayerSaveManager;
import net.firiz.renewatelier.skills.character.IPlayerSkillBuilder;
import net.firiz.renewatelier.skills.character.passive.recipe.FlamRecipeSkill;
import net.firiz.renewatelier.skills.character.passive.recipe.IngotRecipeSkill;
import net.firiz.renewatelier.skills.character.passive.recipe.NeutralizationRecipeSkill;
import net.firiz.renewatelier.skills.character.passive.recipe.RootRecipeSkill;
import org.bukkit.entity.Player;

import java.util.function.Function;

public enum EnumPlayerPassiveSkill implements IPlayerSkillBuilder {
    ROOT(RootRecipeSkill::new),
    FLAM(FlamRecipeSkill::new),
    NEUTRALIZATION(NeutralizationRecipeSkill::new),
    INGOT(IngotRecipeSkill::new)
    ;

    private final Function<Char, PassiveSkill> createSkillSupp;

    EnumPlayerPassiveSkill(Function<Char, PassiveSkill> createSkillSupp) {
        this.createSkillSupp = createSkillSupp;
    }

    public PassiveSkill createSkill(Player player) {
        return createSkillSupp.apply(PlayerSaveManager.INSTANCE.getChar(player));
    }

    public PassiveSkill createSkill(Char player) {
        return createSkillSupp.apply(player);
    }

    @Override
    public String getName() {
        return toString();
    }

    @Override
    public boolean isPassive() {
        return true;
    }

}
