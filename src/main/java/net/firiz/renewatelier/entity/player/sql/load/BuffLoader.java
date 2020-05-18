package net.firiz.renewatelier.entity.player.sql.load;

import net.firiz.renewatelier.buff.Buff;
import net.firiz.renewatelier.buff.BuffType;
import net.firiz.renewatelier.buff.BuffValueType;
import net.firiz.renewatelier.sql.SQLManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

class BuffLoader implements StatusLoader<List<Buff>> {

    @NotNull
    @Override
    public List<Buff> load(int id) {
        final List<List<Object>> saveTypesObj = SQLManager.INSTANCE.select(
                "buffs",
                new String[]{"userId", "buffValueType", "level", "buffType", "duration", "limitDuration", "x"},
                new Object[]{id}
        );
        final List<Buff> saveTypes = new ArrayList<>();
        saveTypesObj.forEach(datas -> saveTypes.add(new Buff(
                null,
                BuffValueType.valueOf((String) datas.get(1)), // buffValueType
                (int) datas.get(2), // level
                BuffType.valueOf((String) datas.get(3)), // buffType
                (int) datas.get(4), // duration
                (int) datas.get(5), // limitDuration
                (int) datas.get(6)) // x
        ));
        return saveTypes;
    }

}
