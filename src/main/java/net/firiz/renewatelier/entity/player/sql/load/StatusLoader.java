package net.firiz.renewatelier.entity.player.sql.load;

import org.jetbrains.annotations.NotNull;

/**
 *
 * @author firiz
 */
interface StatusLoader<T> {

    @NotNull
    T load(int id);
    
}
