/*
 * Quest.java
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
package jp.gr.java_conf.zakuramomiji.renewatelier.quest;

import java.util.ArrayList;
import jp.gr.java_conf.zakuramomiji.renewatelier.quest.result.QuestResult;
import java.util.List;
import jp.gr.java_conf.zakuramomiji.renewatelier.config.ConfigManager;
import jp.gr.java_conf.zakuramomiji.renewatelier.config.loader.QuestLoader;

/**
 *
 * @author firiz
 */
public class Quest {

    private final static ConfigManager CONFIG_MANAGER = ConfigManager.INSTANCE;
    private static List<Quest> IMPORTANT_QUESTS;
    private final String id;
    private final String name;
    private final String[] description;
    private final String nextQuestId;
    private final boolean important;
    private final List<QuestResult> results;

    public Quest(String id, String name, String[] description, String nextQuestId, boolean important, List<QuestResult> results) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.nextQuestId = nextQuestId;
        this.important = important;
        this.results = results;
    }

    public static Quest getQuest(final String id) {
        for (final Object obj : CONFIG_MANAGER.getList(QuestLoader.class)) {
            final Quest quest = (Quest) obj;
            if (quest.getId().equals(id)) {
                return quest;
            }
        }
        return null;
    }
    
    public static void setImportantQuests(final List<Quest> importantQuests) {
        IMPORTANT_QUESTS = importantQuests;
    }
    
    public static List<Quest> getImportantQuests() {
        return new ArrayList<>(IMPORTANT_QUESTS);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String[] getDescription() {
        return description;
    }

    public String getNextQuestId() {
        return nextQuestId;
    }

    public List<QuestResult> getResults() {
        return results;
    }
    
    public boolean isImportant() {
        return important;
    }

}
