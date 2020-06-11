package net.firiz.renewatelier.damage;

class MeruruCalcDamage {

    public double calcPhysicsDamage(
            double baseDamage, // original 基礎ダメージ
            double atk, // 攻撃者の攻撃力
            double power, // 威力値
            double victimDef, // 被害者の防御力
            double victimPhysicsDef, // 被害者の物理耐性
            double criticalMag, // クリティカル倍率 150~
            boolean isCritical
    ) {
        final double atkDamage = Math.pow(Math.abs(atk - 35.5), 1.927) / 185.6 + atk - 2;
        final double victimDefValue = victimDef / 2 + victimDef / 10;
        return (((baseDamage * 0.8 + atkDamage) * power / 100 - victimDefValue) * (100 - victimPhysicsDef) / 100) * (isCritical ? criticalMag * 0.01 : 1);
    }

    public double attributeDamage(
            double atk, // 攻撃者の攻撃力
            double power, // 威力値
            double victimAttributeDef, // 被害者の属性耐性
            double criticalMag, // クリティカル倍率 150~
            boolean isCritical
    ) {
        final double baseDamage = Math.pow(Math.abs(atk - 35.5), 1.927) / 185.6 + atk - 2;
        return ((baseDamage * power / 100) * (100 - victimAttributeDef) / 100) * (isCritical ? criticalMag * 0.01 : 1);
    }

}
