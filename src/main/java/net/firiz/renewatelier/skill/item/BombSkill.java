package net.firiz.renewatelier.skill.item;

import net.firiz.renewatelier.damage.DamageUtilV2;
import net.firiz.renewatelier.item.json.AlchemyItemStatus;
import net.firiz.renewatelier.skill.data.BombData;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

public class BombSkill extends ForceItemSkill<BombData> {

    private static final DamageUtilV2 damageUtilV2 = DamageUtilV2.INSTANCE;
    private final double power;

    public BombSkill(BombData data, Player player, AlchemyItemStatus itemStatus, float force) {
        super(data, player, itemStatus, force);
        this.power = itemStatus.getAlchemyMaterial().getPower();
    }

    @Override
    public void fire() {
        final BombData data = getData();
        final Player player = getPlayer();
        final Location location = player.getLocation();
        final Vector target = location.getDirection().multiply(10f * getForce()).normalize();
        final Vector link = target.subtract(location.toVector());
        final float length = (float) link.length();
        final float pitch = (float) (4 * data.getHeight() / Math.pow(length, 2));
        for (int i = 0; i < data.getParticles(); i++) {
            final Vector v = link.clone().normalize().multiply(length * i / data.getParticles());
            final float x = ((float) i / data.getParticles()) * length - length / 2;
            final float y = (float) (-pitch * Math.pow(x, 2) + data.getHeight());
            effect(location.clone().add(v).add(0, y, 0));
        }
    }

    private void effect(Location location) {
        if (!location.getBlock().getType().isAir()) {
            hit(location);
        } else {
            final List<LivingEntity> entities = (List<LivingEntity>) location.getNearbyLivingEntities(1);
            entities.remove(getPlayer());
            if (entities.isEmpty()) {
                getData().getEffect().effect(location);
            } else {
                hit(location);
            }
        }
    }

    public void hit(Location location) {
        final BombData data = getData();
        data.getEffect().hit(location);
        final World world = location.getWorld();
        world.getNearbyLivingEntities(location, data.getRadius()).forEach(entity -> damageUtilV2.itemDamage(
                getItemStatus(),
                getPlayer(),
                entity,
                1,
                power,
                data.getAttackAttribute()
        ));
    }
}
