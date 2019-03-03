/*
 * GraalEngine.java
 *
 * Copyright (c) 2019 firiz.
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
package jp.gr.java_conf.zakuramomiji.renewatelier.script.engine;

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
    private volatile boolean closed;

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
//        final Bindings engineBindings = scriptContext.getBindings(ScriptContext.ENGINE_SCOPE);
//        if (engineBindings != null && engineBindings instanceof GraalBindings) {
//            engineBindings.clear();
//        }

//        final StringBuilder sb = new StringBuilder();
//        sb.append(type.importText); // import文を含ませるため、一度文字列に変換してSourceを作成
//        final BufferedReader br = IOUtils.toBufferedReader(reader);
//        try {
//            String str;
//            while ((str = br.readLine()) != null) {
//                sb.append(str).append("\n");
//            }
//        } catch (IOException ex) {
//            Logger.getLogger(GraalEngine.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return eval(createSource(sb.toString(), scriptContext), scriptContext);

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
//        c.initialize(source.getLanguage());
        final Bindings globalBindings = scriptContext.getBindings(ScriptContext.GLOBAL_SCOPE);
        final Bindings engineBindings = scriptContext.getBindings(ScriptContext.ENGINE_SCOPE);
        try {
            if (globalBindings != null) {
                globalBindings.keySet().forEach((key) -> c.getPolyglotBindings().putMember(key, globalBindings.get(key)));
            }
            if (engineBindings != null) {
                engineBindings.keySet().forEach((key) -> c.getPolyglotBindings().putMember(key, engineBindings.get(key)));
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
        this.closed = true;
    }

    @NotNull
    public CompiledScript compile(String script) {
//        if (this.closed) {
            throw new IllegalStateException("Context already closed.");
//        } else {
//            final Source source = createSource(script, this.getContext());
//            return new CompiledScript() {
//                public ScriptEngine getEngine() {
//                    return GraalEngine.this;
//                }
//
//                public Object eval(ScriptContext ctx) throws ScriptException {
//                    return GraalEngine.this.eval(source, ctx);
//                }
//            };
//        }
    }

    @NotNull
    public CompiledScript compile(Reader reader) {
//        if (this.closed) {
            throw new IllegalStateException("Context already closed.");
//        } else {
//            final Source source = createSource(createScriptCode(reader), this.getContext());
//            return new CompiledScript() {
//                public ScriptEngine getEngine() {
//                    return GraalEngine.this;
//                }
//
//                public Object eval(ScriptContext ctx) throws ScriptException {
//                    return GraalEngine.this.eval(source, ctx);
//                }
//            };
//        }
    }


    public <T> T getInterface(Class<T> clasz) {
        throw new UnsupportedOperationException("Not supported yet.");
//        return evalInternal(c, "this", type.language).as(clasz);
    }

    public <T> T getInterface(Object thiz, Class<T> clasz) {
        throw new UnsupportedOperationException("Not supported yet.");
//        return c.asValue(thiz).as(clasz);
    }

    @Contract(" -> fail")
    @Override
    @Deprecated
    public ScriptEngineFactory getFactory() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
