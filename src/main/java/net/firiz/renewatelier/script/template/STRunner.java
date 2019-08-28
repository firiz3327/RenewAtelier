package net.firiz.renewatelier.script.template;

import net.firiz.renewatelier.script.template.uses.STBomb;
import net.firiz.renewatelier.utils.Chore;
import org.bukkit.configuration.ConfigurationSection;

import java.lang.reflect.InvocationTargetException;

public class STRunner {

    private enum STS {
        BOMB("bomb", STBomb.class)
        ;

        private final String id;
        private final Class<?> clasz;

        STS(String id, Class<?> clasz) {
            this.id = id;
            this.clasz = clasz;
        }
    }

    public ScriptTemplate getTemplate(final ConfigurationSection stConfig) {
        final String id = stConfig.getString("id");
        for (final STS sts : STS.values()) {
            if (sts.id.equalsIgnoreCase(id)) {
                try {
                    return (ScriptTemplate) sts.clasz.getConstructor(ConfigurationSection.class).newInstance(stConfig);
                } catch (InstantiationException e) {
                    Chore.logWarning(e);
                } catch (IllegalAccessException e) {
                    Chore.logWarning(e);
                } catch (InvocationTargetException e) {
                    Chore.logWarning(e);
                } catch (NoSuchMethodException e) {
                    Chore.logWarning(e);
                }
            }
        }
        return null;
    }

}
