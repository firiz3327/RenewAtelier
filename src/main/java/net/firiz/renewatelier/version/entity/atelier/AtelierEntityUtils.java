package net.firiz.renewatelier.version.entity.atelier;

import javassist.*;
import net.firiz.renewatelier.utils.Chore;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Supplier;

public enum AtelierEntityUtils {
    INSTANCE;

    private final ClassPool pool = ClassPool.getDefault();
    private final Map<TargetEntityTypes, Class<?>> entityMap = new EnumMap<>(TargetEntityTypes.class);
    private final CtClass[] interfaces;

    private Method livingDamageEntity;
    private Method livingDie;
    private Method livingDrop;

    AtelierEntityUtils() {
        try {
            livingDamageEntity = LivingData.class.getDeclaredMethod("onDamageEntity", Object.class, Object.class);
            livingDamageEntity.setAccessible(true);
            livingDie = LivingData.class.getDeclaredMethod("onDie", Object.class);
            livingDie.setAccessible(true);
            livingDrop = LivingData.class.getDeclaredMethod("dropDeathLoot", Object.class, Object.class, Object.class);
            livingDrop.setAccessible(true);
        } catch (NoSuchMethodException e) {
            Chore.logWarning(e);
        }

        final CtClass supplier = pool.getOrNull(Supplier.class.getCanonicalName());
        supplier.setGenericSignature(Object.class.getCanonicalName());
        interfaces = new CtClass[]{supplier};
        for (final TargetEntityTypes types : TargetEntityTypes.values()) {
            if (types.customClass == null) {
                try {
                    final Class<?> entityClass = createWrapClass(
                            "net.firiz.renewatelier.version.entity.wrapper.AWrap".concat(types.clasz.getSimpleName()),
                            types.clasz,
                            types.body
                    );
                    entityMap.put(types, entityClass);
                } catch (NotFoundException | CannotCompileException e) {
                    Chore.logWarning(e);
                }
            }
        }
    }

    private void initEntity(EntityLiving entity, World world) {
        world.addEntity(entity, CreatureSpawnEvent.SpawnReason.CUSTOM);
        entity.canPickUpLoot = false;
    }

    @NotNull
    public LivingData spawn(@NotNull Object wrapEntity) {
        if (wrapEntity instanceof EntityLiving && wrapEntity instanceof Supplier) {
            final EntityLiving entity = (EntityLiving) wrapEntity;
            entity.getWorld().addEntity(entity);
            return (LivingData) ((Supplier<Object>) entity).get();
        }
        throw new IllegalArgumentException("not support class.");
    }

    @NotNull
    public LivingData spawn(@NotNull Object wrapEntity, @NotNull final Location location) {
        if (wrapEntity instanceof EntityLiving && wrapEntity instanceof Supplier) {
            final EntityLiving entity = (EntityLiving) wrapEntity;
            entity.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
            entity.getWorld().addEntity(entity);
            return (LivingData) ((Supplier<Object>) entity).get();
        }
        throw new IllegalArgumentException("not support class.");
    }

    public void spawn(@NotNull final TargetEntityTypes types, @NotNull final Location location) {
        spawn(types, location, true);
    }

    public void spawn(@NotNull final TargetEntityTypes types, @NotNull final Location location, final boolean cancel) {
        if (cancel) {
            // スポーン地点から32マス範囲内にLivingDataを持ったエンティティの総数が10を超える場合、スポーンをキャンセルする
            final Collection<org.bukkit.entity.Entity> nearby32 = location.getNearbyEntities(32, 32, 32);
            if (nearby32.stream().filter(this::hasLivingData).count() >= 10) {
                return;
            }
        }
        final World world = ((CraftWorld) Objects.requireNonNull(location.getWorld())).getHandle();
        final EntityLiving entity;
        if (types.customClass == null) {
            entity = (EntityLiving) createWrapEntity(types, world, location);
        } else {
            entity = (EntityLiving) createCustomClassEntity(types, world, location);
        }
        initEntity(entity, world);
        if (types.initConsumer != null) {
            types.initConsumer.accept(entity.getBukkitEntity());
        }
    }

    public boolean hasLivingData(@NotNull final Entity entity) {
        return entity instanceof EntityLiving && entity instanceof Supplier;
    }

    public boolean hasLivingData(@NotNull final org.bukkit.entity.Entity entity) {
        return hasLivingData(((CraftEntity) entity).getHandle());
    }

    @NotNull
    public LivingData getLivingData(@NotNull final EntityLiving entity) {
        return (LivingData) ((Supplier<Object>) entity).get();
    }

    @NotNull
    public LivingData getLivingData(@NotNull final LivingEntity entity) {
        return getLivingData(((CraftLivingEntity) entity).getHandle());
    }

    @NotNull
    private Object createCustomClassEntity(final TargetEntityTypes types, final World world, final Location location) {
        try {
            final Object wrapEntity = types.customClass.getConstructor(World.class).newInstance(world);

            final LivingData livingData = new LivingData(types, (EntityLiving) wrapEntity, location);
            final Field livingWrapper = types.customClass.getDeclaredField("livingData");
            livingWrapper.setAccessible(true);
            livingWrapper.set(wrapEntity, livingData);

            return wrapEntity;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchFieldException | NoSuchMethodException e) {
            Chore.logWarning(e);
            throw new IllegalStateException("error createCustomClassEntity. " + types.name());
        }
    }

    @NotNull
    private Object createWrapEntity(final TargetEntityTypes types, final World world, final Location location) {
        try {
            final Class<?> wrapClass = entityMap.get(types);
            final Object wrapEntity = wrapClass.getConstructor(World.class).newInstance(world);

            final LivingData livingData = new LivingData(types, (EntityLiving) wrapEntity, location);
            final Field livingWrapper = wrapClass.getDeclaredField("livingData");
            livingWrapper.setAccessible(true);
            livingWrapper.set(wrapEntity, livingData);

            final Field damageEntity = wrapClass.getDeclaredField("damageEntity0001");
            damageEntity.setAccessible(true);
            damageEntity.set(wrapEntity, livingDamageEntity);

            final Field die = wrapClass.getDeclaredField("die0001");
            die.setAccessible(true);
            die.set(wrapEntity, livingDie);

            final Field drop = wrapClass.getDeclaredField("drop0001");
            drop.setAccessible(true);
            drop.set(wrapEntity, livingDrop);

            return wrapEntity;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | NoSuchFieldException e) {
            Chore.logWarning(e);
            throw new IllegalStateException("error createWrapEntity. " + types.name());
        }
    }

    @Nullable
    private <T> Class<? extends T> createWrapClass(@NotNull final String name, @NotNull final Class<T> superClass, @NotNull final String body) throws NotFoundException, CannotCompileException {
        try {
            final Class<?> aClass = Class.forName(name); // reload時、class生成での重複を避ける、クラス更新する場合はrestartで再起動する事
            return (Class<? extends T>) aClass;
        } catch (ClassNotFoundException e) {
            final CtClass clasz = pool.makeClass(name);
            clasz.setSuperclass(pool.get(superClass.getCanonicalName()));
            clasz.setInterfaces(interfaces);
            clasz.setModifiers(Modifier.PUBLIC);

            final CtField livingData = CtField.make("Object livingData;", clasz);
            clasz.addField(livingData);

            final CtMethod getLivingData = CtNewMethod.make("public Object get() { return this.livingData; }", clasz);
            clasz.addMethod(getLivingData);

            final CtField damageEntity = CtField.make("java.lang.reflect.Method damageEntity0001;", clasz);
            clasz.addField(damageEntity);

            final CtField die = CtField.make("java.lang.reflect.Method die0001;", clasz);
            clasz.addField(die);

            final CtField drop = CtField.make("java.lang.reflect.Method drop0001;", clasz);
            clasz.addField(drop);

            // try-catchはなくていい
            // javassistはvarargsに対応してないので、配列にして渡す
            // floatだとObjectクラスとして認識してくれないので、Float.valueOfでFloatにする
            // booleanを返すとBooleanになるので、booleanValue()でbooleanへ変換
            final String damageEntityBody = "public boolean damageEntity(net.minecraft.server.v1_15_R1.DamageSource damagesource, float f) {" +
                    "return ((Boolean) damageEntity0001.invoke(livingData, new Object[]{damagesource, Float.valueOf(f)})).booleanValue();}";
            final CtMethod overrideDamageEntity = CtNewMethod.make(damageEntityBody, clasz);
            clasz.addMethod(overrideDamageEntity);

            final String dieBody = "public void die(net.minecraft.server.v1_15_R1.DamageSource damagesource) {" +
                    "die0001.invoke(livingData, new Object[]{damagesource});}";
            final CtMethod overrideDie = CtNewMethod.make(dieBody, clasz);
            clasz.addMethod(overrideDie);

            final String dropBody = "protected void dropDeathLoot(net.minecraft.server.v1_15_R1.DamageSource damagesource, int i, boolean flag) {" +
                    "drop0001.invoke(livingData, new Object[]{damagesource, Integer.valueOf(i), Boolean.valueOf(flag)});}";
            final CtMethod overrideDrop = CtNewMethod.make(dropBody, clasz);
            clasz.addMethod(overrideDrop);

            final CtClass[] params = new CtClass[]{pool.get(World.class.getCanonicalName())};
            final CtConstructor constructor = CtNewConstructor.make(params, null, CtNewConstructor.PASS_PARAMS, null, null, clasz);
            constructor.setBody(body);
            clasz.addConstructor(constructor);
            return (Class<? extends T>) clasz.toClass();
        }
    }

}
