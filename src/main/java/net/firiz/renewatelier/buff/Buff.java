package net.firiz.renewatelier.buff;

import net.firiz.renewatelier.damage.DamageUtilV2;
import net.firiz.renewatelier.entity.EntityStatus;
import net.firiz.renewatelier.loop.LoopManager;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Buff {

    private EntityStatus status;
    private final BuffValueType buffValueType;
    private final int level;
    private final BuffType type;
    private final int limitDuration;
    private final int x;
    @Nullable
    private final String y;
    private final Runnable timer;

    private static final LoopManager loopManager = LoopManager.INSTANCE;
    private int duration;

    private boolean end;
    private Runnable endHandler;

    public Buff(EntityStatus status, BuffValueType buffValueType, int level, BuffType type, int duration, int limitDuration, int x, @Nullable String y) {
        this.status = status;
        this.buffValueType = buffValueType;
        this.level = level;
        this.type = type;
        this.duration = duration;
        this.limitDuration = limitDuration;
        this.x = x;
        this.y = y;
        this.timer = () -> {
            if (incrementTimer() || status.getEntity().isDead()) {
                stopTimer();
            }
        };
    }

    public void setStatus(@NotNull EntityStatus status) {
        this.status = Objects.requireNonNull(status);
    }

    public BuffValueType getBuffValueType() {
        return buffValueType;
    }

    public int getLevel() {
        return level;
    }

    public BuffType getType() {
        return type;
    }

    public int getX() {
        return x;
    }

    @Nullable
    public String getY() {
        return y;
    }

    public boolean isEnd() {
        return end;
    }

    public void setEndHandler(Runnable handler) {
        this.endHandler = handler;
    }

    public void startTimer() {
        loopManager.addSec(timer);
    }

    private boolean incrementTimer() {
        duration++;
        final Entity entity = status.getEntity();
        if (entity instanceof LivingEntity) {
            final Location eyeLocation = ((LivingEntity) entity).getEyeLocation();
            boolean dot = false;
            switch (type) {
                case POISON:
                    dot = true;
                    eyeLocation.getWorld().spawnParticle(Particle.SPELL_WITCH, eyeLocation, 2);
                    break;
                case BURN:
                    dot = true;
                    eyeLocation.getWorld().spawnParticle(Particle.SMOKE_NORMAL, eyeLocation, 2);
                    break;
                case DOT:
                    dot = true;
                    eyeLocation.getWorld().spawnParticle(Particle.CRIT_MAGIC, eyeLocation, 2);
                    break;
                case AUTO_HEAL:
                    eyeLocation.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, eyeLocation, 2);
                    DamageUtilV2.INSTANCE.abnormalDamage(status, -x);
                    break;
                default: // 想定しない
                    break;
            }
            if (dot) {
                entity.playEffect(EntityEffect.HURT);
                DamageUtilV2.INSTANCE.abnormalDamage(status, x);
            }
        }
        return limitDuration <= duration;
    }

    public void stopTimer() {
        loopManager.removeSec(timer);
        end = true;
        endHandler.run();
    }
}
