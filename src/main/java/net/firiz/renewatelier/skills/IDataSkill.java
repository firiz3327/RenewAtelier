package net.firiz.renewatelier.skills;

public interface IDataSkill<T extends ISkillData> extends ISkill {

    T getData();

}
