package net.firiz.renewatelier.a;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.firiz.renewatelier.alchemy.catalyst.Catalyst;
import net.firiz.renewatelier.alchemy.kettle.bonus.ABonus;
import net.firiz.renewatelier.alchemy.kettle.box.KettleBox;
import net.firiz.renewatelier.alchemy.material.AlchemyMaterial;
import net.firiz.renewatelier.alchemy.recipe.AlchemyRecipe;
import net.firiz.renewatelier.utils.chores.CObjects;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class AData {

    private static final ItemStack AIR = new ItemStack(Material.AIR);

    // initialize
    private final Location location;
    private final AlchemyRecipe recipe;
    private final ABonus aBonus;
    private final List<List<ItemStack>> pageItems = new ObjectArrayList<>(); // 保存されている使用中のアイテム

    private final ACM acm = new ACM();

    private boolean isOpen;
    private ItemStack[] contents; // 保存されているインベントリ
    private ItemStack catalystItem;
    private KettleBox kettleBox;

    public AData(Location location, AlchemyRecipe recipe) {
        this.location = location;
        this.recipe = recipe;
        this.aBonus = new ABonus(this, recipe.getReqBar());
    }

    public Location getLocation() {
        return location;
    }

    public AlchemyRecipe getRecipe() {
        return recipe;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public void setContents(ItemStack[] contents) {
        final ItemStack[] clone = new ItemStack[contents.length];
        for (int i = 0; i < contents.length; i++) {
            clone[i] = contents[i] == null ? null : contents[i].clone();
//            clone[i] = CObjects.nullIf(contents[i], ItemStack::clone, (ItemStack) null);
        }
        this.contents = clone;
    }

    @Nullable
    public ItemStack[] getContents() {
        return contents;
    }

    public void createPageItemSize(int size) {
        pageItems.clear();
        for (int i = 0; i < size; i++) {
            pageItems.add(new ObjectArrayList<>());
        }
    }

    public List<ItemStack> getPageItems(int page) {
        return pageItems.get(page);
    }

    @NotNull
    public List<List<ItemStack>> getPageItems() {
        return Collections.unmodifiableList(pageItems);
    }

    public boolean addPageItem(int page, ItemStack item) {
        return pageItems.get(page).add(item);
    }

    public boolean removePageItem(int page, ItemStack item) {
        return pageItems.get(page).remove(item);
    }

    public void initializeKettleBox() {
        final Catalyst catalyst = CObjects.nullIf(
                getCatalystItem(),
                item -> Objects.requireNonNullElse(AlchemyMaterial.getMaterial(item).getCatalyst(), Catalyst.getDefaultCatalyst()),
                Catalyst.getDefaultCatalyst()
        );
        this.kettleBox = new KettleBox(catalyst.getBonus().get(0).getCS().length);
    }

    public KettleBox getKettleBox() {
        return kettleBox;
    }

    public void setCatalystItem(ItemStack catalystItem) {
        this.catalystItem = catalystItem;
    }

    @Nullable
    public ItemStack getCatalystItem() {
        return catalystItem;
    }

    public ItemStack removeCatalystItem() {
        final ItemStack old = catalystItem.clone();
        catalystItem = null;
        return old;
    }

    public ACM getAcm() {
        return acm;
    }

    public ABonus getBonusManager() {
        return aBonus;
    }
}
