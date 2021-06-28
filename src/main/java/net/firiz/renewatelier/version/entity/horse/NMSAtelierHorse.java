package net.firiz.renewatelier.version.entity.horse;

import net.firiz.ateliercommonapi.MinecraftVersion;
import net.firiz.renewatelier.constants.GameConstants;
import net.firiz.renewatelier.entity.horse.EnumHorseSkill;
import net.firiz.renewatelier.entity.horse.HorseTier;
import net.firiz.renewatelier.inventory.item.json.HorseSaddle;
import net.firiz.renewatelier.utils.CommonUtils;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.animal.horse.EntityHorse;
import net.minecraft.world.entity.animal.horse.HorseColor;
import net.minecraft.world.entity.animal.horse.HorseStyle;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

@MinecraftVersion("1.17")
public class NMSAtelierHorse extends EntityHorse {

    private static final net.minecraft.world.item.ItemStack NMS_SADDLE = CraftItemStack.asNMSCopy(new ItemStack(Material.SADDLE));
    private static final PotionEffect BOOST_POTION = new PotionEffect(PotionEffectType.SPEED, 40, 0); // 2sec
    private static final NamespacedKey key = CommonUtils.createKey("nmsAtelierHorse");

    private final ItemStack saddle;
    private final HorseSaddle horseSaddle;

    private double oldX;
    private double oldZ;
    private Vec3D oldVector;

    private NMSHorseTwoSeater twoSeater;

    private long boostTime;
    private int boostCount;
    private long lastStartBoostTime = 0;
    private double acceleration = 100;
    private double accelerationUp = 1;

    private static final float MIN_ACCELERATION = 40;

    public NMSAtelierHorse(Player player, org.bukkit.World world, ItemStack saddle) {
        super(EntityTypes.M, ((CraftWorld) world).getHandle());
        this.saddle = saddle;
        this.horseSaddle = HorseSaddle.load(saddle);
        init(player.getUniqueId());
    }

    private void init(UUID owner) {
        final PersistentDataContainer dataContainer = getBukkitLivingEntity().getPersistentDataContainer();
        dataContainer.set(key, PersistentDataType.BYTE, (byte) 0);
        getAttributeInstance(GenericAttributes.a).setValue(1);
        refreshHorseStats();
        saddle(null);
//        inventoryChest.setItem(0, NMS_SADDLE.cloneItemStack());
        setVariant(HorseColor.a(horseSaddle.getColor()), HorseStyle.a(horseSaddle.getStyle()));
        setOwnerUUID(owner);
        setTamed(true);
    }

    private void refreshHorseStats() {
        final HorseTier tier = horseSaddle.getTier();
        final int level = horseSaddle.getLevel();
        double speed = tier.getSpeed(level);
        double jump = tier.getJump(level);
        if (twoSeater != null) {
            final int skillLevel = horseSaddle.getSkillLevel(EnumHorseSkill.TWO_SEATER);
            speed *= 0.8 + (skillLevel * 0.01);
            jump *= 0.8 + (skillLevel * 0.01);
        }
        if (horseSaddle.hasSkill(EnumHorseSkill.ACCELERATION)) {
            accelerationUp = 2 + (horseSaddle.getSkillLevel(EnumHorseSkill.ACCELERATION) * 0.1);
        }
        speed *= (int) acceleration * 0.01;
        getAttributeInstance(GenericAttributes.d).setValue(speed);
        getAttributeInstance(GenericAttributes.m).setValue(jump);
    }

    public HorseSaddle getHorseSaddle() {
        return horseSaddle;
    }

    public void spawn(Player player) {
        final Location location = player.getLocation();
        setPosition(location.getX(), location.getY(), location.getZ());
        oldX = location.getX();
        oldZ = location.getZ();
        boostTime = System.currentTimeMillis() + 10000;
        getWorld().addEntity(this);
        ((CraftPlayer) player).getHandle().startRiding(this);
    }

    public boolean hasTwoSeaterRider() {
        return twoSeater != null && twoSeater.hasRider();
    }

    public void startTwoSeater(Player player) {
        if (twoSeater != null) {
            if (twoSeater.hasRider()) {
                return;
            } else {
                twoSeater.die();
                twoSeater = null;
            }
        }
        twoSeater = new NMSHorseTwoSeater(getWorld(), this);
        twoSeater.spawn(player);
        refreshHorseStats();
    }

    public void boost() {
        if (boostCount > 0) {
            changeBoostCount(false);
            playSound(Sound.ENTITY_HORSE_GALLOP, 1, 0.2f);
            getBukkitLivingEntity().addPotionEffect(BOOST_POTION);
        }
    }

    private boolean startBoost(final long nowTime) {
        if (nowTime - lastStartBoostTime > 30000) { // coolTime 30sec
            playSound(Sound.ENTITY_HORSE_GALLOP, 1, 0.3f);
            getBukkitLivingEntity().addPotionEffect(
                    new PotionEffect(PotionEffectType.SPEED, 40 + (horseSaddle.getSkillLevel(EnumHorseSkill.START_BOOST) * 10), 0)
            );
            lastStartBoostTime = nowTime;
            return true;
        }
        return false;
    }

    private boolean isStartMovement() {
        final Vec3D nowVector = getRider().getMot();
        final boolean result = oldVector != null && oldVector.getX() == 0 && oldVector.getZ() == 0 && nowVector.getX() != 0 && nowVector.getZ() != 0;
        oldVector = nowVector;
        return result;
    }

    private boolean checkBoostTime(final long nowTime) {
        final int skillLevel = horseSaddle.getSkillLevel(EnumHorseSkill.BOOST);
        if (nowTime - boostTime > 20000L - (skillLevel * 1000L)) { // 19sec ~ 10sec
            boostTime = nowTime;
            return true;
        }
        return false;
    }

    private void changeBoostCount(boolean add) {
        if (add) {
            boostCount++;
            playSound(Sound.ENTITY_HORSE_GALLOP, 1, 1);
        } else {
            boostCount--;
        }
        final double maxHealth = Math.max(1, boostCount * 2);
        getAttributeInstance(GenericAttributes.a).setValue(maxHealth);
        setHealth((float) maxHealth);
    }

    /*
    die -> a(Entity.RemovalReason reason)
     */
    @Override
    public void a(Entity.RemovalReason reason) {
        super.a(reason);
        if (twoSeater != null) {
            twoSeater.die();
        }
    }

    @Override
    protected boolean removePassenger(Entity entity, boolean suppressCancellation) {
        final boolean r = super.removePassenger(entity, suppressCancellation);
        if (entity.getUniqueID() == getOwnerUUID()) {
            horseSaddle.writeItem(saddle, false);
            die();
        }
        return r;
    }

    @Override
    public void movementTick() {
        super.movementTick();
        boolean refreshHorseStats = false;
        if (horseSaddle.hasSkill(EnumHorseSkill.BOOST) && GameConstants.HORSE_MAX_BOOST_COUNT > boostCount && checkBoostTime(System.currentTimeMillis())) {
            changeBoostCount(true);
        }
        if (isStartMovement()) {
            if (horseSaddle.hasSkill(EnumHorseSkill.START_BOOST) && startBoost(System.currentTimeMillis())) {
                acceleration = 50;
            } else {
                acceleration = MIN_ACCELERATION;
            }
        }
        if (acceleration < 100) {
            acceleration += accelerationUp;
            refreshHorseStats = true;
        }
        boolean hasTwoSeater = twoSeater != null;
        if (hasTwoSeater && !twoSeater.hasRider()) {
            twoSeater.die();
            twoSeater = null;
            hasTwoSeater = false;
            refreshHorseStats = true;
        }
        if (!getPassengers().isEmpty()) {
            final Entity entity = getPassengers().get(0);
            if (entity instanceof EntityPlayer) {
                if (hasTwoSeater) {
                    final Location behind = getBehindEntity(entity);
                    twoSeater.teleportAndSync(behind.getX(), behind.getY(), behind.getZ());
                }
                if (!horseSaddle.isMaxLevel()) {
                    final double x = locX();
                    final double z = locZ();
                    if (Math.abs(x - oldX) > 10 || Math.abs(z - oldZ) > 10) {
                        oldX = x;
                        oldZ = z;
                        if (horseSaddle.addExp(1, saddle, ((EntityPlayer) entity).getBukkitEntity())) {
                            refreshHorseStats = true;
                        }
                    }
                }
            }
        }
        if (refreshHorseStats) {
            refreshHorseStats();
        }
    }

    private Entity getRider() {
        return getPassengers().get(0);
    }

    @Override
    protected boolean damageEntity0(DamageSource damagesource, float f) {
        return false;
    }

    public Location getBehindEntity(Entity entity) {
        final Location location = new Location(null, entity.locX(), entity.locY(), entity.locZ(), entity.getBukkitYaw(), entity.getXRot());
        return location.add(location.getDirection().normalize().multiply(-0.55));
    }

    private void playSound(Sound sound, float volume, float pitch) {
        getWorld().getWorld().playSound(
                new Location(getWorld().getWorld(), locX(), locY() + 0.5, locZ()),
                sound, volume, pitch
        );
    }

    public static boolean hasKey(org.bukkit.entity.Entity entity) {
        return entity.getPersistentDataContainer().has(key, PersistentDataType.BYTE);
    }

//    public void frostWalker(Horse horse, int level) {
//        if (!horse.getLocation().getBlock().getType().equals(Material.AIR)) {
//            return;
//        }
//        Location loc = horse.getLocation().subtract(0, 1, 0);
//        int radius = 2 + level;
//        Block middle = loc.getBlock();
//
//        for (int x = radius; x >= -radius; x--) {
//            for (int z = radius; z >= -radius; z--) {
//                if (middle.getRelative(x, 0, z).getType() == Material.WATER) {
//                    middle.getRelative(x, 0, z).setType(Material.FROSTED_ICE);
//                }
//            }
//        }
//    }

}