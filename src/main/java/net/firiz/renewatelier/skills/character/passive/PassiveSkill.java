package net.firiz.renewatelier.skills.character.passive;

import net.firiz.renewatelier.entity.player.Char;
import net.firiz.renewatelier.skills.ISkill;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class PassiveSkill implements ISkill {

    private final Char character;

    protected PassiveSkill(Char character) {
        this.character = character;
    }

    @NotNull
    @Override
    public Char getCharacter() {
        return character;
    }

    @NotNull
    @Override
    public Player getPlayer() {
        return character.getPlayer();
    }

}
