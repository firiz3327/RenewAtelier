package net.firiz.renewatelier.skills.character.passive.buff;

import net.firiz.renewatelier.buff.BuffData;
import net.firiz.renewatelier.buff.PassiveBuff;
import net.firiz.renewatelier.entity.player.Char;
import net.firiz.renewatelier.entity.player.stats.CharStats;
import net.firiz.renewatelier.skills.character.passive.PassiveSkill;

public class BuffPassiveSkill extends PassiveSkill {

    private final BuffData buffData;

    protected BuffPassiveSkill(Char character, BuffData buffData) {
        super(character);
        this.buffData = buffData;
    }

    @Override
    public boolean fire() {
        final CharStats stats = getCharacter().getCharStats();
        stats.addPassiveBuff(new PassiveBuff(stats, buffData));
        return false;
    }
}
