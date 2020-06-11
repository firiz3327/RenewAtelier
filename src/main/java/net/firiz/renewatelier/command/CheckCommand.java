package net.firiz.renewatelier.command;

import net.firiz.renewatelier.entity.player.sql.load.PlayerSaveManager;
import net.firiz.renewatelier.entity.player.stats.CharStats;
import net.firiz.renewatelier.npc.NPCManager;
import net.firiz.renewatelier.utils.Chore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.function.Function;

public class CheckCommand implements CommandExecutor {

    private static final PlayerSaveManager psm = PlayerSaveManager.INSTANCE;

    private final List<Function<CharStats, String>> statsFunction;

    public CheckCommand() {
        statsFunction = new ArrayList<>();
        statsFunction.add(stats -> "Money: " + Chore.comma(stats.getMoney()));
        statsFunction.add(stats -> "AlchemyLevel: " + stats.getAlchemyLevel());
        statsFunction.add(this::alchemyExp);
        statsFunction.add(stats -> "Level: " + stats.getLevel() + symbol(stats.getPlusLevel()));
        statsFunction.add(this::exp);
        statsFunction.add(stats -> "Hp: " + ((int) stats.getHp()) + "/" + stats.getMaxHp() + symbol(stats.getPlusMaxHp()));
        statsFunction.add(stats -> "Mp: " + stats.getMp() + "/" + stats.getMaxMp() + symbol(stats.getPlusMaxMp()));
        statsFunction.add(stats -> "Atk: " + stats.getAtk() + symbol(stats.getPlusAtk()));
        statsFunction.add(stats -> "Def: " + stats.getDef() + symbol(stats.getPlusDef()));
        statsFunction.add(stats -> "Spd: " + stats.getSpeed() + symbol(stats.getPlusSpeed()));
        statsFunction.add(this::attSpd);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player) {
            final UUID uuid = ((Player) commandSender).getUniqueId();
            NPCManager.INSTANCE.dispose(uuid);

            final CharStats stats = psm.getChar(uuid).getCharStats();
            final StringJoiner joiner = new StringJoiner(" | ");
            statsFunction.stream().map(fun -> fun.apply(stats)).forEach(joiner::add);
            commandSender.sendMessage(joiner.toString());
            return true;
        }
        commandSender.sendMessage("player only.");
        return false;
    }

    @NotNull
    private String symbol(int num) {
        if (num == 0) {
            return "";
        } else if (num > 0) {
            return "(+" + num + ")";
        }
        return "(" + num + ")";
    }

    @NotNull
    private String exp(CharStats stats) {
        final BigDecimal percent = BigDecimal.valueOf(stats.getExp())
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(stats.getRequiredExp()), 0, RoundingMode.DOWN);
        return "Exp: " + Chore.comma(stats.getExp()) + "/" + Chore.comma(stats.getRequiredExp()) + "(" + Chore.comma(percent.intValue()) + "%)";
    }

    @NotNull
    private String alchemyExp(CharStats stats) {
        final BigDecimal percent = BigDecimal.valueOf(stats.getAlchemyExp())
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(stats.getRequiredAlchemyExp()), 0, RoundingMode.DOWN);
        return "AlchemyExp: " + Chore.comma(stats.getAlchemyExp()) + "/" + Chore.comma(stats.getRequiredAlchemyExp()) + "(" + Chore.comma(percent.intValue()) + "%)";
    }

    @NotNull
    private String attSpd(CharStats stats) {
        return "AttSpd: "
                + BigDecimal.valueOf(stats.getAttackSpeed())
                .divide(BigDecimal.valueOf(250), 2, RoundingMode.DOWN)
                .stripTrailingZeros()
                .toPlainString();
    }

}
