package net.firiz.renewatelier.item.json.itemeffect;

import net.firiz.renewatelier.alchemy.recipe.StarEffect;
import net.firiz.renewatelier.buff.BuffData;
import net.firiz.renewatelier.buff.BuffType;
import net.firiz.renewatelier.buff.BuffValueType;
import net.firiz.renewatelier.characteristic.datas.addattack.AddAttackData;
import net.firiz.renewatelier.characteristic.datas.addattack.AddAttackType;
import net.firiz.renewatelier.characteristic.datas.addattack.x.AttributeAddAttack;
import net.firiz.renewatelier.damage.AttackAttribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public enum AlchemyItemEffect {
    FIRE_DAMAGE_1("炎ダメージ・弱", "炎属性のダメージを与える\n炎属性のダメージは、対象の炎耐性によって変動する", new AddAttackData(AddAttackType.ATTRIBUTE_DAMAGE, 100, AddAttackData.AttackLimitCategory.ITEM_ONLY, new AttributeAddAttack(AttackAttribute.FIRE, 20))),
    FIRE_DAMAGE_2("炎ダメージ・中", "中程度炎属性のダメージを与える\n炎属性のダメージは、対象の炎耐性によって変動する", new AddAttackData(AddAttackType.ATTRIBUTE_DAMAGE, 100, AddAttackData.AttackLimitCategory.ITEM_ONLY, new AttributeAddAttack(AttackAttribute.FIRE, 40))),
    FIRE_DAMAGE_3("炎ダメージ・強", "強い炎属性のダメージを与える\n炎属性のダメージは、対象の炎耐性によって変動する", new AddAttackData(AddAttackType.ATTRIBUTE_DAMAGE, 100, AddAttackData.AttackLimitCategory.ITEM_ONLY, new AttributeAddAttack(AttackAttribute.FIRE, 60))),
    FIRE_DAMAGE_4("炎ダメージ・超", "非常に強力な炎属性のダメージを与える\n炎属性のダメージは、対象の炎耐性によって変動する", new AddAttackData(AddAttackType.ATTRIBUTE_DAMAGE, 100, AddAttackData.AttackLimitCategory.ITEM_ONLY, new AttributeAddAttack(AttackAttribute.FIRE, 80))),
    FIRE_1("火傷を負わせる", "", new BuffMobHitEffect(new BuffData(BuffValueType.ITEM, 1, BuffType.BURN, 10, 1))),
    FIRE_2("大火傷を負わせる", "", new BuffMobHitEffect(new BuffData(BuffValueType.ITEM, 2, BuffType.BURN, 10, 5))),
    DEF_DOWN_1("防御力ダウン・小", "対象の防御力の値を少しだけ低下させる", new BuffMobHitEffect(new BuffData(BuffValueType.ITEM, 2, BuffType.STATS_DEF, 30, -5))),
    BLUE_FIRE("青い炎", "通常のフラムより高温の炎を発生させ、大きな防御無視の追加ダメージを与える", new AddAttackData(AddAttackType.ATTRIBUTE_DAMAGE, 100, AddAttackData.AttackLimitCategory.ITEM_ONLY, new AttributeAddAttack(AttackAttribute.FIRE, 10), true)),
    DIG_SPEED_1("採掘強化・小", "", new EnchantInitialize(new StarEffect.EnchantEffect(Enchantment.DIG_SPEED, 1))),
    DIG_SPEED_2("採掘強化・中", "", new EnchantInitialize(new StarEffect.EnchantEffect(Enchantment.DIG_SPEED, 2))),
    DURABILITY_1("耐久力・小", "", new EnchantInitialize(new StarEffect.EnchantEffect(Enchantment.DURABILITY, 1))),
    DURABILITY_2("耐久力・中", "", new EnchantInitialize(new StarEffect.EnchantEffect(Enchantment.DURABILITY, 2))),
    DURABILITY_3("耐久力・大", "", new EnchantInitialize(new StarEffect.EnchantEffect(Enchantment.DURABILITY, 3))),
    HEAL_HP_1("HP回復・微", "", new RandValue(RandValue.Mode.HP, 11, 15)),
    HEAL_HP_2("HP回復・小", "", new RandValue(RandValue.Mode.HP, 18, 25)),
    HEAL_HP_3("HP回復・中", "", new RandValue(RandValue.Mode.HP, 28, 40)),
    HEAL_HP_4("HP回復・大", "", new RandValue(RandValue.Mode.HP, 42, 60)),
    HEAL_HP_5("HP回復・超", "", new RandValue(RandValue.Mode.HP, 49, 70)),
    HEAL_HP_6("HP回復・超大", "", new RandValue(RandValue.Mode.HP, 56, 80)),
    HEAL_MP_1("MP回復・微", "", new RandValue(RandValue.Mode.MP, 7, 10)),
    HEAL_MP_2("MP回復・小", "", new RandValue(RandValue.Mode.MP, 12, 17)),
    HEAL_MP_3("MP回復・中", "", new RandValue(RandValue.Mode.MP, 19, 27)),
    HEAL_MP_4("MP回復・大", "", new RandValue(RandValue.Mode.MP, 28, 40)),
    HEAL_MP_5("MP回復・超", "", new RandValue(RandValue.Mode.MP, 35, 50)),
    HEAL_MP_6("MP回復・超大", "", new RandValue(RandValue.Mode.MP, 42, 60)),
    HEAL_HPMP_1("HPMP回復・微", "", new RandValue(RandValue.Mode.HP, 8, 12), new RandValue(RandValue.Mode.MP, 4, 6)),
    HEAL_HPMP_2("HPMP回復・小", "", new RandValue(RandValue.Mode.HP, 14, 20), new RandValue(RandValue.Mode.MP, 7, 10)),
    HEAL_HPMP_3("HPMP回復・中", "", new RandValue(RandValue.Mode.HP, 22, 32), new RandValue(RandValue.Mode.MP, 11, 16)),
    HEAL_HPMP_4("HPMP回復・超", "", new RandValue(RandValue.Mode.HP, 39, 56), new RandValue(RandValue.Mode.MP, 20, 28)),

    ;

    private final String name;
    private final String description; // 未使用

    @Nullable
    private final ItemInitialize initialize;

    @Nullable
    private final MobHitEffect mobHitEffect;

    @Nullable
    private final AddAttackData addAttackData;

    @Nullable
    private final RaisePower raisePower;

    @NotNull
    private final List<RandValue> randValues;

    AlchemyItemEffect(String name, String description, @NotNull ItemInitialize initialize) {
        this.name = name;
        this.description = description;
        this.initialize = initialize;
        this.mobHitEffect = null;
        this.addAttackData = null;
        this.raisePower = null;
        this.randValues = Collections.emptyList();
    }

    AlchemyItemEffect(String name, String description, @NotNull MobHitEffect mobHitEffect) {
        this.name = name;
        this.description = description;
        this.initialize = null;
        this.mobHitEffect = mobHitEffect;
        this.addAttackData = null;
        this.raisePower = null;
        this.randValues = Collections.emptyList();
    }

    AlchemyItemEffect(String name, String description, @NotNull AddAttackData addAttackData) {
        this.name = name;
        this.description = description;
        this.initialize = null;
        this.mobHitEffect = null;
        this.addAttackData = addAttackData;
        this.raisePower = null;
        this.randValues = Collections.emptyList();
    }

    AlchemyItemEffect(String name, String description, @NotNull RaisePower raisePower) {
        this.name = name;
        this.description = description;
        this.initialize = null;
        this.mobHitEffect = null;
        this.addAttackData = null;
        this.raisePower = raisePower;
        this.randValues = Collections.emptyList();
    }

    AlchemyItemEffect(String name, String description, @NotNull RandValue... randValues) {
        this.name = name;
        this.description = description;
        this.initialize = null;
        this.mobHitEffect = null;
        this.addAttackData = null;
        this.raisePower = null;
        this.randValues = Arrays.asList(randValues);
    }

    @NotNull
    public static AlchemyItemEffect search(@NotNull String name) {
        final Optional<AlchemyItemEffect> effect = Arrays.stream(values()).filter(alchemyItemEffect -> name.equals(alchemyItemEffect.name)).findFirst();
        if (effect.isPresent()) {
            return effect.get();
        }
        throw new IllegalStateException(name + " is not found.");
    }

    public String getName() {
        return name;
    }

    public void initialize(ItemStack item) {
        if (initialize != null) {
            initialize.accept(item);
        }
    }

    public void mobHit(LivingEntity entity) {
        if (mobHitEffect != null) {
            mobHitEffect.accept(entity);
        }
    }

    @Nullable
    public AddAttackData getAddAttackData() {
        return addAttackData;
    }

    @Nullable
    public RaisePower getRaisePower() {
        return raisePower;
    }

    @NotNull
    public List<RandValue> getRandValues() {
        return randValues;
    }
}
