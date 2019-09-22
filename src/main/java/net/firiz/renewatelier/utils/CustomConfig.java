/*
 * CustomConfig.java
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
package net.firiz.renewatelier.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.YamlConstructor;
import org.bukkit.configuration.file.YamlRepresenter;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.representer.Representer;

/**
 * @author firiz
 */
public class CustomConfig {

    private FileConfiguration config = null;
    private final File configFile;
    private final String file;
    private final Plugin plugin;

    public CustomConfig(Plugin plugin) {
        this(plugin, "config.yml");
    }

    public CustomConfig(Plugin plugin, String fileName) {
        this.plugin = plugin;
        this.file = fileName;
        configFile = new File(plugin.getDataFolder(), file);
    }

    public CustomConfig(Plugin plugin, String parent, String fileName) {
        this.plugin = plugin;
        this.file = fileName;
        configFile = new File(new File(plugin.getDataFolder(), parent), file);
    }

    public void saveDefaultConfig() {
        if (!configFile.exists()) {
            plugin.saveResource(file, false);
        }
    }

    public void reloadConfig() {
        try {
            final InputStreamReader reader = new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8);
            Validate.notNull(reader, "Stream cannot be null");
            config = new CConfiguration();
            try {
                config.load(reader);
            } catch (IOException var3) {
                Bukkit.getLogger().log(Level.SEVERE, "Cannot load configuration from stream", var3);
            } catch (InvalidConfigurationException var4) {
                Bukkit.getLogger().log(Level.SEVERE, "Cannot load configuration from stream", var4);
            }
            final InputStream defConfigStream = plugin.getResource(file);
            if (defConfigStream == null) {
                return;
            }
            config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, StandardCharsets.UTF_8)));
        } catch (FileNotFoundException ex) {
            Chore.logWarning(ex);
        }
    }

    public FileConfiguration getConfig() {
        if (config == null) {
            reloadConfig();
        }
        return config;
    }

    public void saveConfig() {
        if (config == null) {
            return;
        }
        try {
            getConfig().save(configFile);
        } catch (IOException ex) {
            Chore.log(Level.SEVERE, "Could not save config to " + configFile, ex);
        }
    }

    private class CConfiguration extends YamlConfiguration {

        private final DumperOptions yamlOptions = new DumperOptions();
        private final Representer yamlRepresenter = new YamlRepresenter();
        private final Yaml overYaml;

        CConfiguration() {
            final YamlConstructor a = new YamlConstructor();
            a.setAllowDuplicateKeys(false);
            final LoaderOptions b = new LoaderOptions();
            b.setAllowDuplicateKeys(false);
            overYaml = new Yaml(a, this.yamlRepresenter, this.yamlOptions, b);
        }

        @Override
        public void loadFromString(@NotNull String contents) throws InvalidConfigurationException {
            Validate.notNull(contents, "Contents cannot be null");

            Map input;
            try {
                input = this.overYaml.load(contents);
            } catch (YAMLException var4) {
                throw new InvalidConfigurationException(var4);
            } catch (ClassCastException var5) {
                throw new InvalidConfigurationException("Top level is not a Map.");
            }

            String header = this.parseHeader(contents);
            if (header.length() > 0) {
                this.options().header(header);
            }

            if (input != null) {
                this.convertMapsToSections(input, this);
            }

        }

    }
}
