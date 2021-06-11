package net.firiz.renewatelier.server.script.conversation;

import net.firiz.renewatelier.npc.MessageObject;
import net.firiz.renewatelier.npc.NPC;
import net.firiz.renewatelier.npc.NPCManager;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.graalvm.polyglot.HostAccess.Export;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.script.Invocable;
import java.util.UUID;

/**
 * @author firiz
 */
public final class NPCConversation extends ScriptConversation {

    private final NPC npc;
    private final MessageObject messageObject;

    public NPCConversation(@NotNull NPC npc, @NotNull String scriptName, @NotNull Player player) {
        super(scriptName, player);
        this.npc = npc;
        this.messageObject = new MessageObject(player, npc.getMessageLocation());
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
        player.sendMessage(prefix + text + suffix);
        messageObject.messagePacket(chatColor(text));
    }

    @Export
    public void sendNpcChat(@NotNull final String msg, final int maxStatus, final int status) {
        final StringBuilder val = new StringBuilder();
        for (int i = 0; i < maxStatus; i++) {
            val.append(i <= status ? "&a●" : "&7●");
        }
        sendNext(
                chatColor(val.insert(0, "&7[").append("&7] &2").append(getNPCName()).append(" ").toString()),
                chatColor("&a" + msg)
        );
    }

}
