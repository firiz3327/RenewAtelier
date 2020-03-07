package net.firiz.renewatelier.entity.player.loadsqls;

import net.firiz.renewatelier.buff.Buff;
import net.firiz.renewatelier.buff.BuffType;
import net.firiz.renewatelier.buff.BuffValueType;
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
                new String[]{"userId", "value", "buffValueType", "level", "buffType", "duration", "limitDuration", "x"},
                new Object[]{id}
        );
        final List<Buff> saveTypes = new ArrayList<>();
        saveTypesObj.forEach(datas -> saveTypes.add(new Buff(
                (String) datas.get(1), // value
                BuffValueType.valueOf((String) datas.get(2)), // buffValueType
                (int) datas.get(3), // level
                BuffType.valueOf((String) datas.get(4)), // buffType
                (int) datas.get(5), // duration
                (int) datas.get(6), // limitDuration
                (int) datas.get(7)) // x
        ));
        return saveTypes;
    }

}
