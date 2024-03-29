package net.firiz.renewatelier.server.script.conversation;

import java.util.List;
import java.util.UUID;
import javax.script.Invocable;
import javax.script.ScriptException;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.firiz.ateliercommonapi.adventure.text.Text;
import net.firiz.renewatelier.AtelierPlugin;
import net.firiz.renewatelier.alchemy.material.*;
import net.firiz.renewatelier.characteristic.Characteristic;
import net.firiz.renewatelier.inventory.Appraisal;
import net.firiz.renewatelier.inventory.ConfirmInventory;
import net.firiz.renewatelier.inventory.manager.InventoryManager;
import net.firiz.renewatelier.inventory.shop.ShopInventory;
import net.firiz.renewatelier.inventory.shop.ShopItem;
import net.firiz.renewatelier.inventory.item.json.itemeffect.AlchemyItemEffect;
import net.firiz.renewatelier.inventory.item.json.AlchemyItemStatus;
import net.firiz.renewatelier.entity.player.sql.load.PlayerSaveManager;
import net.firiz.renewatelier.entity.player.Char;
import net.firiz.renewatelier.quest.Quest;
import net.firiz.renewatelier.quest.QuestStatus;
import net.firiz.renewatelier.utils.CommonUtils;
import net.firiz.renewatelier.utils.minecraft.ItemUtils;
import net.firiz.renewatelier.world.MyRoomManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.graalvm.polyglot.HostAccess.Export;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author firiz
 */
public class ScriptConversation {

    protected static final InventoryManager inventoryManager = InventoryManager.INSTANCE;
    protected final String scriptName;
    protected final Player player;
    protected Invocable iv;

    public ScriptConversation(String scriptName, Player player) {
        this.scriptName = scriptName;
        this.player = player;
    }

    @Export
    public void log(final Object str) {
        CommonUtils.log(str);
    }

    @Export
    public void debug(final Object str) {
        CommonUtils.log(str);
        player.sendMessage(str.toString());
    }

    public String getScriptName() {
        return scriptName;
    }

    @Export
    public void setIv(final Invocable iv) {
        this.iv = iv;
    }

    @Export
    public Player getPlayer() {
        return player;
    }

    @Export
    public Player getPlayer(final UUID uuid) {
        return AtelierPlugin.getPlugin().getServer().getPlayer(uuid);
    }

    @Export
    public Char getStatus() {
        return getStatus(player.getUniqueId());
    }

    @Export
    public Char getStatus(final UUID uuid) {
        return PlayerSaveManager.INSTANCE.getChar(uuid);
    }

    @Export
    public QuestStatus getQuestStatus(final String questId) {
        return getQuestStatus(player.getUniqueId(), questId);
    }

    @Export
    public QuestStatus getQuestStatus(final UUID uuid, final String questId) {
        return getStatus(uuid).getQuestStatus(questId);
    }

    @Export
    public boolean hasQuest(final String questId) {
        return hasQuestUUID(player.getUniqueId(), questId);
    }

    @Export
    public boolean hasQuestUUID(final UUID uuid, final String questId) {
        return getQuestStatus(uuid, questId) != null;
    }

    @Export
    public boolean isQuestClear(final String questId) {
        return isQuestClear(player.getUniqueId(), questId);
    }

    @Export
    public boolean isQuestClear(final UUID uuid, final String questId) {
        return getQuestStatus(uuid, questId).isClear();
    }

    @Export
    public void questStart(final String questId) {
        questStart(questId, true);
    }

    @Export
    public void questStart(final String questId, final boolean view) {
        if (view) {
            getStatus().addQuest(questId);
        } else {
            getStatus().addQuest(new QuestStatus(questId));
        }
    }

    @Export
    public void questClear(final String questId) {
        questClear(questId, true);
    }

    @Export
    public void questClear(final String questId, final boolean view) {
        getStatus().questClear(questId, view);
    }

    @Export
    public Component getQuestName(final String questId) {
        return Quest.getQuest(questId).getName();
    }

    @Export
    public UUID getUUID(final String uuid) {
        return UUID.fromString(uuid);
    }

    @Export
    public Text text() {
        return Text.empty();
    }

    @Export
    public Text text(final String text) {
        return Text.of(text);
    }

    @Export
    public Component translate(final String key) {
        return Component.translatable(key);
    }

    @Export
    public Text chatColor(final String str) {
        return Text.translateColor(str);
    }

    @Export
    public String plain(Component component) {
        return Text.plain(component);
    }

    @Export
    public void warpRoom(final Player player) {
        MyRoomManager.INSTANCE.warpRoom(player);
    }

    @Export
    public void warpRoom(final Player player, final UUID uuid) {
        MyRoomManager.INSTANCE.warpRoom(player, uuid);
    }

    @Export
    public boolean hasRoom(final UUID uuid) {
        return MyRoomManager.INSTANCE.hasRoom(uuid);
    }

    @Export
    public void createRoom(final UUID uuid) {
        MyRoomManager.INSTANCE.createRoom(uuid);
    }

    @Export
    public void openConfirmInventory(final String title, final String yes, final String no, final String confirmFunctionName, final String cancelFunctionName) {
        openConfirmInventory(title, yes, no, confirmFunctionName, cancelFunctionName, null);
    }

    @Export
    public void openConfirmInventory(final String title, final String yes, final String no, final String confirmFunctionName, final String cancelFunctionName, final String closeFunctionName) {
        inventoryManager.getInventory(ConfirmInventory.class).open(
                player,
                new ConfirmInventory.ConfirmInfo(
                        Text.translateColor(title),
                        yes,
                        no,
                        (final Player player1, final int select) -> {
                            final String functionName = select == 1 ? confirmFunctionName : select == 0 ? cancelFunctionName : closeFunctionName;
                            if (functionName != null) {
                                try {
                                    iv.invokeFunction(functionName);
                                } catch (ScriptException ex) {
                                    CommonUtils.logWarning(ex);
                                } catch (NoSuchMethodException ignored) {
                                }
                            }
                        }
                )
        );
    }

    @Export
    public ShopItem shopItem(final ItemStack item, final int amount, final int price) {
        return new ShopItem(item, amount, price, null);
    }

    @Export
    public ShopItem shopItem(final ItemStack item, final int amount, final int price, final AlchemyMaterial coinType) {
        return new ShopItem(item, amount, price, coinType);
    }

    @Export
    public void openShopInventory(final String title, List<ShopItem> shopItems) {
        final boolean isNull = shopItems == null;
        if (!isNull && shopItems.size() > 28) {
            throw new IllegalArgumentException("No more than 29 shop items can be placed.");
        }
        inventoryManager.getInventory(ShopInventory.class).open(player, title, isNull ? new ObjectArrayList<>() : shopItems);
    }

    @Export
    public void openAppraisal() {
        inventoryManager.getInventory(Appraisal.class).open(player);
    }

    @NotNull
    @Export
    public AlchemyMaterial getAlchemyMaterial(final String material_id) {
        return AlchemyMaterial.getMaterial(material_id);
    }

    @NotNull
    @Export
    public AlchemyMaterial getAlchemyMaterial(final ItemStack item) {
        return AlchemyItemStatus.getMaterialNonNull(item);
    }

    @Nullable
    @Export
    public AlchemyMaterial getAlchemyMaterialOrNull(final String material_id) {
        return AlchemyMaterial.getMaterialOrNull(material_id);
    }

    @Nullable
    @Export
    public AlchemyMaterial getAlchemyMaterialOrNull(final ItemStack item) {
        return AlchemyItemStatus.getMaterialNullable(item);
    }

    @Export
    public ItemStack itemStack(final Material material) {
        return itemStack(material, 1);
    }

    @Export
    public ItemStack itemStack(final Material material, int amount) {
        return new ItemStack(material, amount);
    }

    @Export
    public Material getMaterial(final String id) {
        return ItemUtils.getMaterial(id);
    }

    @Export
    public EntityType getEntityType(final String entityType) {
        return EntityType.valueOf(entityType.toUpperCase());
    }

    @Export
    public AlchemyIngredients getIngredients(final String id) {
        return AlchemyIngredients.valueOf(id);
    }

    @Export
    public AlchemyIngredients getIngredientsForName(final String name) {
        return AlchemyIngredients.searchName(name);
    }

    @Export
    public int[] getSize(final String id, final int rotate) {
        return MaterialSize.valueOf(id).getSize(rotate);
    }

    @Export
    public Characteristic getCharacteristic(final String id) {
        return Characteristic.getCharacteristic(id);
    }

    @Export
    public Characteristic getCharacteristicForName(final String name) {
        return Characteristic.search(name);
    }

    @Export
    public AlchemyItemStatus loadItemStatus(final ItemStack item) {
        return AlchemyItemStatus.load(item);
    }

    @Export
    public ItemStack alchemyMaterial(final AlchemyMaterial material) {
        return alchemyMaterial(material, 1);
    }

    @Export
    public ItemStack alchemyMaterial(final AlchemyMaterial material, int amount) {
        final ItemStack item = AlchemyItemStatus.getItem(material);
        item.setAmount(amount);
        return item;
    }

    @Export
    public ItemStack alchemyMaterial(
            final AlchemyMaterial material,
            final int over_quality,
            final List<AlchemyIngredients> overIngs,
            int[] overSize,
            final List<AlchemyItemEffect> activeEffects,
            final List<Characteristic> overCharacteristics,
            final List<Category> overCategory,
            final boolean notVisibleCatalyst
    ) {
        return AlchemyItemStatus.getItem(
                material,
                overIngs,
                null,
                over_quality,
                overSize,
                activeEffects,
                overCharacteristics,
                overCategory,
                notVisibleCatalyst
        );
    }

    @Export
    public void applyAlchemyMaterial(
            final ItemStack item,
            final AlchemyMaterial material,
            final List<AlchemyIngredients> overIngs,
            final int overQuality,
            final int[] overSize,
            final List<AlchemyItemEffect> activeEffects,
            final List<Characteristic> overCharacteristics,
            final List<Category> overCategory,
            final boolean not_visible_catalyst
    ) {
        AlchemyItemStatus.getItem(
                material,
                overIngs,
                item,
                overQuality,
                overSize,
                activeEffects,
                overCharacteristics,
                overCategory,
                not_visible_catalyst
        );
    }

}
