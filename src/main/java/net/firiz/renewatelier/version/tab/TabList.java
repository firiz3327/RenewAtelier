package net.firiz.renewatelier.version.tab;

import com.mojang.authlib.GameProfile;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.firiz.renewatelier.loop.LoopManager;
import net.firiz.renewatelier.utils.CommonUtils;
import net.firiz.renewatelier.utils.pair.ImmutablePair;
import net.firiz.renewatelier.version.VersionUtils;
import net.firiz.renewatelier.version.minecraft.skin.SkinProperty;
import net.firiz.renewatelier.version.packet.PacketUtils;
import net.firiz.renewatelier.version.tab.contents.FriendListTabContents;
import net.firiz.renewatelier.version.tab.contents.PartyListTabContents;
import net.firiz.renewatelier.version.tab.contents.PlayerListTabContents;
import net.firiz.renewatelier.version.tab.contents.StatusListTabContents;
import net.firiz.renewatelier.version.tab.contents.TabContents;
import net.minecraft.server.v1_16_R3.EnumGamemode;
import net.minecraft.server.v1_16_R3.IChatBaseComponent;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftChatMessage;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class TabList {

    private final List<TabListItem> tabItems = new ObjectArrayList<>();
    private final List<ImmutablePair<String, TabContents>> tabs = new ObjectArrayList<>();

    private int updateTime;

    public TabList() {
        tabs.add(new ImmutablePair<>(ChatColor.GREEN + "    プレイヤー", new PlayerListTabContents()));
        tabs.add(new ImmutablePair<>(ChatColor.GREEN + "     フレンド", new FriendListTabContents()));
        tabs.add(new ImmutablePair<>(ChatColor.GREEN + "    パーティー", new PartyListTabContents()));
        tabs.add(new ImmutablePair<>(ChatColor.GREEN + "    ステータス", new StatusListTabContents()));
    }

    public void init() {
        int mode = -1;
        for (int i = 0; i < 80; i++) { // width 4, height 20
            final TabListItem item = new TabListItem(i, countName(i));
            if (i % 20 == 0) {
                mode++;
                item.modifyTextures(SkinProperty.LIGHT_GREEN);
                item.modifyListName(tabs.get(i / 20).getLeft());
            } else {
                item.setContents(tabs.get(mode).getRight());
                item.resetTabItem();
            }
            tabItems.add(item);
        }
        LoopManager.INSTANCE.addSec(() -> {
            if (updateTime < 3) { // 3秒に１回
                updateTime++;
                return;
            }
            updateTime = 0;
            update();
        });
        update();
    }

    private void update() {
        int i = 0;
        TabContents lastTabContents = null;
        for (final TabListItem item : tabItems) {
            final TabContents contents = item.getContents();
            if (contents != null && !contents.isUpdater()) {
                if (!contents.equals(lastTabContents)) {
                    lastTabContents = contents;
                    i = 0;
                }
                contents.update(i, item, null);
                i++;
            }
        }
        Bukkit.getOnlinePlayers().forEach(player -> infoPacket(player, InfoAction.ADD_PLAYER));
    }

    public void update(Player player) {
        infoPacket(player, InfoAction.ADD_PLAYER);
    }

    private void infoPacket(Player player, InfoAction action) {
        final PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(action.enumAction);
        try {
            final List b = CommonUtils.cast(VersionUtils.getFieldValue(PacketPlayOutPlayerInfo.class, packet, "b"));
            final Constructor infoDataConstructor = Class.forName("net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo$PlayerInfoData").getDeclaredConstructor(
                    PacketPlayOutPlayerInfo.class, GameProfile.class, int.class, EnumGamemode.class, IChatBaseComponent.class
            );
            infoDataConstructor.setAccessible(true);
            int i = 0;
            TabContents lastTabContents = null;
            for (TabListItem item : tabItems) {
                final TabContents contents = item.getContents();
                if (contents != null && contents.isUpdater()) {
                    if (!contents.equals(lastTabContents)) {
                        lastTabContents = contents;
                        i = 0;
                    }
                    item = item.updateCopy(i, player);
                    i++;
                }
                b.add(infoDataConstructor.newInstance(
                        packet,
                        item.getProfile(),
                        item.getPing(),
                        item.getGameMode(),
                        CraftChatMessage.fromStringOrNull(item.getListName())
                ));
            }
            PacketUtils.sendPacket(player, packet);
        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException | ClassNotFoundException e) {
            CommonUtils.logWarning(e);
        }
    }

    private String countName(int sl) {
        final StringBuilder sb = new StringBuilder();
        sb.append(sl);
        for (int i = sb.length(); i < 11; ++i) {
            sb.insert(0, 0);
        }
        sb.insert(0, "!");
        return sb.toString();
    }

    public enum InfoAction {
        ADD_PLAYER(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER),
        UPDATE_GAME_MODE(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_GAME_MODE),
        UPDATE_PING(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_LATENCY),
        UPDATE_DISPLAY_NAME(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_DISPLAY_NAME),
        REMOVE_PLAYER(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER);

        private final PacketPlayOutPlayerInfo.EnumPlayerInfoAction enumAction;

        InfoAction(PacketPlayOutPlayerInfo.EnumPlayerInfoAction enumAction) {
            this.enumAction = enumAction;
        }
    }

}
