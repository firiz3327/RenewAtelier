/*
 * ScriptManager.java
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
package net.firiz.renewatelier.script.execution;

import com.oracle.truffle.js.scriptengine.GraalJSEngineFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import net.firiz.renewatelier.constants.ServerConstants;
import net.firiz.renewatelier.entity.player.PlayerSaveManager;
import net.firiz.renewatelier.entity.player.Char;
import net.firiz.renewatelier.script.conversation.ScriptConversation;
import net.firiz.renewatelier.script.engine.GraalEngine;
import net.firiz.renewatelier.script.engine.GraalEngine.Type;
import org.bukkit.entity.Player;

/**
 * @author firiz
 */
public enum ScriptManager {
    INSTANCE; // enum singleton style

    private final String STR_LOAD_ENGINE = "スクリプトエンジンを読み込み中です。しばらく経ってからもう一度実行してください。";
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
            player.sendMessage(STR_LOAD_ENGINE);
            return;
        }
        script.start(engine, name, player, null, conversation);
    }

    public void start(final String name, final Player player, final ScriptConversation conversation, final String functionName, final Object... args) {
        final ScriptEngine engine = getEngine(name, player);
        if (engine == null) {
            player.sendMessage(STR_LOAD_ENGINE);
            return;
        }
        script.start(engine, name, player, functionName, conversation, args);
    }

    private ScriptEngine getEngine(final String name, final Player player) {
        final Char status = PlayerSaveManager.INSTANCE.getChar(player.getUniqueId());
        if (status.isEnginesUsable()) {
            final ScriptEngine engine;
            if (name.endsWith(".py") || name.endsWith(".PY")) {
                engine = status.getPy3Engine();
            } else {
                engine = status.getJsEngine();
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
