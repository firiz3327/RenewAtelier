package net.firiz.renewatelier.version.inject;

import net.firiz.renewatelier.event.PlayerArmorChangeEvent;
import net.firiz.renewatelier.utils.Chore;
import net.firiz.renewatelier.version.VersionUtils;
import net.minecraft.server.v1_14_R1.EntityHuman;
import net.minecraft.server.v1_14_R1.ItemStack;
import net.minecraft.server.v1_14_R1.NonNullList;
import net.minecraft.server.v1_14_R1.PlayerInventory;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class PlayerInjection {

    public static void injectArmorChangeEvent(Player player) {
        final EntityHuman human = ((CraftPlayer) player).getHandle();
        try {
            final Field f = EntityHuman.class.getDeclaredField("inventory");
            f.setAccessible(true);

            final PlayerInventory inventory = (PlayerInventory) f.get(human);
            final Field armorField = PlayerInventory.class.getDeclaredField("armor");
            armorField.setAccessible(true);

            final NonNullList<ItemStack> armorList = (NonNullList<ItemStack>) armorField.get(inventory);
            final Field a = NonNullList.class.getDeclaredField("a");
            a.setAccessible(true);
            // reload時に更新処理が入らないためjoin時のみinjectすれば問題ない
            a.set(armorList, new ArrayList<ItemStack>((List<ItemStack>) a.get(armorList)) {
                @Override
                public boolean add(ItemStack itemStack) {
                    final boolean result = super.add(itemStack);
                    Bukkit.getPluginManager().callEvent(new PlayerArmorChangeEvent(player, VersionUtils.asItem(itemStack), PlayerArmorChangeEvent.ChangeType.ADD));
                    return result;
                }

                @Override
                public ItemStack set(int slot, ItemStack itemStack) {
                    final ItemStack result = super.set(slot, itemStack);
                    Bukkit.getPluginManager().callEvent(new PlayerArmorChangeEvent(player, VersionUtils.asItem(itemStack), PlayerArmorChangeEvent.ChangeType.SET));
                    return result;
                }

                @Override
                public ItemStack remove(int slot) {
                    final ItemStack result = super.remove(slot);
                    Bukkit.getPluginManager().callEvent(new PlayerArmorChangeEvent(player, VersionUtils.asItem(result), PlayerArmorChangeEvent.ChangeType.REMOVE));
                    return result;
                }

                @Override
                public void clear() {
                    super.clear();
                    Bukkit.getPluginManager().callEvent(new PlayerArmorChangeEvent(player, null, PlayerArmorChangeEvent.ChangeType.CLEAR));
                }
            });
            Chore.log("inject successfully");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Chore.logWarning(e);
        }
    }

}
