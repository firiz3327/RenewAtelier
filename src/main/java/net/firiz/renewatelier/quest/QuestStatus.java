package net.firiz.renewatelier.quest;

/**
 *
 * @author firiz
 */
public class QuestStatus {

    private final String id;
    private boolean clear;
    
    public QuestStatus(String id) {
        this.id = id;
        this.clear = false;
    }

    public QuestStatus(String id, boolean clear) {
        this.id = id;
        this.clear = clear;
    }
    
    public String getId() {
        return id;
    }

    public boolean isClear() {
        return clear;
    }

    public void clear() {
        this.clear = true;
    }

}
