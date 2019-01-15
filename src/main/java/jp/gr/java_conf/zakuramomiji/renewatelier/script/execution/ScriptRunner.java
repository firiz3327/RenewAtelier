/*
 * ScriptGraalJS.java
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import jp.gr.java_conf.zakuramomiji.renewatelier.AtelierPlugin;
import jp.gr.java_conf.zakuramomiji.renewatelier.constants.ServerConstants;
import jp.gr.java_conf.zakuramomiji.renewatelier.script.conversation.ScriptConversation;
import jp.gr.java_conf.zakuramomiji.renewatelier.utils.Chore;
import org.bukkit.entity.Player;

/**
 *
 * @author firiz
 */
final class ScriptRunner {

    private final ScriptEngineManager SEM;
    private final GraalJSEngineFactory GJEF;
    
    public ScriptRunner() {
        if(ServerConstants.NASHORN) {
            SEM = new ScriptEngineManager();
            GJEF = null;
        } else {
            SEM = null;
            GJEF = new GraalJSEngineFactory();
        }
    }

    public void start(final String name, final Player player, final String functionName, final ScriptConversation conversation, final Object... args) {
        try {
            final ScriptEngine scriptengine = getScriptEngine(name);
            if (scriptengine != null) {
                scriptengine.put("sc", conversation);
                
                final Invocable iv = (Invocable) scriptengine;
                conversation.setIv(iv);
                try {
                    iv.invokeFunction("init");
                } catch (NoSuchMethodException ex) {
                }
                try {
                    iv.invokeFunction(functionName == null ? "start" : functionName, args);
                } catch (NoSuchMethodException ex) {
                    Chore.log(Level.SEVERE, null, ex);
                }
            } else {
                Chore.log(name.concat(" is not found for script."));
            }
        } catch (ScriptException ex) {
            Chore.log(Level.SEVERE, null, ex);
        }
    }

    private ScriptEngine getScriptEngine(final String name) {
        final File file;
        if (name.contains("/")) {
            final String[] split = name.split("/");
            File f = new File(AtelierPlugin.getPlugin().getDataFolder(), "scripts");
            for (final String path : split) {
                f = new File(f, path);
            }
            file = f;
        } else {
            file = new File(
                    new File(AtelierPlugin.getPlugin().getDataFolder(), "scripts"),
                    name.concat(".js")
            );
        }
        final ScriptEngine engine = SEM != null ? SEM.getEngineByName("javascript") : GJEF.getScriptEngine();
        try (final FileInputStream fis = new FileInputStream(file);
                final InputStreamReader reader = new InputStreamReader(fis, StandardCharsets.UTF_8)) {
            engine.eval(reader);
            return engine;
        } catch (FileNotFoundException | ScriptException ex) {
            Chore.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Chore.log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
}
