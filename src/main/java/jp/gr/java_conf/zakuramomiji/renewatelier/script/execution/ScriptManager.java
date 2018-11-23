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

import jp.gr.java_conf.zakuramomiji.renewatelier.constants.ServerConstants;
import org.bukkit.entity.Player;

/**
 *
 * @author firiz
 */
public final class ScriptManager {

    private static final ScriptManager INSTANCE = new ScriptManager();
    private final ScriptObject script;
    
    private ScriptManager() {
        script = ServerConstants.NASHORN ? new ScriptNashorn() : new ScriptGraalJS();
    }

    public static ScriptManager getInstance() {
        return INSTANCE;
    }

    public void start(final String name, final Player player) {
        script.start(name, player);
    }

    public void start(final String name, final Player player, final String functionName, final Object... args) {
        script.start(name, player, functionName, args);
    }
}
