package net.firiz.renewatelier.damage;

public class DamageComponent {

    private final double damage;
    private final AttackAttribute attackAttribute;

    public DamageComponent(double damage, AttackAttribute attackAttribute) {
        this.damage = damage;
        this.attackAttribute = attackAttribute;
    }

    public double getDamage() {
        return damage;
    }

    public AttackAttribute getAttackAttribute() {
        return attackAttribute;
    }
}
