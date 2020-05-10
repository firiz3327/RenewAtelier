package net.firiz.renewatelier.alchemy.catalyst;

import java.util.Arrays;

/**
 * @author firiz
 */
class DefaultCatalyst extends Catalyst {

    DefaultCatalyst() {
        super(Arrays.asList(new CatalystBonus(
                new int[]{
                        1, 1, 1, 1, 0,
                        1, 0, 0, 0, 0,
                        1, 0, 0, 0, 0,
                        1, 0, 0, 0, 0,
                        0, 0, 0, 0, 0
                },
                new CatalystBonusData(CatalystBonusData.BonusType.QUALITY_PERCENT, 10)
        ), new CatalystBonus(
                new int[]{
                        0, 0, 0, 0, 0,
                        0, 0, 0, 0, 1,
                        0, 0, 0, 0, 1,
                        0, 0, 0, 0, 1,
                        0, 1, 1, 1, 1
                },
                new CatalystBonusData(CatalystBonusData.BonusType.SIZE, 1)
        )));
    }

}
