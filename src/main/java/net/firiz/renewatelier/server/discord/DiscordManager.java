package net.firiz.renewatelier.server.discord;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLongImmutablePair;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.firiz.ateliercommonapi.adventure.text.Text;
import net.firiz.ateliercommonapi.loop.LoopManager;
import net.firiz.renewatelier.AtelierPlugin;
import net.firiz.renewatelier.entity.player.Char;
import net.firiz.renewatelier.entity.player.sql.load.PlayerSaveManager;
import net.firiz.renewatelier.server.chat.Chat;
import net.firiz.renewatelier.sql.SQLManager;
import net.firiz.renewatelier.utils.CommonUtils;
import net.firiz.renewatelier.utils.java.CObjects;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.util.*;
import java.util.function.Consumer;

public enum DiscordManager {
    INSTANCE;

    private String discordBotToken;
    private long chatChannelId;

    private JDA jda;
    private TextChannel chatChannel;
    private String webhookUrl;

    private final Int2ObjectMap<DiscordStatus> statusList = new Int2ObjectOpenHashMap<>();
    private final Map<Player, ObjectLongImmutablePair<String>> tokens = new Object2ObjectOpenHashMap<>();

    public void setup() {
        final AtelierPlugin plugin = AtelierPlugin.getPlugin();
        try (final InputStream inputstream = new FileInputStream(new File(plugin.getDataFolder(), "discord.properties"))) {
            final Properties prop = new Properties();
            prop.load(inputstream);
            discordBotToken = prop.getProperty("token");
            chatChannelId = CObjects.nullIfFunction(prop.getProperty("chatChannelId"), Long::parseLong, 0L);
            webhookUrl = prop.getProperty("webhookUrl");
        } catch (FileNotFoundException ignored) {
        } catch (IOException ex) {
            CommonUtils.logWarning(ex);
        }
        if (discordBotToken == null) {
            CommonUtils.log("token is not found. [Discord]");
        } else {
            CommonUtils.log("load properties file [Discord]");
            try {
                CommonUtils.log("building... [Discord]");
                jda = JDABuilder.createDefault(discordBotToken)
                        .addEventListeners(new ListenerAdapter() {
                            @Override
                            public void onPrivateMessageReceived(@NotNull PrivateMessageReceivedEvent event) {
                                dmCheckDiscordToken(event);
                            }

                            @Override
                            public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
                                DiscordManager.INSTANCE.onGuildMessageReceived(event);
                            }
                        }).build().awaitReady();
                if (chatChannelId != 0) {
                    chatChannel = jda.getTextChannelById(chatChannelId);
                }
                CommonUtils.log("successful build [Discord]");
            } catch (LoginException | InterruptedException ex) {
                CommonUtils.logWarning(ex);
            }
        }
        LoopManager.INSTANCE.addSeconds(() -> {
            final long now = System.currentTimeMillis();
            tokens.keySet().stream()
                    .filter(player -> now - tokens.get(player).rightLong() >= 180000)
                    .forEach(tokens::remove);
        });
        load();
        CommonUtils.log("successful setup [Discord]");
    }

    public void stop() {
        jda.shutdownNow();
    }

    private void load() {
        final List<List<Object>> saveTypesObj = SQLManager.INSTANCE.select(
                "discordview",
                new String[]{"userId", "discordId", "uuid"},
                null
        );
        if (!saveTypesObj.isEmpty()) {
            saveTypesObj.forEach(datas -> {
                final int userId = (int) datas.get(0);
                statusList.put(userId, new DiscordStatus(
                        userId,
                        (long) datas.get(1), // discordId
                        UUID.fromString((String) datas.get(2)) // uuid
                ));
            });
        }
    }

    @NotNull
    public DiscordStatus getDiscordStatus(int userId, UUID uuid) {
        final DiscordStatus status;
        if (statusList.containsKey(userId)) {
            status = statusList.get(userId);
        } else {
            status = new DiscordStatus(userId, uuid);
            statusList.put(userId, status);
        }
        return status;
    }

    @Nullable
    public DiscordStatus getDiscordStatus(long discordId) {
        for (final DiscordStatus status : statusList.values()) {
            if (discordId == status.getDiscordId()) {
                return status;
            }
        }
        return null;
    }

    @Nullable
    public DiscordStatus getDiscordStatus(UUID uuid) {
        for (final DiscordStatus status : statusList.values()) {
            if (uuid.equals(status.getUUID())) {
                return status;
            }
        }
        return null;
    }

    @Nullable
    public Member getDiscordMember(long discordId) {
        final User user = jda.getUserById(discordId);
        System.out.println("getdiscordmember " + user);
        if (user != null) {
            System.out.println("getdiscordmember " + chatChannel.getGuild().getMember(user));
            return chatChannel.getGuild().getMember(user);
        }
        return null;
    }

    public void sendMessage(String message) {
        Objects.requireNonNull(chatChannel);
        chatChannel.sendMessage(message).queue();
    }

    public void sendWebhookMessage(String name, String avatarUrl, String content) {
        try (final WebhookClient client = WebhookClient.withUrl(webhookUrl)) {
            client.send(new WebhookMessageBuilder().setUsername(name).setAvatarUrl(avatarUrl).setContent(content).build());
        }
    }

    public void sendDiscordToken(Player player) {
        final String token = UUID.randomUUID().toString();
        tokens.put(player, new ObjectLongImmutablePair<>(token, System.currentTimeMillis()));
        player.sendMessage("Discord内のFirizLab(Bot)に下記コマンドをDMで送信してください。");
        final String command = "/token " + token + " " + player.getName();
        player.sendMessage(
                Text.of(command)
                        .clickEvent(ClickEvent.copyToClipboard(command))
                        .hoverEvent(HoverEvent.showText(Text.of("クリップボードにコピー")))
        );
    }

    private void dmCheckDiscordToken(PrivateMessageReceivedEvent event) {
        final Message message = event.getMessage();
        final StringTokenizer tokenizer = new StringTokenizer(message.getContentRaw());
        if (tokenizer.countTokens() >= 3 && "/token".equals(tokenizer.nextToken())) {
            final String token = tokenizer.nextToken();
            final String playerName = tokenizer.nextToken();
            final Player player = Bukkit.getPlayer(playerName);
            final String text;
            if (player == null) {
                text = playerName + " はオンラインではありません";
            } else {
                if (tokens.containsKey(player)) {
                    final var tokenData = tokens.get(player);
                    if (tokenData != null && token.equals(tokenData.left())) {
                        final Char character = PlayerSaveManager.INSTANCE.getChar(player);
                        character.getDiscordStatus().setDiscordId(event.getAuthor().getIdLong());
                        character.getDiscordStatus().save();
                        text = "承認されました";
                    } else {
                        text = "エラーが発生しました";
                    }
                } else {
                    text = "トークンの有効期限が切れています";
                }
                player.sendMessage(text);
            }
            event.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(text).queue());
        }
    }

    private void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        final User author = event.getAuthor();
        if (!event.isWebhookMessage() && !author.isBot() && event.getMessage().getChannel().getIdLong() == chatChannelId) {
            String name = Objects.requireNonNull(event.getMember()).getEffectiveName();
            final DiscordStatus status = getDiscordStatus(author.getIdLong());
            if (status != null) {
                final String playerName = status.getPlayerName();
                if (playerName != null) {
                    name += "(" + playerName + ")";
                }
            }
            Chat.discordChat(name, Text.of(event.getMessage().getContentDisplay()));
        }
    }

    public void sendMessage(Player player, String message) {
        final Char character = PlayerSaveManager.INSTANCE.getChar(player);
        if (character != null) {
            final long discordId = character.getDiscordStatus().getDiscordId();
            if (discordId != 0) {
                findMember(discordId, member -> {
                    if (member == null) {
                        sendWebhookMessage(player.getName(), null, message);
                    } else {
                        sendWebhookMessage(
                                String.format("%s (%s)", member.getEffectiveName(), player.getName()),
                                member.getUser().getAvatarUrl(),
                                message
                        );
                    }
                });
                return;
            }
            sendWebhookMessage(player.getName(), null, message);
        }
    }

    public void findMember(long discordId, Consumer<Member> consumer) {
        chatChannel.getGuild().retrieveMemberById(discordId).queue(consumer);
    }
}
