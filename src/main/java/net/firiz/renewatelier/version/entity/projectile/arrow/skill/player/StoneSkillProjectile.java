package net.firiz.renewatelier.version.entity.projectile.arrow.skill.player;

import net.firiz.renewatelier.damage.AttackAttribute;
import net.firiz.renewatelier.damage.DamageUtilV2;
import net.firiz.renewatelier.entity.player.Char;
import net.firiz.renewatelier.skills.character.skill.bow.StoneShootSkill;
import net.firiz.renewatelier.utils.Randomizer;
import net.firiz.renewatelier.utils.minecraft.EntityUtils;
import net.firiz.renewatelier.version.entity.projectile.arrow.skill.SkillProjectile;
import net.minecraft.world.entity.item.EntityFallingBlock;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftFallingBlock;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class StoneSkillProjectile extends SkillProjectile {

    private final StoneShootSkill skill;
    private final SkillFallingBlock[] skillFallingBlocks = new SkillFallingBlock[3];

    private boolean shoot;
    private int diedBlocks = 2;

    // this.R(1.17) = this.ticksLived(1.16)

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
            dieAll();
            return;
        }
        if (!shoot && this.R % 40 == 0 && diedBlocks != 0) {
            final SkillFallingBlock block = skillFallingBlocks[diedBlocks];
            block.die();
            final World world = getWorld().getWorld();
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
                            .filter(net.minecraft.world.entity.Entity::isAlive)
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

    public void dieAll() {
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
        private CraftFallingBlock bukkitEntity;

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
            this.R = 580;
            this.c = false; // dropItem
        }

        @Override
        public void tick() {
            this.R = 580;
        }

        public void spawn() {
            getWorld().addEntity(this);
        }

        public void setVelocity(Vector velocity) {
            setNoGravity(false);
            getBukkitEntity().setVelocity(velocity);
        }

        @Override
        public @NotNull CraftFallingBlock getBukkitEntity() {
            if (bukkitEntity == null) {
                bukkitEntity = new CraftFallingBlock(getWorld().getCraftServer(), this);
            }
            return bukkitEntity;
        }
    }

}
