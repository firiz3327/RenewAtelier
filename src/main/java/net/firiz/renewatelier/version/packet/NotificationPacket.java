package net.firiz.renewatelier.version.packet;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.server.v1_15_R1.AdvancementProgress;
import net.minecraft.server.v1_15_R1.CriterionProgress;
import net.minecraft.server.v1_15_R1.MinecraftKey;
import net.minecraft.server.v1_15_R1.PacketPlayOutAdvancements;
import net.minecraft.server.v1_15_R1.PacketPlayOutRecipes;
import org.bukkit.entity.Player;

/**
 *
 * @author firiz
 */
public class NotificationPacket {

    private NotificationPacket() {
    }

    public static void sendRecipe(final Player player, final String id, final boolean remove) {
        final ArrayList<MinecraftKey> var1 = new ArrayList<>();
        var1.add(new MinecraftKey(id));
        final PacketPlayOutRecipes packet = new PacketPlayOutRecipes(
                remove ? PacketPlayOutRecipes.Action.REMOVE : PacketPlayOutRecipes.Action.ADD,
                var1,
                new ArrayList<>(),
                false,
                false,
                false,
                false
        );
        PacketUtils.sendPacket(player, packet);
    }

    @Deprecated
    public static void sendAdvancement(final Player player, final String id, final boolean remove) {
        final HashMap<MinecraftKey, AdvancementProgress> var3 = new HashMap<>();
        final AdvancementProgress progress = new AdvancementProgress();
        if (!remove) {
            try {
                final Field a = progress.getClass().getDeclaredField("a");
                a.setAccessible(true);
                final HashMap<String, CriterionProgress> p = new HashMap<>();
                final CriterionProgress cp = new CriterionProgress();
                final Field b2 = cp.getClass().getDeclaredField("b");
                b2.setAccessible(true);
                b2.set(cp, new Date());
                p.put("traded", cp);
                a.set(progress, p);

                final Field b = progress.getClass().getDeclaredField("b");
                b.setAccessible(true);
                b.set(progress, new String[][]{new String[]{"traded"}});
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
                Logger.getLogger(NotificationPacket.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        var3.put(new MinecraftKey(id), progress);
        final PacketPlayOutAdvancements packet = new PacketPlayOutAdvancements(
                false,
                new ArrayList<>(),
                new LinkedHashSet<>(),
                var3
        );
        PacketUtils.sendPacket(player, packet);
    }

}
