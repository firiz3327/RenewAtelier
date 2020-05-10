package net.firiz.renewatelier.quest.result;

import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.entity.Player;

public interface QuestResult {

    void appendQuestResult(Player player, ComponentBuilder builder);

}
