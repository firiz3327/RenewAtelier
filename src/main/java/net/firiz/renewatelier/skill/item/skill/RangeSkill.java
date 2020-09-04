package net.firiz.renewatelier.skill.item.skill;

import net.firiz.renewatelier.entity.player.Char;
import net.firiz.renewatelier.item.json.AlchemyItemStatus;
import net.firiz.renewatelier.skill.effect.RangeEffect;
import net.firiz.renewatelier.skill.item.data.RangeData;
import net.firiz.renewatelier.utils.chores.EntityUtils;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public class RangeSkill extends ItemSkill<RangeData> {

    public RangeSkill(@NotNull RangeData data, @NotNull Char character, @NotNull AlchemyItemStatus itemStatus) {
        super(data, character, itemStatus);
    }

    @Override
    public void fire() {
        final Location location = getPlayer().getLocation();
        final RangeEffect rangeEffect = data.getEffect();
        rangeEffect.effect(location);
        if (data.isHeal()) {
            EntityUtils.rangePlayers(location, data.getRadius(), data.getMobCount())
                    .forEach(entity -> {
                        damageUtilV2.itemHeal(itemStatus, getPlayer(), entity);
                        rangeEffect.hit(entity.getLocation());
                    });
        } else if (data.getAttackAttribute() != null) {
            EntityUtils.rangeMobs(location, data.getRadius(), data.getMobCount())
                    .forEach(entity -> {
                        damageUtilV2.itemDamage(itemStatus, getPlayer(), entity, 1, data.getAttackAttribute());
                        rangeEffect.hit(entity.getLocation());
                    });
        }
    }
}
