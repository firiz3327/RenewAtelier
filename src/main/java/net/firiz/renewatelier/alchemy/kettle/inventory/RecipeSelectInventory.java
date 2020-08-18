package net.firiz.renewatelier.alchemy.kettle.inventory;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.firiz.renewatelier.alchemy.RequireAmountMaterial;
import net.firiz.renewatelier.alchemy.kettle.KettleManager;
import net.firiz.renewatelier.alchemy.material.AlchemyMaterial;
import net.firiz.renewatelier.alchemy.recipe.AlchemyRecipe;
import net.firiz.renewatelier.alchemy.recipe.RecipeLevelEffect;
import net.firiz.renewatelier.alchemy.recipe.RecipeStatus;
import net.firiz.renewatelier.alchemy.recipe.result.ARecipeResult;
import net.firiz.renewatelier.alchemy.recipe.result.AlchemyMaterialRecipeResult;
import net.firiz.renewatelier.alchemy.recipe.result.MinecraftMaterialRecipeResult;
import net.firiz.renewatelier.constants.GameConstants;
import net.firiz.renewatelier.entity.player.Char;
import net.firiz.renewatelier.entity.player.sql.load.PlayerSaveManager;
import net.firiz.renewatelier.inventory.AlchemyInventoryType;
import net.firiz.renewatelier.inventory.Appraisal;
import net.firiz.renewatelier.inventory.manager.InventoryManager;
import net.firiz.renewatelier.inventory.manager.ParamInventory;
import net.firiz.renewatelier.item.CustomModelMaterial;
import net.firiz.renewatelier.utils.CommonUtils;
import net.firiz.renewatelier.utils.chores.ItemUtils;
import net.firiz.renewatelier.utils.pair.ImmutablePair;
import net.firiz.renewatelier.version.packet.InventoryPacket;
import net.firiz.renewatelier.version.packet.InventoryPacket.InventoryPacketType;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author firiz
 */
public final class RecipeSelectInventory implements ParamInventory<Location> {

    private static final NamespacedKey locationKey = CommonUtils.createKey("location");
    private static final NamespacedKey recipeKey = CommonUtils.createKey("recipe");
    private static final String STRING_RECIPE = "レシピを選択してください。";
    private static final String STRING_MATERIAL = "material:";
    private final InventoryManager manager;
    private final List<String> recipeLore;

    public RecipeSelectInventory(final InventoryManager manager) {
        this.manager = manager;
        recipeLore = new ObjectArrayList<>();
        recipeLore.add(ChatColor.WHITE + STRING_RECIPE);
        recipeLore.add("");
    }

    @Override
    public boolean check(@NotNull final InventoryView view) {
        return view.getTitle().equals(AlchemyInventoryType.KETTLE_SELECT_RECIPE.getCheck());
    }

    @Override
    public void open(@NotNull final Player player, @NotNull final Location loc) {
        final Inventory inv = Bukkit.createInventory(player, 54, AlchemyInventoryType.KETTLE_SELECT_RECIPE.getCheck());
        inv.setItem(0, ItemUtils.ci(Material.DIAMOND_AXE, 1522, "", recipeLore)); // 外見上
        inv.setItem(45, ItemUtils.ci(Material.DIAMOND_AXE, 1562, "", null)); // 外見下
        inv.setItem(43, ItemUtils.ci(Material.ENCHANTED_BOOK, 0, ChatColor.GREEN + "鑑定", null)); // 鑑定ボタン
        inv.setItem(1, ItemUtils.setSetting(ItemUtils.ci(Material.BARRIER, 0, "", null), KettleConstants.scrollKey, 0)); // スクロール
        setLocation(inv, loc);

        setRecipeScroll(player.getUniqueId(), inv, 0);
        player.openInventory(inv);
        InventoryPacket.update(player, "", InventoryPacketType.CHEST);
    }

    private void addRecipeStatus(final UUID uuid, final AlchemyRecipe recipe, final RecipeStatus recipeStatus, final ItemMeta meta, final List<String> lore) {
        final Char status = PlayerSaveManager.INSTANCE.getChar(uuid);
        setRecipe(meta, recipe);
        lore.add(ChatColor.GRAY + "必要錬金レベル: " + (status.getCharStats().getAlchemyLevel() >= recipe.getReqAlchemyLevel() ? ChatColor.GREEN : "") + recipe.getReqAlchemyLevel());

        int addAmount = 0; // 熟練度による作成個数増加
        final int level = recipeStatus.getLevel();
        if (level != 0) {
            lore.add(ChatColor.GRAY + "熟練度: ".concat(GameConstants.RANK_RECIPE[level]));
            lore.add(getRecipeExpBar(recipeStatus));

            final List<RecipeLevelEffect> recipeLevelEffects = recipe.getLevels().get(recipeStatus.getLevel());
            if (recipeLevelEffects != null && !recipeLevelEffects.isEmpty()) {
                for (final RecipeLevelEffect rle : recipeLevelEffects) {
                    final RecipeLevelEffect.RecipeLEType type = rle.getType();
                    lore.add(ChatColor.GRAY + "- ".concat(type.getName()).concat(type.isViewNumber() ? " +".concat(String.valueOf(rle.getCount(type))) : ""));
                    if (type == RecipeLevelEffect.RecipeLEType.ADD_AMOUNT) {
                        addAmount += rle.getCount();
                    }
                }
            } else {
                lore.add(ChatColor.GRAY + "熟練度効果なし");
            }
        } else {
            lore.add(ChatColor.GRAY + "熟練度なし");
        }
        lore.add("");

        lore.add(ChatColor.GRAY + "作成量: " + (recipe.getAmount() + addAmount));
        lore.add(ChatColor.GRAY + "必要素材:");
        for (final RequireAmountMaterial req : recipe.getReqMaterial()) {
            switch (req.getType()) {
                case CATEGORY:
                    lore.add("§7- " + ChatColor.stripColor(req.getCategory().getName()) + " × " + req.getAmount());
                    break;
                case MATERIAL:
                    lore.add("§7- " + ChatColor.stripColor(req.getMaterial().getName()) + " × " + req.getAmount());
                    break;
                default: // 想定しない
                    break;
            }
        }
    }

    private void setRecipeScroll(final UUID uuid, final Inventory inv, final int scroll) {
        final List<ImmutablePair<RecipeStatus, CustomModelMaterial>> recipeItems = new ObjectArrayList<>();
        final Char status = PlayerSaveManager.INSTANCE.getChar(uuid);
        status.getRecipeStatusList().stream().filter(RecipeStatus::isAcquired).forEach(recipeStatus -> {
            final ARecipeResult<?> resultData = recipeStatus.getRecipe().getResult();
            final CustomModelMaterial material = resultData.getCustomModelMaterial();
            recipeItems.add(new ImmutablePair<>(recipeStatus, material));
        });
        recipeItems.sort(Comparator.comparing((ImmutablePair<RecipeStatus, CustomModelMaterial> o) -> o.getLeft().getId()));
        final int dScroll = scroll * 6;
        if (recipeItems.size() > dScroll) {
            setScroll(inv, scroll);
            int slot = 9;
            deleteInv:
            for (int i = slot; i < slot + 24; i++) {
                inv.setItem(slot, null);
                switch (slot) {
                    case 14:
                    case 23:
                    case 32:
                        slot += 4;
                        break;
                    case 41:
                        break deleteInv;
                    default:
                        slot++;
                        break;
                }
            }

            slot = 9;
            for (int i = dScroll; i < dScroll + 24; i++) {
                if (recipeItems.size() <= i) {
                    break;
                }
                final ImmutablePair<RecipeStatus, CustomModelMaterial> dd = recipeItems.get(i);
                final CustomModelMaterial material = dd.getRight();
                final RecipeStatus rs = dd.getLeft();
                final AlchemyRecipe recipe = rs.getRecipe();
                final RecipeStatus recipeStatus = status.getRecipeStatus(recipe.getId());

                assert recipeStatus != null;
                final ItemStack item = recipeStatus.getLevel() == 0 ? new ItemStack(Material.FILLED_MAP) : material.toItemStack();

                final ItemMeta meta = item.getItemMeta();
                final List<String> lore = new ObjectArrayList<>();
                addRecipeStatus(uuid, recipe, rs, meta, lore);
                if (recipe.getResult() instanceof AlchemyMaterialRecipeResult) {
                    final AlchemyMaterial alchemyMaterial = ((AlchemyMaterialRecipeResult) recipe.getResult()).getResult();
                    setNameAndFlags(meta, alchemyMaterial);
                }
                lore.add("");
                meta.setLore(lore);
                item.setItemMeta(meta);

                inv.setItem(slot, item);
                switch (slot) {
                    case 14:
                    case 23:
                    case 32:
                        slot += 4;
                        break;
                    case 41:
                        return;
                    default:
                        slot++;
                        break;
                }
            }
        }
    }

    private void setNameAndFlags(final ItemMeta meta, final AlchemyMaterial material) {
        if (material != null) {
            if (!material.isDefaultName()) {
                meta.setDisplayName(material.getName());
            }
            ItemUtils.addHideFlags(meta, material);
        }
    }

    @Override
    public void onClick(@NotNull final InventoryClickEvent e) {
        if (e.getAction() == InventoryAction.COLLECT_TO_CURSOR || e.isShiftClick()) { // 増殖防止 || アイテム混入防止
            e.setCancelled(true);
            return;
        }
        final Inventory inv = e.getInventory();
        final int raw = e.getRawSlot();
        if (raw >= 0 && raw < inv.getSize()) {
            e.setCancelled(true);
        }
        if (e.getSlotType() == SlotType.CONTAINER && inv.getItem(1) != null) {
            final Player player = (Player) e.getWhoClicked();
            final int scroll = getScroll(inv);
            final ItemStack item = e.getCurrentItem();

            switch (raw) {
                case 43:
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.1f, 1);
                    manager.getInventory(Appraisal.class).open(player);
                    break;
                case 46:
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.1f, 1);
                    setRecipeScroll(player.getUniqueId(), inv, Math.max(0, scroll - 1));
                    break;
                case 49:
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.1f, 1);
                    setRecipeScroll(player.getUniqueId(), inv, scroll + 1);
                    break;
                case 25:
                    if (item != null) {
                        final AlchemyRecipe recipe = getRecipe(item);
                        if (ItemUtils.hasMaterial(player.getInventory(), recipe.getReqMaterial())) {
                            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.1f, 1);
                            KettleManager.INSTANCE.create(player.getUniqueId(), getLocation(inv), recipe);
                            manager.getInventory(ItemSelectInventory.class).open(player, recipe, inv);
                            return;
                        }
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.1f, 1);
                    }
                    break;
                default:
                    if (raw >= 9 && raw <= 14
                            || raw >= 18 && raw <= 23
                            || raw >= 27 && raw <= 32
                            || raw >= 36 && raw <= 41) {
                        if (item != null && item.getType() != Material.AIR && item.hasItemMeta()) {
                            final AlchemyRecipe recipe = getRecipe(item);
                            final Char status = PlayerSaveManager.INSTANCE.getChar(player.getUniqueId());
                            if (!ItemUtils.hasMaterial(player.getInventory(), recipe.getReqMaterial())) {
                                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.1f, 1);
                                break;
                            } else {
                                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.1f, 1);
                            }

                            final ARecipeResult<?> resultData = recipe.getResult();
                            AlchemyMaterial alchemyMaterial = null;
                            CustomModelMaterial material = null;
                            if (resultData instanceof AlchemyMaterialRecipeResult) {
                                alchemyMaterial = ((AlchemyMaterialRecipeResult) resultData).getResult();
                                material = alchemyMaterial.getMaterial();
                            } else if (resultData instanceof MinecraftMaterialRecipeResult) {
                                material = resultData.getCustomModelMaterial();
                            }

                            final List<String> lore = new ObjectArrayList<>();
                            lore.add(ChatColor.WHITE + "  を作成します。");
                            final RecipeStatus recipeStatus = status.getRecipeStatus(recipe.getId());
                            if (recipeStatus != null && material != null) {
                                final ItemStack resultItem = recipeStatus.getLevel() == 0 ? new ItemStack(Material.FILLED_MAP, recipe.getAmount()) : material.toItemStack();
                                final ItemMeta meta = resultItem.getItemMeta();
                                addRecipeStatus(player.getUniqueId(), recipe, recipeStatus, meta, lore);
                                setNameAndFlags(meta, alchemyMaterial);
                                meta.setLore(lore);
                                resultItem.setItemMeta(meta);
                                inv.setItem(25, resultItem);
                            }
                        } else {
                            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.1f, 1);
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public void onDrag(@NotNull final InventoryDragEvent e) {
        final Set<Integer> raws = e.getRawSlots();
        final Inventory inv = e.getInventory();
        raws.stream().filter(raw -> (raw >= 0 && raw < inv.getSize())).forEach(itemValue -> e.setCancelled(true));
    }

    public int getScroll(Inventory inv) {
        return ItemUtils.getSettingInt(Objects.requireNonNull(inv.getItem(1)), KettleConstants.scrollKey);
    }

    public void setScroll(Inventory inv, int scroll) {
        ItemUtils.setSetting(Objects.requireNonNull(inv.getItem(1)), KettleConstants.scrollKey, scroll);
    }

    public Location getLocation(Inventory inv) {
        final String[] data = ItemUtils.getSetting(Objects.requireNonNull(inv.getItem(1)), locationKey).split(",");
        return new Location(
                Bukkit.getWorld(data[0]),
                Double.parseDouble(data[1]),
                Double.parseDouble(data[2]),
                Double.parseDouble(data[3])
        );
    }

    public void setLocation(Inventory inv, Location location) {
        final StringJoiner joiner = new StringJoiner(",");
        joiner.add(location.getWorld().getName())
                .add(String.valueOf(location.getX()))
                .add(String.valueOf(location.getY()))
                .add(String.valueOf(location.getZ()));
        ItemUtils.setSetting(Objects.requireNonNull(inv.getItem(1)), locationKey, joiner.toString());
    }

    public AlchemyRecipe getRecipe(ItemStack item) {
        return AlchemyRecipe.search(ItemUtils.getSetting(item, recipeKey));
    }

    public void setRecipe(ItemMeta meta, AlchemyRecipe recipe) {
        CommonUtils.setSetting(meta, recipeKey, recipe.getId());
    }

    public String getRecipeExpBar(final RecipeStatus recipeStatus) {
        final StringBuilder sb = new StringBuilder();
        if (recipeStatus.getLevel() != 4) {
            final int expPer = (int) (100 * ((double) recipeStatus.getExp() / GameConstants.RECIPE_REQ_EXPS[recipeStatus.getLevel()]));
            for (int j = 0; j < 100; j++) {
                sb.append(expPer > j ? ChatColor.GREEN : ChatColor.WHITE).append("|");
            }
        } else {
            sb.append(ChatColor.GREEN).append("||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
        }
        return sb.toString();
    }
}
