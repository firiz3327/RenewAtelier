package net.firiz.renewatelier.server.discord;

import net.dv8tion.jda.api.entities.Member;
import net.firiz.renewatelier.sql.SQLManager;
import net.firiz.renewatelier.utils.java.CObjects;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class DiscordStatus {

    private final int userId;
    private long discordId;
    private UUID uuid;

    public DiscordStatus(int userId, UUID uuid) {
        this.userId = userId;
        this.uuid = uuid;
    }

    public DiscordStatus(int userId, long discordId, UUID uuid) {
        this.userId = userId;
        this.discordId = discordId;
        this.uuid = uuid;
    }

    public void save() {
        SQLManager.INSTANCE.insert(
                "discord",
                new String[]{"userId", "discordId"},
                new Object[]{userId, discordId}
        );
    }

    public long getDiscordId() {
        return discordId;
    }

    public void setDiscordId(long discordId) {
        this.discordId = discordId;
    }

    public UUID getUUID() {
        return uuid;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    @Nullable
    public String getPlayerName() {
        return CObjects.nullIfFunction(
                Bukkit.getPlayer(uuid),
                Player::getName,
                CObjects.nullIfFunction(
                        Bukkit.getOfflinePlayer(uuid),
                        OfflinePlayer::getName,
                        null
                )
        );
    }

}
