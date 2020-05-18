package net.firiz.renewatelier.quest.result;

import net.firiz.renewatelier.item.json.AlchemyItemStatus;
import net.firiz.renewatelier.quest.QuestItem;
import net.firiz.renewatelier.utils.TellrawUtils;
import net.firiz.renewatelier.version.LanguageItemUtil;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author firiz
 */
public class ItemQuestResult extends ObjectQuestResult<QuestItem> {
    
    public ItemQuestResult(QuestItem result) {
        super(result);
    }

    @Override
    public void appendQuestResult(Player player, ComponentBuilder builder) {
        final QuestItem questItem = getResult();
        final ItemStack viewItem = questItem.getItem(new AlchemyItemStatus.VisibleFlags(
                false, // id
                true, // quality - default min
                (questItem.getIngredients() != null), // ings
                false, // size
                false, // catalyst
                false, // category
                false,
                true
        ));
        final String name = viewItem.hasItemMeta() && viewItem.getItemMeta().hasDisplayName()
                ? viewItem.getItemMeta().getDisplayName()
                : LanguageItemUtil.getLocalizeName(viewItem, player);
        builder.append("アイテム: ").append(
                questItem.getName() == null ? name : questItem.getName()
        ).event(TellrawUtils.createHoverEvent(viewItem));
    }
}
