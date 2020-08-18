package net.firiz.renewatelier.skill.character;

import net.firiz.renewatelier.skill.character.data.CharSkillData;

public enum EnumCharSkill {
    STONE_SHOOT(true, new CharSkillData() {
    });

    private final boolean bow;
    private final CharSkillData data;

    EnumCharSkill(boolean bow, CharSkillData data) {
        this.bow = bow;
        this.data = data;
    }
}
