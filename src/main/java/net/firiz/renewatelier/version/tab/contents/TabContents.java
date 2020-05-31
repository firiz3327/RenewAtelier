package net.firiz.renewatelier.version.tab.contents;

import net.firiz.renewatelier.entity.player.Char;
import net.firiz.renewatelier.version.tab.TabListItem;

public interface TabContents {

    /**
     *
     * @param slot
     * @param item
     * @param player isUpdater ? NotNull : Nullable
     */
    void update(int slot, TabListItem item, Char player);

    boolean isUpdater();

}
