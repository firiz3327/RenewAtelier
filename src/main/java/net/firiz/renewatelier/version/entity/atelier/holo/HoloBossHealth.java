package net.firiz.renewatelier.version.entity.atelier.holo;

import net.firiz.renewatelier.version.entity.atelier.LivingData;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class HoloBossHealth extends AbstractHoloHealth {

    private final BossBar bossBar;

    public HoloBossHealth(@NotNull LivingEntity entity, @NotNull LivingData livingData, @NotNull String customName) {
        super(entity, Objects.requireNonNull(livingData), customName);
        this.bossBar = BossBar.bossBar(Component.text(customName), 0, BossBar.Color.RED, BossBar.Overlay.PROGRESS);
    }

    @Override
    public void holo() {
        assert livingData != null;
        bossBar.progress((float) getPercentHealth());
        livingData.getDamageSources().keySet().forEach(player -> player.showBossBar(bossBar));
    }

    @Override
    public void die() {
        assert livingData != null;
        livingData.getDamageSources().keySet().forEach(player -> player.hideBossBar(bossBar));
    }

}
