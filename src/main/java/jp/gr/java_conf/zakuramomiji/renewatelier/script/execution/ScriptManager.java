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
package jp.gr.java_conf.zakuramomiji.renewatelier.script.execution;

import com.oracle.truffle.js.scriptengine.GraalJSEngineFactory;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import jp.gr.java_conf.zakuramomiji.renewatelier.constants.ServerConstants;
import jp.gr.java_conf.zakuramomiji.renewatelier.player.PlayerSaveManager;
import jp.gr.java_conf.zakuramomiji.renewatelier.player.PlayerStatus;
import jp.gr.java_conf.zakuramomiji.renewatelier.script.conversation.ScriptConversation;
import jp.gr.java_conf.zakuramomiji.renewatelier.script.engine.GraalEngine;
import jp.gr.java_conf.zakuramomiji.renewatelier.script.engine.GraalEngine.Type;
import org.bukkit.entity.Player;
import org.python.jsr223.PyScriptEngineFactory;

/**
 *
 * @author firiz
 */
public enum ScriptManager {
    INSTANCE; // enum singleton style
    
    private final ScriptRunner script;
    private final ScriptEngineManager sem;
    private final GraalJSEngineFactory gjef;
    private final PyScriptEngineFactory psef;
    
    ScriptManager() {
        sem = ServerConstants.NASHORN ? new ScriptEngineManager() : null;
        gjef = ServerConstants.NASHORN ? null : new GraalJSEngineFactory();
        psef = ServerConstants.PYTHON_2 ? new PyScriptEngineFactory() : null;
        script = new ScriptRunner();
    }

    public void start(final String name, final Player player, final ScriptConversation conversation) {
        script.start(getEngine(name, player), name, player, null, conversation);
    }

    public void start(final String name, final Player player, final ScriptConversation conversation, final String functionName, final Object... args) {
        script.start(getEngine(name, player), name, player, functionName, conversation, args);
    }
    
    private ScriptEngine getEngine(final String name, final Player player) {
        final PlayerStatus status = PlayerSaveManager.INSTANCE.getStatus(player.getUniqueId());
        final ScriptEngine engine;
        if(name.endsWith(".py") || name.endsWith(".PY")) {
            if(name.endsWith(".2.py") || name.endsWith(".2.PY")) {
                engine = status.getPy2Engine();
            } else {
                engine = status.getPy3Engine();
            }
        } else {
            engine = status.getJsEngine();
        }
        return engine;
    }
    
    public ScriptEngine createJsEngine() {
        return sem == null ? gjef.getScriptEngine() : sem.getEngineByName("javascript");
    }
    
    public ScriptEngine createPy2Engine() {
        return psef == null ? null : psef.getScriptEngine();
    }
    
    public ScriptEngine createPy3Engine() {
        return ServerConstants.PYTHON_3 ? new GraalEngine(Type.PYTHON) : null;
    }
    
    
}
