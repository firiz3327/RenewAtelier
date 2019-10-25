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
package net.firiz.renewatelier.script.execution;

import java.io.*;
import java.nio.charset.StandardCharsets;
import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import net.firiz.renewatelier.AtelierPlugin;
import net.firiz.renewatelier.script.conversation.ScriptConversation;
import net.firiz.renewatelier.utils.Chore;
import org.bukkit.entity.Player;

/**
 *
 * @author firiz
 */
final class ScriptRunner {

    protected void start(final ScriptEngine engine, final String name, final Player player, final String functionName, final ScriptConversation conversation, final Object... args) {
        if (engine == null) {
            throw new NullPointerException("This engine is unsupported language file or null.");
        }
        try {
            final Bindings bindings = engine.createBindings();
            bindings.put("polyglot.js.allowAllAccess", true);
            bindings.put("sc", conversation);
            engine.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
            if (eval(engine, name)) {
                final Invocable iv = (Invocable) engine;
                conversation.setIv(iv);
                try {
                    iv.invokeFunction("init");
                } catch (NoSuchMethodException ignored) {
                }
                try {
                    iv.invokeFunction(functionName == null ? "start" : functionName, args);
                } catch (NoSuchMethodException ex) {
                    Chore.logWarning(ex);
                }
            } else {
                Chore.log(name.concat(" is not found or error for script."));
            }
        } catch (ScriptException ex) {
            Chore.logWarning(ex);
        }
    }

    private boolean eval(final ScriptEngine engine, final String script) {
        final File file;
        if (script.contains("/")) {
            final String[] split = script.split("/");
            File f = new File(AtelierPlugin.getPlugin().getDataFolder(), "scripts");
            for (final String path : split) {
                f = new File(f, path);
            }
            file = f;
        } else {
            file = new File(new File(AtelierPlugin.getPlugin().getDataFolder(), "scripts"), script);
        }

        try (final FileInputStream fis = new FileInputStream(file);
             final InputStreamReader reader = new InputStreamReader(fis, StandardCharsets.UTF_8)) {
            engine.eval(reader);
            return true;
        } catch(FileNotFoundException ex) {
            return false;
        } catch (ScriptException | IOException ex) {
            Chore.logWarning(ex);
        }
        return false;
    }

}
