package net.firiz.renewatelier.version.entity.atelier;

import javassist.*;
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
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

public enum AtelierEntityUtils {
    INSTANCE;

    private final ClassPool pool = ClassPool.getDefault();
    private final Map<TargetEntityTypes, Class<?>> entityMap = new HashMap<>();
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
                e.printStackTrace();
            }
        }
    }

    @NotNull
    public LivingData spawn(@NotNull Object wrapEntity, @NotNull final Location location) {
        if (wrapEntity instanceof EntityLiving && wrapEntity instanceof Supplier) {
            final EntityLiving entity = (EntityLiving) wrapEntity;
            entity.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
            ((CraftWorld) Objects.requireNonNull(location.getWorld())).getHandle().addEntity(entity);
            return (LivingData) ((Supplier<Object>) entity).get();
        }
        throw new IllegalArgumentException("not support class.");
    }

    @NotNull
    public LivingData spawn(@NotNull final TargetEntityTypes types, @NotNull final Location location) {
        final World world = ((CraftWorld) Objects.requireNonNull(location.getWorld())).getHandle();
        final EntityLiving entity = (EntityLiving) createWrapEntity(types, world);
        entity.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        world.addEntity(entity);
        return (LivingData) ((Supplier<Object>) entity).get();
    }

    public boolean hasLivingData(@NotNull final LivingEntity entity) {
        return ((CraftLivingEntity) entity).getHandle() instanceof Supplier;
    }

    @NotNull
    public LivingData getLivingData(@NotNull final LivingEntity entity) {
        return (LivingData) ((Supplier<Object>) ((CraftLivingEntity) entity).getHandle()).get();
    }

    @NotNull
    public LivingData createLivingData(@NotNull final TargetEntityTypes types, @NotNull final EntityLiving wrapEntity) {
        return new LivingData(types, wrapEntity);
    }

    @Nullable
    private Object createWrapEntity(final TargetEntityTypes types, final World world) {
        try {
            final Class<?> wrapClass = entityMap.get(types);
            final Object wrapEntity = wrapClass.getConstructor(new Class[]{World.class}).newInstance(world);

            final LivingData livingData = createLivingData(types, (EntityLiving) wrapEntity);
            final Field livingWrapper = wrapClass.getDeclaredField("livingData");
            livingWrapper.setAccessible(true);
            livingWrapper.set(wrapEntity, livingData);

            final BiFunction<Object, Object, Boolean> damageEntityFunction = livingData::damageEntity;
            final Field damageEntity = wrapClass.getDeclaredField("damageEntity");
            damageEntity.setAccessible(true);
            damageEntity.set(wrapEntity, damageEntityFunction);

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

            final CtField damageEntity = CtField.make("java.util.function.BiFunction<Object, Object, Boolean> damageEntity;", clasz);
            clasz.addField(damageEntity);

            final StringBuilder damageEntityBody = new StringBuilder();
            damageEntityBody.append("public boolean damageEntity(net.minecraft.server.v1_15_R1.DamageSource ds, float f) {");
            damageEntityBody.append("return damageEntity.apply(ds, f).booleanValue();");
            damageEntityBody.append("}");
            final CtMethod overrideDamageEntity = CtNewMethod.make(damageEntityBody.toString(), clasz);
            clasz.addMethod(overrideDamageEntity);

//            final StringBuilder methodBody = new StringBuilder();
//            methodBody.append("public void entityBaseTick() {");
//            methodBody.append("try {");
//            methodBody.append("java.lang.reflect.Method method = livingData.getClass().getDeclaredMethod(\"entityBaseTick\");");
//            methodBody.append("method.setAccessible(true);");
//            methodBody.append("method.invoke(livingData);");
//            methodBody.append("} catch (Exception ex) {");
//            methodBody.append("ex.printStackTrace();");
//            methodBody.append("}}");
//            final CtMethod overrideEntityBaseTick = CtNewMethod.make(methodBody.toString(), clasz);
//            clasz.addMethod(overrideEntityBaseTick);

            final CtClass[] params = new CtClass[]{pool.get(World.class.getCanonicalName())};
            final CtConstructor constructor = CtNewConstructor.make(params, null, CtNewConstructor.PASS_PARAMS, null, null, clasz);
            constructor.setBody(body);
            clasz.addConstructor(constructor);
            return (Class<? extends T>) clasz.toClass();
        }
    }

    @NotNull
    protected CtClass getInnerClass(@NotNull final String superClass, @NotNull final String name) throws NotFoundException {
        final String search = superClass.concat("$").concat(name);
        for (final CtClass clasz : pool.get(superClass).getNestedClasses()) {
            if (clasz.getName().equals(search)) {
                return clasz;
            }
        }
        throw new IllegalArgumentException("not found classes");
    }

    @NotNull
    protected Class<?> createExtendsClass(@NotNull final String name, @NotNull final CtClass superClass, @NotNull final Consumer<CtClass> consumer) throws ClassNotFoundException, NotFoundException, CannotCompileException {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            final CtClass clasz = pool.makeClass(name);
            clasz.setSuperclass(superClass);
            clasz.setModifiers(Modifier.PUBLIC);
            consumer.accept(clasz);
            return clasz.toClass();
        }
    }

}
