package net.firiz.renewatelier.skill.character;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.firiz.renewatelier.skill.character.data.CharSkillData;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.IntStream;

public final class CharSkillManager {

    private final CSD csd = new CSD();

    public void click(final boolean right) {
        csd.click(right);
    }

    private void skill(@NotNull final CharSkillData data) {
        
    }

    public void putSkill(final int[] key, @NotNull final CharSkillData skillData) {
        csd.charSkillDataMap.put(key, skillData);
    }

    private class CSD {
        private static final int LEN = 3;
        final Map<int[], CharSkillData> charSkillDataMap = new Object2ObjectOpenHashMap<>();
        final int[] clicks = new int[LEN];

        void click(final boolean right) {
            if (clicks[LEN - 1] == 0) {
                clicks[IntStream.range(0, LEN).filter(i -> clicks[i] == 0).findFirst().orElse(0)] = right ? 2 : 1;
                if (clicks[LEN - 1] != 0) {
                    charSkillDataMap
                            .entrySet()
                            .stream()
                            .filter(entry -> Arrays.equals(entry.getKey(), clicks))
                            .map(Map.Entry::getValue).findFirst()
                            .ifPresent(CharSkillManager.this::skill);
                }
            } else {
                clicks[0] = right ? 2 : 1;
                for (int i = 1; i < LEN; i++) {
                    clicks[i] = 0;
                }
            }
        }

    }

}
