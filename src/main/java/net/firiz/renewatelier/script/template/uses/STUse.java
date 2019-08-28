package net.firiz.renewatelier.script.template.uses;

import net.firiz.renewatelier.script.template.ScriptTemplate;

public abstract class STUse extends ScriptTemplate {

    private final int useCount;

    public STUse(String id, int useCount) {
        super(id);
        this.useCount = useCount;
    }

    public int getUseCount() {
        return useCount;
    }
}
