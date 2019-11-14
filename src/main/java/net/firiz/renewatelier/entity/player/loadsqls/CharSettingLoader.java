package net.firiz.renewatelier.entity.player.loadsqls;

import net.firiz.renewatelier.entity.player.CharSettings;
import net.firiz.renewatelier.sql.SQLManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CharSettingLoader implements StatusLoader<CharSettings> {

    @NotNull
    @Override
    public CharSettings load(int id) {
        final List<List<Object>> charSettings = SQLManager.INSTANCE.select(
                "charSettings",
                new String[]{"userId", "showDamage", "showOthersDamage"},
                new Object[]{id}
        );
        if (!charSettings.isEmpty()) {
            final List<Object> datas = charSettings.get(0);
            return new CharSettings(
                    (boolean) datas.get(1),
                    (boolean) datas.get(2)
            );
        }
        return new CharSettings(true, true);
    }
}
