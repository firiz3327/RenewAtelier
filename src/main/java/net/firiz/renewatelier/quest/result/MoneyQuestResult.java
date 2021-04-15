package net.firiz.renewatelier.quest.result;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.entity.Player;

/**
 * @author firiz
 */
public class MoneyQuestResult extends IntQuestResult {

    public MoneyQuestResult(int result) {
        super(result);
    }

    @Override
    public void appendQuestResult(Player player, TextComponent.Builder builder) {
        final int money = getResult();
        builder.append(Component.text("報酬金: " + money + " $"));
    }
}
