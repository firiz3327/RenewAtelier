package net.firiz.renewatelier.alchemy.kettle.bonus;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.firiz.renewatelier.alchemy.kettle.KettleUserData;
import net.firiz.renewatelier.alchemy.catalyst.CatalystBonus;
import net.firiz.renewatelier.alchemy.kettle.box.KettleBox;
import net.firiz.renewatelier.alchemy.material.AlchemyAttribute;
import net.firiz.renewatelier.alchemy.material.AlchemyIngredients;
import net.firiz.renewatelier.inventory.item.json.AlchemyItemStatus;
import net.firiz.renewatelier.utils.CommonUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ABonus {

    private final KettleUserData kettleUserData;
    private final BonusPlayerData bpd;
    private final List<CatalystBonus> catalystBonuses = new ObjectArrayList<>() {
        @Override
        public boolean contains(Object obj) {
            if (!(obj instanceof CatalystBonus)) {
                CommonUtils.log("not contains : " + obj);
            }
            return super.contains(obj);
        }
    };

    public ABonus(KettleUserData kettleUserData, int req) {
        this.kettleUserData = kettleUserData;
        this.bpd = new BonusPlayerData(req);
    }

    public int getBonus(AlchemyAttribute type) {
        final AtomicInteger sizes = new AtomicInteger();
        final KettleBox kettleBox = kettleUserData.getKettleBox();
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

    public String getBar() {
        final StringBuilder sb = new StringBuilder();
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
                sb.append(ChatColor.WHITE).append("||||||");
            }
        }
        sb.append(ChatColor.WHITE).append(" [").append(bpd.getLevel()).append("] ").append(bpd.getBar()).append("/").append(bpd.getReq()).append("        ");
        return sb.toString();
    }

    public void addBar(int plus, AlchemyAttribute[] aas) {
        final int bonus = Arrays.stream(aas).mapToInt(this::getBonus).sum();
        final KettleBox kettleBox = kettleUserData.getKettleBox();
        if (kettleBox != null) {
            final List<BonusItem> kettleSelects = kettleBox.getItems();
            plus += plus * ((double) bonus * 0.01 + (kettleSelects.isEmpty() ? 0 : kettleSelects.get(kettleSelects.size() - 1).getBonus() * 0.01));

            final AtomicInteger nextPlus = new AtomicInteger(plus);
            final List<CatalystBonus> usedBonus = kettleBox.getBonus();
            catalystBonuses.stream().filter(cb -> cb.getData().getType().isOnce() && (usedBonus.isEmpty() || !usedBonus.contains(cb))).forEach(cb -> {
                nextPlus.addAndGet((int) Math.round(nextPlus.intValue() * (cb.getData().getX() * 0.01)));
                kettleBox.addBonus(cb);
            });
            bpd.add(nextPlus.intValue(), aas);
        }
    }

    @NotNull
    public List<AlchemyAttribute[]> getLevelUps() {
        return bpd.getLevelUps();
    }

    public int getLevel() {
        return bpd.getLevel();
    }

    public void back() {
        bpd.back();
    }

    public boolean hasCatalystBonus(final CatalystBonus bonus) {
        return catalystBonuses.contains(bonus);
    }

    public void addCatalystBonus(final CatalystBonus data) {
        catalystBonuses.add(data);
    }

    public boolean removeCatalystBonus(final CatalystBonus data) {
        return catalystBonuses.remove(data);
    }

    public List<CatalystBonus> getCatalystBonuses() {
        return catalystBonuses;
    }
}
