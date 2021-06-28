package net.firiz.renewatelier.version.tab;

import com.mojang.authlib.GameProfile;
import net.firiz.ateliercommonapi.SkinProperty;
import net.firiz.ateliercommonapi.adventure.text.C;
import net.firiz.ateliercommonapi.adventure.text.Text;
import net.firiz.renewatelier.entity.player.sql.load.PlayerSaveManager;
import net.firiz.renewatelier.utils.java.CObjects;
import net.firiz.renewatelier.version.tab.contents.TabContents;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TabListItem {

    private static final PlayerSaveManager psm = PlayerSaveManager.INSTANCE;

    @NotNull
    private final GameProfile profile;
    @Nullable
    private TabContents contents;

    private int ping;
    @NotNull
    private Component listName;
    @NotNull
    private GameMode gameMode;

    public TabListItem(int slot, String name) {
        this.profile = new GameProfile(new UUID(slot, slot), name);
        this.ping = 32;
        this.listName = Component.empty();
        this.gameMode = GameMode.CREATIVE;
    }

    /**
     * 個別プレイヤー用にTabListItem作成用コンストラクタ
     */
    private TabListItem(@NotNull GameProfile profile, int ping, @NotNull Component listName, @NotNull GameMode gameMode) {
        this.profile = profile;
        this.ping = ping;
        this.listName = listName;
        this.gameMode = gameMode;
    }

    public void setContents(@Nullable TabContents contents) {
        this.contents = contents;
    }

    @Nullable
    public TabContents getContents() {
        return contents;
    }

    public TabListItem updateCopy(int slot, @NotNull Player player) {
        final TabListItem clone = new TabListItem(profile, ping, listName, gameMode);
        CObjects.nonNullConsumer(contents, update -> update.update(
                slot,
                clone,
                psm.getChar(Objects.requireNonNull(player).getUniqueId()))
        );
        return clone;
    }

    public void resetTabItem() {
        modifyTextures(SkinProperty.GRAY);
        modifyListName(Text.of("                     ", C.WHITE));
        modifyPing(0);
    }

    @NotNull
    public GameProfile getProfile() {
        return profile;
    }

    public int getPing() {
        return ping;
    }

    @NotNull
    public Component getListName() {
        return listName;
    }

    @NotNull
    public GameMode getGameMode() {
        return gameMode;
    }

    public void modifyPing(int ping) {
        this.ping = ping;
    }

    public void modifyListName(@NotNull Component listName) {
        this.listName = Objects.requireNonNull(listName);
    }

    public void modifyTextures(@NotNull SkinProperty property) {
        property.modifyTextures(this.profile);
    }

    public void modifyTextures(@NotNull GameProfile profile) {
        SkinProperty.modifyTextures(this.profile, profile);
    }

    public void modifyGameMode(@NotNull GameMode gameMode) {
        this.gameMode = gameMode;
    }

}
