package net.firiz.renewatelier.script.template;

public abstract class ScriptTemplate {

    private final String id;

    public ScriptTemplate(String id) {
        this.id = id;
    }

    public abstract void script();

}
