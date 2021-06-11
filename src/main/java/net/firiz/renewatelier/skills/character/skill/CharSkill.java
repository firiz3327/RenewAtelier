package net.firiz.renewatelier.skills.character.skill;

import net.firiz.renewatelier.entity.player.Char;
import net.firiz.renewatelier.inventory.item.json.AlchemyItemStatus;
import net.firiz.renewatelier.skills.ISkill;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CharSkill implements ISkill {

    @NotNull
    private final Char character;
    @Nullable
    private final AlchemyItemStatus itemStatus;
    private boolean die;

    protected CharSkill(@NotNull Char character, @Nullable AlchemyItemStatus itemStatus) {
        this.character = character;
        this.itemStatus = itemStatus;
    }

    @Override
    @NotNull
    public Char getCharacter() {
        return character;
    }

    @Override
    @NotNull
    public Player getPlayer() {
        return character.getPlayer();
    }

    @Nullable
    public AlchemyItemStatus getItemStatus() {
        return itemStatus;
    }

    public void die() {
        this.die = true;
    }

    public boolean isDie() {
        return die;
    }
}
