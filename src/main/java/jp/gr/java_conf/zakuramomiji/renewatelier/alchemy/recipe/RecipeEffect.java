/*
 * RecipeEffect.java
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
package jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.recipe;

import java.util.List;
import java.util.UUID;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.catalyst.CatalystBonus;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.catalyst.CatalystBonusData;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.catalyst.CatalystBonusData.BonusType;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.kettle.KettleBonusManager;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.kettle.KettleItemManager;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.material.AlchemyAttribute;
import net.md_5.bungee.api.ChatColor;

/**
 *
 * @author firiz
 */
public class RecipeEffect {

    private final KettleItemManager KETTLE = KettleItemManager.getInstance();
    private final KettleBonusManager abm = KettleBonusManager.getInstance();
    private final AlchemyAttribute attribute;
    private final List<Integer> star;
    private final List<StarEffect> starEffects;

    public RecipeEffect(AlchemyAttribute attribute, List<Integer> star, List<StarEffect> starEffects) {
        this.attribute = attribute;
        this.star = star;
        this.starEffects = starEffects;
    }

    public AlchemyAttribute getAttribute() {
        return attribute;
    }

    public List<Integer> getStar() {
        return star;
    }

    public List<StarEffect> getStarEffects() {
        return starEffects;
    }

    private double getUpCount(final UUID uuid) {
        double upcount = 0;
        final List<AlchemyAttribute[]> ups = abm.getLevelUps(uuid);
        if (ups != null) {
            for (final AlchemyAttribute[] aas : ups) {
                for (final AlchemyAttribute aa : aas) {
                    if (aa == attribute) {
                        upcount++;
                    }
                }
            }
        }
        final List<CatalystBonus> bonusDatas = KETTLE.getCatalystBonusList(uuid);
        if (bonusDatas != null) {
            for (final CatalystBonus cb : bonusDatas) {
                if (cb.getData().getType() == BonusType.STARLEVEL && attribute == cb.getData().getY()) {
                    upcount += cb.getData().getX();
                }
            }
        }
        upcount += abm.getLevel(uuid) * 0.5;
        return upcount;
    }

    private int getBigStar(final UUID uuid) {
        final double upcount = getUpCount(uuid);
        int bigstar = 0;
        for (int i = 0; i < star.size(); i++) {
            int _star = star.get(i);
            if (i < (int) upcount) {
                if (_star != 0) {
                    bigstar++;
                }
            }
        }
        return bigstar;
    }

    public String getStar(final UUID uuid) {
        final double upcount = getUpCount(uuid);
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < star.size(); i++) {
            final int _star = star.get(i);
            if (i < (int) upcount) {
                if (_star == 0) {
                    sb.append(ChatColor.GOLD).append("★");
                } else {
                    sb.append(ChatColor.GOLD).append("✮");
                }
            } else if (i < upcount) {
                if (_star == 0) {
                    sb.append(ChatColor.YELLOW).append("★");
                } else {
                    sb.append(ChatColor.YELLOW).append("✭");
                }
            } else {
                if (_star == 0) {
                    sb.append(ChatColor.WHITE).append("★");
                } else {
                    sb.append(ChatColor.WHITE).append("✭");
                }
            }
        }
        return sb.toString();
    }

    public String getName(final UUID uuid) {
        final int bigstar = getBigStar(uuid);
        final StarEffect effect = bigstar != 0 ? starEffects.get(bigstar - 1) : null;
        return effect != null ? effect.getName() : null;
    }

    public StarEffect getActiveEffect(final UUID uuid) {
        final int bigstar = getBigStar(uuid);
        final StarEffect effect = bigstar != 0 ? starEffects.get(bigstar - 1) : null;
        return effect;
    }

}
