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
