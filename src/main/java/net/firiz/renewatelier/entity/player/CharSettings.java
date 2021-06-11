package net.firiz.renewatelier.entity.player;

import net.firiz.renewatelier.sql.SQLManager;

public class CharSettings {

    private boolean showDamage = true;
    private boolean showOthersDamage = true;
    private boolean showPlayerChat = true;

    public CharSettings() {
    }

    public CharSettings(boolean damage, boolean showOthersDamage, boolean showPlayerChat) {
        this.showDamage = damage;
        this.showOthersDamage = showOthersDamage;
        this.showPlayerChat = showPlayerChat;
    }

    public void save(int id) {
        SQLManager.INSTANCE.insert(
                "charSettings",
                new String[]{"userId", "showDamage", "showOthersDamage", "showPlayerChat"},
                new Object[]{id, showDamage, showOthersDamage, showPlayerChat}
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

    public boolean isShowPlayerChat() {
        return showPlayerChat;
    }

    public void setShowPlayerChat(boolean showPlayerChat) {
        this.showPlayerChat = showPlayerChat;
    }
}
