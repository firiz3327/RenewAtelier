package net.firiz.renewatelier.entity.horse;

import com.google.gson.annotations.Expose;
import net.firiz.renewatelier.utils.java.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class HorseSkillList {

    public static class HorseEntry {
        @Expose
        private final EnumHorseSkill skill;
        @Expose
        private int level;

        public HorseEntry(EnumHorseSkill skill, int level) {
            this.skill = skill;
            this.level = level;
        }

        public EnumHorseSkill getSkill() {
            return skill;
        }

        public int getLevel() {
            return level;
        }
    }

    @Expose
    private final List<HorseEntry> skillList;

    public HorseSkillList() {
        this.skillList = new ArrayList<>();
    }

    @NotNull
    public Set<HorseEntry> entrySet() {
        return new LinkedHashSet<>(skillList);
    }

    public boolean isEmpty() {
        return skillList.isEmpty();
    }

    public int size() {
        return skillList.size();
    }

    public EnumHorseSkill random() {
        return CollectionUtils.getRandomValue(skillList).skill;
    }

    public void put(EnumHorseSkill skill, int level) {
        int index = getIndex(skill);
        if (index == -1) {
            skillList.add(new HorseEntry(skill, level));
        } else {
            skillList.get(index).level = level;
        }
    }

    private int getIndex(EnumHorseSkill skill) {
        final List<HorseEntry> pairs = new ArrayList<>(skillList);
        for (int i = 0, pairsSize = pairs.size(); i < pairsSize; i++) {
            final HorseEntry pair = pairs.get(i);
            if (pair.skill == skill) {
                return i;
            }
        }
        return -1;
    }

    public int remove(EnumHorseSkill skill) {
        return Objects.requireNonNullElse(skillList.remove(getIndex(skill)).level, -1);
    }

    public boolean containsKey(EnumHorseSkill skill) {
        return getIndex(skill) != -1;
    }

    public int getInt(EnumHorseSkill skill) {
        return Objects.requireNonNullElse(skillList.get(getIndex(skill)).level, -1);
    }

}
