/*
 * GraalPy3Engine.java
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

import java.io.IOException;
import java.io.Reader;
import javax.script.AbstractScriptEngine;
import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

/**
 *
 * @author firiz
 */
public class GraalPy3Engine extends AbstractScriptEngine implements Compilable, Invocable, AutoCloseable {

    private static final String ID = "python";
    private final Context c;

    public GraalPy3Engine() {
        c = Context.newBuilder(ID).allowIO(true).build();
    }

    @Override
    public Object eval(Reader reader, ScriptContext ctxt) throws ScriptException {
        return eval(createSource(reader, ctxt), ctxt);
    }

    private Source createSource(Reader reader, ScriptContext ctxt) throws ScriptException {
        try {
            return Source.newBuilder(ID, reader, getScriptName(ctxt)).build();
        } catch (IOException var3) {
            throw new ScriptException(var3);
        }
    }

    @Override
    public Object eval(String script, ScriptContext ctxt) throws ScriptException {
        return eval(createSource(script, ctxt), ctxt);
    }

    private Source createSource(String script, ScriptContext ctxt) {
        return Source.newBuilder(ID, script, getScriptName(ctxt)).buildLiteral();
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
        return new SimpleBindings();
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
        final Value value = c.getBindings(ID).getMember(name);
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
