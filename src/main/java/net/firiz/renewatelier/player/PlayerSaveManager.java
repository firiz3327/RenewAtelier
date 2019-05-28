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
package net.firiz.renewatelier.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.firiz.renewatelier.alchemy.recipe.RecipeStatus;
import net.firiz.renewatelier.player.loadSQLs.DiscoveredRecipeLoader;
import net.firiz.renewatelier.player.loadSQLs.QuestStatusLoader;
import net.firiz.renewatelier.player.loadSQLs.RecipeStatusLoader;
import net.firiz.renewatelier.player.loadSQLs.StatusLoader;
import net.firiz.renewatelier.player.minecraft.MinecraftRecipeSaveType;
import net.firiz.renewatelier.quest.QuestStatus;
import net.firiz.renewatelier.script.execution.ScriptManager;
import net.firiz.renewatelier.sql.SQLManager;
import org.bukkit.Bukkit;

/**
 *
 * @author firiz
 */
public enum PlayerSaveManager {
    INSTANCE; // enum singleton style

    private final SQLManager sql = SQLManager.INSTANCE;
    private final ScriptManager script = ScriptManager.INSTANCE;
    private final Map<UUID, PlayerStatus> statusList = new HashMap<>();
    private final StatusLoader[] loaders = {
        new RecipeStatusLoader(),
        new QuestStatusLoader(),
        new DiscoveredRecipeLoader()
    };
    
    public void loadPlayers() {
        Bukkit.getWorlds().forEach((world) -> world.getPlayers().forEach((player) -> {
            loadStatus(player.getUniqueId());
        }));
    }

    public PlayerStatus getStatus(final UUID uuid) {
        if (!statusList.containsKey(uuid)) {
            throw new IllegalStateException("PlayerStatus: ".concat(uuid.toString()).concat(" is unload"));
        } else {
            return statusList.get(uuid);
        }
    }

    public void loadStatus(final UUID uuid) {
        //<editor-fold defaultstate="collapsed" desc="select id">
        final String[] columns = new String[]{"uuid", "id", "alchemy_level", "alchemy_exp"};
        final Object[] wheres = new Object[]{uuid.toString()};
        List<List<Object>> select = sql.select("accounts", columns, wheres);
        if (select.isEmpty()) {
            sql.insert("accounts", "uuid", uuid.toString());
            select = sql.select("accounts", columns, wheres);
        }
        //</editor-fold>
        final int id = (int) select.get(0).get(1);
        final int alchemy_level = (int) select.get(0).get(2);
        final int alchemy_exp = (int) select.get(0).get(3);
        final List<Object> objs = new ArrayList<>();
        for (final StatusLoader sloader : loaders) {
            objs.add(sloader.load(id));
        }
        final PlayerStatus status = new PlayerStatus(
                id,
                alchemy_level,
                alchemy_exp,
                (List<RecipeStatus>) objs.get(0), // recipeStatus
                (List<QuestStatus>) objs.get(1), // questStatus
                (List<MinecraftRecipeSaveType>) objs.get(2) // discoveredRecipes
        );
        new Thread(() -> {
            status.setJsEngine(script.createJsEngine());
            status.setPy2Engine(script.createPy2Engine());
            status.setPy3Engine(script.createPy3Engine());
        }).start();
        statusList.put(uuid, status);
    }

    public void unloadStatus(final UUID uuid) {
        statusList.remove(uuid);
    }

}
