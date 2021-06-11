package net.firiz.renewatelier.constants;

import net.firiz.ateliercommonapi.adventure.text.C;
import net.firiz.ateliercommonapi.adventure.text.Text;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * @author firiz
 */
public final class GameConstants {

    private GameConstants() {
    }

    public static final String W_W = "⬜";
    public static final String W_B = "⬛";

    public static final double HOLO_HEALTH_POS = 0.2;
    public static final double HOLO_HEALTH_INTERVAL = 0.3;
    public static final double HOLO_DAMAGE_POS = 1.2;
    public static final double HOLO_DAMAGE_INTERVAL = 0.3;

    public static final long PLAYER_MONEY_CAP = 20000000000L; // 20b
    public static final int PLAYER_LEVEL_CAP = 50;
    public static final long[] PLAYER_REQ_EXPS = {
            0, 100, 101, 107, 118, 134, 155, 181, 212, 249, // 10lv
            320, 220, 273, 331, 395, 465, 541, 623, 711, 805, // 20lv
            996, 662, 773, 891, 1017, 1150, 1290, 1438, 1593, 1756, // 30lv
            2119, 1381, 1561, 1750, 1948, 2155, 2372, 2599, 2836, 3083, // 40lv
            3674, 2369, 2630, 2903, 3188, 3485, 3795, 4118, 4455, 4805, // 50lv
            5685, 3646, 4005, 4380, 4771, 5179, 5604, 6047, 6508, 6988, // 60lv
            8235, 5266, 5745, 6245, 6767, 7312, 7880, 8472, 9090, 9734, // 70lv
            11444, 7306, 7933, 8589, 9275, 9992, 10741, 11524, 12341, 13194, // 80lv
            15493, 9885, 10700, 11554, 12449, 13386, 14368, 15396, 16472, 17598, // 90lv
            20654, 13179, 14235, 15344, 16509, 17733, 19018, 20367, 21783, 23270, // 100lv
            27313, 17438, 18809, 20254, 21776, 23379, 25067, 26845, 28717, 30687, // 110lv
            36037, 23029, 24822, 26717, 28719, 30834, 33069, 35429, 37922, 40554, // 120lv
            47666, 30500, 32869, 35380, 38042, 40863, 43853, 47021, 50378, 53934, // 130lv
            63472, 40678, 43850, 47223, 50810, 54625, 58681, 62994, 67579, 72453, // 140lv
            85398, 54832, 59150, 63758, 68676, 73923, 79521, 85494, 91866, 98664, // 150lv
            116506, 74964, 80961, 87383, 94259, 101622, 109505, 117945, 126981, 136654, // 160lv
            161709, 104293, 112810, 121962, 131797, 142364, 153718, 165918, 179025, 193107, // 170lv
            229059, 148112, 160514, 173889, 188312, 203865, 220636, 238720, 258220, 279246, // 180lv
            332108, 215344, 233902, 253986, 275722, 299246, 324704, 352254, 382068, 414332, // 190lv
            494171, 321382, 349970, 381022, 414749, 451382, 491170, 534385, 581322, 632300, // 200lv
            756433, 493488, 538901, 588407, 642373, 701201, 765329, 835233, 911433, 994496, // 210lv
            1193544, 781204, 855687, 937176, 1026330, 1123870, 1230583, 1347332, 1475061, 1614801, // 220lv
            1944450, 1276993, 1403238, 1541860, 1694072, 1861206, 2044724, 2246231, 2467491, 2710440, // 230lv
            3274923, 2158206, 2379493, 2623356, 2892098, 3188256, 3514628, 3874295, 4270653, 4707444, // 240lv
            5707672, 3774629, 4175939, 4619793, 5110701, 5653650, 6254156, 6918321, 7652893, 8465334, // 250lv
            10300288, 6836017, 7589228, 8425298, 9353340, 10383472, 11526923, 12796159, 14205016, 15768852, // 260lv
            19255186, 12824730, 14288049, 15918191, 17734174, 19757184, 22010822, 24521380, 27318147, 30433750, // 270lv
            37294990, 24928777, 27871722, 31161940, 34840408, 38952941, 43550758, 48691122, 54438054, 60863129, // 280lv
            74851004, 50210890, 56338018, 63212661, 70926015, 79580403, 89290632, 100185514, 112409576, 126124979, // 290lv
            155665032, 104794566, 118000131, 132869602, 149612631, 168465287, 189693383, 213596224, 240510828, 270816677 // 300lv
    };

    public static final int ALCHEMY_LEVEL_CAP = 99;
    public static final int[] ALCHEMY_REQ_EXPS = {
            0, 4, 6, 8, 10, 12, 14, 16, 18, 20, // 10lv
            20, 21, 22, 23, 24, 25, 26, 27, 28, 29, // 20lv
            30, 32, 34, 36, 38, 40, 42, 44, 46, 48, // 30lv
            50, 53, 56, 59, 62, 65, 68, 71, 74, 77, // 40lv
            80, 84, 88, 92, 96, 100, 104, 108, 112, 116, // 50lv
            120, 125, 130, 135, 140, 145, 150, 155, 160, 165, // 60lv
            170, 176, 182, 188, 194, 200, 206, 212, 218, 224, // 70lv
            230, 237, 244, 251, 258, 265, 272, 279, 286, 293, // 80lv
            300, 308, 316, 324, 332, 340, 348, 356, 364, 372, // 90lv
            380, 389, 398, 407, 416, 425, 434, 443, 452, 461 // 100lv
    };

    public static final int HORSE_MAX_BOOST_COUNT = 3;
    public static final long HORSE_MATING_MAX_COUNT = 2;
    public static final long HORSE_MATING_REQUIRE_HOUR = 48;
    public static final int[] HORSE_REQ_EXPS = {
            0, 50, 70, 90, 120, 140, 160, 180, 200, 200, // 10lv
            200, 210, 220, 230, 240, 250, 260, 270, 280, 290, // 20lv
            300, 320, 340, 360, 380, 400, 420, 440, 460, 480, // 30lv
            500, 530, 560, 590, 620, 650, 680, 710, 740, 770, // 40lv
    };

    // ブロンズ・シルバー・ゴールド・ダイアモンド・>>>未定
    public static final int RECIPE_EXP = 25;
    public static final int[] RECIPE_REQ_EXPS = {0, 60, 140, 300, 620, 1260, 2540, 5100, 10000};
    public static final Component[] RECIPE_RANK_NAME = {
            Text.of("熟練度なし"),
            Text.of("ブロンズ", C.GRAY),
            Text.of("シルバー", C.FLAT_SILVER1),
            Text.of("ゴールド", C.GOLD),
            Text.of("ダイアモンド", C.DARK_AQUA)
    };

    public static final String[] ROTATION_STR = {"上向き", "右向き", "下向き", "左向き"};
    public static final String[][] TURN_STR = {
            {"左右反転： OFF", "上下反転： OFF"},
            {"左右反転： ON", "上下反転： OFF"},
            {"左右反転： OFF", "上下反転： ON"},
            {"左右反転： ON", "上下反転： ON"}
    };

    public static final Material USABLE_MATERIAL = Material.SPECTRAL_ARROW;

    /**
     * クリティカルハック及びフライハック対策
     * <p>
     * 厳密な判定は行わない
     *
     * @param player 検知対象のプレイヤー
     * @return 実際にクリティカルしているかどうか
     */
    public static boolean isCritical(@NotNull final Player player) {
        final boolean isOnGround = player.isOnGround(); // 非推奨の理由は問題ない
        return !isOnGround && (player.getLocation().getY() % 1 != 0 || player.getVelocity().getY() < -0.0784);
    }

    /**
     * 「attackSpeed * 0.25」でクールタイムを算出できる
     *
     * @param material
     * @return
     */
    public static int getCoolTimeMillis(@NotNull Material material) {
        if (isSword(material)) {
            return 600; // 0.6
        } else if (isShovel(material)) {
            return 1000; // 1
        } else if (isPickaxe(material)) {
            return 850; // 0.85
        } else if (material == Material.TRIDENT) {
            return 950; // 0.95
        } else {
            switch (material) {
                case WOODEN_AXE:
                case STONE_AXE:
                    return 1250; // 1.25
                case GOLDEN_AXE:
                case DIAMOND_AXE:
                case NETHERITE_AXE:
                case WOODEN_HOE:
                case GOLDEN_HOE:
                    return 1000; // 1
                case IRON_AXE:
                    return 1100; // 1.1
                case STONE_HOE:
                    return 500; // 0.5
                default:
                    return 250; // 0.25
            }
        }
    }

    public static boolean isSword(@NotNull Material material) {
        switch (material) {
            case WOODEN_SWORD:
            case STONE_SWORD:
            case IRON_SWORD:
            case GOLDEN_SWORD:
            case DIAMOND_SWORD:
            case NETHERITE_SWORD:
                return true;
            default:
                return false;
        }
    }

    public static boolean isAxe(@NotNull Material material) {
        switch (material) {
            case WOODEN_AXE:
            case STONE_AXE:
            case IRON_AXE:
            case GOLDEN_AXE:
            case DIAMOND_AXE:
            case NETHERITE_AXE:
                return true;
            default:
                return false;
        }
    }

    public static boolean isPickaxe(@NotNull Material material) {
        switch (material) {
            case WOODEN_PICKAXE:
            case STONE_PICKAXE:
            case IRON_PICKAXE:
            case GOLDEN_PICKAXE:
            case DIAMOND_PICKAXE:
            case NETHERITE_PICKAXE:
                return true;
            default:
                return false;
        }
    }

    public static boolean isShovel(@NotNull Material material) {
        switch (material) {
            case WOODEN_SHOVEL:
            case STONE_SHOVEL:
            case IRON_SHOVEL:
            case GOLDEN_SHOVEL:
            case DIAMOND_SHOVEL:
            case NETHERITE_SHOVEL:
                return true;
            default:
                return false;
        }
    }

    public static double getVanillaItemDamage(Material material) {
        switch (material) {
            case WOODEN_PICKAXE:
            case GOLDEN_PICKAXE:
                return 2;
            case WOODEN_SHOVEL:
            case GOLDEN_SHOVEL:
                return 2.5;
            case STONE_PICKAXE:
                return 3;
            case STONE_SHOVEL:
                return 3.5;
            case WOODEN_SWORD:
            case GOLDEN_SWORD:
            case IRON_PICKAXE:
            case NETHERITE_HOE:
                return 4;
            case IRON_SHOVEL:
                return 4.5;
            case STONE_SWORD:
            case DIAMOND_PICKAXE:
                return 5;
            case DIAMOND_SHOVEL:
                return 5.5;
            case IRON_SWORD:
            case NETHERITE_PICKAXE:
                return 6;
            case NETHERITE_SHOVEL:
                return 6.5;
            case DIAMOND_SWORD:
            case WOODEN_AXE:
            case GOLDEN_AXE:
                return 7;
            case NETHERITE_SWORD:
                return 8;
            case STONE_AXE:
            case IRON_AXE:
            case DIAMOND_AXE:
                return 9;
            case NETHERITE_AXE:
                return 10;
            default:
                return 1;
        }
    }

    public static boolean isArmor(Material material) {
        switch (material) {
            case TURTLE_HELMET:
            case LEATHER_HELMET:
            case LEATHER_CHESTPLATE:
            case LEATHER_LEGGINGS:
            case LEATHER_BOOTS:
            case CHAINMAIL_HELMET:
            case CHAINMAIL_CHESTPLATE:
            case CHAINMAIL_LEGGINGS:
            case CHAINMAIL_BOOTS:
            case IRON_HELMET:
            case IRON_CHESTPLATE:
            case IRON_LEGGINGS:
            case IRON_BOOTS:
            case GOLDEN_HELMET:
            case GOLDEN_CHESTPLATE:
            case GOLDEN_LEGGINGS:
            case GOLDEN_BOOTS:
            case DIAMOND_HELMET:
            case DIAMOND_CHESTPLATE:
            case DIAMOND_LEGGINGS:
            case DIAMOND_BOOTS:
                return true;
            default:
                return false;
        }
    }

    public static int getItemDefense(Material material) {
        switch (material) {
            case TURTLE_HELMET: // turtle 1
            case LEATHER_HELMET: // leather all 5 [1, 2, 1, 1]
            case LEATHER_LEGGINGS:
            case LEATHER_BOOTS:
                return 1;
            case LEATHER_CHESTPLATE:
            case CHAINMAIL_HELMET: // chainmail all 9 [2, 3, 2, 2]
            case CHAINMAIL_LEGGINGS:
            case CHAINMAIL_BOOTS:
                return 2;
            case CHAINMAIL_CHESTPLATE:
            case IRON_HELMET: // iron all 13 [3, 4, 3, 3]
            case IRON_LEGGINGS:
            case IRON_BOOTS:
            case GOLDEN_HELMET: // golden all 13 [3, 4, 3, 3]
            case GOLDEN_LEGGINGS:
            case GOLDEN_BOOTS:
                return 3;
            case IRON_CHESTPLATE:
            case GOLDEN_CHESTPLATE:
                return 4;
            case DIAMOND_HELMET: // diamond all 21 [5, 6, 5, 5]
            case DIAMOND_LEGGINGS:
            case DIAMOND_BOOTS:
                return 5;
            case DIAMOND_CHESTPLATE:
            case NETHERITE_HELMET: // netherite all 25 [6, 7, 6, 6]
            case NETHERITE_LEGGINGS:
            case NETHERITE_BOOTS:
                return 6;
            case NETHERITE_CHESTPLATE:
                return 7;
            default:
                return 0;
        }
    }

    public static boolean isOre(Material material) {
        switch (material) {
            case COAL_ORE:
            case IRON_ORE:
            case GOLD_ORE:
            case DIAMOND_ORE:
            case EMERALD_ORE:
            case LAPIS_ORE:
            case REDSTONE_ORE:
            case ANCIENT_DEBRIS:
                return true;
            default:
                return false;
        }
    }

}
