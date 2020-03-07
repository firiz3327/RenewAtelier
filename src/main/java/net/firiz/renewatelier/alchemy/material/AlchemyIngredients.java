/*
 * AlchemyIngredients.java
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

package net.firiz.renewatelier.alchemy.material;

import com.google.common.collect.Maps;
import net.firiz.renewatelier.item.AlchemyItemStatus;
import net.firiz.renewatelier.utils.doubledata.DoubleData;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author firiz
 */
public enum AlchemyIngredients {
    I0P_5("しなびてる", AlchemyAttribute.PURPLE, -5),
    I1P10("ずっしり", AlchemyAttribute.PURPLE, 10),
    I2P15("とても硬い", AlchemyAttribute.PURPLE, 15),
    I3P15("とても重い", AlchemyAttribute.PURPLE, 15),
    I4P20("にごり成分", AlchemyAttribute.PURPLE, 20),
    I5P15("ぬめり", AlchemyAttribute.PURPLE, 15),
    I6P_5("ヒビが入る", AlchemyAttribute.PURPLE, -5),
    I7P_5("異物がある", AlchemyAttribute.PURPLE, -5),
    I8P25("角張り", AlchemyAttribute.PURPLE, 25),
    I9P10("乾きやすい", AlchemyAttribute.PURPLE, 10),
    I10P10("岩の重さ", AlchemyAttribute.PURPLE, 10),
    I11P15("危険な香り", AlchemyAttribute.PURPLE, 15),
    I12P5("曲がってる", AlchemyAttribute.PURPLE, 5),
    I13P_10("砕きやすい", AlchemyAttribute.PURPLE, -10),
    I14P5("紫色", AlchemyAttribute.PURPLE, 5),
    I15P20("脂っこい", AlchemyAttribute.PURPLE, 20),
    I16P30("呪われた力", AlchemyAttribute.PURPLE, 30),
    I17P10("重い", AlchemyAttribute.PURPLE, 10),
    I18P10("鉄分豊富", AlchemyAttribute.PURPLE, 10),
    I19P10("粘っこい", AlchemyAttribute.PURPLE, 10),
    I20P20("毛深い", AlchemyAttribute.PURPLE, 20),
    I21P5("掠れた文字", AlchemyAttribute.PURPLE, 5),
    I22P20("たくましい", AlchemyAttribute.PURPLE, 20),
    I23P_5("つやがない", AlchemyAttribute.PURPLE, -5),
    I24P10("トゲの葉", AlchemyAttribute.PURPLE, 10),
    I25P15("とても堅い", AlchemyAttribute.PURPLE, 15),
    I26P10("ニトロ臭", AlchemyAttribute.PURPLE, 10),
    I27P_5("ヒビがある", AlchemyAttribute.PURPLE, -5),
    I28P10("よく膨らむ", AlchemyAttribute.PURPLE, 10),
    I29P10("意識が飛ぶ", AlchemyAttribute.PURPLE, 10),
    I30P20("割れている", AlchemyAttribute.PURPLE, 20),
    I31P35("驚異的堅さ", AlchemyAttribute.PURPLE, 35),
    I32P5("僅かな苦み", AlchemyAttribute.PURPLE, 5),
    I33P10("苦み", AlchemyAttribute.PURPLE, 10),
    I34P_5("欠けやすい", AlchemyAttribute.PURPLE, -5),
    I35P10("血のにおい", AlchemyAttribute.PURPLE, 10),
    I36P10("光を吸う", AlchemyAttribute.PURPLE, 10),
    I37P10("磁力がある", AlchemyAttribute.PURPLE, 10),
    I38P75("邪悪な気配", AlchemyAttribute.PURPLE, 75),
    I39P10("渋い", AlchemyAttribute.PURPLE, 10),
    I40P_5("純度が低い", AlchemyAttribute.PURPLE, -5),
    I41P_10("傷がある", AlchemyAttribute.PURPLE, -10),
    I42P_10("折れている", AlchemyAttribute.PURPLE, -10),
    I43P15("浅黒い", AlchemyAttribute.PURPLE, 15),
    I44P10("毒がでる", AlchemyAttribute.PURPLE, 10),
    I45P15("毒の香り", AlchemyAttribute.PURPLE, 15),
    I46P10("毒々しい", AlchemyAttribute.PURPLE, 10),
    I47P10("肥満の友", AlchemyAttribute.PURPLE, 10),
    I48P5("放電する", AlchemyAttribute.PURPLE, 5),
    I49P20("魅惑的な光", AlchemyAttribute.PURPLE, 20),
    I50G10("とげとげ", AlchemyAttribute.GREEN, 10),
    I51G5("何かの文字", AlchemyAttribute.GREEN, 5),
    I52G10("厚い皮", AlchemyAttribute.GREEN, 10),
    I53G10("丈夫なカラ", AlchemyAttribute.GREEN, 10),
    I54G10("色が濃い", AlchemyAttribute.GREEN, 10),
    I55G20("新鮮", AlchemyAttribute.GREEN, 20),
    I56G20("太い", AlchemyAttribute.GREEN, 20),
    I57G15("模様がある", AlchemyAttribute.GREEN, 15),
    I58G10("粒が大きい", AlchemyAttribute.GREEN, 10),
    I59G5("くっつく", AlchemyAttribute.GREEN, 5),
    I60G30("コクがある", AlchemyAttribute.GREEN, 15),
    I61G5("とげがある", AlchemyAttribute.GREEN, 5),
    I62G35("鋭い", AlchemyAttribute.GREEN, 35),
    I63G30("鋭いうまみ", AlchemyAttribute.GREEN, 30),
    I64G5("鋭利なトゲ", AlchemyAttribute.GREEN, 5),
    I65G30("液模様", AlchemyAttribute.GREEN, 30),
    I66G15("詰まってる", AlchemyAttribute.GREEN, 15),
    I67G10("苦い香り", AlchemyAttribute.GREEN, 10),
    I68G15("香ばしい", AlchemyAttribute.GREEN, 15),
    I69G20("骨張ってる", AlchemyAttribute.GREEN, 20),
    I70G15("実が多い", AlchemyAttribute.GREEN, 15),
    I71G15("丈夫", AlchemyAttribute.GREEN, 15),
    I72G10("丈夫な貝", AlchemyAttribute.GREEN, 10),
    I73G15("森の香り", AlchemyAttribute.GREEN, 15),
    I74G5("針がある", AlchemyAttribute.GREEN, 5),
    I75G10("繊維質", AlchemyAttribute.GREEN, 10),
    I76G5("大きなかさ", AlchemyAttribute.GREEN, 5),
    I77G10("張り付く", AlchemyAttribute.GREEN, 10),
    I78G30("波模様", AlchemyAttribute.GREEN, 30),
    I79G5("薄い殻", AlchemyAttribute.GREEN, 5),
    I80G10("緑ぷにぷに", AlchemyAttribute.GREEN, 10),
    I81G5("緑色", AlchemyAttribute.GREEN, 5),
    I82R20("あたたかい", AlchemyAttribute.RED, 20),
    I83R10("おいしそう", AlchemyAttribute.RED, 10),
    I84R20("くすんでる", AlchemyAttribute.RED, 20),
    I85R10("ふかふか", AlchemyAttribute.RED, 10),
    I86R15("やわらかい", AlchemyAttribute.RED, 15),
    I87R15("よく動く", AlchemyAttribute.RED, 15),
    I88R20("果肉が多い", AlchemyAttribute.RED, 20),
    I89R20("甘い", AlchemyAttribute.RED, 20),
    I90R20("強い香り", AlchemyAttribute.RED, 20),
    I91R30("強い生命力", AlchemyAttribute.RED, 30),
    I92R15("極めて丸い", AlchemyAttribute.RED, 15),
    I93R20("黒光り", AlchemyAttribute.RED, 20),
    I94R20("心が安らぐ", AlchemyAttribute.RED, 20),
    I95R5("赤い", AlchemyAttribute.RED, 5),
    I96R10("大きな花弁", AlchemyAttribute.RED, 10),
    I97R15("弾力がある", AlchemyAttribute.RED, 15),
    I98R20("肉厚", AlchemyAttribute.RED, 20),
    I99R5("熱をためる", AlchemyAttribute.RED, 5),
    I100R25("燃えやすい", AlchemyAttribute.RED, 25),
    I101R10("剥離する", AlchemyAttribute.RED, 10),
    I102R20("崩れやすい", AlchemyAttribute.RED, 20),
    I103R20("あふれる力", AlchemyAttribute.RED, 20),
    I104R20("ざらざら", AlchemyAttribute.RED, 20),
    I105R10("ぷにぷに", AlchemyAttribute.RED, 10),
    I106R30("ほくほく", AlchemyAttribute.RED, 30),
    I107R5("まろやか", AlchemyAttribute.RED, 5),
    I108R10("もふもふ", AlchemyAttribute.RED, 10),
    I109R10("意外な栄養", AlchemyAttribute.RED, 10),
    I110R10("角がある", AlchemyAttribute.RED, 10),
    I111R5("幾何学模様", AlchemyAttribute.RED, 5),
    I112R25("鏡の光沢", AlchemyAttribute.RED, 25),
    I113R10("血の色", AlchemyAttribute.RED, 10),
    I114R15("健康に良い", AlchemyAttribute.RED, 15),
    I115R20("黒光", AlchemyAttribute.RED, 20),
    I116R5("砂岩質", AlchemyAttribute.RED, 5),
    I117R_5("砕けやすい", AlchemyAttribute.RED, -5),
    I118R10("脂身が多い", AlchemyAttribute.RED, 10),
    I119R75("朱の光沢", AlchemyAttribute.RED, 75),
    I120R10("熟している", AlchemyAttribute.RED, 10),
    I121R20("焼ける熱さ", AlchemyAttribute.RED, 20),
    I122R5("色鮮やか", AlchemyAttribute.RED, 5),
    I123R5("食欲がわく", AlchemyAttribute.RED, 5),
    I124R5("水気がない", AlchemyAttribute.RED, 5),
    I125R10("赤ぷにぷに", AlchemyAttribute.RED, 10),
    I126R10("発火する", AlchemyAttribute.RED, 10),
    I127R5("豊かな土", AlchemyAttribute.RED, 5),
    I128R10("魔物を誘う", AlchemyAttribute.RED, 10),
    I129R10("味が濃い", AlchemyAttribute.RED, 10),
    I130R50("湧き出る力", AlchemyAttribute.RED, 50),
    I131R15("陽の香り", AlchemyAttribute.RED, 15),
    I132R_5("裂けている", AlchemyAttribute.RED, -5),
    I133Y10("いい手触り", AlchemyAttribute.YELLOW, 10),
    I134Y15("カラフル", AlchemyAttribute.YELLOW, 15),
    I135Y20("つやがある", AlchemyAttribute.YELLOW, 20),
    I136Y30("ビリビリ", AlchemyAttribute.YELLOW, 30),
    I137Y25("ほのかな光", AlchemyAttribute.YELLOW, 25),
    I138Y25("羽根のよう", AlchemyAttribute.YELLOW, 25),
    I139Y5("黄色", AlchemyAttribute.YELLOW, 5),
    I140Y50("強い光輝", AlchemyAttribute.YELLOW, 50),
    I141Y10("金属の硬さ", AlchemyAttribute.YELLOW, 10),
    I142Y10("光っている", AlchemyAttribute.YELLOW, 10),
    I143Y20("光沢がある", AlchemyAttribute.YELLOW, 20),
    I144Y40("高密度", AlchemyAttribute.YELLOW, 40),
    I145Y15("臭い", AlchemyAttribute.YELLOW, 15),
    I146Y10("色が抜けた", AlchemyAttribute.YELLOW, 10),
    I147Y30("神秘の光", AlchemyAttribute.YELLOW, 30),
    I148Y15("生臭い", AlchemyAttribute.YELLOW, 15),
    I149Y5("尖っている", AlchemyAttribute.YELLOW, 5),
    I150Y20("粘り気", AlchemyAttribute.YELLOW, 20),
    I151Y5("仄かに光る", AlchemyAttribute.YELLOW, 5),
    I152Y10("きしむ音", AlchemyAttribute.YELLOW, 10),
    I153Y10("さらさら", AlchemyAttribute.YELLOW, 10),
    I154Y5("すっぱい", AlchemyAttribute.YELLOW, 5),
    I155Y25("つやつや", AlchemyAttribute.YELLOW, 25),
    I156Y20("とても臭い", AlchemyAttribute.YELLOW, 20),
    I157Y5("黄金の味", AlchemyAttribute.YELLOW, 5),
    I158Y10("温泉の香り", AlchemyAttribute.YELLOW, 10),
    I159Y20("音が鳴る", AlchemyAttribute.YELLOW, 20),
    I160Y15("加護", AlchemyAttribute.YELLOW, 15),
    I161Y35("奇跡の加護", AlchemyAttribute.YELLOW, 35),
    I162Y20("輝いている", AlchemyAttribute.YELLOW, 20),
    I163Y25("輝くめしべ", AlchemyAttribute.YELLOW, 25),
    I164Y35("金ぷにぷに", AlchemyAttribute.YELLOW, 35),
    I165Y5("金属光沢", AlchemyAttribute.YELLOW, 5),
    I166Y25("金粉つき", AlchemyAttribute.YELLOW, 25),
    I167Y25("光るおしべ", AlchemyAttribute.YELLOW, 25),
    I168Y5("砕けない", AlchemyAttribute.YELLOW, 5),
    I169Y10("刺激臭", AlchemyAttribute.YELLOW, 10),
    I170Y45("神々しい", AlchemyAttribute.YELLOW, 45),
    I171Y10("星の輝き", AlchemyAttribute.YELLOW, 10),
    I172Y25("正絹", AlchemyAttribute.YELLOW, 25),
    I173Y25("聖なる加護", AlchemyAttribute.YELLOW, 25),
    I174Y15("土の香り", AlchemyAttribute.YELLOW, 15),
    I175Y10("独特の匂い", AlchemyAttribute.YELLOW, 10),
    I176Y15("虹色の輝き", AlchemyAttribute.YELLOW, 15),
    I177Y15("粘土質", AlchemyAttribute.YELLOW, 15),
    I178Y5("粉々", AlchemyAttribute.YELLOW, 5),
    I179Y10("摩擦がない", AlchemyAttribute.YELLOW, 10),
    I180B15("いい香り", AlchemyAttribute.BLUE, 15),
    I181B10("しっとり", AlchemyAttribute.BLUE, 10),
    I182B15("ひんやり", AlchemyAttribute.BLUE, 15),
    I183B15("ミネラル", AlchemyAttribute.BLUE, 15),
    I184B15("羽根の軽さ", AlchemyAttribute.BLUE, 15),
    I185B15("甘い香り", AlchemyAttribute.BLUE, 15),
    I186B20("実っている", AlchemyAttribute.BLUE, 20),
    I187B30("純度が高い", AlchemyAttribute.BLUE, 30),
    I188B15("伸びる", AlchemyAttribute.BLUE, 15),
    I189B35("深い青", AlchemyAttribute.BLUE, 35),
    I190B15("水気がある", AlchemyAttribute.BLUE, 15),
    I191B10("尊い", AlchemyAttribute.BLUE, 10),
    I192B25("氷の冷たさ", AlchemyAttribute.BLUE, 25),
    I193B75("碧の光沢", AlchemyAttribute.BLUE, 75),
    I194B15("目が細かい", AlchemyAttribute.BLUE, 15),
    I195B25("薬効成分", AlchemyAttribute.BLUE, 25),
    I196B15("冷たい", AlchemyAttribute.BLUE, 15),
    I197B5("フローラル", AlchemyAttribute.BLUE, 5),
    I198B10("まどろむ", AlchemyAttribute.BLUE, 10),
    I199B5("よく跳ねる", AlchemyAttribute.BLUE, 5),
    I200B25("強い甘み", AlchemyAttribute.BLUE, 25),
    I201B10("空気が出る", AlchemyAttribute.BLUE, 10),
    I202B10("空気の軽さ", AlchemyAttribute.BLUE, 10),
    I203B10("古代の水", AlchemyAttribute.BLUE, 10),
    I204B10("口で溶ける", AlchemyAttribute.BLUE, 10),
    I205B5("硬水", AlchemyAttribute.BLUE, 5),
    I206B_5("細い", AlchemyAttribute.BLUE, -5),
    I207B20("震える寒さ", AlchemyAttribute.BLUE, 20),
    I208B10("水になじむ", AlchemyAttribute.BLUE, 10),
    I209B15("水を吸う", AlchemyAttribute.BLUE, 15),
    I210B5("水を弾く", AlchemyAttribute.BLUE, 5),
    I211B15("水分豊富", AlchemyAttribute.BLUE, 15),
    I212B25("水溶性", AlchemyAttribute.BLUE, 25),
    I213B10("澄んだ青", AlchemyAttribute.BLUE, 10),
    I214B30("精霊の魂", AlchemyAttribute.BLUE, 30),
    I215B5("青い", AlchemyAttribute.BLUE, 5),
    I216B10("青ぷにぷに", AlchemyAttribute.BLUE, 10),
    I217B35("尖った味", AlchemyAttribute.BLUE, 35),
    I218B10("霜がつく", AlchemyAttribute.BLUE, 10),
    I219B25("弾ける力", AlchemyAttribute.BLUE, 25),
    I220B_5("中身がない", AlchemyAttribute.BLUE, -5),
    I221B15("潮の香り", AlchemyAttribute.BLUE, 15),
    I222B30("滴る水滴", AlchemyAttribute.BLUE, 30),
    I223B10("透明", AlchemyAttribute.BLUE, 10),
    I224B5("熱を奪う", AlchemyAttribute.BLUE, 5),
    I225B5("背が高い", AlchemyAttribute.BLUE, 5),
    I226B5("浮かぶ", AlchemyAttribute.BLUE, 5),
    I227B15("粉っぽい", AlchemyAttribute.BLUE, 15),
    I228B5("粉末状", AlchemyAttribute.BLUE, 5),
    I229B10("霧がつく", AlchemyAttribute.BLUE, 10),
    I230B25("溶けやすい", AlchemyAttribute.BLUE, 25),
    I231B10("裂けやすい", AlchemyAttribute.BLUE, 10),
    I232B5("薄い", AlchemyAttribute.BLUE, 5),
    I233B20("滲まない", AlchemyAttribute.BLUE, 20),
    I234G10("新品のにおい", AlchemyAttribute.GREEN, 10),
    I235B10("インクの匂い", AlchemyAttribute.BLUE, 10),
    I236B25("濃いインクの匂い", AlchemyAttribute.BLUE, 25),
    I237G20("植物の繊維", AlchemyAttribute.GREEN, 20),
    I238B15("煙が出る", AlchemyAttribute.BLUE, 15),
    I239R15("火薬臭", AlchemyAttribute.RED, 15),
    I240R10("わずかな熱", AlchemyAttribute.RED, 10),
    I241P25("奇妙な香り", AlchemyAttribute.PURPLE, 25),
    ;

    private static final Map<String, AlchemyIngredients> BY_NAME = Maps.newHashMap();
    private final String name;
    private final AlchemyAttribute type;
    private final int level;

    static {
        AlchemyIngredients[] var3 = values();
        int var2 = var3.length;

        for(int var1 = 0; var1 < var2; ++var1) {
            final AlchemyIngredients ai = var3[var1];
            BY_NAME.put(ai.name(), ai);
        }
    }

    AlchemyIngredients(String name, AlchemyAttribute type, int level) {
        this.name = name;
        this.type = type;
        this.level = level;
    }

    public AlchemyAttribute getType() {
        return type;
    }

    public int getLevel() {
        return level;
    }

    public String getName() {
        return name;
    }

    public static AlchemyIngredients searchName(final String str) {
        final AlchemyIngredients ai = BY_NAME.get(str.toUpperCase());
        if(ai == null) {
            for (final AlchemyIngredients ing : values()) {
                if (ing.name.equals(str)) {
                    return ing;
                }
            }
            throw new IllegalStateException(str.concat(" is not found."));
        }
        return ai;
    }

    public static DoubleData<Integer, AlchemyAttribute[]> getAllLevel(final ItemStack item) {
        if (item != null && item.hasItemMeta() && item.getItemMeta().hasLore()) {
            final List<String> lores = AlchemyItemStatus.getLores(AlchemyItemStatus.Type.ALCHEMY_INGREDIENTS, item);
            final String values = lores.get(0).substring(AlchemyItemStatus.Type.ALCHEMY_INGREDIENTS.getCheck().length() + 10);
            final int level = Integer.parseInt(values.substring(0, values.indexOf(' ')));
            final String[] types = values.substring(values.indexOf(' ') + 1).split("●");
            final List<AlchemyAttribute> list = new ArrayList<>();
            for (final String type : types) {
                list.add(AlchemyAttribute.searchColor(type));
            }
            return new DoubleData<>(level, list.toArray(new AlchemyAttribute[0]));
        }
        return null;
    }

    public static int getLevel(final ItemStack item, final AlchemyAttribute type) {
        if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
            final List<String> lores = item.getItemMeta().getLore();
            boolean loreArea = false;
            int allLevel = 0;
            for (final String lore : lores) {
                if (loreArea) {
                    if (lore.contains("-")) {
                        String ingredients = lore.substring(lore.lastIndexOf(' ') + 1);
                        AlchemyAttribute aa = AlchemyAttribute.searchColor(ingredients.substring(0, 2));
                        if (type == aa) {
                            allLevel += Integer.parseInt(ingredients.substring(2));
                        }
                    } else {
                        break;
                    }
                } else if (lore.startsWith(AlchemyItemStatus.Type.ALCHEMY_INGREDIENTS.getCheck())) {
                    loreArea = true;
                }
            }
            return allLevel;
        }
        return 0;
    }

}
