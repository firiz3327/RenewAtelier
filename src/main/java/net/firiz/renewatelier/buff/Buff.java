package net.firiz.renewatelier.buff;

import net.firiz.ateliercommonapi.loop.LoopManager;
import net.firiz.ateliercommonapi.loop.TickRunnable;
import net.firiz.renewatelier.damage.DamageUtilV2;
import net.firiz.renewatelier.entity.EntityStatus;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Buff {

    private static final LoopManager loopManager = LoopManager.INSTANCE;

    private final BuffData buffData;
    private final TickRunnable timer;
    private int taskId;

    private EntityStatus status;
    private int duration;
    private boolean end;
    private Runnable endHandler;

    public Buff(EntityStatus status, BuffData buffData, int duration) {
        this(status, buffData.buffValueType, buffData.level, buffData.type, duration, buffData.limitDuration, buffData.x, buffData.y);
    }

    public Buff(EntityStatus status, BuffData buffData, int duration, int x) {
        this(status, buffData.buffValueType, buffData.level, buffData.type, duration, buffData.limitDuration, x, buffData.y);
    }

    public Buff(EntityStatus status, BuffValueType buffValueType, int level, BuffType type, int duration, int limitDuration, int x, @Nullable String y) {
        this.status = status;
        this.buffData = new BuffData(
                buffValueType,
                level,
                type,
                limitDuration,
                x,
                y
        );
        this.duration = duration;
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
        return buffData.buffValueType;
    }

    public int getLevel() {
        return buffData.level;
    }

    public BuffType getType() {
        return buffData.type;
    }

    public int getX() {
        return buffData.x;
    }

    @Nullable
    public String getY() {
        return buffData.y;
    }

    public boolean isEnd() {
        return end;
    }

    public void setEndHandler(Runnable handler) {
        this.endHandler = handler;
    }

    public void startTimer() {
        taskId = loopManager.addSeconds(timer);
    }

    private boolean incrementTimer() {
        duration++;
        final Entity entity = status.getEntity();
        if (entity instanceof LivingEntity) {
            final Location eyeLocation = ((LivingEntity) entity).getEyeLocation();
            boolean dot = false;
            switch (buffData.type) {
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
                    DamageUtilV2.INSTANCE.abnormalDamage(status, -buffData.x);
                    break;
                default: // 想定しない
                    break;
            }
            if (dot) {
                entity.playEffect(EntityEffect.HURT);
                DamageUtilV2.INSTANCE.abnormalDamage(status, buffData.x);
            }
        }
        return buffData.limitDuration <= duration;
    }

    public void stopTimer() {
        loopManager.removeSeconds(taskId);
        end = true;
        endHandler.run();
    }
}
