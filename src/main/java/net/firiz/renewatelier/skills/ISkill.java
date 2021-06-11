package net.firiz.renewatelier.skills;

import net.firiz.renewatelier.entity.player.Char;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface ISkill {

    boolean fire();

    @NotNull
    Char getCharacter();

    @NotNull
    Player getPlayer();
}
