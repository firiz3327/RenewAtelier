package net.firiz.renewatelier.item.json.itemeffect;

import net.firiz.renewatelier.buff.Buff;
import net.firiz.renewatelier.buff.BuffData;
import net.firiz.renewatelier.version.entity.atelier.AtelierEntityUtils;
import net.firiz.renewatelier.version.entity.atelier.LivingData;
import org.bukkit.entity.LivingEntity;

class BuffMobHitEffect implements MobHitEffect {

    private static final AtelierEntityUtils aEntityUtils = AtelierEntityUtils.INSTANCE;

    private final BuffData buffData;

    BuffMobHitEffect(BuffData buffData) {
        this.buffData = buffData;
    }

    @Override
    public void accept(LivingEntity entity) {
        if (aEntityUtils.hasLivingData(entity)) {
            final LivingData livingData = aEntityUtils.getLivingData(entity);
            if(livingData.hasStats()) {
                assert livingData.getStats() != null;
                livingData.getStats().addBuff(new Buff(livingData.getStats(), buffData, 0));
            }
        }
    }
}
