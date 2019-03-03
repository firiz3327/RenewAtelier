/*
 * DefaultCatalyst.java
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
package jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.catalyst;

import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.catalyst.CatalystBonusData.BonusType;
import jp.gr.java_conf.zakuramomiji.renewatelier.alchemy.material.Category;

import java.util.Arrays;

/**
 * @author firiz
 */
public class DefaultCatalyst extends Catalyst {

    public DefaultCatalyst() {
        super(Category.CATALYST, Arrays.asList(new CatalystBonus(
                new int[]{
                        1, 1, 1, 1, 0,
                        1, 0, 0, 0, 0,
                        1, 0, 0, 0, 0,
                        1, 0, 0, 0, 0,
                        0, 0, 0, 0, 0
                },
                new CatalystBonusData(BonusType.QUALITY_PERCENT, 10)
        ), new CatalystBonus(
                new int[]{
                        0, 0, 0, 0, 0,
                        0, 0, 0, 0, 1,
                        0, 0, 0, 0, 1,
                        0, 0, 0, 0, 1,
                        0, 1, 1, 1, 1
                },
                new CatalystBonusData(BonusType.SIZE, 1)
        )));
    }

}
