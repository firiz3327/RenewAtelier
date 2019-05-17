/*
 * ConfigLoader.java
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
package net.firiz.renewatelier.config.loader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.firiz.renewatelier.AtelierPlugin;
import net.firiz.renewatelier.utils.CustomConfig;
import org.bukkit.configuration.file.FileConfiguration;

/**
 *
 * @author firiz
 */
public abstract class ConfigLoader<T> {

    private final String folder;
    private final List<T> list = new ArrayList<>();
    protected final AtelierPlugin plugin = AtelierPlugin.getPlugin();

    public ConfigLoader(final String folder) {
        this.folder = folder;
    }

    public final void load() {
        final File[] files = new File(plugin.getDataFolder(), folder).listFiles();
        if (files != null) {
            list.clear();
            for (final File f : files) {
                if (!(f.getName().endsWith(".yml") || f.getName().endsWith(".yaml") || f.getName().endsWith(".YML") || f.getName().endsWith(".YAML"))) {
                    continue;
                }
                loadConfig(new CustomConfig(plugin, folder, f.getName()).getConfig());
            }
        }
    }

    abstract protected void loadConfig(final FileConfiguration config);

    public final void add(final T value) {
        list.add(value);
    }

    public final List<T> getList() {
        return list;
    }

    public final String getFolder() {
        return folder;
    }

}
