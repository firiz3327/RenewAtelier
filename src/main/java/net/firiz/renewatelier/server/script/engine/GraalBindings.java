package net.firiz.renewatelier.server.script.engine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import javax.script.Bindings;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

/**
 *
 * @author firiz
 */
public class GraalBindings extends HashMap<String, Object> implements Bindings {

    private final transient Value bindings;

    public GraalBindings(Context c) {
        this.bindings = c.getPolyglotBindings();
    }

    @Override
    public Object put(String key, Object value) {
        final Object oldVal = super.put(key, value);
        bindings.putMember(key, value);
        return oldVal;
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        m.keySet().forEach(key -> put(key, m.get(key)));
    }

    @Override
    public Object putIfAbsent(String key, Object value) {
        final Object oldVal = super.putIfAbsent(key, value);
        bindings.putMember(key, value);
        return oldVal;
    }

    @Override
    public Object remove(Object key) {
        final Object oldVal = super.remove(key);
        bindings.removeMember((String) key);
        return oldVal;
    }

    @Override
    public boolean remove(Object key, Object value) {
        final boolean val = super.remove(key, value);
        bindings.removeMember((String) key);
        return val;
    }

    @Override
    public void clear() {
        if (!isEmpty()) {
            new HashSet<>(keySet()).forEach(this::remove);
        }
    }

    @Override
    public boolean equals(Object obj) {
        throw new UnsupportedOperationException("equals is not supported.");
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException("hashCode is not supported.");
    }

}
