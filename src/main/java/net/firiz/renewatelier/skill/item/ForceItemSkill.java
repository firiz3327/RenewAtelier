package net.firiz.renewatelier.skill.item;

import net.firiz.renewatelier.item.json.AlchemyItemStatus;
import net.firiz.renewatelier.skill.data.ItemSkillData;
import org.bukkit.entity.Player;

public abstract class ForceItemSkill<T extends ItemSkillData> extends ItemSkill<T> {

    private final float force;

    public ForceItemSkill(T data, Player player, AlchemyItemStatus itemStatus, float force) {
        super(data, player, itemStatus);
        this.force = force;
    }

    public float getForce() {
        return force;
    }

}
