/*
 * Nodification.java
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
package jp.gr.java_conf.zakuramomiji.renewatelier.nodification;

import jp.gr.java_conf.zakuramomiji.renewatelier.AtelierPlugin;
import jp.gr.java_conf.zakuramomiji.renewatelier.player.PlayerSaveManager;
import jp.gr.java_conf.zakuramomiji.renewatelier.player.minecraft.MinecraftRecipeSaveType;
import jp.gr.java_conf.zakuramomiji.renewatelier.utils.TellrawUtils;
import jp.gr.java_conf.zakuramomiji.renewatelier.version.VersionUtils;
import jp.gr.java_conf.zakuramomiji.renewatelier.version.packet.PayloadPacket;
import jp.gr.java_conf.zakuramomiji.renewatelier.version.packet.NodificationPacket;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BookMeta;

/**
 *
 * @author firiz
 */
public class Nodification {

    public static void loginNodification(final Player player) {
        final ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        final BookMeta meta = (BookMeta) book.getItemMeta();

        final ComponentBuilder builder = new ComponentBuilder("");
        builder.append("アップデート\n").color(ChatColor.GREEN);
        builder.append("オープンURL\n").event(
                TellrawUtils.createClickEvent(
                        ClickEvent.Action.OPEN_URL,
                        "https://www.google.com/"
                )
        );
        builder.append("ホバーテキスト\n").reset().event(
                TellrawUtils.createHoverEvent("テキスト")
        ).append("appendをつなげて書けるよ\n").reset().event(
                TellrawUtils.createHoverEvent(new ItemStack(Material.ACACIA_BOAT))
        );

        meta.spigot().setPages(builder.create());
        book.setItemMeta(meta);

        Bukkit.getScheduler().runTaskLater(AtelierPlugin.getPlugin(), () -> {
            if (player.isOnline()) {
                final PlayerInventory inv = player.getInventory();
                final ItemStack offHand = inv.getItemInOffHand();
                inv.setItemInOffHand(book);
                PayloadPacket.openBook(player, EquipmentSlot.OFF_HAND);
                inv.setItemInOffHand(offHand);
            }
        }, 20); // 1 sec
    }

    public static void recipeNodification(final Player player, final Material material) {
        recipeNodification(player, new ItemStack(material));
    }

    public static void recipeNodification(final Player player, final ItemStack item) {
        final String itemId = VersionUtils.asVItemCopy(item).getMinecraftId();
        NodificationPacket.sendRecipe(player, itemId, false); // add

        /*
        所持しているレシピを取得する方法がまったくわからないので、所持確認はSQLで管理する
        もしくは、そもそもパケットでレシピを得ても気にしない
         */
        if (!PlayerSaveManager.INSTANCE.getStatus(player.getUniqueId()).discoveredRecipe(
                MinecraftRecipeSaveType.search(itemId)
        )) {
            Bukkit.getScheduler().runTaskLater(AtelierPlugin.getPlugin(), () -> {
                NodificationPacket.sendRecipe(player, itemId, true); // remove
            }, 2); // 0.1 sec
        }
    }

    @Deprecated
    public static void advancementNodification(final Player player, final String id) {
        NodificationPacket.sendAdvancement(player, id, false); // add

        // java.lang.IllegalArgumentException: advancement
        if (!player.getAdvancementProgress(
                Bukkit.getAdvancement(new NamespacedKey(
                        AtelierPlugin.getPlugin(),
                        id.replace("minecraft:", "")
                ))
        ).isDone()) {
            Bukkit.getScheduler().runTaskLater(AtelierPlugin.getPlugin(), () -> {
                NodificationPacket.sendAdvancement(player, id, true); // remove
            }, 2); // 0.1 sec
        }
    }

}
