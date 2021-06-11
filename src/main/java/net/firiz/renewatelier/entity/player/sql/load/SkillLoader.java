package net.firiz.renewatelier.entity.player.sql.load;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.firiz.renewatelier.skills.character.IPlayerSkillBuilder;
import net.firiz.renewatelier.skills.character.passive.EnumPlayerPassiveSkill;
import net.firiz.renewatelier.skills.character.skill.EnumPlayerSkill;
import net.firiz.renewatelier.sql.SQLManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SkillLoader implements StatusLoader<List<IPlayerSkillBuilder>> {

    @NotNull
    @Override
    public List<IPlayerSkillBuilder> load(int id) {
        final List<List<Object>> skillsObj = SQLManager.INSTANCE.select(
                "skills",
                new String[]{"userId", "skill", "isPassive"},
                new Object[]{id}
        );
        final List<IPlayerSkillBuilder> skills = new ObjectArrayList<>();
        skillsObj.forEach(objects -> {
            final String skillName = (String) objects.get(1);
            final boolean isPassive = (boolean) objects.get(2);
            if (isPassive) {
                skills.add(EnumPlayerPassiveSkill.valueOf(skillName));
            } else {
                skills.add(EnumPlayerSkill.valueOf(skillName));
            }
        });
        return skills;
    }
}
