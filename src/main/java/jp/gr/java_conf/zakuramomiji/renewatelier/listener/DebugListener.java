/*
 * DebugListener.java
 * 
 * Copyright (c) 2018 firiz.
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
package jp.gr.java_conf.zakuramomiji.renewatelier.listener;

import de.tr7zw.itemnbtapi.NBTItem;
import java.util.UUID;
import java.util.logging.Level;
import jp.gr.java_conf.zakuramomiji.renewatelier.AtelierPlugin;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.material.AlchemyMaterial;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.recipe.AlchemyRecipe;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.recipe.RecipeStatus;
import jp.gr.java_conf.zakuramomiji.renewatelier.config.ConfigManager;
import jp.gr.java_conf.zakuramomiji.renewatelier.item.AlchemyItemStatus;
import jp.gr.java_conf.zakuramomiji.renewatelier.item.bag.AlchemyBagItem;
import jp.gr.java_conf.zakuramomiji.renewatelier.npc.NPCManager;
import jp.gr.java_conf.zakuramomiji.renewatelier.player.PlayerSaveManager;
import jp.gr.java_conf.zakuramomiji.renewatelier.player.PlayerStatus;
import jp.gr.java_conf.zakuramomiji.renewatelier.sql.SQLManager;
import jp.gr.java_conf.zakuramomiji.renewatelier.utils.Chore;
import jp.gr.java_conf.zakuramomiji.renewatelier.world.MyRoomManager;
import net.minecraft.server.v1_13_R2.ChatMessage;
import net.minecraft.server.v1_13_R2.EntityPlayer;
import net.minecraft.server.v1_13_R2.PacketPlayOutOpenWindow;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author firiz
 */
public class DebugListener implements Listener {

    private boolean nonbreak = true;

//    @EventHandler
//    private void projectileHit(final ProjectileHitEvent e) {
//        final Projectile projectile = e.getEntity();
//        final ProjectileSource shooter = projectile.getShooter();
//        if (shooter != null && shooter instanceof Player) {
//            if (projectile.getCustomName() != null && projectile.getCustomName().equals("arrowrain")) {
//                projectile.remove();
//            } else {
//                Location location = null;
//                if (e.getHitBlock() != null) {
//                    location = e.getHitBlock().getLocation();
//                } else if (e.getHitEntity() != null) {
//                    location = e.getHitEntity().getLocation();
//                }
//                
//                if (location != null) {
//                    final List<Location> locs = new ArrayList<>();
//                    final Location loc = location;
//                    loc.setY(loc.getY() + 8);
//                    loc.setZ(loc.getZ() + 2);
//                    loc.setX(loc.getX() + 2);
//                    for (int j = 0; j < 5; j++) {
//                        for (int l = 0; l < 5; l++) {
//                            loc.setX(loc.getX() - 1);
//                            locs.add(loc.clone());
//                        }
//                        loc.setX(loc.getX() + 5);
//                        loc.setZ(loc.getZ() - 1);
//                    }
//                    
//                    for (int i = 0; i < 30; i++) {
//                        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(AtelierPlugin.getPlugin(), () -> {
//                            Collections.shuffle(locs);
//                            final Arrow arrow = (Arrow) loc.getWorld().spawnEntity(locs.get(0), EntityType.ARROW);
//                            arrow.setCustomName("arrowrain");
//                            arrow.setShooter(shooter);
//                        }, 2 * i);
//                    }
//                }
//            }
//        }
//    }

    @EventHandler
    private void debug(final AsyncPlayerChatEvent e) {
        if (!e.getPlayer().isOp()) {
            return;
        }
        e.setCancelled(true);
        try {
            final String strs[] = e.getMessage().split(" ");
            switch (strs[0].trim().toLowerCase()) {
                case "aaa": {
                    final ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
                    final ItemMeta meta = item.getItemMeta();
                    meta.addAttributeModifier(
                            Attribute.GENERIC_ATTACK_DAMAGE,
                            new AttributeModifier(
                                    UUID.randomUUID(),
                                    "attackDamage",
                                    10,
                                    AttributeModifier.Operation.ADD_NUMBER,
                                    EquipmentSlot.HAND
                            )
                    );
                    item.setItemMeta(meta);
                    break;
                }
                case "debug": {
                    final ItemStack item = Chore.createDamageableItem(Material.DIAMOND_AXE, 1, 1524);
                    e.getPlayer().getInventory().addItem(item);
                    break;
                }
                case "item": {
                    final ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
                    AlchemyItemStatus.getItem(strs[1], item);
                    break;
                }
                case "select": {
                    final String[] split2 = split(strs[2]);
                    final int length;
                    if (strs.length > 4) {
                        length = Integer.parseInt(strs[4]);
                    } else {
                        length = split2.length;
                    }
                    final String[] split3 = split(strs[3]);
                    for (int i = 0; i < split3.length; i++) {
                        if (split3[i].equals("uuid")) {
                            split3[i] = e.getPlayer().getUniqueId().toString();
                        }
                    }
                    SQLManager.INSTANCE.select(strs[1], split2, split3, length).forEach((a) -> {
                        Chore.log(a.toString());
                    });
                    break;
                }
                case "insert": {
                    final String[] split3 = split(strs[3]);
                    for (int i = 0; i < split3.length; i++) {
                        if (split3[i].equals("uuid")) {
                            split3[i] = e.getPlayer().getUniqueId().toString();
                        }
                    }
                    SQLManager.INSTANCE.insert(strs[1], split(strs[2]), split3);
                    break;
                }
                case "delete": {
                    final String[] split3 = split(strs[3]);
                    for (int i = 0; i < split3.length; i++) {
                        if (split3[i].equals("uuid")) {
                            split3[i] = e.getPlayer().getUniqueId().toString();
                        }
                    }
                    SQLManager.INSTANCE.delete(strs[1], split(strs[2]), split3);
                    break;
                }
                case "disable": {
                    AtelierPlugin.getPlugin().getPluginLoader().disablePlugin(AtelierPlugin.getPlugin());
                    break;
                }
                case "break": {
                    nonbreak = !nonbreak;
                    break;
                }
                case "anvil": {
                    final Inventory inv = Bukkit.createInventory(
                            e.getPlayer(), InventoryType.ANVIL
                    );
                    e.getPlayer().openInventory(inv);
                    update(e.getPlayer(), strs[1], inv, "minecraft:anvil");
                    break;
                }
                case "inv": {
                    final Inventory inv = Bukkit.createInventory(e.getPlayer(), Integer.parseInt(strs[1]), strs[2]);
                    e.getPlayer().openInventory(inv);
                    e.getPlayer().sendMessage(inv.getTitle());
                    break;
                }
                case "islandcreate": {
                    Bukkit.getScheduler().runTask(AtelierPlugin.getPlugin(), () -> {
                        final UUID uuid = UUID.randomUUID();
                        MyRoomManager.INSTANCE.createRoom(uuid);
                        MyRoomManager.INSTANCE.warpRoom(e.getPlayer(), uuid);
                    });
                    break;
                }
                case "addrecipe": {
                    final PlayerStatus status = PlayerSaveManager.INSTANCE.getStatus(e.getPlayer().getUniqueId());
                    final AlchemyRecipe search = AlchemyRecipe.search(strs[1]);
                    if (search != null) {
                        final RecipeStatus rs = status.getRecipeStatus(strs[1]);
                        if (rs == null) {
                            status.addRecipe(new RecipeStatus(search.getId(), 0, 0));
                        } else if (strs.length > 2) {
                            status.addRecipeExp(strs[1], Integer.parseInt(strs[2]));
                        }
                    }
                    break;
                }
                case "configreload": {
                    ConfigManager.INSTANCE.reloadConfigs();
                    break;
                }
                case "bag": {
                    final AlchemyMaterial am = AlchemyMaterial.getMaterial(strs[1]);
                    if (am != null) {
                        Chore.addItem(e.getPlayer(), new AlchemyBagItem(am).getItem());
                    }
                    break;
                }
                case "viewlore": {
                    final ItemStack mainHand = e.getPlayer().getInventory().getItemInMainHand();
                    if (mainHand != null && mainHand.hasItemMeta()) {
                        final ItemMeta meta = mainHand.getItemMeta();
                        if (meta.hasLore()) {
                            e.getPlayer().sendMessage(meta.getLore().toString());
                            Chore.log(meta.getLore());
                        }
                    }
                    break;
                }
                case "test": {
                    final ItemStack mainHand = e.getPlayer().getInventory().getItemInMainHand();
                    if (mainHand != null && mainHand.hasItemMeta()) {
                        final ItemMeta meta = mainHand.getItemMeta();
                        if (meta.hasLore()) {
                            final ItemStack item = new ItemStack(Material.STONE, 1);
                            final NBTItem nbti = new NBTItem(item);
                            nbti.setString("ddata", meta.getLore().toString());
                            Chore.addItem(e.getPlayer(), nbti.getItem());
                        }
                    }
                    break;
                }
                case "getnbt": {
                    final ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
                    if (item != null) {
                        final NBTItem nbti = new NBTItem(item);
                        Chore.log(nbti.getString(strs[1]));
                    }
                    break;
                }
                case "debugdrop": {
                    final int val = Integer.parseInt(strs[1]);
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(AtelierPlugin.getPlugin(), () -> {
                        for (int i = 0; i < val; i++) {
                            final ItemStack item = AlchemyItemStatus.getItem("kaen_stone", new ItemStack(Material.STONE));
                            Chore.drop(e.getPlayer().getLocation(), item);
                        }
                    }, 20);
                    break;
                }
                case "npc": { // npc <name> <script> <entityType>
                    NPCManager.INSTANCE.createNPC(
                            e.getPlayer().getLocation(),
                            EntityType.valueOf(strs[3]),
                            strs[1],
                            strs[2]
                    );
                    break;
                }
                case "player": {
                    NPCManager.INSTANCE.createNPCPlayer(
                            e.getPlayer().getLocation(),
                            strs[1],
                            UUID.fromString(strs[3]),
                            strs[2]
                    );
                    break;
                }
                case "save_player": {
                    NPCManager.INSTANCE.createNPCPlayer(
                            e.getPlayer().getLocation(),
                            strs[1],
                            UUID.fromString(strs[3]),
                            strs[2],
                            true
                    );
                    break;
                }
                default: {
                    e.setCancelled(false);
                    break;
                }
            }
        } catch (Exception ex) {
            e.getPlayer().sendMessage(ex.getMessage());
            Chore.log(Level.SEVERE, null, ex);
        }
    }

    @EventHandler
    private void debugServer(final ServerCommandEvent e) {
        final String strs[] = e.getCommand().split(" ");
        switch (strs[0].trim().toLowerCase()) {
            case "debugdrop": {
                final Player player = Bukkit.getServer().getPlayer(strs[1]);
                final int val = Integer.parseInt(strs[2]);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(AtelierPlugin.getPlugin(), () -> {
                    for (int i = 0; i < val; i++) {
                        final ItemStack item = AlchemyItemStatus.getItem("kaen_stone", new ItemStack(Material.STONE));
                        Chore.drop(player.getLocation(), item);
                    }
                }, 20);
                break;
            }
        }
    }

    private void update(final Player player, final String title, final Inventory inv, final String id) {
        EntityPlayer ep = ((CraftPlayer) player).getHandle();
        PacketPlayOutOpenWindow packet = new PacketPlayOutOpenWindow(
                ep.activeContainer.windowId,
                id,
                new ChatMessage(title),
                inv.getSize()
        );
        ep.playerConnection.sendPacket(packet);
        ep.updateInventory(ep.activeContainer);
    }

    @EventHandler
    public final void blockbreak(BlockBreakEvent e) {
        e.setCancelled(nonbreak);
    }

    private String[] split(final String str) {
        if (str.contains(",")) {
            return str.split(",");
        }
        return new String[]{str};
    }

}
