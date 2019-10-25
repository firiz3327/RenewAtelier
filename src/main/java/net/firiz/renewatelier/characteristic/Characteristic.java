package net.firiz.renewatelier.characteristic;

import net.firiz.renewatelier.config.ConfigManager;
import net.firiz.renewatelier.config.CharacteristicLoader;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class Characteristic {

    private static final ConfigManager CONFIG_MANAGER = ConfigManager.INSTANCE;
    private final String id;
    private final int lv;
    private final String name;
    private final String desc;
    private final CharacteristicCategory[] categorys;
    private final List<List<String>> reqIds;
    private final Map<CharacteristicType, Object> datas;

    public Characteristic(String id, int lv, String name, String desc, CharacteristicCategory[] categorys, List<List<String>> reqIds, Map<CharacteristicType, Object> datas) {
        this.id = id;
        this.lv = lv;
        this.name = name;
        this.desc = desc;
        this.categorys = categorys;
        this.reqIds = reqIds;
        this.datas = datas;
    }

    public String getId() {
        return id;
    }

    public int getLevel() {
        return lv;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public List<List<String>> getReqIds() {
        return reqIds;
    }

    public CharacteristicCategory[] getCategorys() {
        return categorys;
    }

    public boolean hasData(CharacteristicType type) {
        return datas.containsKey(type);
    }

    public Object getData(CharacteristicType type) {
        return datas.get(type);
    }

    @NotNull
    public static Characteristic getCharacteristic(@NotNull final String id) {
        for (final Characteristic characteristic : CONFIG_MANAGER.getList(CharacteristicLoader.class, Characteristic.class)) {
            if (characteristic.getId().equalsIgnoreCase(id)) {
                return characteristic;
            }
        }
        throw new IllegalArgumentException(id.concat(" not found."));
    }

    @NotNull
    public static Characteristic search(String name) {
        for (final Characteristic characteristic : CONFIG_MANAGER.getList(CharacteristicLoader.class, Characteristic.class)) {
            if (characteristic.getName().equals(name)) {
                return characteristic;
            }
        }
        return getCharacteristic(name);
    }

    /*
    デバフ系確率調査
    3/10 30% 眠り付与 sleep
    6/10 60% 毒付与 poison
    8/12 60% 呪い付与 curse
    3/12 25% 重力を操る slow
    */
    public enum CharacteristicType {
        SELL, // 売価％
        QUALITY, // 品質％
        DAMAGE, // 攻撃アイテム効果％
        DAMAGE_FIXED, //威力固定強化 <最低値, 最大値>
        HEAL, // 回復力増加％
        HEAL_FIXED, // 回復固定強化 <最低値, 最大値>
        POWER, // 威力値をx％増減
        CRITICAL, // クリティカル率をx％増減
        DEATH, // 0=ボスを除き一撃死, 1=一部ボスを除き一撃死
        RANDOM, // 効果安定
        USECOUNT, // 使用回数増減
        STATS_HP, // HP強化％
        STATS_MP, // MP強化％
        STATS_ATK, // 攻撃力強化％
        STATS_DEF, // 防御力強化％
        STATS_SPD, // 素早さ強化％
        STATS_HP_FIXED, // HP強化
        STATS_MP_FIXED, // MP強化
        STATS_ATK_FIXED, // 攻撃力強化
        STATS_DEF_FIXED, // 防御力強化
        STATS_SPD_FIXED, // 素早さ強化
        STATS_HP_QUALITY, // HP強化 品質で上下
        STATS_MP_QUALITY, // MP強化 品質で上下
        STATS_ATK_QUALITY, // 攻撃力強化 品質で上下
        STATS_DEF_QUALITY, // 防御力強化 品質で上下
        STATS_SPD_QUALITY, // 素早さ強化 品質で上下
        RESISTANCE, // 耐性（用途不明）
        CONSUME_MP, // 消費MP％
        SKILL_DAMAGE, // スキル威力％
        CRITICAL_RACE, // クリティカル率％<確率, 種族(Race class)>
        CHASING, // 追い打ち強化
        CHARACTERISTIC_POWER, // 特性強化 威力値が[(付与されている特性レベル合計値の0.7乗)+x]％上昇
        MULTIPLE_BONUS, // 複数ボーナス 威力値が[効果範囲の対象者*x]％上昇
        SINGULAR_BONUS, // 少数ボーナス 威力値が[x/効果範囲の対象者]％上昇
        USE_UP, // 使い切り強化 使用回数が1回の時、威力値をx％増減
        USE_ONE, // 強制使い切り
        COUNT_POWER, // 回数補正 威力値が[最大使用回数*x]％増減
        USE_SPEED, // アイテム待機時間 アイテム使用のクールタイムx％増減
        USE_SPEED_POWER, // 待機時間強化 威力値が[アイテム使用時の待機時間*x]％増減
        SIZE, // サイズ増減
        SIZE_POWER, // サイズで強化 威力値が[アイテムのサイズ*x]％上昇
        STATS_ACC, // 命中率強化％
        STATS_ACC_FIXED, // 命中率強化
        BREAK_DAMAGE, // ブレイク耐性にxダメージ貫通効果
        TAKEN_DAMAGE_REDUCTION, // 被ダメージのx％分回復
        CLEAR_DEBUFF, // 使用：状態異常を解除、武具：状態異常を無効化 <Debuff名>
        SKILL_SPEED, // スキル待機時間 スキル使用のクールタイムx％増減
        ACTION_SPEED, // 待機時間軽減 <確率, 行動時間軽減％>
        CONSUME_MP_REDUCTION, // 消費MPのx％分回復
        DAMAGE_MITIGATION, // ダメージ軽減
        RESISTANCE_ATTRIBUTE, // 属性耐性 レベル段階上昇 <AttackAttribute, レベル>
        SWAP_HP_MP, // HPとMPを入れ替え
        STATS_AVO, // 回避強化％
        STATS_AVO_FIXED, // 回避強化
        STATS_AVO_QUALITY, // 回避強化 品質で上下
        SUPPORT_ATTACK, // サポートアタック効果％
        SUPPORT_GUARD, // サポートガード効果％
        ACTION_HEAL_MP, // 行動時MP回復％
        SKILL_POWER_FIXED, // スキル威力値強化
        ADD_ATTACK, // 追加攻撃 <AddAttackType, 確率, (-1=全ての攻撃 0=スキル以外 1=アイテムのみ　2=武器のみ 3=通常攻撃のみ), AddAttackTypeによる値...>
        BUFF, // バフ・デバフ <BuffType, 確率, 時間, 値>
        ADD_WAIT_TIME, // 待機時間％
        LEVEL_STATS_UP, // 期待の新星用 レベル*x分攻撃力、防御力、素早さ上昇
        RESPAWN, // 復活 <確率, 復活後残りHP％>
        AWAKENING_OF_COURAGE, // 勇気の覚醒用 未実装
        BURST_MODE, // バーストモード補正％ 未実装
        GAGE, // ゲージ補正％ 未実装
        RESISTANCE_BREAK, // ブレイク耐性
        HEAL_SPEED_BREAK, // ブレイク耐性回復速度
        ;

    }

    public class CharacteristicValue<T> {
    }

    protected static final CharacteristicType[] alchemyKettleBonusTypes = {
            CharacteristicType.QUALITY,
            CharacteristicType.USECOUNT,
            CharacteristicType.SIZE
    };

    public enum Race {
        ANIMAL(0), // 動物
        DRAGON(1), // ドラゴン
        ELEMENTAL(2), // 精霊
        ALCHEMY(3), // 錬金生物
        AIJN(4), // 亜人
        PUNI(5), // プニ
        MAGIC(6), // 魔法生物
        UNDEAD(7), // 幽霊・アンデッド
        DEMON(8), // 悪魔・デーモン
        GOD(9); // 神族

        private final int id;

        Race(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    public enum Debuff {
        ALL,
        SLEEP,
        POISON,
        SLOW,
        CURSE,
        DARKNESS,
        WEAKNESS,
        DISABLE_HEAL,
        SLOW_SKILL,
        BREAK_DAMAGE
    }

}
