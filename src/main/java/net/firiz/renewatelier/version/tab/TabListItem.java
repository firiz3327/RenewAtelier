package net.firiz.renewatelier.version.tab;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.firiz.renewatelier.entity.player.sql.load.PlayerSaveManager;
import net.firiz.renewatelier.utils.chores.CObjects;
import net.firiz.renewatelier.version.tab.contents.TabContents;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TabListItem {

    private static final String TEXTURES_KEY = "textures";
    private static final PlayerSaveManager psm = PlayerSaveManager.INSTANCE;

    @NotNull
    private final GameProfile profile;
    @Nullable
    private TabContents contents;

    private int ping;
    @NotNull
    private String listName;
    @NotNull
    private EnumGamemode gameMode;

    public TabListItem(int slot, String name) {
        this.profile = new GameProfile(new UUID(slot, slot), name);
        this.ping = 32;
        this.listName = "";
        this.gameMode = EnumGamemode.CREATIVE;
    }

    /**
     * 個別プレイヤー用にTabListItem作成用コンストラクタ
     */
    private TabListItem(@NotNull GameProfile profile, int ping, @NotNull String listName, @NotNull EnumGamemode gameMode) {
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
        modifyListName(ChatColor.RESET + "                     ");
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
    public String getListName() {
        return listName;
    }

    @NotNull
    public EnumGamemode getGameMode() {
        return gameMode;
    }

    public void modifyPing(int ping) {
        this.ping = ping;
    }

    public void modifyListName(@NotNull String listName) {
        this.listName = Objects.requireNonNull(listName);
    }

    public void modifyTextures(@NotNull SkinProperty property) {
        modifyTextures(property.getValue(), property.getSignature());
    }

    public void modifyTextures(@NotNull GameProfile profile) {
        this.profile.getProperties().removeAll(TEXTURES_KEY);
        this.profile.getProperties().putAll(TEXTURES_KEY, profile.getProperties().get(TEXTURES_KEY));
    }

    private void modifyTextures(@NotNull String value, @NotNull String signature) {
        this.profile.getProperties().removeAll(TEXTURES_KEY);
        this.profile.getProperties().put(TEXTURES_KEY, new Property(TEXTURES_KEY, value, signature));
    }

    public void modifyGameMode(@NotNull GameMode gameMode) {
        this.gameMode = EnumGamemode.valueOf(Objects.requireNonNull(gameMode).name());
    }

}
