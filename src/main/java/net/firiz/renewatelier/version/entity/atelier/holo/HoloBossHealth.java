package net.firiz.renewatelier.version.entity.atelier.holo;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import net.firiz.renewatelier.version.entity.atelier.LivingData;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class HoloBossHealth extends AbstractHoloHealth {

    private final BossBar bossBar;

    public HoloBossHealth(@NotNull LivingEntity entity, @NotNull LivingData livingData, @NotNull String customName) {
        super(entity, Objects.requireNonNull(livingData), customName);
        this.bossBar = Bukkit.createBossBar(customName, BarColor.RED, BarStyle.SOLID, BarFlag.PLAY_BOSS_MUSIC);
    }

    @Override
    public void holo() {
        assert livingData != null;
        final Object2DoubleMap<Player> damageSources = livingData.getDamageSources();
        bossBar.getPlayers().stream().filter(player -> !damageSources.containsKey(player)).forEach(bossBar::removePlayer);
        damageSources.forEach((player, damage) -> bossBar.addPlayer(player));
        bossBar.setProgress(getPercentHealth());
    }

    @Override
    public void die() {
        bossBar.removeAll();
    }

}
