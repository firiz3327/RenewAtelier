package net.firiz.renewatelier.alchemy.material;

import com.google.common.collect.Maps;
import net.firiz.renewatelier.inventory.item.CustomModelMaterial;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;

import java.util.Map;

/**
 * @author firiz
 */
public enum Category {
    PLANTS("植物類", Material.OAK_SAPLING),
    FOODSTUFF("食材", Material.BEEF),
    ANIMAL("動物素材", Material.LEATHER),
    CLAY("粘土", Material.CLAY_BALL),
    ORE("鉱石", Material.IRON_ORE),
    WATER("水", Material.WATER_BUCKET),
    PAPER("紙", Material.PAPER),
    COAL("燃料", Material.COAL),
    GUNPOWDER("火薬", Material.GUNPOWDER),
    WOOD("木材", Material.OAK_PLANKS),
    JEWELRY("宝石", Material.DIAMOND),
    STRING("糸素材", Material.STRING),
    POTION_MATERIAL("薬の材料", Material.SPIDER_EYE),
    POISON("毒の材料", Material.POISONOUS_POTATO),
    POTION("薬品", Material.SPLASH_POTION),
    MAGIC_ITEM("魔法の道具", new CustomModelMaterial(Material.DIAMOND_AXE, 1524)),
    MYSTERY("神秘の力", new CustomModelMaterial(Material.DIAMOND_AXE, 0)), // 竜核画像
    AN_ELIXIR("エリキシル", new CustomModelMaterial(Material.DIAMOND_AXE, 0)), // 竜のウロコ画像
    NEUTRALIZATION("中和剤", new CustomModelMaterial(Material.POTION, 0)), // ポーション色あり
    CLOTH("布", new CustomModelMaterial(Material.DIAMOND_AXE, 0)), // 布画像
    TNT("爆弾", new CustomModelMaterial(Material.DIAMOND_AXE, 0)), // フラム画像
    INGOT("金属", Material.IRON_INGOT),
    SWEETS("お菓子", Material.CAKE),
    FOOD("食品", Material.BREAD),
    WEAPON_MATERIAL("武器素材", Material.GOLD_INGOT),
    ARMOR_MATERIAL("防具素材", new CustomModelMaterial(Material.DIAMOND_AXE, 0)),// 布画像
    WEAPON("武器", Material.IRON_SWORD),
    ARMOR("防具", Material.IRON_CHESTPLATE),
    DECORATION("装飾品", new CustomModelMaterial(Material.DIAMOND_AXE, 0)), // イヤリング画像
    FLOWER("花", Material.DANDELION),
    SAND("砂", Material.SAND),
    PUNIPUNI("プニプニ玉", new CustomModelMaterial(Material.DIAMOND_AXE, 0)), // ぷにぷに玉画像
    FISH("魚介類", Material.PUFFERFISH),
    OBJECT("オブジェ", Material.CAULDRON),
    IMPORTANT("重要", Material.FILLED_MAP),
    CATALYST("触媒", new CustomModelMaterial(Material.DIAMOND_AXE, 0)), // 触媒画像
    COLLECTION_TOOL("採取道具", Material.IRON_PICKAXE),
    BOOK("本", Material.BOOK), // custom
    RED_STONE("赤い科学", Material.REDSTONE), // custom
    ;

    private static final Map<String, Category> BY_NAME = Maps.newHashMap();
    private final String name;
    private final Component nameComponent;
    private final CustomModelMaterial material;

    static {
        final Category[] var3 = values();
        for (final Category c : var3) {
            BY_NAME.put(c.name(), c);
        }
    }

    Category(String name, Material material) {
        this.name = name;
        this.nameComponent = Component.text(name);
        this.material = new CustomModelMaterial(material, 0);
    }

    Category(String name, CustomModelMaterial material) {
        this.name = name;
        this.nameComponent = Component.text(name);
        this.material = material;
    }

    public String getName() {
        return "(".concat(name).concat(")");
    }

    public Component getNameComponent() {
        return nameComponent;
    }

    public CustomModelMaterial getMaterial() {
        return material;
    }

    public static Category searchName(final String str) {
        String search = str;
        if (str.startsWith("(") || str.endsWith(")")) {
            search = str.replace("(", "").replace(")", "");
        }
        final Category c = BY_NAME.get(str.toUpperCase());
        if (c == null) {
            for (final Category category : values()) {
                if (category.name.equals(search)) {
                    return category;
                }
            }
            throw new IllegalStateException(str.concat(" is not found."));
        }
        return c;
    }

}
