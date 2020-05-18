package net.firiz.renewatelier.alchemy.kettle.bonus;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import net.firiz.renewatelier.alchemy.catalyst.CatalystBonus;
import net.firiz.renewatelier.alchemy.kettle.KettleItemManager;
import net.firiz.renewatelier.alchemy.kettle.box.KettleBox;
import net.firiz.renewatelier.alchemy.material.AlchemyAttribute;
import net.firiz.renewatelier.alchemy.material.AlchemyIngredients;
import net.firiz.renewatelier.item.json.AlchemyItemStatus;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * @author firiz
 */
public enum KettleBonusManager {
    INSTANCE; // enum singleton style

    private final Map<UUID, BonusPlayerData> datas;
    private final KettleItemManager kettleItemManager = KettleItemManager.INSTANCE;

    KettleBonusManager() {
        datas = new HashMap<>();
    }

    public int getBonus(Player player, AlchemyAttribute type) {
        return getBonus(player.getUniqueId(), type);
    }

    public int getBonus(UUID uuid, AlchemyAttribute type) {
        final AtomicInteger sizes = new AtomicInteger();
        final KettleBox kettleBox = kettleItemManager.getKettleData(uuid);
        if (kettleBox != null) {
            final List<BonusItem> kettleSelects = kettleBox.getResultItems();
            kettleSelects.stream().filter(bonusItem -> bonusItem.getItem() != null).forEach(bonusItem -> {
                final ItemStack item = bonusItem.getItem();
                for (final AlchemyAttribute aa : Objects.requireNonNull(AlchemyIngredients.getMaxTypes(item).getRight())) {
                    if (aa == type && AlchemyIngredients.getLevel(item, type) != 0) {
                        sizes.addAndGet(AlchemyItemStatus.getSizeCount(item));
                    }
                }
            });
        }
        return (int) Math.pow(sizes.intValue(), 2);
    }

    public String getBar(final UUID uuid, final int req) {
        if (datas.containsKey(uuid)) {
            final StringBuilder sb = new StringBuilder();
            final BonusPlayerData bpd = datas.get(uuid);
            final int bar = (int) ((double) bpd.getBar() / bpd.getReq() * 10);
            for (int i = 0; i < 10; i++) {
                if (i < bar) {
                    final AlchemyAttribute[] aas = bpd.getUp();
                    final int color = 6 / aas.length; //錬金成分３つまで想定
                    Arrays.stream(aas).forEach(aa -> {
                        for (int j = 0; j < color; j++) {
                            sb.append(aa.getColor()).append("|");
                        }
                    });
                } else {
                    sb.append(ChatColor.RESET).append("||||||");
                }
            }
            sb.append(ChatColor.RESET).append(" [").append(bpd.getLevel()).append("] ").append(bpd.getBar()).append("/").append(bpd.getReq()).append("        ");
            return sb.toString();
        }
        return ChatColor.RESET + "|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||| [0] 0/" + req + "        ";
    }

    public void addBar(UUID uuid, int req, int plus, AlchemyAttribute[] aas) {
        BonusPlayerData bpd;
        if (datas.containsKey(uuid)) {
            bpd = datas.get(uuid);
        } else {
            bpd = new BonusPlayerData(req);
            datas.put(uuid, bpd);
        }

        int bonus = Arrays.stream(aas).mapToInt(aa -> getBonus(uuid, aa)).sum();
        final KettleBox kettleBox = kettleItemManager.getKettleData(uuid);
        if (kettleBox != null) {
            final List<BonusItem> kettleSelects = kettleBox.getItems();
            plus += plus * ((double) bonus * 0.01 + (kettleSelects.isEmpty() ? 0 : kettleSelects.get(kettleSelects.size() - 1).getBonus() * 0.01));

            final AtomicInteger nextPlus = new AtomicInteger(plus);
            final List<CatalystBonus> bonusDatas = kettleItemManager.getCatalystBonusList(uuid);
            if (bonusDatas != null) {
                final List<CatalystBonus> usedBonus = kettleBox.getBonus();
                bonusDatas.stream().filter(cb -> cb.getData().getType().isOnce() && (usedBonus.isEmpty() || !usedBonus.contains(cb))).forEach(cb -> {
                    nextPlus.addAndGet((int) Math.round(nextPlus.intValue() * (cb.getData().getX() * 0.01)));
                    kettleBox.addBonus(cb);
                });
            }
            bpd.add(nextPlus.intValue(), aas);
        }
    }

    @NotNull
    public List<AlchemyAttribute[]> getLevelUps(UUID uuid) {
        final BonusPlayerData bpd = datas.get(uuid);
        if (bpd != null) {
            return bpd.getLevelUps();
        }
        return new ArrayList<>(0);
    }

    public int getLevel(UUID uuid) {
        final BonusPlayerData bpd = datas.get(uuid);
        if (bpd != null) {
            return bpd.getLevel();
        }
        return 0;
    }

    public void removeData(UUID uuid) {
        datas.remove(uuid);
    }

    public void back(UUID uuid) {
        if (datas.containsKey(uuid)) {
            datas.get(uuid).back();
        }
    }

}
