package net.firiz.renewatelier.quest.result;

import net.firiz.renewatelier.inventory.item.json.AlchemyItemStatus;
import net.firiz.renewatelier.quest.QuestItem;
import net.firiz.renewatelier.utils.TellrawUtils;
import net.firiz.renewatelier.version.LanguageItemUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

/**
 * @author firiz
 */
public class ItemQuestResult extends ObjectQuestResult<QuestItem> {

    public ItemQuestResult(QuestItem result) {
        super(result);
    }

    @Override
    public void appendQuestResult(Player player, TextComponent.Builder builder) {
        final QuestItem questItem = getResult();
        final ItemStack viewItem = questItem.getItem(new AlchemyItemStatus.VisibleFlags(
                false,
                true, // quality - default min
                (questItem.getIngredients() != null), // ingredients
                false,
                false,
                false,
                false,
                true
        ));
        final Component name = Objects.requireNonNullElse(viewItem.hasItemMeta() && viewItem.getItemMeta().hasDisplayName()
                        ? viewItem.getItemMeta().displayName()
                        : Component.text(LanguageItemUtil.getLocalizeName(viewItem, player)),
                Component.text("ErrorName"));
        builder.append(Component.text("アイテム: "))
                .append(name.hoverEvent(viewItem.asHoverEvent()));
    }
}
