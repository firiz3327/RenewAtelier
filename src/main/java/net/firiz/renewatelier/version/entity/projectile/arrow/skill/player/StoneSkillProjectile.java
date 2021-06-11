package net.firiz.renewatelier.version.entity.projectile.arrow.skill.player;

import com.google.common.base.Preconditions;
import net.firiz.renewatelier.damage.AttackAttribute;
import net.firiz.renewatelier.damage.DamageUtilV2;
import net.firiz.renewatelier.entity.player.Char;
import net.firiz.renewatelier.skills.character.skill.bow.StoneShootSkill;
import net.firiz.renewatelier.utils.Randomizer;
import net.firiz.renewatelier.utils.minecraft.EntityUtils;
import net.firiz.renewatelier.version.entity.projectile.arrow.skill.SkillProjectile;
import net.minecraft.server.v1_16_R3.EntityFallingBlock;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftVector;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Arrays;

public class StoneSkillProjectile extends SkillProjectile {

    private final StoneShootSkill skill;
    private final SkillFallingBlock[] skillFallingBlocks = new SkillFallingBlock[3];

    private boolean shoot;
    private int diedBlocks = 2;

    // 構えながらESC押して切断したら、矢が残り続けるバグ

    public StoneSkillProjectile(Char player, World world, StoneShootSkill skill) {
        super(player, world);
        this.skill = skill;
    }

    @Override
    public SkillProjectile spawn(Location location, CreatureSpawnEvent.SpawnReason reason) {
        for (int i = 0; i < 3; i++) {
            final SkillFallingBlock skillFallingBlock = new SkillFallingBlock(location.clone().add(random(), random(), random()), randomBlockData());
            skillFallingBlock.spawn();
            skillFallingBlocks[i] = skillFallingBlock;
        }
        return super.spawn(location, reason);
    }

    @Override
    public void tick() {
        super.tick();
        if (getPlayer() == null || !getPlayer().getPlayer().isOnline()) {
            die();
            return;
        }
        if (!shoot && ticksLived % 40 == 0 && diedBlocks != 0) {
            final SkillFallingBlock block = skillFallingBlocks[diedBlocks];
            block.die();
            final World world = this.world.getWorld();
            final Location location = new Location(world, block.locX(), block.locY(), block.locZ());
            for (int i = 0; i < 3; i++) {
                world.playSound(location, Sound.BLOCK_STONE_BREAK, 1.5f, 0.8f);
                world.spawnParticle(Particle.CRIT, location, 5, 0.5, 0.5, 0.5);
                world.spawnParticle(Particle.BLOCK_CRACK, location.add(0, 0.5, 0), 10, 1, 1, 1, skillFallingBlocks[i].blockData);
            }
            diedBlocks--;
        }
    }

    @Override
    protected void hitEntity(Entity entity) {
        if (entity != null) {
            hit(entity.getLocation());
        }
    }

    @Override
    protected void hitBlock(Location location) {
        hit(location);
    }

    private void hit(Location location) {
        final World world = location.getWorld();
        world.playSound(location, Sound.BLOCK_SAND_BREAK, 0.5f, 0.2f);
        world.playSound(location, Sound.BLOCK_STONE_BREAK, 1.5f, 0.8f);
        world.spawnParticle(Particle.CRIT, location, 5, 0.5, 0.5, 0.5);
        for (int i = 0; i < 3; i++) {
            world.spawnParticle(Particle.BLOCK_CRACK, location.add(0, 0.5, 0), 10, 1, 1, 1, skillFallingBlocks[i].blockData);
        }
        EntityUtils.rangeMobs(location, 1.5, 3).forEach(mob -> {
            DamageUtilV2.INSTANCE.skillDamage(
                    AttackAttribute.BLOW,
                    getPlayer().getCharStats(),
                    mob,
                    1,
                    Arrays.stream(skillFallingBlocks)
                            .filter(net.minecraft.server.v1_16_R3.Entity::isAlive)
                            .count() * 40D
            );
            mob.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 0)); // 2秒
        });
    }

    @Override
    public void setVelocity(Vector velocity) {
        setNoGravity(false);
        this.shoot = true;
        super.setVelocity(velocity);
        for (final SkillFallingBlock block : skillFallingBlocks) {
            if (block.isAlive()) {
                block.setVelocity(velocity);
            }
        }
    }

    @Override
    public void die() {
        super.die();
        for (final SkillFallingBlock block : skillFallingBlocks) {
            if (block.isAlive()) {
                block.die();
            }
        }
        skill.dieAuto();
    }

    private double random() {
        return Randomizer.nextDouble() * 0.3 * (Randomizer.nextBoolean() ? 1 : -1);
    }

    private BlockData randomBlockData() {
        final int value = Randomizer.nextInt(10);
        return switch (value) {
            case 0, 1 -> Material.COBBLESTONE.createBlockData();
            case 2, 3 -> Material.ANDESITE.createBlockData();
            case 4 -> Material.COAL_ORE.createBlockData();
            default -> Material.STONE.createBlockData();
        };
    }

    public static class SkillFallingBlock extends EntityFallingBlock {

        private final BlockData blockData;

        public SkillFallingBlock(Location location, BlockData blockData) {
            super(
                    ((CraftWorld) location.getWorld()).getHandle(),
                    location.getX(),
                    location.getY(),
                    location.getZ(),
                    ((CraftBlockData) blockData).getState()
            );
            this.blockData = blockData;
            setNoGravity(true);
            ticksLived = 580;
            dropItem = false;
        }

        @Override
        public void tick() {
            ticksLived = 580;
        }

        public void spawn() {
            world.addEntity(this);
        }

        public void setVelocity(Vector velocity) {
            setNoGravity(false);
            Preconditions.checkArgument(velocity != null, "velocity");
            velocity.checkFinite();
            setMot(CraftVector.toNMS(velocity));
            velocityChanged = true;
        }
    }

}
