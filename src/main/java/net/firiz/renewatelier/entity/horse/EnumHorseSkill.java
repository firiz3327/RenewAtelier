package net.firiz.renewatelier.entity.horse;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.firiz.ateliercommonapi.adventure.text.C;
import net.firiz.ateliercommonapi.adventure.text.Lore;
import net.firiz.ateliercommonapi.adventure.text.Text;
import net.firiz.renewatelier.utils.java.CollectionUtils;

import java.util.List;

public enum EnumHorseSkill {
    TWO_SEATER("二人乗り", 10) {
        @Override
        public Lore getDescription(int level) {
            return Lore.asLore(
                    Text.of("移動速度が落ちますが、馬に二人乗せることができます。", C.GRAY),
                    Text.of("移動速度低下: ").color(C.WHITE).append((20 - level) + " ％").color(C.GREEN)
            );
        }
    },
    START_BOOST("瞬発力", 10) {
        @Override
        public Lore getDescription(int level) {
            return Lore.asLore(
                    Text.of("止まっている状態から動き始めた時、加速します。", C.GRAY),
                    Text.of("移動速度上昇: ").color(C.WHITE).append((2 + (level * 0.5)) + " 秒").color(C.GREEN),
                    Text.of("クールタイム: ").color(C.WHITE).append("30 秒").color(C.GREEN)
            );
        }
    },
    BOOST("ダッシュ", 10) {
        @Override
        public Lore getDescription(int level) {
            return Lore.asLore(
                    Text.of("サドルを持って右クリックすると加速します。", C.GRAY),
                    Text.of("移動速度上昇: ").color(C.WHITE).append("2 秒").color(C.GREEN),
                    Text.of("クールタイム: ").color(C.WHITE).append((20 - level) + " 秒").color(C.GREEN)
            );
        }
    },
    ACCELERATION("加速", 10) {
        @Override
        public Lore getDescription(int level) {
            return Lore.asLore(
                    Text.of("加速しきるまでにかかる時間が早くなります。", C.GRAY),
                    Text.of("移動速度上昇: ").color(C.WHITE).append((2 + (level * 0.1)) + " ％").color(C.GREEN)
            );
        }
    };

    private final String name;
    private final int maxLevel;

    private static final List<EnumHorseSkill> normalHorseSkills = new ObjectArrayList<>();
    private static final List<EnumHorseSkill> rareHorseSkills = new ObjectArrayList<>();

    static {
        CollectionUtils.add(
                normalHorseSkills,
                START_BOOST, BOOST, ACCELERATION
        );
        CollectionUtils.add(
                rareHorseSkills,
                TWO_SEATER, START_BOOST, BOOST, ACCELERATION
        );
    }

    EnumHorseSkill(String name, int maxLevel) {
        this.name = name;
        this.maxLevel = maxLevel;
    }

    public String getName() {
        return name;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public abstract Lore getDescription(int level);

    public static EnumHorseSkill random(HorseTier tier) {
        return tier.isRare() ? CollectionUtils.getRandomValue(rareHorseSkills) : CollectionUtils.getRandomValue(normalHorseSkills);
    }
}
