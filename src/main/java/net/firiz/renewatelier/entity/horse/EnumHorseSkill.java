package net.firiz.renewatelier.entity.horse;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.firiz.ateliercommonapi.adventure.text.C;
import net.firiz.ateliercommonapi.adventure.text.Lore;
import net.firiz.ateliercommonapi.adventure.text.Text;
import net.firiz.renewatelier.language.Lang;
import net.firiz.renewatelier.utils.java.CollectionUtils;
import net.kyori.adventure.text.Component;

import java.util.List;

public enum EnumHorseSkill {
    TWO_SEATER(Lang.HORSE_SKILL_TWO_SEATER.get(), 10) {
        @Override
        public Lore getDescription(int level) {
            return Lore.asLore(
                    Lang.HORSE_SKILL_TWO_SEATER_DESC1.get(),
                    Lang.HORSE_SKILL_TWO_SEATER_DESC2.get(20 - level)
            );
        }
    },
    START_BOOST(Lang.HORSE_SKILL_START_BOOST.get(), 10) {
        @Override
        public Lore getDescription(int level) {
            return Lore.asLore(
                    Lang.HORSE_SKILL_START_BOOST_DESC1.get(),
                    Lang.HORSE_SKILL_START_BOOST_DESC2.get(2 + (level * 0.5)),
                    Lang.HORSE_SKILL_START_BOOST_DESC3.get(30)
            );
        }
    },
    BOOST(Lang.HORSE_SKILL_BOOST.get(), 10) {
        @Override
        public Lore getDescription(int level) {
            return Lore.asLore(
                    Lang.HORSE_SKILL_BOOST_DESC1.get(),
                    Lang.HORSE_SKILL_BOOST_DESC2.get(2),
                    Lang.HORSE_SKILL_BOOST_DESC3.get(20 - level)
            );
        }
    },
    ACCELERATION(Lang.HORSE_SKILL_ACCELERATION.get(), 10) {
        @Override
        public Lore getDescription(int level) {
            return Lore.asLore(
                    Lang.HORSE_SKILL_ACCELERATION_DESC1.get(),
                    Lang.HORSE_SKILL_ACCELERATION_DESC2.get(2 + (level * 0.1))
            );
        }
    };

    private final Component name;
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

    EnumHorseSkill(Component name, int maxLevel) {
        this.name = name;
        this.maxLevel = maxLevel;
    }

    public Component getName() {
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
