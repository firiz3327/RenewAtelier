package net.firiz.renewatelier.entity.block;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.firiz.renewatelier.sql.SQLManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

public class EntityBlockManager {

    private final List<EntityBlock> npcList = new ObjectArrayList<>();

    public final void load() {
//        SQLManager.INSTANCE.select("fallingblock", new String[]{
//                "id", // 0
//                "world", // 1
//                "x", // 2
//                "y", // 3
//                "z", // 4
//                "block" // 5
//        }, null).stream().map(objects ->
//                new EntityBlock(
//                        Objects.requireNonNull(Bukkit.getWorld((String) objects.get(1))),
//                        (double) objects.get(2),
//                        (double) objects.get(3),
//                        (double) objects.get(4),
//
//                )
//        ).forEach(npcList::add);
    }

    private final void sendPackets(Player player) {
        final World world = player.getWorld();
    }

}
