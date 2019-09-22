package net.firiz.renewatelier.damage;

import net.firiz.renewatelier.AtelierPlugin;
import net.firiz.renewatelier.utils.doubledata.DoubleData;
import net.firiz.renewatelier.utils.Randomizer;
import net.firiz.renewatelier.version.packet.EntityPacket;
import net.firiz.renewatelier.version.packet.FakeEntity;
import net.firiz.renewatelier.version.packet.PacketUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DamageUtil {

    private DamageUtil() {
    }

    /**
     * フィリスのアトリエを出来るだけ模したダメージ計算を行う
     * <p>
     * このダメージ計算では敵の防御力によるダメージ変動を無視しています
     * 詳細は http://uq-laboratory.com/play/firis_itemdamage/top.html を見てください
     *
     * @param baseDamage           基礎ダメージ(攻略本参照)
     * @param powerValue           威力値(効果による威力値 * 品質補正倍率 * 割合強化倍率 + 反比例強化量)
     * @param itemPowerEnhancement アイテム威力強化(100%基準から足していく)
     * @param criticalRate         クリティカル倍率(125%基準から足していく)
     * @param attributeValue       属性ダメージ
     * @param finalDamageRate      最終ダメージ倍率
     * @param cooperationRate      連携率
     * @return 計算したダメージの値
     */
    public static int calcDamage(
            double baseDamage,
            double powerValue,
            double itemPowerEnhancement,
            double criticalRate,
            double attributeValue,
            double finalDamageRate,
            double cooperationRate
    ) {
        return (int) Math.floor(baseDamage * powerValue * itemPowerEnhancement * criticalRate * attributeValue * finalDamageRate * cooperationRate / 100);
    }

    public static void damage(Damageable damageable, double damage) {
        damageable.damage(damage);
        damageable.setLastDamageCause(new EntityDamageEvent(
                damageable,
                EntityDamageEvent.DamageCause.CUSTOM,
                damage
        ));
    }

    @SafeVarargs
    public static void holoDamage(@NotNull Location loc, DoubleData<Double, AttackAttribute>... damages) {
        final Location l = loc.clone();
        l.setY(l.getY() + 1.6);
        for (int i = 0; i < damages.length; i++) {
            final String damage = damages[i].getRight().getColor().toString() + damages[i].getLeft();
            Bukkit.getScheduler().scheduleSyncDelayedTask(
                    AtelierPlugin.getPlugin(),
                    () -> {
                        final FakeEntity fakeEntity = new FakeEntity(Randomizer.nextInt(Integer.MAX_VALUE), FakeEntity.FakeEntityType.ARMOR_STAND, 0);
                        final List<Player> players = new ArrayList<>();
                        l.getWorld().getNearbyEntities(l, 20, 10, 20).stream().filter(e -> e instanceof Player).forEach(e -> players.add((Player) e));
                        players.forEach(player -> {
                            PacketUtils.sendPacket(player, EntityPacket.getSpawnPacket(fakeEntity, l));
                            PacketUtils.sendPacket(player, EntityPacket.getMessageSmallStandMeta(player, damage).compile(fakeEntity.getEntityId()));
                            Bukkit.getScheduler().runTaskLater(
                                    AtelierPlugin.getPlugin(),
                                    () -> PacketUtils.sendPacket(player, EntityPacket.getDespawnPacket(fakeEntity.getEntityId())),
                                    10
                            );
                        });
                        l.setY(l.getY() + 0.3);
                    },
                    2L * i
            );
        }
    }

}
