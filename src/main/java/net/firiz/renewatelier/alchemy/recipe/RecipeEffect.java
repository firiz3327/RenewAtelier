package net.firiz.renewatelier.alchemy.recipe;

import java.util.List;

import net.firiz.ateliercommonapi.adventure.text.C;
import net.firiz.ateliercommonapi.adventure.text.Text;
import net.firiz.renewatelier.alchemy.kettle.KettleUserData;
import net.firiz.renewatelier.alchemy.catalyst.CatalystBonus;
import net.firiz.renewatelier.alchemy.material.AlchemyAttribute;
import net.firiz.renewatelier.alchemy.catalyst.CatalystBonusData;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

/**
 * @author firiz
 */
public record RecipeEffect(
        AlchemyAttribute attribute,
        List<Integer> star,
        List<StarEffect> starEffects,
        StarEffect defaultStarEffect
) {

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
        int bigStar = 0;
        for (int i = 0; i < star.size(); i++) {
            if (i < (int) upCount && star.get(i) != 0) {
                bigStar++;
            }
        }
        return bigStar;
    }

    public Component getStar(final KettleUserData kettleUserData) {
        final double upCount = getUpCount(kettleUserData);
        final Text text = new Text();
        for (int i = 0; i < star.size(); i++) {
            final int _star = star.get(i);
            if (i < (int) upCount) {
                if (_star == 0) {
                    text.append("★").color(C.GOLD);
                } else {
                    text.append("✮").color(C.GOLD);
                }
            } else if (i < upCount) {
                if (_star == 0) {
                    text.append("★").color(C.YELLOW);
                } else {
                    text.append("✭").color(C.YELLOW);
                }
            } else {
                if (_star == 0) {
                    text.append("★").color(C.WHITE);
                } else {
                    text.append("✭").color(C.WHITE);
                }
            }
        }
        return text;
    }

    @Nullable
    public Component getName(final KettleUserData kettleUserData) {
        final int bigStar = getBigStar(kettleUserData);
        final StarEffect effect = bigStar != 0 ? starEffects.get(bigStar - 1) : defaultStarEffect;
        return effect != null ? effect.getName() : null;
    }

    public StarEffect getActiveEffect(final KettleUserData kettleUserData) {
        final int bigStar = getBigStar(kettleUserData);
        return bigStar != 0 ? starEffects.get(bigStar - 1) : defaultStarEffect;
    }

}
