package net.firiz.renewatelier.version;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.firiz.renewatelier.utils.Chore;
import net.firiz.renewatelier.version.nms.VItemStack;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_15_R1.ChatMessage;
import net.minecraft.server.v1_15_R1.EntityItem;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftItem;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Item;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author firiz
 */
public class VersionUtils {

    private VersionUtils() {
    }

    private static MethodHandles.Lookup lkp;

    static {
        try {
            final Field implLookup = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            implLookup.setAccessible(true);
            lkp = (MethodHandles.Lookup) implLookup.get(null);
            implLookup.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Chore.logWarning(e);
            System.exit(1);
        }
    }

    public static Object createNBTTagCompound() {
        return new NBTTagCompound();
    }

    public static Object createChatMessage(final String msg) {
        return new ChatMessage(msg);
    }

    public static TextComponent createTextComponent(final String msg) {
        return new TextComponent(msg);
    }

    public static VItemStack asVItemCopy(final ItemStack item) {
        return new VItemStack(CraftItemStack.asNMSCopy(item));
    }
    public static net.minecraft.server.v1_15_R1.ItemStack asNMSCopy(final ItemStack item) {
        return CraftItemStack.asNMSCopy(item);
    }

    public static ItemStack asItem(final VItemStack item) {
        final net.minecraft.server.v1_15_R1.ItemStack nms = (net.minecraft.server.v1_15_R1.ItemStack) item.getNmsItem();
        return CraftItemStack.asBukkitCopy(nms);
    }

    public static ItemStack asItem(final net.minecraft.server.v1_15_R1.ItemStack item) {
        return CraftItemStack.asBukkitCopy(item);
    }

    public static Item drop(@NotNull final Location location, @NotNull final ItemStack item, @NotNull final CreatureSpawnEvent.SpawnReason reason) {
        Objects.requireNonNull(location);
        Objects.requireNonNull(item);
        Objects.requireNonNull(reason);
        final CraftWorld world = (CraftWorld) Objects.requireNonNull(location.getWorld());
        EntityItem entity = new EntityItem(world.getHandle(), location.getX(), location.getY(), location.getZ(), CraftItemStack.asNMSCopy(item));
        entity.pickupDelay = 10;
        world.getHandle().addEntity(entity, reason);
        return new CraftItem(world.getHandle().getServer(), entity);
    }

    @NotNull
    public static Field getField(Object object, String name) {
        try {
            final Field field = object.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            Chore.logWarning(e);
        }
        throw new IllegalStateException("getField error");
    }

    @Nullable
    public static Object getFieldValue(Class<?> clasz, Object obj, String name) {
        try {
            return getField(clasz, name).get(obj);
        } catch (IllegalAccessException e) {
            Chore.logWarning(e);
        }
        return null;
    }

    @Nullable
    public static Object getFieldValue(Field field, Object obj) {
        try {
            return field.get(obj);
        } catch (IllegalAccessException e) {
            Chore.logWarning(e);
        }
        return null;
    }

    @NotNull
    public static Field getField(Class<?> clasz, String name) {
        try {
            final Field field = clasz.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            Chore.logWarning(e);
        }
        throw new IllegalStateException("getField error");
    }

    public static void setFieldValue(Field field, Object obj, Object value) {
        try {
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            Chore.logWarning(e);
        }
    }

    public static void setFieldValue(Class<?> clasz, Object obj, String name, Object value) {
        try {
            getField(clasz, name).set(obj, value);
        } catch (IllegalAccessException e) {
            Chore.logWarning(e);
        }
    }

    public static Object invoke(Object obj, String name, Class<?>[] params, Object[] args) {
        try {
            final Method method = obj.getClass().getDeclaredMethod(name, params);
            method.setAccessible(true);
            return method.invoke(obj, args);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            Chore.logWarning(e);
        }
        return null;
    }

    @Nullable
    public static MethodHandle getGetter(Class<?> clazz, String name) {
        try {
            return lkp.unreflectGetter(getField(clazz, name));
        } catch (Exception e) {
            Chore.logWarning(e);
        }
        return null;
    }

    @Nullable
    public static MethodHandle getSetter(Class<?> clazz, String name) {
        try {
            return lkp.unreflectSetter(getField(clazz, name));
        } catch (Exception e) {
            Chore.logWarning(e);
        }
        return null;
    }

    @Nullable
    public static <T> T superInvoke(String methodName, Object obj, Class<?> superClass, Class<T> rType, Map<Object, Class<?>> parameters) {
        try {
            MethodHandle handle = lkp.findSpecial(
                    superClass,
                    methodName,
                    MethodType.methodType(rType, parameters.values().toArray(new Class[0])),
                    obj.getClass()
            );
            final List<Object> p = new ObjectArrayList<>(parameters.keySet());
            p.add(0, obj);
            return (T) handle.invokeWithArguments(p);
        } catch (Throwable e) {
            Chore.logWarning(e);
        }
        return null;
    }

    public static Method getMethod(Class<?> clazz, String method, Class<?>... params) {
        if (clazz == null)
            return null;
        Method f = null;
        try {
            f = clazz.getDeclaredMethod(method, params);
            f.setAccessible(true);
        } catch (Exception e) {
            Chore.logWarning(e);
        }
        return f;
    }

    public static MethodHandle getMethodHandle(Class<?> clazz, String method, Class<?>... params) {
        if (clazz == null)
            return null;
        try {
            return lkp.unreflect(getMethod(clazz, method, params));
        } catch (Exception e) {
            Chore.logWarning(e);
        }
        return null;
    }

}
