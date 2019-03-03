/*
 * QuestStatusLoader.java
 * 
 * Copyright (c) 2019 firiz.
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
package jp.gr.java_conf.zakuramomiji.renewatelier.player.loadSQLs;

import java.util.ArrayList;
import java.util.List;
import jp.gr.java_conf.zakuramomiji.renewatelier.quest.QuestStatus;
import jp.gr.java_conf.zakuramomiji.renewatelier.sql.SQLManager;

/**
 *
 * @author firiz
 */
public class QuestStatusLoader implements StatusLoader<List<QuestStatus>> {

    @Override
    public List<QuestStatus> load(int id) {
        final List<List<Object>> quest_statuses_obj = SQLManager.INSTANCE.select(
                "questDatas",
                new String[]{"user_id", "quest_id", "clear"},
                new Object[]{id}
        );
        final List<QuestStatus> quest_statuses = new ArrayList<>();
        quest_statuses_obj.forEach((datas) -> quest_statuses.add(new QuestStatus(
                (String) datas.get(1), // quest_id
                (int) datas.get(2) != 0 // clear
        )));
        return quest_statuses;
    }

}
