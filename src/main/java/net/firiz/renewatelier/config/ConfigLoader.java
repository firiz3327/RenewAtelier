package net.firiz.renewatelier.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.firiz.renewatelier.AtelierPlugin;
import net.firiz.renewatelier.utils.CustomConfig;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * @author firiz
 */
public abstract class ConfigLoader<T> {

    private final File file;
    private final boolean folder;
    private final List<T> list = new ArrayList<>();
    protected final AtelierPlugin plugin = AtelierPlugin.getPlugin();

    ConfigLoader(final File file, final boolean folder) {
        this.file = file;
        this.folder = folder;
    }

    protected void initClear() {
    }

    public final void load() {
        if (folder) {
            final File[] files = file.listFiles();
            if (files != null) {
                list.clear();
                initClear();
                for (final File f : files) {
                    if (!(f.getName().endsWith(".yml") || f.getName().endsWith(".yaml") || f.getName().endsWith(".YML") || f.getName().endsWith(".YAML"))) {
                        continue;
                    }
                    loadConfig(new CustomConfig(plugin, file.getName(), f.getName()).getConfig());
                }
            }
        } else {
            loadConfig(new CustomConfig(plugin, file.getName()).getConfig());
        }
        loadEnd();
    }

    protected void loadEnd() {
    }

    protected abstract void loadConfig(final FileConfiguration config);

    public final void add(final T value) {
        list.add(value);
    }

    public final List<T> getList() {
        return list;
    }

}
