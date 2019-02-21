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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.AbstractScriptEngine;
import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;
import org.apache.commons.io.IOUtils;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

/**
 *
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
    public Object eval(String script, ScriptContext ctxt) throws ScriptException {
        return eval(createSource(script, ctxt), ctxt);
    }

    @Override
    public Object eval(Reader reader, ScriptContext ctxt) throws ScriptException {
        final StringBuilder sb = new StringBuilder();
        sb.append(type.importText); // import文を含ませるため、一度文字列に変換してSourceを作成
        final BufferedReader br = IOUtils.toBufferedReader(reader);
        try {
            String str;
            while ((str = br.readLine()) != null) {
                sb.append(str).append("\n");
            }
        } catch (IOException ex) {
            Logger.getLogger(GraalEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        return eval(createSource(sb.toString(), ctxt), ctxt);
    }

    private Source createSource(String script, ScriptContext ctxt) {
        return Source.newBuilder(type.language, type.importText.concat(script), getScriptName(ctxt)).buildLiteral();
    }

    private Object eval(Source source, ScriptContext scriptContext) throws ScriptException {
        final Bindings globalBindings = scriptContext.getBindings(ScriptContext.GLOBAL_SCOPE);
        final Bindings engineBindings = scriptContext.getBindings(ScriptContext.ENGINE_SCOPE);
        try {
            if (globalBindings != null) {
                globalBindings.keySet().forEach((key) -> {
                    c.getPolyglotBindings().putMember(key, globalBindings.get(key));
                });
            }
            if (engineBindings != null) {
                engineBindings.keySet().forEach((key) -> {
                    c.getPolyglotBindings().putMember(key, engineBindings.get(key));
                });
            }
            return c.eval(source).as(Object.class);
        } catch (PolyglotException var10) {
            throw new ScriptException(var10);
        }
    }

    private String getScriptName(final ScriptContext ctxt) {
        Object val = ctxt.getAttribute("javax.script.filename");
        return val != null ? val.toString() : "<eval>";
    }

    @Override
    public Bindings createBindings() {
        final Bindings oldb = getBindings(ScriptContext.ENGINE_SCOPE);
        if (oldb != null && oldb instanceof GraalBindings) {
            oldb.clear();
        }
        return new GraalBindings(c);
    }

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

    public static Object invoke(String methodName, Value function, Object... args) throws NoSuchMethodException, ScriptException {
        if (function == null) {
            throw new NoSuchMethodException(methodName);
        } else if (!function.canExecute()) {
            throw new NoSuchMethodException(methodName + " is not a function");
        } else {
            try {
                return function.execute(args).as(Object.class);
            } catch (PolyglotException var4) {
                throw new ScriptException(var4);
            }
        }
    }

    @Override
    public void close() throws Exception {
        c.close();
    }

    //<editor-fold defaultstate="collapsed" desc="not support codes">
    @Override
    @Deprecated
    public ScriptEngineFactory getFactory() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    @Deprecated
    public CompiledScript compile(String script) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    @Deprecated
    public CompiledScript compile(Reader script) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    @Deprecated
    public <T> T getInterface(Class<T> clasz) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    @Deprecated
    public <T> T getInterface(Object thiz, Class<T> clasz) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    //</editor-fold>

}
