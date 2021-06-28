package net.firiz.renewatelier.version.tab;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import net.firiz.ateliercommonapi.SkinProperty;
import net.firiz.ateliercommonapi.adventure.text.C;
import net.firiz.ateliercommonapi.adventure.text.Text;
import net.firiz.ateliercommonapi.loop.LoopManager;
import net.firiz.ateliercommonapi.nms.entity.player.PlayerInfoAction;
import net.firiz.ateliercommonapi.nms.entity.player.PlayerInfoData;
import net.firiz.ateliercommonapi.nms.packet.PacketUtils;
import net.firiz.renewatelier.version.tab.contents.FriendListTabContents;
import net.firiz.renewatelier.version.tab.contents.PartyListTabContents;
import net.firiz.renewatelier.version.tab.contents.PlayerListTabContents;
import net.firiz.renewatelier.version.tab.contents.StatusListTabContents;
import net.firiz.renewatelier.version.tab.contents.TabContents;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class TabList {

    private final List<TabListItem> tabItems = new ObjectArrayList<>();
    private final List<ObjectObjectImmutablePair<Component, TabContents>> tabs = new ObjectArrayList<>();

    private int updateTime;

    public TabList() {
        tabs.add(new ObjectObjectImmutablePair<>(Text.of("    プレイヤー", C.GREEN), new PlayerListTabContents()));
        tabs.add(new ObjectObjectImmutablePair<>(Text.of("     フレンド", C.GREEN), new FriendListTabContents()));
        tabs.add(new ObjectObjectImmutablePair<>(Text.of("    パーティー", C.GREEN), new PartyListTabContents()));
        tabs.add(new ObjectObjectImmutablePair<>(Text.of("    ステータス", C.GREEN), new StatusListTabContents()));
    }

    public void init() {
        int mode = -1;
        for (int i = 0; i < 80; i++) { // width 4, height 20
            final TabListItem item = new TabListItem(i, countName(i));
            if (i % 20 == 0) {
                mode++;
                item.modifyTextures(SkinProperty.LIGHT_GREEN);
                item.modifyListName(tabs.get(i / 20).left());
            } else {
                item.setContents(tabs.get(mode).right());
                item.resetTabItem();
            }
            tabItems.add(item);
        }
        LoopManager.INSTANCE.addSeconds(() -> {
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
        Bukkit.getOnlinePlayers().forEach(this::infoPacket);
    }

    public void update(Player player) {
        infoPacket(player);
    }

    private void infoPacket(Player player) {
        int i = 0;
        final List<PlayerInfoData> players = new ObjectArrayList<>();
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
            players.add(new PlayerInfoData(item.getProfile(), item.getPing(), item.getGameMode(), item.getListName()));
        }
        PacketUtils.sendPacket(player, PlayerInfoAction.ADD_PLAYER.compile(players.toArray(new PlayerInfoData[0])));
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

}
