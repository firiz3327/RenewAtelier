package net.firiz.renewatelier.quest.result;

public abstract class IntQuestResult implements QuestResult {

    private final int result;

    public IntQuestResult(int result) {
        this.result = result;
    }

    public int getResult() {
        return result;
    }
}
