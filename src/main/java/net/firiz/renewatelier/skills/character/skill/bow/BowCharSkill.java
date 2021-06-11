package net.firiz.renewatelier.skills.character.skill.bow;

import net.firiz.renewatelier.entity.player.Char;
import net.firiz.renewatelier.inventory.item.json.AlchemyItemStatus;
import net.firiz.renewatelier.skills.character.skill.CharSkill;
import org.bukkit.entity.Projectile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BowCharSkill extends CharSkill {

    protected BowCharSkill(@NotNull Char character, @Nullable AlchemyItemStatus itemStatus) {
        super(character, itemStatus);
    }

    public abstract boolean shoot(Projectile base);

}
