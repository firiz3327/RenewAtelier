package net.firiz.renewatelier.entity.player.sql.load;

import java.util.List;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.firiz.renewatelier.quest.QuestStatus;
import net.firiz.renewatelier.sql.SQLManager;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author firiz
 */
class QuestStatusLoader implements StatusLoader<List<QuestStatus>> {

    @NotNull
    @Override
    public List<QuestStatus> load(int id) {
        final List<List<Object>> questStatusesObj = SQLManager.INSTANCE.select(
                "questDatas",
                new String[]{"userId", "questId", "clear"},
                new Object[]{id}
        );
        final List<QuestStatus> questStatuses = new ObjectArrayList<>();
        questStatusesObj.forEach(datas -> questStatuses.add(new QuestStatus(
                (String) datas.get(1), // questId
                (boolean) datas.get(2) // clear
        )));
        return questStatuses;
    }

}
