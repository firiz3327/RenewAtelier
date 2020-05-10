package net.firiz.renewatelier.script.engine;

import org.apache.commons.io.IOUtils;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.script.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author firiz
 */
public final class GraalEngine extends AbstractScriptEngine implements Compilable, Invocable, AutoCloseable {

    public enum Type {
        PYTHON("python", "import polyglot\nsc = polyglot.import_value('sc')\n");

        final String language;
        final String importText;

        Type(String language, String importText) {
            this.language = language;
            this.importText = importText;
        }
    }

    private final Type type;
    private final Context c;

    public GraalEngine(Type type) {
        this.type = type;
        this.c = Context.newBuilder(type.language).allowIO(true).build();
        setBindings(createBindings(), ScriptContext.ENGINE_SCOPE);
    }

    @Override
    public Object eval(String script, ScriptContext scriptContext) throws ScriptException {
        return eval(createSource(script, scriptContext), scriptContext);
    }

    @Override
    public Object eval(Reader reader, ScriptContext scriptContext) throws ScriptException {
        return eval(createSource(createScriptCode(reader), scriptContext), scriptContext);
    }

    @NotNull
    private String createScriptCode(Reader reader) {
        final StringBuilder sb = new StringBuilder();
        final BufferedReader br = IOUtils.toBufferedReader(reader);
        try {
            String str;
            while ((str = br.readLine()) != null) {
                sb.append(str).append("\n");
            }
        } catch (IOException ex) {
            Logger.getLogger(GraalEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sb.toString();
    }

    private Source createSource(String script, ScriptContext scriptContext) {
        return Source.newBuilder(
                type.language,
                type.importText.concat(script),
                getScriptName(scriptContext)
        ).buildLiteral();
    }

    private Object eval(Source source, ScriptContext scriptContext) throws ScriptException {
        final Bindings globalBindings = scriptContext.getBindings(ScriptContext.GLOBAL_SCOPE);
        final Bindings engineBindings = scriptContext.getBindings(ScriptContext.ENGINE_SCOPE);
        try {
            if (globalBindings != null) {
                globalBindings.keySet().forEach(key -> c.getPolyglotBindings().putMember(key, globalBindings.get(key)));
            }
            if (engineBindings != null) {
                engineBindings.keySet().forEach(key -> c.getPolyglotBindings().putMember(key, engineBindings.get(key)));
            }
            return c.eval(source).as(Object.class);
        } catch (PolyglotException ex) {
            throw new ScriptException(ex);
        }
    }

    static Value evalInternal(Context context, String script, String language) {
        return context.eval(Source.newBuilder(language, script, "internal-script").internal(true).buildLiteral());
    }

    private String getScriptName(final ScriptContext scriptContext) {
        final Object val = scriptContext.getAttribute("javax.script.filename");
        return val != null ? val.toString() : "<eval>";
    }

    @NotNull
    @Contract(" -> new")
    @Override
    public Bindings createBindings() {
        final Bindings oldBindings = getBindings(ScriptContext.ENGINE_SCOPE);
        if (oldBindings != null && oldBindings instanceof GraalBindings) {
            oldBindings.clear();
        }
        return new GraalBindings(c);
    }

    @Contract("null, _, _ -> fail")
    @Override
    public Object invokeMethod(Object thiz, String name, Object... args) throws ScriptException, NoSuchMethodException {
        if (thiz == null) {
            throw new IllegalArgumentException("thiz is not a valid object.");
        } else {
            final Value value = c.asValue(thiz);
            final Value function = value.getMember(name);
            return invoke(name, function, args);
        }
    }

    @Override
    public Object invokeFunction(String name, Object... args) throws ScriptException, NoSuchMethodException {
        final Value value = c.getBindings(type.language).getMember(name);
        return invoke(name, value, args);
    }

    @Contract("_, null, _ -> fail")
    public static Object invoke(String methodName, Value function, Object... args) throws NoSuchMethodException, ScriptException {
        if (function == null) {
            throw new NoSuchMethodException(methodName);
        } else if (!function.canExecute()) {
            throw new NoSuchMethodException(methodName + " is not a function");
        } else {
            try {
                return function.execute(args).as(Object.class);
            } catch (PolyglotException ex) {
                throw new ScriptException(ex);
            }
        }
    }

    @Override
    public void close() {
        c.close();
    }

    @NotNull
    public CompiledScript compile(String script) {
        throw new IllegalStateException("Context already closed.");
    }

    @NotNull
    public CompiledScript compile(Reader reader) {
        throw new IllegalStateException("Context already closed.");
    }


    public <T> T getInterface(Class<T> clasz) {
        throw new UnsupportedOperationException("getInterface is not supported.");
    }

    public <T> T getInterface(Object thiz, Class<T> clasz) {
        throw new UnsupportedOperationException("getInterface is not supported.");
    }

    @Contract(" -> fail")
    @Override
    @Deprecated
    public ScriptEngineFactory getFactory() {
        throw new UnsupportedOperationException("getFactory is not supported.");
    }

}
