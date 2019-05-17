/*
 * QuestStatus.java
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
package net.firiz.renewatelier.quest;

/**
 *
 * @author firiz
 */
public class QuestStatus {

    private final String id;
    private boolean clear;
    
    public QuestStatus(String id) {
        this.id = id;
        this.clear = false;
    }

    public QuestStatus(String id, boolean clear) {
        this.id = id;
        this.clear = clear;
    }
    
    public String getId() {
        return id;
    }

    public boolean isClear() {
        return clear;
    }

    public void clear() {
        this.clear = true;
    }

}
