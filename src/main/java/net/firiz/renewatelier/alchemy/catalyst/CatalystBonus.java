package net.firiz.renewatelier.alchemy.catalyst;

/**
 *
 * @author firiz
 */
public class CatalystBonus {
    private final int[] cs;
    private final CatalystBonusData data;

    public CatalystBonus(int[] cs, CatalystBonusData data) {
        this.cs = cs;
        this.data = data;
    }
    
    public int[] getCS() {
        return cs;
    }
    
    public CatalystBonusData getData() {
        return data;
    }
}
