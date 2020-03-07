package net.firiz.renewatelier.entity.monster;

import net.firiz.renewatelier.buff.Buff;

import java.util.Collection;

public interface StatusMonster {

    int getAtk();
    int getDef();
    int getSpeed();
    void addBuff(Buff buff);
    Collection<Buff> getBuffs();

}
