package net.firiz.renewatelier.skills.item.skill;

import net.firiz.renewatelier.entity.player.Char;
import net.firiz.renewatelier.inventory.item.json.AlchemyItemStatus;
import net.firiz.renewatelier.skills.item.data.BombData;
import net.firiz.renewatelier.utils.minecraft.EntityUtils;
import net.firiz.renewatelier.version.entity.projectile.arrow.skill.item.BombProjectile;
import net.firiz.renewatelier.version.entity.projectile.arrow.skill.item.IBombProjectile;
import org.bukkit.Location;

public class BombSkill extends ItemSkill<BombData> {

    public BombSkill(BombData data, Char character, AlchemyItemStatus itemStatus) {
        super(data, character, itemStatus);
    }

    @Override
    public boolean fire() {
        final Location location = getPlayer().getEyeLocation();
        final IBombProjectile bomb = new BombProjectile(
                getCharacter(),
                this::effect,
                (b, entity) -> hit(b, entity.getLocation()),
                this::hit,
                location.getWorld()
        );
        bomb.spawn(location);
        bomb.setVelocity(location.getDirection().setY(0.2 + (location.getPitch() * -0.01)).multiply(0.75));
        return true;
    }

    private void effect(final IBombProjectile bombProjectile) {
        data.getEffect().effect(bombProjectile.getLocation());
    }

    protected void hit(final IBombProjectile bombProjectile, final Location location) {
        bombProjectile.remove();
        data.getEffect().hit(location);
        EntityUtils.rangeMobs(
                location,
                data.getRadius(),
                data.getMobCount()
        ).forEach(entity -> damageUtilV2.itemDamage(
                itemStatus,
                getPlayer(),
                entity,
                1,
                data.getAttackAttribute()
        ));
    }
}
