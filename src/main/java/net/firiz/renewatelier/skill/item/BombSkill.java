package net.firiz.renewatelier.skill.item;

import net.firiz.renewatelier.damage.DamageUtilV2;
import net.firiz.renewatelier.item.json.AlchemyItemStatus;
import net.firiz.renewatelier.skill.data.BombData;
import net.firiz.renewatelier.utils.chores.EntityUtils;
import net.firiz.renewatelier.version.entity.projectile.arrow.BombProjectile;
import net.firiz.renewatelier.version.entity.projectile.arrow.IBombProjectile;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class BombSkill extends ForceItemSkill<BombData> {

    private static final DamageUtilV2 damageUtilV2 = DamageUtilV2.INSTANCE;
    private final double power;

    public BombSkill(BombData data, Player player, AlchemyItemStatus itemStatus, float force) {
        super(data, player, itemStatus, force);
        this.power = itemStatus.getAlchemyMaterial().getPower();
    }

    @Override
    public void fire() {
        final Location location = getPlayer().getLocation();
        final IBombProjectile bomb = BombProjectile.spawn(this::effect, location.clone().add(0, 0.5, 0));
        final float force = getForce();
        bomb.setVelocity(location.getDirection().setY(0.5 + (force * 0.2)).multiply(0.5 + (force / 2)));
    }

    private void effect(final IBombProjectile bombProjectile) {
        final Location location = bombProjectile.getLocation();
        if (bombProjectile.isGround() || !location.getBlock().getType().isAir()) {
            hit(bombProjectile, location);
        } else {
            final long mobCount = EntityUtils.rangeCreatures(
                    location,
                    1,
                    1
            ).count();
            if (mobCount == 0) {
                getData().getEffect().effect(location);
            } else {
                hit(bombProjectile, location);
            }
        }
    }

    private void hit(final IBombProjectile bombProjectile, final Location location) {
        bombProjectile.remove();
        final BombData data = getData();
        data.getEffect().hit(location);
        EntityUtils.rangeCreatures(
                location,
                data.getRadius(),
                6
        ).forEach(entity -> damageUtilV2.itemDamage(
                getItemStatus(),
                getPlayer(),
                entity,
                1,
                power,
                data.getAttackAttribute()
        ));
    }
}
