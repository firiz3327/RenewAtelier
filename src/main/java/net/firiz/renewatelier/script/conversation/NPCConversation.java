package net.firiz.renewatelier.script.conversation;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.firiz.renewatelier.AtelierPlugin;
import net.firiz.renewatelier.alchemy.material.AlchemyIngredients;
import net.firiz.renewatelier.alchemy.material.AlchemyMaterial;
import net.firiz.renewatelier.characteristic.Characteristic;
import net.firiz.renewatelier.inventory.delivery.DeliveryInventory;
import net.firiz.renewatelier.inventory.delivery.DeliveryObject;
import net.firiz.renewatelier.npc.NPC;
import net.firiz.renewatelier.npc.NPCManager;
import net.firiz.renewatelier.utils.CommonUtils;
import net.firiz.renewatelier.version.packet.EntityPacket;
import net.firiz.renewatelier.version.packet.FakeEntity;
import net.firiz.renewatelier.version.packet.PacketUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.graalvm.polyglot.HostAccess.Export;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.script.Invocable;
import java.util.List;
import java.util.UUID;

/**
 * @author firiz
 */
public final class NPCConversation extends ScriptConversation {

    private final NPC npc;
    private FakeEntity fakeEntity;
    private BukkitTask runTaskLater;

    public NPCConversation(@NotNull NPC npc, @NotNull String scriptName, @NotNull Player player) {
        super(scriptName, player);
        this.npc = npc;
    }

    @Nullable
    @Export
    public Invocable getIv() {
        return iv;
    }

    @Export
    public NPC getNPC() {
        return npc;
    }

    @NotNull
    @Export
    public String getNPCName() {
        return npc.getName();
    }

    @NotNull
    @Export
    public Location getLocation() {
        return npc.getLocation();
    }

    @NotNull
    @Export
    public List<DeliveryObject> listDeliveryOf() {
        return new ObjectArrayList<>();
    }

    @NotNull
    @Export
    public DeliveryObject createDeliveryObject(@NotNull AlchemyMaterial material, int reqAmount) {
        return createDeliveryObject(material, reqAmount, 0, new ObjectArrayList<>(), new ObjectArrayList<>());
    }

    @NotNull
    @Export
    public DeliveryObject createDeliveryObject(@NotNull AlchemyMaterial material, int reqAmount, int reqQuality) {
        return createDeliveryObject(material, reqAmount, reqQuality, new ObjectArrayList<>(), new ObjectArrayList<>());
    }

    @NotNull
    @Export
    public DeliveryObject createDeliveryObject(@NotNull AlchemyMaterial material, int reqAmount, int reqQuality, List<Characteristic> reqCharacteristic, List<AlchemyIngredients> reqIngredients) {
        return new DeliveryObject(material, reqAmount, reqQuality, reqCharacteristic, reqIngredients);
    }

    @Export
    public void openDeliveryInventory(
            @NotNull final String title,
            final int line_size,
            @NotNull final List<DeliveryObject> deliveryObjects
    ) {
        DeliveryInventory.INSTANCE.openInventory(player, title, line_size, deliveryObjects);
    }

    @Export
    public void removeNPC(@NotNull final String name, @NotNull final String script, final double x, final double y, final double z, @NotNull final EntityType type) {
        removeNPC(name, script, new Location(player.getWorld(), x, y, z), type);
    }

    @Export
    public void removeNPC(@NotNull final String name, @NotNull final String script, @NotNull final Location location, @NotNull final EntityType type) {
//        NPCManager.INSTANCE.removeNPC(location, type, name, script);
    }

    @Export
    public void createNPC(@NotNull final String name, @NotNull final String script, final double x, final double y, final double z, @NotNull final EntityType type) {
        createNPC(name, script, new Location(player.getWorld(), x, y, z), type);
    }

    @Export
    public void createNPC(@NotNull final String name, @NotNull final String script, @NotNull final Location location, @NotNull final EntityType type) {
//        NPCManager.INSTANCE.createNPC(location, type, name, script);
    }

    @Export
    public void removePlayerNPC(@NotNull final String name, @NotNull final String script, final double x, final double y, final double z, @NotNull final UUID uuid) {
        removePlayerNPC(name, script, new Location(player.getWorld(), x, y, z), uuid);
    }

    @Export
    public void removePlayerNPC(@NotNull final String name, @NotNull final String script, @NotNull final Location location, @NotNull final UUID uuid) {
//        NPCManager.INSTANCE.removeNPCPlayer(location, name, uuid, script);
    }

    @Export
    public void createPlayerNPC(@NotNull final String name, @NotNull final String script, final double x, final double y, final double z, @NotNull final UUID uuid) {
        createPlayerNPC(name, script, new Location(player.getWorld(), x, y, z), uuid);
    }

    @Export
    public void createPlayerNPC(@NotNull final String name, @NotNull final String script, @NotNull final Location location, @NotNull final UUID uuid) {
//        NPCManager.INSTANCE.createNPCPlayer(location, name, uuid, script);
    }

    @Export
    public void dispose() {
        NPCManager.INSTANCE.dispose(player.getUniqueId());
    }

    @Export
    public void sendNext(@NotNull final String text) {
        sendNext("", text);
    }

    @Export
    public void sendNext(@NotNull final String prefix, @NotNull final String text) {
        sendNext(prefix, "", text);
    }

    @Export
    public void sendNext(@NotNull final String prefix, @NotNull final String suffix, @NotNull final String text) {
        sendNext(prefix, suffix, text, 60); // 3 sec
    }

    @Export
    public void sendNext(@NotNull final String prefix, @NotNull final String suffix, @NotNull final String text, final int time) {
        player.sendMessage(prefix + text + suffix);
        messagePacket(chatColor(text), time);
    }

    @Export
    public void sendNpcChat(@NotNull final String msg, final int maxStatus, final int status) {
        final StringBuilder val = new StringBuilder();
        for (int i = 0; i < maxStatus; i++) {
            val.append(i <= status ? "●" : "○");
        }
        sendNext(
                chatColor(val.insert(0, "&7[").append("] &2").append(getNPCName()).append(" ").toString()),
                chatColor("&a" + msg)
        );
    }

    private void messagePacket(@NotNull final String text, final int time) {
        final Location balloonLoc = getLocation();
        balloonLoc.setY(balloonLoc.getY() + 0.25);

        MessageType.FAKE_ENTITY.broadcast(
                this,
                FakeEntity.FakeEntityType.ARMOR_STAND,
                balloonLoc,
                text
        );
        if (runTaskLater != null) {
            Bukkit.getScheduler().cancelTask(runTaskLater.getTaskId());
        }
        runTaskLater = Bukkit.getScheduler().runTaskLater(
                AtelierPlugin.getPlugin(),
                () -> MessageType.DESTROY_FAKE_ENTITY.broadcast(this),
                time
        );
    }

    private enum MessageType {
        FAKE_ENTITY {
            /**
             *　アーマースタンドのパケットを生成し、Scriptの対象プレイヤーへ送信します。
             *
             * @param conversation
             * @param args EntityType, Location, Stringを引数とします
             */
            @Override
            void run(NPCConversation conversation, Object... args) {
                try {
                    final FakeEntity fakeEntity = new FakeEntity(-1, (FakeEntity.FakeEntityType) args[0], 0);
                    conversation.fakeEntity = fakeEntity;
                    PacketUtils.sendPacket(conversation.player, EntityPacket.getSpawnPacket(
                            fakeEntity,
                            (Location) args[1]
                    ));
                    PacketUtils.sendPacket(conversation.player, EntityPacket.getMessageStandMeta(conversation.player.getWorld(), (String) args[2]).compile(fakeEntity.getEntityId()));
                } catch (Exception e) {
                    CommonUtils.logWarning(e);
                }
            }
        },
        DESTROY_FAKE_ENTITY {
            @Override
            void run(NPCConversation conversation, Object... args) {
                PacketUtils.sendPacket(conversation.player, EntityPacket.getDespawnPacket(conversation.fakeEntity.getEntityId()));
            }
        };

        abstract void run(NPCConversation conversation, Object... args);

        public void broadcast(@NotNull final NPCConversation conversation, @NotNull final Object... args) {
            run(conversation, args);
        }
    }

}
