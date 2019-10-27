package net.firiz.renewatelier.entity.player.loadsqls;

import net.firiz.renewatelier.buff.Buff;
import net.firiz.renewatelier.buff.BuffType;
import net.firiz.renewatelier.sql.SQLManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BuffLoader implements StatusLoader<List<Buff>> {

    @NotNull
    @Override
    public List<Buff> load(int id) {
        final List<List<Object>> saveTypesObj = SQLManager.INSTANCE.select(
                "buffs",
                new String[]{"userId", "buffType", "duration", "limitDuration", "x"},
                new Object[]{id}
        );
        final List<Buff> saveTypes = new ArrayList<>();
        saveTypesObj.forEach(datas -> saveTypes.add(new Buff(
                BuffType.valueOf((String) datas.get(1)), // buffType
                (int) datas.get(2), // duration
                (int) datas.get(3), // limitDuration
                (int) datas.get(4)) // x
        ));
        return saveTypes;
    }

}
