/*
 * QuestBook.java
 * 
 * Copyright (c) 2019 firiz.
 * 
 * This file is part of Expression program is undefined on line 6, column 40 in Templates/Licenses/license-licence-gplv3.txt..
 * 
 * Expression program is undefined on line 8, column 19 in Templates/Licenses/license-licence-gplv3.txt. is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Expression program is undefined on line 13, column 19 in Templates/Licenses/license-licence-gplv3.txt. is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Expression program is undefined on line 19, column 30 in Templates/Licenses/license-licence-gplv3.txt..  If not, see <http ://www.gnu.org/licenses/>.
 */
package jp.gr.java_conf.zakuramomiji.renewatelier.quest.book;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.ArrayList;
import java.util.List;
import jp.gr.java_conf.zakuramomiji.renewatelier.AtelierPlugin;
import jp.gr.java_conf.zakuramomiji.renewatelier.player.PlayerSaveManager;
import jp.gr.java_conf.zakuramomiji.renewatelier.player.PlayerStatus;
import jp.gr.java_conf.zakuramomiji.renewatelier.quest.Quest;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.minecraft.server.v1_13_R2.MinecraftKey;
import net.minecraft.server.v1_13_R2.PacketDataSerializer;
import net.minecraft.server.v1_13_R2.PacketPlayOutCustomPayload;
import net.minecraft.server.v1_13_R2.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

/**
 *
 * @author firiz
 */
public class QuestBook {

    public static void openQuestBook(final Player player, final ItemStack book, final EquipmentSlot hand) {
        final PlayerStatus status = PlayerSaveManager.INSTANCE.getStatus(player.getUniqueId());
        final List<Quest> progress_quests = new ArrayList<>();
        final List<Quest> clear_quests = new ArrayList<>();
        status.getQuestStatusList().forEach((qs) -> {
            final Quest quest = Quest.getQuest(qs.getId());
            if (qs.isClear()) {
                clear_quests.add(quest);
            } else {
                progress_quests.add(quest);
            }
        });

        final List<BaseComponent[]> pages = new ArrayList<>();
        progress_quests.forEach((quest) -> { // 進行中クエスト
            addSpigotPage(pages, quest, 0);
        });
        Quest.getImportantQuests().forEach((quest) -> { // 重要クエスト
            addSpigotPage(pages, quest, 2);
        });
        clear_quests.forEach((quest) -> { // クリア済みクエスト
            addSpigotPage(pages, quest, 1);
        });
        final BookMeta meta = (BookMeta) book.getItemMeta();
        meta.spigot().setPages(pages);
        book.setItemMeta(meta);

        // book open packet
        Bukkit.getScheduler().runTaskLater(AtelierPlugin.getPlugin(), () -> {
            final ByteBuf buf = Unpooled.buffer(256);
            buf.setByte(0, (byte) (hand == EquipmentSlot.HAND ? 0 : 1)); // hand
            buf.writerIndex(1);
            final PacketPlayOutCustomPayload payload = new PacketPlayOutCustomPayload(
                    new MinecraftKey("minecraft:book_open"),
                    new PacketDataSerializer(buf)
            );
            final PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
            playerConnection.sendPacket(payload);
        }, 5); // 0.25 sec
    }

    private static void addSpigotPage(final List<BaseComponent[]> pages, final Quest quest, final int type) {
        final String flag_text;
        final ChatColor color;
        switch (type) {
            case 0:
                flag_text = "進行中";
                color = ChatColor.BLUE;
                break;
            case 1:
                flag_text = "クリア済み";
                color = ChatColor.GREEN;
                break;
            default:
                flag_text = "未受注";
                color = ChatColor.RED;
                break;
        }
        ComponentBuilder builder = new ComponentBuilder("【")
                .append(quest.getName())
                .color(color)
                .event(new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder(flag_text).create()
                ))
                .append("】\n\n")
                .reset();
        for (final String line : quest.getDescription()) {
            builder = builder.append(line + "\n");
        }
        pages.add(builder.create());
    }

}
