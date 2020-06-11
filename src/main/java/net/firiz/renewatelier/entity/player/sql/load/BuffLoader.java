package net.firiz.renewatelier.entity.player.sql.load;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.firiz.renewatelier.buff.Buff;
import net.firiz.renewatelier.buff.BuffType;
import net.firiz.renewatelier.buff.BuffValueType;
import net.firiz.renewatelier.sql.SQLManager;
import org.jetbrains.annotations.NotNull;

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
        final List<Buff> saveTypes = new ObjectArrayList<>();
        saveTypesObj.forEach(obj -> saveTypes.add(new Buff(
                null,
                BuffValueType.valueOf((String) obj.get(1)), // buffValueType
                (int) obj.get(2), // level
                BuffType.valueOf((String) obj.get(3)), // buffType
                (int) obj.get(4), // duration
                (int) obj.get(5), // limitDuration
                (int) obj.get(6), // x
                (String) obj.get(7) // y
        )));
        return saveTypes;
    }

}
