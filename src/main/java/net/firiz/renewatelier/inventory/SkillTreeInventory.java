package net.firiz.renewatelier.inventory;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.firiz.ateliercommonapi.adventure.text.Text;
import net.firiz.renewatelier.entity.player.sql.load.PlayerSaveManager;
import net.firiz.renewatelier.inventory.manager.NonParamInventory;
import net.firiz.renewatelier.skills.character.tree.SkillTree;
import net.firiz.renewatelier.utils.CommonUtils;
import net.firiz.renewatelier.utils.minecraft.ItemUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SkillTreeInventory implements NonParamInventory {

    private final int MIN_X = -8;
    private final int MIN_Y = -8;
    private final int MAX_X = 8;
    private final int MAX_Y = 8;
    private final NamespacedKey xKey = CommonUtils.createKey("x");
    private final NamespacedKey yKey = CommonUtils.createKey("y");
    private final Text title = new Text("設定");

    @Override
    public boolean check(@NotNull InventoryView view) {
        return view.title().equals(title);
    }

    @Override
    public void open(@NotNull Player player) {
        final Inventory inv = Bukkit.createInventory(player, 54, title);
        inv.setItem(0, ItemUtils.setSetting(
                ItemUtils.setSetting(
                        ItemUtils.unavailableItem(Material.BLACK_STAINED_GLASS_PANE, Component.empty()),
                        xKey,
                        0
                ),
                yKey,
                -1
        ));
        final SkillTree skillTree = PlayerSaveManager.INSTANCE.getChar(player).getCharStats().getSkillManager().getSkillTree();
        setBlocks(inv, skillTree.getBlocks());
        player.openInventory(inv);
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        event.setCancelled(true);
        final Player player = (Player) event.getWhoClicked();
        final SkillTree skillTree = PlayerSaveManager.INSTANCE.getChar(player).getCharStats().getSkillManager().getSkillTree();
        final Inventory inv = event.getInventory();
        final ItemStack setting = inv.getItem(0);
        assert setting != null;
        final int x = ItemUtils.getSettingInt(setting, xKey);
        final int y = ItemUtils.getSettingInt(setting, yKey);
        switch (event.getRawSlot()) {
            case 1, 7: // up
                ItemUtils.setSetting(setting, yKey, Math.max(y - 1, MIN_Y));
                break;
            case 9, 36: // left
                ItemUtils.setSetting(setting, xKey, Math.max(x - 1, MIN_X));
                break;
            case 17, 44: // right
                ItemUtils.setSetting(setting, xKey, Math.min(x + 1, MAX_X));
                break;
            case 46, 52: // down
                ItemUtils.setSetting(setting, yKey, Math.min(y + 1, MAX_Y));
                break;
            default:
                // ignored
                break;
        }
        setBlocks(inv, skillTree.getBlocks());
        player.updateInventory();
    }

    private void setBlocks(Inventory inv, SkillTree.Block[] blocks) {
        final ItemStack setting = inv.getItem(0);
        if (ItemUtils.hasSettingInt(setting, xKey)) {
            final int x = ItemUtils.getSettingInt(setting, xKey);
            final int y = ItemUtils.getSettingInt(setting, yKey);
            final int pos = SkillTree.CENTER + x + (y * SkillTree.WIDTH);
            final IntList slots = slots(SkillTree.WIDTH, pos, 9, 6);
            for (int i = 1; i < 54; i++) {
                switch (i) {
                    case 1, 7 -> inv.setItem(i, ItemUtils.unavailableItem(Material.STICK, new Text("↑")));
                    case 9, 36 -> inv.setItem(i, ItemUtils.unavailableItem(Material.STICK, new Text("←")));
                    case 17, 44 -> inv.setItem(i, ItemUtils.unavailableItem(Material.STICK, new Text("→")));
                    case 46, 52 -> inv.setItem(i, ItemUtils.unavailableItem(Material.STICK, new Text("↓")));
                    case 8, 45, 53 -> inv.setItem(i, ItemUtils.unavailableItem(Material.BLACK_STAINED_GLASS_PANE, Component.empty()));
                    default -> {
                        final int index = slots.getInt(i);
                        if (index < 0 || blocks.length <= index) {
                            inv.setItem(i, null);
                        } else {
                            final SkillTree.Block block = blocks[index];
                            if (block == null) {
                                inv.setItem(i, null);
                            } else {
                                inv.setItem(i, block.createItem());
                            }
                        }
                    }
                }
            }
        }
    }

    public IntList slots(final int arrayWidth, final int center, final int width, final int height) {
        final IntList slotList = new IntArrayList();
        if (width % 2 == 0) {
            throw new IllegalArgumentException("The width must be an odd number.");
        }
        final int start = center - arrayWidth - ((width - 1) / 2);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                slotList.add(start + (j + (i * arrayWidth)));
            }
        }
        return slotList;
    }
}
