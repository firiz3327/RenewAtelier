/*
 * KettleBonusManager.java
 * 
 * Copyright (c) 2018 firiz.
 * 
 * This file is part of Expression program is undefined on line 6, column 40 in Templates/Licenses/license-licence-gplv3.txt..
 * 
 * Expression program is undefined on line 8, column 19 in Templates/Licenses/license-licence-gplv3.txt. is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Expression program is undefined on line 13, column 19 in Templates/Licenses/license-licence-gplv3.txt. is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Expression program is undefined on line 19, column 30 in Templates/Licenses/license-licence-gplv3.txt..  If not, see <http ://www.gnu.org/licenses/>.
 */
package jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.kettle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.catalyst.CatalystBonus;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.kettle.box.KettleBox;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.material.AlchemyAttribute;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.material.AlchemyIngredients;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.material.MaterialSize;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author firiz
 */
public enum KettleBonusManager {
    INSTANCE; // enum singleton style

    private final Map<UUID, BonusPlayerData> datas;
    private final KettleItemManager KETTLE = KettleItemManager.INSTANCE;

    KettleBonusManager() {
        datas = new HashMap<>();
    }

    public int getBonus(Player player, AlchemyAttribute type) {
        return getBonus(player.getUniqueId(), type);
    }

    public int getBonus(UUID uuid, AlchemyAttribute type) {
        int sizes = 0;
        final KettleBox kettleBox = KETTLE.getKettleData(uuid);
        if (kettleBox != null) {
            final List<BonusItem> kettleSelects = kettleBox.getResultItems();
            for (final BonusItem itemData : kettleSelects) {
                final ItemStack item = itemData.getItem();
                if (item != null) {
                    for (final AlchemyAttribute aa : AlchemyIngredients.getAllLevel(item).getRight()) {
                        if (aa == type && AlchemyIngredients.getLevel(item, type) != 0) {
                            sizes += MaterialSize.getSizeCount(item);
                        }
                    }
                }
            }
        }
        return (int) Math.pow(sizes, 2);
    }

    public String getBar(final UUID uuid, final int req) {
        if (datas.containsKey(uuid)) {
            final StringBuilder sb = new StringBuilder();
            final BonusPlayerData bpd = datas.get(uuid);
            final int bar = (int) ((double) bpd.getBar() / bpd.req * 10);
            for (int i = 0; i < 10; i++) {
                if (i < bar) {
                    final AlchemyAttribute[] aas = bpd.getUp();
                    final int color = 6 / aas.length; //錬金成分３つまで想定
                    for (final AlchemyAttribute aa : aas) {
                        for (int j = 0; j < color; j++) {
                            sb.append(aa.getColor()).append("|");
                        }
                    }
                } else {
                    sb.append(ChatColor.RESET).append("||||||");
                }
            }
            sb.append(ChatColor.RESET).append(" [").append(bpd.getLevel()).append("] ").append(bpd.getBar()).append("/").append(bpd.req).append("        ");
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

        int bonus = 0;
        for (final AlchemyAttribute aa : aas) {
            bonus += getBonus(uuid, aa);
        }
        final KettleBox kettleBox = KETTLE.getKettleData(uuid);
        final List<BonusItem> kettleSelects = kettleBox == null ? null : kettleBox.getItems();
        plus += plus * (((double) bonus * 0.01) + ((kettleSelects == null || kettleSelects.isEmpty()) ? 0 : kettleSelects.get(kettleSelects.size() - 1).getBonus() * 0.01));

        final List<CatalystBonus> bonusDatas = KETTLE.getCatalystBonusList(uuid);
        if (bonusDatas != null) {
            for (final CatalystBonus cb : bonusDatas) {
                final List<CatalystBonus> usedBonus = kettleBox.getBonus();
                if (cb.getData().getType().isOnce() && (usedBonus == null || !usedBonus.contains(cb))) {
                    plus += Math.round(plus * (cb.getData().getX() * 0.01));
                    kettleBox.addBonus(cb);
                }
            }
        }
        bpd.add(plus, aas);
    }

    public List<AlchemyAttribute[]> getLevelUps(UUID uuid) {
        final BonusPlayerData bpd = datas.get(uuid);
        if (bpd != null) {
            return bpd.getLevelUps();
        }
        return null;
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

    private class BonusPlayerData {

        private final int req;
        private final List<Integer> bars;
        private final List<AlchemyAttribute[]> ups;
        private final Map<Integer, AlchemyAttribute[]> levelups;

        public BonusPlayerData(int req) {
            this.req = req;
            this.bars = new ArrayList<>();
            this.ups = new ArrayList<>();
            this.levelups = new LinkedHashMap<>();
        }

        public int getBar() {
            int bar = bars.size() - 1;
            if (bar != -1) {
                return bars.get(bar) % req;
            }
            return 0;
        }

        public int getLevel() {
            int bar = bars.size() - 1;
            if (bar != -1) {
                return bars.get(bar) / req;
            }
            return 0;
        }

        public AlchemyAttribute[] getUp() {
            return ups.get(ups.size() - 1);
        }

        public List<AlchemyAttribute[]> getLevelUps() {
            List<AlchemyAttribute[]> result = new ArrayList<>();
            levelups.keySet().forEach((i) -> result.add(levelups.get(i)));
            return result;
        }

        public void add(int plus, AlchemyAttribute[] ups) {
            int old_level = getLevel();
            bars.add(plus + (bars.isEmpty() ? 0 : bars.get(bars.size() - 1)));
            this.ups.add(ups);
            if (old_level < getLevel()) {
                levelups.put(bars.size() - 1, ups);
            }
        }

        public void back() {
            int remove = bars.size() - 1;
            if (remove != -1) {
                bars.remove(remove);
                ups.remove(remove);
                levelups.remove(remove);
            }
        }
    }

}
