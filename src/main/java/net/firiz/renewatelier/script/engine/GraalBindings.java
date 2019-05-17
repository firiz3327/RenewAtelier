/*
 * GraalBindings.java
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
package net.firiz.renewatelier.script.engine;

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

    private final Value bindings;

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
        m.keySet().forEach((key) -> put(key, m.get(key)));
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

}
