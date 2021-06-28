package net.firiz.renewatelier.language;

import net.firiz.ateliercommonapi.adventure.text.C;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public enum Lang {
    HORSE_SKILL_TWO_SEATER("atelier.horse.skill.two_seater"), // 二人乗り
    HORSE_SKILL_TWO_SEATER_DESC1("atelier.horse.skill.two_seater.desc1", C.GRAY), // 移動速度が落ちますが、馬に二人乗せることができます。
    HORSE_SKILL_TWO_SEATER_DESC2("atelier.horse.skill.two_seater.desc2", 1, C.WHITE, C.GREEN), // 移動速度低下: %s ％
    HORSE_SKILL_START_BOOST("atelier.horse.skill.start_boost"), // 瞬発力
    HORSE_SKILL_START_BOOST_DESC1("atelier.horse.skill.start_boost.desc1", C.GRAY), // 止まっている状態から動き始めた時、加速します。
    HORSE_SKILL_START_BOOST_DESC2("atelier.horse.skill.start_boost.desc2", 1, C.WHITE, C.GREEN), // 移動速度上昇: %s 秒
    HORSE_SKILL_START_BOOST_DESC3("atelier.horse.skill.start_boost.desc3", 1, C.WHITE, C.GREEN), // クールタイム: %s 秒
    HORSE_SKILL_BOOST("atelier.horse.skill.boost"), // ダッシュ
    HORSE_SKILL_BOOST_DESC1("atelier.horse.skill.boost.desc1", C.GRAY), // サドルを持って右クリックすると加速します。
    HORSE_SKILL_BOOST_DESC2("atelier.horse.skill.boost.desc2", 1, C.WHITE, C.GREEN), // 移動速度上昇: %s 秒
    HORSE_SKILL_BOOST_DESC3("atelier.horse.skill.boost.desc3", 1, C.WHITE, C.GREEN), // クールタイム: %s 秒
    HORSE_SKILL_ACCELERATION("atelier.horse.skill.acceleration"), // 加速
    HORSE_SKILL_ACCELERATION_DESC1("atelier.horse.skill.acceleration.desc1", C.GRAY), // 加速しきるまでにかかる時間が早くなります。
    HORSE_SKILL_ACCELERATION_DESC2("atelier.horse.skill.acceleration.desc2", 1, C.WHITE, C.GREEN), // 移動速度上昇: %s ％

    HORSE_LEVELUP("atelier.horse.levelup", 1, C.GRAY), // あなたの馬が %s にレベルアップしました！
    HORSE_LEVELUP_STATUSUP("atelier.horse.levelup.statusup", 2, C.GRAY), // 移動速度%1$s, ジャンプ力%2$s
    HORSE_LEVELUP_MAX("atelier.horse.levelup.max", C.GREEN), // あなたの馬は最大レベルに到達しました！
    HORSE_LEVELUP_SKILL_NEW("atelier.horse.levelup.skill.new", 1, C.GREEN), // 新しく %s を覚えました！
    HORSE_LEVELUP_SKILL_LVUP("atelier.horse.levelup.skill.lvup", 1, C.GREEN), // %s のレベルが上がりました！

    HORSE_SADDLE_LORE_DESC_RANK("atelier.horse.saddle.desc.rank", 1, C.GRAY, C.WHITE),
    HORSE_SADDLE_LORE_DESC_GENDER("atelier.horse.saddle.desc.gender", 1, C.GRAY, C.WHITE),
    HORSE_SADDLE_LORE_DESC_SPEED("atelier.horse.saddle.desc.speed", 1, C.GRAY, C.WHITE),
    HORSE_SADDLE_LORE_DESC_JUMP("atelier.horse.saddle.desc.jump", 1, C.GRAY, C.WHITE),
    HORSE_SADDLE_LORE_DESC_SKILL("atelier.horse.saddle.desc.skill", C.GRAY),
    HORSE_SADDLE_LORE_DESC_MATING("atelier.horse.saddle.desc.mating", 1, C.GRAY),
    HORSE_SADDLE_LORE_DESC_MATING_TIME("atelier.horse.saddle.desc.mating.time", 1, C.GRAY, C.WHITE),

    HORSE_GENDER_FEMALE("atelier.horse.gender.female"),
    HORSE_GENDER_MALE("atelier.horse.gender.male"),
    ;

    private final TranslatableComponent component;
    private final int length;
    private final TextColor[] childColors;

    Lang(String key) {
        this(key, 0, null);
    }

    Lang(String key, @Nullable TextColor mainColor) {
        this(key, 0, mainColor);
    }

    Lang(String key, int length) {
        this(key, length, null);
    }

    Lang(String key, int length, @Nullable TextColor mainColor, TextColor... childColors) {
        this.component = Component.translatable(key).color(mainColor);
        this.length = length;
        this.childColors = childColors;
    }

    public TranslatableComponent get(ComponentLike... args) {
        if (this.length != args.length) {
            throw new IllegalArgumentException("The length of the argument is insufficient.");
        }
        return component.args(args);
    }

    public TranslatableComponent get(Object... args) {
        if (this.length != args.length) {
            throw new IllegalArgumentException("The length of the argument is insufficient.");
        }
        final ComponentLike[] argComponents = new ComponentLike[args.length];
        final Function<String, Component> function = Component::text;
        for (int i = 0, size = args.length; i < size; i++) {
            Component c = function.apply(args[i].toString());
            if (i < childColors.length) {
                c = c.color(childColors[i]);
            }
            argComponents[i] = c;
        }
        return component.args(argComponents);
    }
}
