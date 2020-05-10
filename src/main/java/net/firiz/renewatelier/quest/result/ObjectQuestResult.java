package net.firiz.renewatelier.quest.result;

/**
 *
 * @author firiz
 */
public abstract class ObjectQuestResult<T> implements QuestResult {

    private final T result;

    public ObjectQuestResult(T result) {
        this.result = result;
    }

    public T getResult() {
        return result;
    }

}
