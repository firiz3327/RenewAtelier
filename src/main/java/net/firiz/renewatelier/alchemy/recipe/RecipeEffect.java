package net.firiz.renewatelier.alchemy.recipe;

import java.util.List;

import net.firiz.renewatelier.alchemy.kettle.KettleUserData;
import net.firiz.renewatelier.alchemy.catalyst.CatalystBonus;
import net.firiz.renewatelier.alchemy.material.AlchemyAttribute;
import net.firiz.renewatelier.alchemy.catalyst.CatalystBonusData;
import net.md_5.bungee.api.ChatColor;

/**
 * @author firiz
 */
public class RecipeEffect {

    private final AlchemyAttribute attribute;
    private final List<Integer> star;
    private final List<StarEffect> starEffects;
    private final StarEffect defaultStarEffect;

    public RecipeEffect(AlchemyAttribute attribute, List<Integer> star, List<StarEffect> starEffects, StarEffect defaultStarEffect) {
        this.attribute = attribute;
        this.star = star;
        this.starEffects = starEffects;
        this.defaultStarEffect = defaultStarEffect;
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

    private double getUpCount(final KettleUserData kettleUserData) {
        double upCount = 0;
        final List<AlchemyAttribute[]> ups = kettleUserData.getBonusManager().getLevelUps();
        for (final AlchemyAttribute[] aas : ups) {
            for (final AlchemyAttribute aa : aas) {
                if (aa == attribute) {
                    upCount++;
                }
            }
        }
        final List<CatalystBonus> catalystBonuses = kettleUserData.getBonusManager().getCatalystBonuses();
        if (catalystBonuses != null) {
            for (final CatalystBonus cb : catalystBonuses) {
                if (cb.getData().getType() == CatalystBonusData.BonusType.STARLEVEL && attribute == cb.getData().getY()) {
                    upCount += cb.getData().getX();
                }
            }
        }
        upCount += kettleUserData.getBonusManager().getLevel() * 0.5;
        return upCount;
    }

    private int getBigStar(final KettleUserData kettleUserData) {
        final double upCount = getUpCount(kettleUserData);
        int bigstar = 0;
        for (int i = 0; i < star.size(); i++) {
            if (i < (int) upCount && star.get(i) != 0) {
                bigstar++;
            }
        }
        return bigstar;
    }

    public String getStar(final KettleUserData kettleUserData) {
        final double upCount = getUpCount(kettleUserData);
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < star.size(); i++) {
            final int _star = star.get(i);
            if (i < (int) upCount) {
                if (_star == 0) {
                    sb.append(ChatColor.GOLD).append("★");
                } else {
                    sb.append(ChatColor.GOLD).append("✮");
                }
            } else if (i < upCount) {
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

    public String getName(final KettleUserData kettleUserData) {
        final int bigStar = getBigStar(kettleUserData);
        final StarEffect effect = bigStar != 0 ? starEffects.get(bigStar - 1) : defaultStarEffect;
        return effect != null ? effect.getName() : null;
    }

    public StarEffect getActiveEffect(final KettleUserData kettleUserData) {
        final int bigStar = getBigStar(kettleUserData);
        return bigStar != 0 ? starEffects.get(bigStar - 1) : defaultStarEffect;
    }

}
