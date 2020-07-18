package net.firiz.renewatelier.utils;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.firiz.renewatelier.AtelierPlugin;
import net.firiz.renewatelier.alchemy.material.AlchemyAttribute;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;

import java.awt.*;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CommonUtils {

    private static final AtelierPlugin plugin = AtelierPlugin.getPlugin();
    private static final Map<String, NamespacedKey> keys = new Object2ObjectOpenHashMap<>();
    private static final Logger log = plugin.getLogger();
    public static final NumberFormat commaFormatter = NumberFormat.getNumberInstance();
    private static final String S_WARNING = "\u001B[41m";
    private static final String S_OFF = "\u001B[0m";

    private CommonUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static NamespacedKey createKey(String key) {
        return new NamespacedKey(plugin, key);
    }

    public static String getSetting(final PersistentDataHolder dataHolder, final NamespacedKey key) {
        return dataHolder.getPersistentDataContainer().get(key, PersistentDataType.STRING);
    }

    public static void setSetting(final PersistentDataHolder dataHolder, final NamespacedKey key, final String value) {
        dataHolder.getPersistentDataContainer().set(key, PersistentDataType.STRING, value);
    }

    public static int getSettingInt(final PersistentDataHolder dataHolder, final NamespacedKey key) {
        return Objects.requireNonNull(dataHolder.getPersistentDataContainer().get(key, PersistentDataType.INTEGER), "null integer.");
    }

    public static void setSettingInt(final PersistentDataHolder dataHolder, final NamespacedKey key, final int value) {
        dataHolder.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, value);
    }

    public static String intCcolor(int i) {
        switch (i) {
            case 0:
                return ChatColor.GRAY.toString();
            case 1:
                return ChatColor.WHITE.toString();
            case 2:
                return AlchemyAttribute.RED.getColor();
            case 3:
                return AlchemyAttribute.BLUE.getColor();
            case 4:
                return AlchemyAttribute.GREEN.getColor();
            case 5:
                return AlchemyAttribute.YELLOW.getColor();
            case 6:
                return AlchemyAttribute.PURPLE.getColor();
            default:
                return "";
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object obj) {
        return (T) obj;
    }

    public static void log(final Object obj) {
        if (obj instanceof Exception) {
            logWarning(obj);
        } else {
            log.log(Level.INFO, "{0}", obj);
        }
    }

    public static void log(final String str) {
        log.info(str);
    }

    public static void logWarning(final Object obj) {
        if (obj instanceof Exception) {
            final Exception ex = ((Exception) obj);
            log.log(Level.WARNING, S_WARNING.concat(ex.getMessage() == null ? "null message" : ex.getMessage()), ex);
            log.log(Level.OFF, S_OFF);
        } else {
            log.log(Level.WARNING, "{0}", obj);
        }
    }

    public static void logWarning(final String str) {
        log.log(Level.WARNING, "{0}", S_WARNING.concat(str).concat(S_OFF));
    }

    public static void logWarning(final String str, final Throwable throwable) {
        log.log(Level.WARNING, S_WARNING.concat(str), throwable);
        log.log(Level.OFF, S_OFF);
    }

    public static void logLightWarning(final String str) {
        log.log(Level.WARNING, "{0}", "\u001B[30m\u001B[43m".concat(str).concat(S_OFF));
    }

    public static void logWhiteWarning(final String str) {
        log.log(Level.WARNING, "{0}", "\u001B[30m\u001B[47m".concat(str).concat(S_OFF));
    }

    public static void log(final Level level, final String str, final Throwable throwable) {
        log.log(level, str, throwable);
    }

    public static boolean isNumMatch(String number) {
        final Pattern pattern = Pattern.compile("^[0-9]*$");
        final Matcher matcher = pattern.matcher(number);
        return matcher.matches();
    }

    public static String comma(Number number) {
        return commaFormatter.format(number);
    }

    public static List<Location> generateSphere(Location centerBlock, int radius, boolean hollow) {
        List<Location> circleBlocks = new ObjectArrayList<>();
        int bx = centerBlock.getBlockX();
        int by = centerBlock.getBlockY();
        int bz = centerBlock.getBlockZ();

        for (int x = bx - radius; x <= bx + radius; x++) {
            for (int y = by - radius; y <= by + radius; y++) {
                for (int z = bz - radius; z <= bz + radius; z++) {
                    double distance = ((bx - x) * (bx - x) + ((bz - z) * (bz - z)) + ((by - y) * (by - y)));
                    if (distance < radius * radius && !(hollow && distance < ((radius - 1) * (radius - 1)))) {
                        Location l = new Location(centerBlock.getWorld(), x, y, z);
                        circleBlocks.add(l);
                    }
                }
            }
        }

        return circleBlocks;
    }

    public static int near(int val, int truncation) {
        TreeSet<Integer> tree = new TreeSet<>();
        for (int i = 0; i < val; i++) {
            int value = i * truncation;
            tree.add(value);
            if (value >= val) {
                break;
            }
        }
        return near(tree, val);
    }

    public static int near(NavigableSet<Integer> truncation, int val) {
        final int floor = Objects.requireNonNull(truncation.floor(val));
        final int ceiling = Objects.requireNonNull(truncation.ceiling(val));
        return Math.abs(floor - val) <= Math.abs(ceiling - val) ? floor : ceiling;
    }

    public static boolean isRight(Action a) {
        return a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK;
    }

    public static boolean isRightOnly(Action a, boolean block) {
        return block ? a == Action.RIGHT_CLICK_BLOCK : a == Action.RIGHT_CLICK_AIR;
    }

    public static boolean isLeft(Action a) {
        return a == Action.LEFT_CLICK_AIR || a == Action.LEFT_CLICK_BLOCK;
    }

    public static boolean isLeftOnly(Action a, boolean block) {
        return block ? a == Action.LEFT_CLICK_BLOCK : a == Action.LEFT_CLICK_AIR;
    }

    public static boolean distanceSq(final Location locCenter, final Location locCheck, int rangeSq, int rangeY) {
        return new Point(locCenter.getBlockX(), locCenter.getBlockZ())
                .distanceSq(new Point(
                        locCheck.getBlockX(),
                        locCheck.getBlockZ()
                )) <= rangeSq
                && Math.abs(locCenter.getY() - locCheck.getY()) <= rangeY;
    }
}
