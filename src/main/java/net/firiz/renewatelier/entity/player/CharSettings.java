package net.firiz.renewatelier.entity.player;

import net.firiz.renewatelier.sql.SQLManager;

public class CharSettings {

    private boolean showDamage;
    private boolean showOthersDamage;

    public CharSettings(boolean damage, boolean showOthersDamage) {
        this.showDamage = damage;
        this.showOthersDamage = showOthersDamage;
    }

    public void save(int id) {
        SQLManager.INSTANCE.insert(
                "charSettings",
                new String[]{"userId", "showDamage", "showOthersDamage"},
                new Object[]{id, showDamage, showOthersDamage}
        );
    }

    public boolean isShowDamage() {
        return showDamage;
    }

    public void setShowDamage(boolean showDamage) {
        this.showDamage = showDamage;
    }

    public boolean isShowOthersDamage() {
        return showOthersDamage;
    }

    public void setShowOthersDamage(boolean showOthersDamage) {
        this.showOthersDamage = showOthersDamage;
    }
}
