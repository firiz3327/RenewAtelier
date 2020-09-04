package net.firiz.renewatelier.skill.item.skill;

import net.firiz.renewatelier.entity.player.Char;
import net.firiz.renewatelier.item.json.AlchemyItemStatus;
import net.firiz.renewatelier.skill.item.data.BombData;
import net.firiz.renewatelier.utils.chores.EntityUtils;
import net.firiz.renewatelier.version.entity.projectile.arrow.BombProjectile;
import net.firiz.renewatelier.version.entity.projectile.arrow.IBombProjectile;
import org.bukkit.Location;

public class BombSkill extends ItemSkill<BombData> {

    public BombSkill(BombData data, Char character, AlchemyItemStatus itemStatus) {
        super(data, character, itemStatus);
    }

    @Override
    public void fire() {
        final Location location = getPlayer().getLocation();
        final IBombProjectile bomb = BombProjectile.spawn(this::effect, location.clone().add(0, 0.5, 0));
        bomb.setVelocity(location.getDirection().setY(0.6).multiply(0.75));
    }

    private void effect(final IBombProjectile bombProjectile) {
        final Location location = bombProjectile.getLocation();
        if (bombProjectile.isGround() || !location.getBlock().getType().isAir()) {
            hit(bombProjectile, location);
        } else {
            final long mobCount = EntityUtils.rangeMobs(
                    location,
                    1,
                    1
            ).count();
            if (mobCount == 0) {
                data.getEffect().effect(location);
            } else {
                hit(bombProjectile, location);
            }
        }
    }

    private void hit(final IBombProjectile bombProjectile, final Location location) {
        bombProjectile.remove();
        data.getEffect().hit(location);
        EntityUtils.rangeMobs(
                location,
                data.getRadius(),
                6
        ).forEach(entity -> damageUtilV2.itemDamage(
                itemStatus,
                getPlayer(),
                entity,
                1,
                data.getAttackAttribute()
        ));
    }
}
