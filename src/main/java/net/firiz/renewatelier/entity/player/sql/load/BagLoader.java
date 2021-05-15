package net.firiz.renewatelier.entity.player.sql.load;

import net.firiz.renewatelier.inventory.item.json.AlchemyItemBag;
import net.firiz.renewatelier.sql.SQLManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BagLoader implements StatusLoader<AlchemyItemBag> {

    @NotNull
    @Override
    public AlchemyItemBag load(int id) {
        final List<List<Object>> bagItems = SQLManager.INSTANCE.select(
                "bagitems",
                new String[]{"userId", "json"},
                new Object[]{id}
        );
        if (!bagItems.isEmpty()) {
            final List<Object> datas = bagItems.get(0);
            return new AlchemyItemBag((String) datas.get(1));
        }
        return new AlchemyItemBag();
    }

}
