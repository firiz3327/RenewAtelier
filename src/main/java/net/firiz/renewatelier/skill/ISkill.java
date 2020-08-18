package net.firiz.renewatelier.skill;

import org.bukkit.entity.Player;

public interface ISkill<T extends SkillData> {

    void fire();

    Player getPlayer();

    T getData();

}
