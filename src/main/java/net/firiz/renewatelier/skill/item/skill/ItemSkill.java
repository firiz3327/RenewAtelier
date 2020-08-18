package net.firiz.renewatelier.skill.item.skill;

import net.firiz.renewatelier.damage.DamageUtilV2;
import net.firiz.renewatelier.entity.player.Char;
import net.firiz.renewatelier.entity.player.sql.load.PlayerSaveManager;
import net.firiz.renewatelier.item.json.AlchemyItemStatus;
import net.firiz.renewatelier.skill.ISkill;
import net.firiz.renewatelier.skill.item.data.ItemSkillData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class ItemSkill<T extends ItemSkillData> implements ISkill<T> {

    protected static final PlayerSaveManager psm = PlayerSaveManager.INSTANCE;
    protected static final DamageUtilV2 damageUtilV2 = DamageUtilV2.INSTANCE;
    @NotNull
    protected final T data;
    @NotNull
    protected final Char character;
    @NotNull
    protected final AlchemyItemStatus itemStatus;

    public ItemSkill(@NotNull T data, @NotNull Char character, @NotNull AlchemyItemStatus itemStatus) {
        this.data = data;
        this.character = character;
        this.itemStatus = itemStatus;
    }

    @Override
    public Player getPlayer() {
        return character.getPlayer();
    }

    @Override
    @NotNull
    public T getData() {
        return data;
    }
}
