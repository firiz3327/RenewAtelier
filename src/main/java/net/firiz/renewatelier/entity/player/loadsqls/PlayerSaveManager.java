/*
 * PlayerSaveManager.java
 *
 * Copyright (c) 2018 firiz.
 *
 * This file is part of Expression program is undefined on line 6, column 40 in Templates/Licenses/license-licence-gplv3.txt..
 *
 * Expression program is undefined on line 8, column 19 in Templates/Licenses/license-licence-gplv3.txt. is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Expression program is undefined on line 13, column 19 in Templates/Licenses/license-licence-gplv3.txt. is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Expression program is undefined on line 19, column 30 in Templates/Licenses/license-licence-gplv3.txt..  If not, see <http ://www.gnu.org/licenses/>.
 */
package net.firiz.renewatelier.entity.player.loadsqls;

import java.util.*;

import net.firiz.renewatelier.entity.player.Char;
import net.firiz.renewatelier.entity.player.stats.CharStats;
import net.firiz.renewatelier.script.execution.ScriptManager;
import net.firiz.renewatelier.sql.SQLManager;
import net.firiz.renewatelier.utils.Chore;
import net.firiz.renewatelier.version.inject.PlayerInjection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * @author firiz
 */
public enum PlayerSaveManager {
    INSTANCE; // enum singleton style

    private final SQLManager sql = SQLManager.INSTANCE;
    private final ScriptManager script = ScriptManager.INSTANCE;
    private final Map<UUID, Char> statusList = new HashMap<>();
    private final Set<StatusLoader<?>> loaders = new LinkedHashSet<>();

    private final String[] columns = {
            "uuid",
            "id",
            "email",
            "password",
            "level",
            "exp",
            "alchemyLevel",
            "alchemyExp",
            "maxHp",
            "hp",
            "maxMp",
            "mp",
            "atk",
            "def",
            "speed",
            "money"
    };

    PlayerSaveManager() {
        loaders.add(new RecipeStatusLoader());
        loaders.add(new QuestStatusLoader());
        loaders.add(new DiscoveredRecipeLoader());
        loaders.add(new BuffLoader());
        loaders.add(new CharSettingLoader());
    }

    public void loadPlayers() {
        Bukkit.getWorlds().forEach(world -> world.getPlayers().forEach(player -> {
            if (player.isDead()) {
                player.spigot().respawn();
            }
            loadStatus(player);
            PlayerInjection.inject(player);
        }));
    }

    public Char getChar(final UUID uuid) {
        if (!statusList.containsKey(uuid)) {
            throw new IllegalStateException("PlayerStatus: ".concat(uuid.toString()).concat(" is unload"));
        } else {
            return statusList.get(uuid);
        }
    }

    public void loadStatus(final Player player) {
        final UUID uuid = player.getUniqueId();
        final Object[] wheres = new Object[]{uuid.toString()};
        final String tableName = "accounts";
        List<List<Object>> select = sql.select(tableName, columns, wheres);
        if (select.isEmpty()) {
            sql.insert(tableName, "uuid", uuid.toString());
            select = sql.select(tableName, columns, wheres);
        }
        final List<Object> dataList = select.get(0);
        final int id = (int) dataList.get(1);
        final String email = (String) dataList.get(2);
        final String password = (String) dataList.get(3);
        final int level = (int) dataList.get(4);
        final long exp = (long) dataList.get(5);
        final int alchemyLevel = (int) dataList.get(6);
        final int alchemyExp = (int) dataList.get(7);
        final int maxHp = (int) dataList.get(8);
        final int hp = (int) dataList.get(9);
        final int maxMp = (int) dataList.get(10);
        final int mp = (int) dataList.get(11);
        final int atk = (int) dataList.get(12);
        final int def = (int) dataList.get(13);
        final int speed = (int) dataList.get(14);
        final int money = (int) dataList.get(15);
        final List<Object> loaderValues = new ArrayList<>();
        for (final StatusLoader<?> sLoader : loaders) {
            loaderValues.add(sLoader.load(id));
        }
        final Char status = new Char(
                uuid,
                id,
                email,
                password,
                new CharStats(
                        player,
                        level,
                        exp,
                        alchemyLevel,
                        alchemyExp,
                        money,
                        maxHp,
                        hp,
                        maxMp,
                        mp,
                        atk,
                        def,
                        speed,
                        Chore.cast(loaderValues.get(3)) // buffLoader
                ),
                Chore.cast(loaderValues.get(0)), // recipeStatusLoader
                Chore.cast(loaderValues.get(1)), // questStatusLoader
                Chore.cast(loaderValues.get(2)), // discoveredRecipeLoader
                Chore.cast(loaderValues.get(4)) // charSettingLoader
        );
        new Thread(() -> {
            status.setJsEngine(script.createJsEngine());
            status.setPy3Engine(script.createPy3Engine());
            status.setEnginesUsable(true);
        }).start();
        statusList.put(uuid, status);
    }

    public void unloadStatus(final UUID uuid) {
        final Char c = statusList.remove(uuid);
        if (c != null) {
            c.unload();
        }
    }

}
