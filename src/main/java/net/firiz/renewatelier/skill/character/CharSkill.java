package net.firiz.renewatelier.skill.character;

import net.firiz.renewatelier.entity.player.Char;
import net.firiz.renewatelier.item.json.AlchemyItemStatus;
import net.firiz.renewatelier.skill.ISkill;
import net.firiz.renewatelier.skill.character.data.CharSkillData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CharSkill<T extends CharSkillData> implements ISkill<T> {

    @NotNull
    private final T data;
    @NotNull
    private final Char character;
    @Nullable
    private final AlchemyItemStatus itemStatus;

    public CharSkill(@NotNull T data, @NotNull Char character, @Nullable AlchemyItemStatus itemStatus) {
        this.data = data;
        this.character = character;
        this.itemStatus = itemStatus;
    }

    @Override
    @NotNull
    public Player getPlayer() {
        return character.getPlayer();
    }

    @Override
    @NotNull
    public T getData() {
        return data;
    }

    @Nullable
    public AlchemyItemStatus getItemStatus() {
        return itemStatus;
    }
}
