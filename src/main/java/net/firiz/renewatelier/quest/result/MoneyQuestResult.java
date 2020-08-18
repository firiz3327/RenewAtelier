package net.firiz.renewatelier.quest.result;

import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.entity.Player;

/**
 *
 * @author firiz
 */
public class MoneyQuestResult extends IntQuestResult {

    public MoneyQuestResult(int result) {
        super(result);
    }

    @Override
    public void appendQuestResult(Player player, ComponentBuilder builder) {
        final int money = getResult();
        builder.append("報酬金: " + money + " $");
    }
}
