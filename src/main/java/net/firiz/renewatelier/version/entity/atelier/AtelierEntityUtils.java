package net.firiz.renewatelier.version.entity.atelier;

import javassist.*;
import net.firiz.renewatelier.entity.monster.MonsterStats;
import net.firiz.renewatelier.utils.Chore;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;
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

    AtelierEntityUtils() {
        final CtClass supplier = pool.getOrNull(Supplier.class.getCanonicalName());
        supplier.setGenericSignature(Object.class.getCanonicalName());
        interfaces = new CtClass[]{supplier};
        for (final TargetEntityTypes types : TargetEntityTypes.values()) {
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

    @NotNull
    public LivingData spawn(@NotNull Object wrapEntity) {
        if (wrapEntity instanceof EntityLiving && wrapEntity instanceof Supplier) {
            final EntityLiving entity = (EntityLiving) wrapEntity;
            ((EntityLiving) wrapEntity).getWorld().addEntity(entity);
            return (LivingData) ((Supplier<Object>) entity).get();
        }
        throw new IllegalArgumentException("not support class.");
    }

    @NotNull
    public LivingData spawn(@NotNull Object wrapEntity, @NotNull final Location location) {
        if (wrapEntity instanceof EntityLiving && wrapEntity instanceof Supplier) {
            final EntityLiving entity = (EntityLiving) wrapEntity;
            entity.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
            ((EntityLiving) wrapEntity).getWorld().addEntity(entity);
            return (LivingData) ((Supplier<Object>) entity).get();
        }
        throw new IllegalArgumentException("not support class.");
    }

    @NotNull
    public LivingData spawn(@NotNull final TargetEntityTypes types, @NotNull final Location location) {
        final World world = ((CraftWorld) Objects.requireNonNull(location.getWorld())).getHandle();
        final EntityLiving entity = (EntityLiving) createWrapEntity(types, world, location);
        world.addEntity(entity);
        return (LivingData) ((Supplier<Object>) entity).get();
    }

    // 名前の比較にする？
    public boolean hasLivingData(@NotNull final EntityLiving entity) {
        return entity instanceof Supplier;
    }

    public boolean hasLivingData(@NotNull final LivingEntity entity) {
        return hasLivingData(((CraftLivingEntity) entity).getHandle());
    }

    @NotNull
    public LivingData getLivingData(@NotNull final EntityLiving entity) {
        return (LivingData) ((Supplier<Object>) entity).get();
    }

    @NotNull
    public LivingData getLivingData(@NotNull final LivingEntity entity) {
        return getLivingData(((CraftLivingEntity) entity).getHandle());
    }

    @Nullable
    private Object createWrapEntity(final TargetEntityTypes types, final World world, final Location location) {
        try {
            final Class<?> wrapClass = entityMap.get(types);
            final Object wrapEntity = wrapClass.getConstructor(new Class[]{World.class}).newInstance(world);

            final LivingData livingData = new LivingData(types, (EntityLiving) wrapEntity, location);
            final Field livingWrapper = wrapClass.getDeclaredField("livingData");
            livingWrapper.setAccessible(true);
            livingWrapper.set(wrapEntity, livingData);

            final Field damageEntity = wrapClass.getDeclaredField("damageEntity");
            damageEntity.setAccessible(true);
            final Method method = LivingData.class.getDeclaredMethod("damageEntity", Object.class, Object.class);
            method.setAccessible(true);
            damageEntity.set(wrapEntity, method);

            return wrapEntity;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | NoSuchFieldException e) {
            Chore.logWarning(e);
        }
        return null;
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

            final CtField damageEntity = CtField.make("java.lang.reflect.Method damageEntity;", clasz);
            clasz.addField(damageEntity);

            // try-catchはなくていい
            // javassistはvarargsに対応してないので、配列にして渡す
            // floatだとObjectクラスとして認識してくれないので、Float.valueOfでFloatにする
            // booleanを返すとBooleanになるので、booleanValue()でbooleanへ変換
            final String damageEntityBody = "public boolean damageEntity(net.minecraft.server.v1_15_R1.DamageSource damagesource, float f) {" +
                    "return ((Boolean) damageEntity.invoke(livingData, new Object[]{damagesource, Float.valueOf(f)})).booleanValue();}";
            final CtMethod overrideDamageEntity = CtNewMethod.make(damageEntityBody, clasz);
            clasz.addMethod(overrideDamageEntity);

            final CtClass[] params = new CtClass[]{pool.get(World.class.getCanonicalName())};
            final CtConstructor constructor = CtNewConstructor.make(params, null, CtNewConstructor.PASS_PARAMS, null, null, clasz);
            constructor.setBody(body);
            clasz.addConstructor(constructor);
            return (Class<? extends T>) clasz.toClass();
        }
    }

}
