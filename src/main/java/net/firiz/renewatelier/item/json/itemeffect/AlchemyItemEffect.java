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

import java.util.Arrays;
import java.util.Optional;

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

    AlchemyItemEffect(String name, String description, @NotNull ItemInitialize initialize) {
        this.name = name;
        this.description = description;
        this.initialize = initialize;
        this.mobHitEffect = null;
        this.addAttackData = null;
        this.raisePower = null;
    }

    AlchemyItemEffect(String name, String description, @NotNull MobHitEffect mobHitEffect) {
        this.name = name;
        this.description = description;
        this.initialize = null;
        this.mobHitEffect = mobHitEffect;
        this.addAttackData = null;
        this.raisePower = null;
    }

    AlchemyItemEffect(String name, String description, @NotNull AddAttackData addAttackData) {
        this.name = name;
        this.description = description;
        this.initialize = null;
        this.mobHitEffect = null;
        this.addAttackData = addAttackData;
        this.raisePower = null;
    }

    AlchemyItemEffect(String name, String description, @NotNull RaisePower raisePower) {
        this.name = name;
        this.description = description;
        this.initialize = null;
        this.mobHitEffect = null;
        this.addAttackData = null;
        this.raisePower = raisePower;
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
}
