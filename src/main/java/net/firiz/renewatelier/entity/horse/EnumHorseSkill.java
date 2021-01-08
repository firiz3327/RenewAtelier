package net.firiz.renewatelier.entity.horse;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.firiz.renewatelier.utils.java.CollectionUtils;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.List;

public enum EnumHorseSkill {
    TWO_SEATER(
            "二人乗り",
            10
    ) {
        @Override
        public List<String> getDescription(int level) {
            return Arrays.asList(
                    ChatColor.GRAY + "移動速度が落ちますが、馬に二人乗せることができます。",
                    ChatColor.WHITE + "移動速度低下: " + ChatColor.GREEN + (20 - level) + " ％"
            );
        }
    },
    START_BOOST(
            "瞬発力",
            10
    ) {
        @Override
        public List<String> getDescription(int level) {
            return Arrays.asList(
                    ChatColor.GRAY + "止まっている状態から動き始めた時、加速します。",
                    ChatColor.WHITE + "移動速度上昇: " + ChatColor.GREEN + (2 + (level * 0.5)) + " 秒"
            );
        }
    },
    BOOST(
            "ダッシュ",
            10
    ) {
        @Override
        public List<String> getDescription(int level) {
            return Arrays.asList(
                    ChatColor.GRAY + "サドルを持って右クリックすると加速します。",
                    ChatColor.WHITE + "クールタイム: " + ChatColor.GREEN + (20 - level) + " 秒"
            );
        }
    };

    private final String name;
    private final int maxLevel;

    private static final List<EnumHorseSkill> normalHorseSkills = new ObjectArrayList<>();
    private static final List<EnumHorseSkill> rareHorseSkills = new ObjectArrayList<>();

    static {
        CollectionUtils.add(normalHorseSkills, START_BOOST, BOOST);
        CollectionUtils.add(rareHorseSkills, TWO_SEATER, START_BOOST, BOOST);
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

    public abstract List<String> getDescription(int level);

    public static EnumHorseSkill random(HorseTier tier) {
        return tier.isRare() ? CollectionUtils.getRandomValue(rareHorseSkills) : CollectionUtils.getRandomValue(normalHorseSkills);
    }
}
