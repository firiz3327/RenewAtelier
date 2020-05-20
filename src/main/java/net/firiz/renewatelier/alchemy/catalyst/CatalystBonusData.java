package net.firiz.renewatelier.alchemy.catalyst;

import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.firiz.renewatelier.alchemy.material.AlchemyAttribute;
import net.firiz.renewatelier.characteristic.Characteristic;
import net.firiz.renewatelier.utils.pair.ImmutablePair;
import net.md_5.bungee.api.ChatColor;

/**
 * @author firiz
 */
public class CatalystBonusData {

    private static final String ERROR_NAME = "ERROR";
    private final BonusType type;
    private final int x;
    private final Object y;

    public CatalystBonusData(BonusType type, int x) {
        this.type = type;
        this.x = x;
        this.y = null;
    }

    public CatalystBonusData(BonusType type, int x, String y) {
        this.type = type;
        this.x = x;
        this.y = type.yConversionToObject(y);
    }

    public String getName() {
        return type.name
                .replace("$x", (x >= 0 ? "+" : "-") + x)
                .replace("$y", y != null ? (String) type.yConversionToString(y) : ERROR_NAME);
    }

    public List<String> getDesc() {
        final String[] desc = type.desc
                .replace("$x", String.valueOf(x))
                .replace("$y", y != null ? (String) type.yConversionToString(y) : ERROR_NAME)
                .replace("$z", type.descRepletion != null ? type.descRepletion.apply(x) : ERROR_NAME)
                .split("\n");
        final List<String> result = new ObjectArrayList<>();
        for (final String str : desc) {
            result.add(ChatColor.GRAY + str);
        }
        return result;
    }

    public BonusType getType() {
        return type;
    }

    public int getX() {
        return x;
    }

    public Object getY() {
        return y;
    }

    public enum BonusType {
        INHERITING(
                "特性引継ぎ可能数$x",
                "調合終了時に引き\n継がれる特性の数が\n$xつ$zます",
                false,
                null,
                o -> o >= 0 ? "増え" : "減り"
        ), // ok
        QUALITY(
                "品質$x",
                "できあがるアイテムの\n最終的な品質が\n$z$xされます",
                false,
                null,
                o -> o >= 0 ? "+" : "-"
        ), // ok
        QUALITY_PERCENT(
                "品質$x%",
                "できあがるアイテムの\n最終的な品質が\n$x%$zします",
                false,
                null,
                o -> o >= 0 ? "％増加" : "％減少"
        ), // ok
        AMOUNT(
                "作成数$x",
                "",
                false,
                null,
                o -> o >= 0 ? "増加" : "減少"
        ), // ok
        USECOUNT(
                "使用回数$x",
                "できあがるアイテムが\n使用できる場合、使用\n回数が$x回$zます",
                false,
                null,
                o -> o >= 0 ? "増え" : "減り"
        ), // 使用回数ありのアイテムを作っていない
        STARLEVEL(
                "効果レベル$x・$y",
                "$y色で示されている\n効果のレベルを全て\n$x段階$zさせます",
                false,
                new ImmutablePair<>(
                        AlchemyAttribute::valueOf,
                        o -> ((AlchemyAttribute) o).getName()
                ),
                o -> o >= 0 ? "アップ" : "ダウン"
        ), // ok
        SIZE(
                "サイズ$x",
                "できあがるアイテムの\n最終的なサイズが\n$x段階$zなります",
                false,
                null,
                o -> o >= 0 ? "大きく" : "小さく"
        ),
        INGREDIENT_AMOUNT_PERCENT(
                "錬金成分量$x%",
                "次に投入する材料の\n錬金成分量が通常より\n$x%$zします",
                true,
                null,
                o -> o >= 0 ? "増加" : "減少"
        ), // ok
        CHARACTERISTIC("$y付与",
                "できあがるアイテムに\n「$y」の特性が\n追加されます",
                false,
                new ImmutablePair<>(
                        id -> {
                            Characteristic c;
                            try {
                                c = Characteristic.getCharacteristic(id);
                            } catch (IllegalArgumentException e) {
                                c = Characteristic.search(id);
                            }
                            return c;
                        },
                        o -> ((Characteristic) o).getName()
                ),
                null
        ); // ok

        private final String name;
        private final String desc;
        private final boolean once; // 使い切りであるかどうか
        private final ImmutablePair<Function<String, Object>, Function<Object, String>> yParse; // FinalDoubleData<文字列から特定のデータへ変換用, オブジェクトからデータ取得用>
        private final IntFunction<String> descRepletion;

        BonusType(
                final String name,
                final String desc,
                final boolean once,
                final ImmutablePair<Function<String, Object>, Function<Object, String>> yParse,
                final IntFunction<String> descRepletion) {
            this.name = name;
            this.desc = desc;
            this.once = once;
            this.yParse = yParse;
            this.descRepletion = descRepletion;
        }

        public boolean isOnce() {
            return once;
        }

        /**
         * 文字列から特定のオブジェクトに変換します
         *
         * @param y
         * @return
         */
        private Object yConversionToObject(final String y) {
            if (yParse != null) {
                return yParse.getLeft().apply(y);
            }
            return y;
        }

        /**
         * 特定のオブジェクトから特定の文字列に変換します
         *
         * @param y
         * @return
         */
        private Object yConversionToString(final Object y) {
            if (yParse != null) {
                return yParse.getRight().apply(y);
            }
            return y;
        }
    }

}
