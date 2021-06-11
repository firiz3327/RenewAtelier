package net.firiz.renewatelier.server.script.execution;

import com.oracle.truffle.js.scriptengine.GraalJSEngineFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import net.firiz.renewatelier.constants.ServerConstants;
import net.firiz.renewatelier.entity.player.EngineManager;
import net.firiz.renewatelier.entity.player.sql.load.PlayerSaveManager;
import net.firiz.renewatelier.server.script.conversation.ScriptConversation;
import net.firiz.renewatelier.server.script.engine.GraalEngine;
import net.firiz.renewatelier.server.script.engine.GraalEngine.Type;
import org.bukkit.entity.Player;

/**
 * @author firiz
 */
public enum ScriptManager {
    INSTANCE; // enum singleton style

    private final String stringLoadEngine = "スクリプトエンジンを読み込み中です。しばらく経ってからもう一度実行してください。";
    private final ScriptRunner script;
    private final ScriptEngineManager scriptEngineManager;
    private final GraalJSEngineFactory graalJSEngineFactory;

    ScriptManager() {
        scriptEngineManager = ServerConstants.NASHORN ? new ScriptEngineManager() : null;
        graalJSEngineFactory = ServerConstants.NASHORN ? null : new GraalJSEngineFactory();
        script = new ScriptRunner();
    }

    public void start(final String name, final Player player, final ScriptConversation conversation) {
        final ScriptEngine engine = getEngine(name, player);
        if (engine == null) {
            player.sendMessage(stringLoadEngine);
            return;
        }
        script.start(engine, name, player, null, conversation);
    }

    public void start(final String name, final Player player, final ScriptConversation conversation, final String functionName, final Object... args) {
        final ScriptEngine engine = getEngine(name, player);
        if (engine == null) {
            player.sendMessage(stringLoadEngine);
            return;
        }
        script.start(engine, name, player, functionName, conversation, args);
    }

    private ScriptEngine getEngine(final String name, final Player player) {
        final EngineManager engineManager = PlayerSaveManager.INSTANCE.getChar(player).getEngineManager();
        if (engineManager.isEnginesUsable()) {
            final ScriptEngine engine;
            if (name.endsWith(".py") || name.endsWith(".PY")) {
                engine = engineManager.getPy3Engine();
            } else {
                engine = engineManager.getJsEngine();
            }
            return engine;
        }
        return null;
    }

    public ScriptEngine createJsEngine() {
        return scriptEngineManager == null ? graalJSEngineFactory.getScriptEngine() : scriptEngineManager.getEngineByName("javascript");
    }

    public ScriptEngine createPy3Engine() {
        return ServerConstants.PYTHON ? new GraalEngine(Type.PYTHON) : null;
    }


}
