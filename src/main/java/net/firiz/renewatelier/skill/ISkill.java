package net.firiz.renewatelier.skill;

import org.bukkit.entity.Player;

public interface ISkill<T extends SkillData> {

    boolean fire();

    Player getPlayer();

    T getData();

}
