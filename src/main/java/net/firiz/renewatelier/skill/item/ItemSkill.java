package net.firiz.renewatelier.skill.item;

import net.firiz.renewatelier.entity.player.Char;
import net.firiz.renewatelier.entity.player.sql.load.PlayerSaveManager;
import net.firiz.renewatelier.item.json.AlchemyItemStatus;
import net.firiz.renewatelier.skill.ISkill;
import net.firiz.renewatelier.skill.data.ItemSkillData;
import org.bukkit.entity.Player;

public abstract class ItemSkill<T extends ItemSkillData> implements ISkill<T> {

    private static final PlayerSaveManager psm = PlayerSaveManager.INSTANCE;
    private final T data;
    private final Char character;
    private final AlchemyItemStatus itemStatus;

    public ItemSkill(T data, Player player, AlchemyItemStatus itemStatus) {
        this.data = data;
        this.character = psm.getChar(player.getUniqueId());
        this.itemStatus = itemStatus;
    }

    @Override
    public Player getPlayer() {
        return character.getPlayer();
    }

    @Override
    public T getData() {
        return data;
    }

    public Char getChar() {
        return character;
    }

    public AlchemyItemStatus getItemStatus() {
        return itemStatus;
    }
}
