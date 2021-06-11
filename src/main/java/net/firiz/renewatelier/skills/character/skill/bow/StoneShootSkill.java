package net.firiz.renewatelier.skills.character.skill.bow;

import net.firiz.renewatelier.entity.player.Char;
import net.firiz.renewatelier.inventory.item.json.AlchemyItemStatus;
import net.firiz.renewatelier.version.entity.projectile.arrow.skill.player.StoneSkillProjectile;
import org.bukkit.Location;
import org.bukkit.entity.Projectile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StoneShootSkill extends BowCharSkill {

    private StoneSkillProjectile projectile;

    public StoneShootSkill(@NotNull Char character, @Nullable AlchemyItemStatus itemStatus) {
        super(character, itemStatus);
    }

    @Override
    public boolean fire() {
        if (projectile == null) {
            final Location playerLocation = getPlayer().getLocation();
            final Location away = playerLocation.add(playerLocation.getDirection().multiply(0.8));
            away.setY(away.getY() + 2);
            projectile = new StoneSkillProjectile(getCharacter(), away.getWorld(), this);
            projectile.spawn(away);
        }
        return false;
    }

    @Override
    public boolean shoot(Projectile base) {
        if (projectile != null && projectile.isAlive()) {
            projectile.setVelocity(base.getVelocity().multiply(0.5).setY(-0.1 + (getPlayer().getLocation().getPitch() * -0.01)));
        }
        return true;
    }

    @Override
    public void die() {
        super.die();
        projectile.die();
    }

    public void dieAuto() {
        super.die();
    }

    public static StoneShootSkill create(Char player, AlchemyItemStatus itemStatus) {
        return new StoneShootSkill(player, itemStatus);
    }
}
