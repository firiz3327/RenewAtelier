package net.firiz.renewatelier.quest;

import java.util.List;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.firiz.renewatelier.config.ConfigManager;
import net.firiz.renewatelier.config.QuestLoader;
import net.firiz.renewatelier.quest.result.QuestResult;

/**
 *
 * @author firiz
 */
public class Quest {

    private static final ConfigManager CONFIG_MANAGER = ConfigManager.INSTANCE;
    private static List<Quest> importantQuests;
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
        for (final Quest quest : CONFIG_MANAGER.getList(QuestLoader.class, Quest.class)) {
            if (quest.getId().equals(id)) {
                return quest;
            }
        }
        throw new IllegalArgumentException("not found quest. " + id);
    }
    
    public static void setImportantQuests(final List<Quest> importantQuests) {
        Quest.importantQuests = importantQuests;
    }
    
    public static List<Quest> getImportantQuests() {
        return new ObjectArrayList<>(importantQuests);
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
