package net.firiz.renewatelier.entity.player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.script.ScriptEngine;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class EngineManager {

    @NotNull
    private final AtomicBoolean isEnginesUsable = new AtomicBoolean();
    @NotNull
    private final AtomicReference<ScriptEngine> jsEngine = new AtomicReference<>();
    @NotNull
    private final AtomicReference<ScriptEngine> py3Engine = new AtomicReference<>();

    public boolean isEnginesUsable() {
        return isEnginesUsable.get();
    }

    public void setEnginesUsable(boolean enginesUsable) {
        isEnginesUsable.set(enginesUsable);
    }

    @Nullable
    public ScriptEngine getJsEngine() {
        return jsEngine.get();
    }

    public void setJsEngine(@Nullable ScriptEngine jsEngine) {
        this.jsEngine.set(jsEngine);
    }

    @Nullable
    public ScriptEngine getPy3Engine() {
        return py3Engine.get();
    }

    public void setPy3Engine(@Nullable ScriptEngine py3Engine) {
        this.py3Engine.set(py3Engine);
    }
}
