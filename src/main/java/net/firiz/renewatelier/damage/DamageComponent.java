package net.firiz.renewatelier.damage;

import org.jetbrains.annotations.NotNull;

public class DamageComponent {

    private double damage;
    @NotNull
    private final AttackAttribute attackAttribute;

    public DamageComponent(double damage, @NotNull AttackAttribute attackAttribute) {
        this.damage = damage;
        this.attackAttribute = attackAttribute;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    @NotNull
    public AttackAttribute getAttackAttribute() {
        return attackAttribute;
    }
}
