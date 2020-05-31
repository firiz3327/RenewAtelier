package net.firiz.renewatelier.version.tab.contents;

import net.firiz.renewatelier.entity.player.Char;
import net.firiz.renewatelier.version.tab.TabListItem;

public class FriendListTabContents implements TabContents {

    @Override
    public void update(int slot, TabListItem item, Char player) {
    }

    @Override
    public boolean isUpdater() {
        return true;
    }

}
