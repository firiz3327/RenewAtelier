package net.firiz.renewatelier.inventory.item.json.itemeffect;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import net.firiz.renewatelier.alchemy.recipe.StarEffect;
import net.firiz.renewatelier.buff.BuffData;
import net.firiz.renewatelier.buff.BuffType;
import net.firiz.renewatelier.buff.BuffValueType;
import net.firiz.renewatelier.characteristic.datas.addattack.AddAttackData;
import net.firiz.renewatelier.characteristic.datas.addattack.AddAttackType;
import net.firiz.renewatelier.characteristic.datas.addattack.x.AttributeAddAttack;
import net.firiz.renewatelier.damage.AttackAttribute;
import net.firiz.renewatelier.utils.CommonUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public enum AlchemyItemEffect {
    // blow
    BLOW_DAMAGE_1("打撃ダメージ・弱", Desc.ATTRIBUTE_1.create("打撃"), new AddAttackData(AddAttackType.ATTRIBUTE_DAMAGE, 100, AddAttackData.AttackLimitCategory.ITEM_ONLY, new AttributeAddAttack(AttackAttribute.BLOW, 100))),
    BLOW_DAMAGE_2("打撃ダメージ・中", Desc.ATTRIBUTE_2.create("打撃"), new AddAttackData(AddAttackType.ATTRIBUTE_DAMAGE, 100, AddAttackData.AttackLimitCategory.ITEM_ONLY, new AttributeAddAttack(AttackAttribute.BLOW, 110))),
    BLOW_DAMAGE_3("打撃ダメージ・強", Desc.ATTRIBUTE_3.create("打撃"), new AddAttackData(AddAttackType.ATTRIBUTE_DAMAGE, 100, AddAttackData.AttackLimitCategory.ITEM_ONLY, new AttributeAddAttack(AttackAttribute.BLOW, 120))),
    BLOW_DAMAGE_4("打撃ダメージ・超", Desc.ATTRIBUTE_4.create("打撃"), new AddAttackData(AddAttackType.ATTRIBUTE_DAMAGE, 100, AddAttackData.AttackLimitCategory.ITEM_ONLY, new AttributeAddAttack(AttackAttribute.BLOW, 130))),
    BLOW_DAMAGE_5("荒ぶる衝撃", Desc.ATTRIBUTE_4.create("打撃"), new AddAttackData(AddAttackType.ATTRIBUTE_DAMAGE, 100, AddAttackData.AttackLimitCategory.ITEM_ONLY, new AttributeAddAttack(AttackAttribute.BLOW, 140))),
    BLOW_DAMAGE_6("巨人の一撃", Desc.ATTRIBUTE_4.create("打撃"), new AddAttackData(AddAttackType.ATTRIBUTE_DAMAGE, 100, AddAttackData.AttackLimitCategory.ITEM_ONLY, new AttributeAddAttack(AttackAttribute.BLOW, 150))),
    // fire
    FIRE_DAMAGE_1("炎ダメージ・弱", Desc.ATTRIBUTE_1.create("炎"), new AddAttackData(AddAttackType.ATTRIBUTE_DAMAGE, 100, AddAttackData.AttackLimitCategory.ITEM_ONLY, new AttributeAddAttack(AttackAttribute.FIRE, 100))),
    FIRE_DAMAGE_2("炎ダメージ・中", Desc.ATTRIBUTE_2.create("炎"), new AddAttackData(AddAttackType.ATTRIBUTE_DAMAGE, 100, AddAttackData.AttackLimitCategory.ITEM_ONLY, new AttributeAddAttack(AttackAttribute.FIRE, 110))),
    FIRE_DAMAGE_3("炎ダメージ・強", Desc.ATTRIBUTE_3.create("炎"), new AddAttackData(AddAttackType.ATTRIBUTE_DAMAGE, 100, AddAttackData.AttackLimitCategory.ITEM_ONLY, new AttributeAddAttack(AttackAttribute.FIRE, 120))),
    FIRE_DAMAGE_4("炎ダメージ・超", Desc.ATTRIBUTE_4.create("炎"), new AddAttackData(AddAttackType.ATTRIBUTE_DAMAGE, 100, AddAttackData.AttackLimitCategory.ITEM_ONLY, new AttributeAddAttack(AttackAttribute.FIRE, 130))),
    FIRE_DAMAGE_5("煉獄の炎", Desc.ATTRIBUTE_4.create("炎"), new AddAttackData(AddAttackType.ATTRIBUTE_DAMAGE, 100, AddAttackData.AttackLimitCategory.ITEM_ONLY, new AttributeAddAttack(AttackAttribute.FIRE, 140))),
    FIRE_DAMAGE_6("地獄の業火", Desc.ATTRIBUTE_4.create("炎"), new AddAttackData(AddAttackType.ATTRIBUTE_DAMAGE, 100, AddAttackData.AttackLimitCategory.ITEM_ONLY, new AttributeAddAttack(AttackAttribute.FIRE, 150))),
    // ice
    ICE_DAMAGE_1("氷ダメージ・弱", Desc.ATTRIBUTE_1.create("氷"), new AddAttackData(AddAttackType.ATTRIBUTE_DAMAGE, 100, AddAttackData.AttackLimitCategory.ITEM_ONLY, new AttributeAddAttack(AttackAttribute.ICE, 100))),
    ICE_DAMAGE_2("氷ダメージ・中", Desc.ATTRIBUTE_2.create("氷"), new AddAttackData(AddAttackType.ATTRIBUTE_DAMAGE, 100, AddAttackData.AttackLimitCategory.ITEM_ONLY, new AttributeAddAttack(AttackAttribute.ICE, 110))),
    ICE_DAMAGE_3("氷ダメージ・強", Desc.ATTRIBUTE_3.create("氷"), new AddAttackData(AddAttackType.ATTRIBUTE_DAMAGE, 100, AddAttackData.AttackLimitCategory.ITEM_ONLY, new AttributeAddAttack(AttackAttribute.ICE, 120))),
    ICE_DAMAGE_4("氷ダメージ・超", Desc.ATTRIBUTE_4.create("氷"), new AddAttackData(AddAttackType.ATTRIBUTE_DAMAGE, 100, AddAttackData.AttackLimitCategory.ITEM_ONLY, new AttributeAddAttack(AttackAttribute.ICE, 130))),
    ICE_DAMAGE_5("凍てつく世界", Desc.ATTRIBUTE_4.create("氷"), new AddAttackData(AddAttackType.ATTRIBUTE_DAMAGE, 100, AddAttackData.AttackLimitCategory.ITEM_ONLY, new AttributeAddAttack(AttackAttribute.ICE, 140))),
    ICE_DAMAGE_6("絶対零度", Desc.ATTRIBUTE_4.create("氷"), new AddAttackData(AddAttackType.ATTRIBUTE_DAMAGE, 100, AddAttackData.AttackLimitCategory.ITEM_ONLY, new AttributeAddAttack(AttackAttribute.ICE, 150))),
    // lightning
    LIGHTNING_DAMAGE_1("雷ダメージ・弱", Desc.ATTRIBUTE_1.create("雷"), new AddAttackData(AddAttackType.ATTRIBUTE_DAMAGE, 100, AddAttackData.AttackLimitCategory.ITEM_ONLY, new AttributeAddAttack(AttackAttribute.LIGHTNING, 100))),
    LIGHTNING_DAMAGE_2("雷ダメージ・中", Desc.ATTRIBUTE_2.create("雷"), new AddAttackData(AddAttackType.ATTRIBUTE_DAMAGE, 100, AddAttackData.AttackLimitCategory.ITEM_ONLY, new AttributeAddAttack(AttackAttribute.LIGHTNING, 110))),
    LIGHTNING_DAMAGE_3("雷ダメージ・強", Desc.ATTRIBUTE_3.create("雷"), new AddAttackData(AddAttackType.ATTRIBUTE_DAMAGE, 100, AddAttackData.AttackLimitCategory.ITEM_ONLY, new AttributeAddAttack(AttackAttribute.LIGHTNING, 120))),
    LIGHTNING_DAMAGE_4("雷ダメージ・超", Desc.ATTRIBUTE_4.create("雷"), new AddAttackData(AddAttackType.ATTRIBUTE_DAMAGE, 100, AddAttackData.AttackLimitCategory.ITEM_ONLY, new AttributeAddAttack(AttackAttribute.LIGHTNING, 130))),
    LIGHTNING_DAMAGE_5("断罪の雷", Desc.ATTRIBUTE_4.create("雷"), new AddAttackData(AddAttackType.ATTRIBUTE_DAMAGE, 100, AddAttackData.AttackLimitCategory.ITEM_ONLY, new AttributeAddAttack(AttackAttribute.LIGHTNING, 140))),
    LIGHTNING_DAMAGE_6("神の鉄槌", Desc.ATTRIBUTE_4.create("雷"), new AddAttackData(AddAttackType.ATTRIBUTE_DAMAGE, 100, AddAttackData.AttackLimitCategory.ITEM_ONLY, new AttributeAddAttack(AttackAttribute.LIGHTNING, 150))),
    // magic
    MAGIC_DAMAGE_1("小型誘導弾", Desc.ATTRIBUTE_1.create("魔法"), new AddAttackData(AddAttackType.ATTRIBUTE_DAMAGE, 100, AddAttackData.AttackLimitCategory.ITEM_ONLY, new AttributeAddAttack(AttackAttribute.MAGIC, 75))),
    MAGIC_DAMAGE_2("大型誘導弾", Desc.ATTRIBUTE_2.create("魔法"), new AddAttackData(AddAttackType.ATTRIBUTE_DAMAGE, 100, AddAttackData.AttackLimitCategory.ITEM_ONLY, new AttributeAddAttack(AttackAttribute.MAGIC, 85))),

    PRIMITIVE_POWER("原初の力", ""),
    CREATION_POWER("創生の力", ""),

    LIGHT_OF_THE_END("終末の光", ""),
    THE_AURORA_OF_COLLAPSE("崩壊の極光", ""),

    // falling
    FALLING_UNI("うにが落ちてくる", ""),
    FALLING_PUNI("プニが落ちてくる", ""),
    FALLING_METEORITE("隕石が落ちてくる", ""),
    FALLING_QUESTION("???が落ちてくる", ""),
    FALLING_METEOR_SHOWER("流星群が落ちてくる", ""),
    FALLING_BOMB("爆弾が落ちてくる", ""),
    FALLING_ISLAND_FISH("島魚が落ちてくる", ""),

    GIFT_FROM_HEAVEN("天界からの贈り物", ""),

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
    REGENERATION_1("HP再生付与・微", "", new BuffData(BuffValueType.ITEM, 1, BuffType.AUTO_HEAL, 20, 3)),
    REGENERATION_2("HP再生付与・小", "", new BuffData(BuffValueType.ITEM, 2, BuffType.AUTO_HEAL, 20, 5)),
    REGENERATION_3("HP再生付与・中", "", new BuffData(BuffValueType.ITEM, 3, BuffType.AUTO_HEAL, 20, 7)),
    REGENERATION_4("HP再生付与・大", "", new BuffData(BuffValueType.ITEM, 4, BuffType.AUTO_HEAL, 20, 10)),
    REGENERATION_5("HP再生付与・超", "", new BuffData(BuffValueType.ITEM, 5, BuffType.AUTO_HEAL, 20, 15)),
    AUTO_USE("自動発動20％", ""),
    HEAL_BONUS_1("回復ボーナス・小", ""),
    HEAL_BONUS_2("回復ボーナス・中", ""),
    HEAL_BONUS_3("回復ボーナス・大", ""),
    BUFF_ACC_1("命中率アップ・小", ""),
    BUFF_AVO_1("回避率アップ・小", ""),

    INCREASE_EFFECT_RANGE_1("効果範囲拡大", ""),
    INCREASE_EFFECT_RANGE_2("効果範囲拡大＋", ""),
    INCREASE_EFFECT_RANGE_3("効果範囲拡大＋＋", ""),

    // 発破用フラム用
    BROKEN_LEVEL_1("破壊レベル・弱", ""),
    BROKEN_LEVEL_2("破壊レベル・中", ""),
    BROKEN_LEVEL_3("破壊レベル・強", ""),
    LOOT_BONUS_1("採取量増加・小", ""), // 幸運2
    LOOT_BONUS_2("採取量増加・中", ""), // 幸運3
    LOOT_BONUS_3("採取量増加・大", ""), // 幸運4
    ;

    enum Desc {
        ATTRIBUTE_1("%0属性のダメージを与える\n%0属性のダメージは、対象の%0耐性によって変動する"),
        ATTRIBUTE_2("中程度%0属性のダメージを与える\n%0属性のダメージは、対象の%0耐性によって変動する"),
        ATTRIBUTE_3("強い%0属性のダメージを与える\n%0属性のダメージは、対象の%0耐性によって変動する"),
        ATTRIBUTE_4("非常に強力な%0属性のダメージを与える\n%0属性のダメージは、対象の%0耐性によって変動する"),
        ;

        final String description;

        Desc(String description) {
            this.description = description;
        }

        public String create(String... args) {
            String result = description;
            for (int i = 0; i < args.length; i++) {
                result = result.replace("%" + i, args[i]);
            }
            return result;
        }
    }

    private final String name;
    private final Component nameComponent;
    private final Component description; // 未使用

    @NotNull
    private final List<IItemEffect> effects;

    AlchemyItemEffect(String name, String description, @NotNull IItemEffect effect) {
        this.name = name;
        this.nameComponent = Component.text(name);
        this.description = Component.text(description);
        this.effects = ObjectLists.singleton(effect);
    }

    AlchemyItemEffect(String name, String description, @NotNull RandValue... randValues) {
        this.name = name;
        this.nameComponent = Component.text(name);
        this.description = Component.text(description);
        this.effects = new ObjectArrayList<>(randValues);
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

    public Component getNameComponent() {
        return nameComponent;
    }

    public Component getDescription() {
        return description;
    }

    public boolean isSingleton() {
        return effects instanceof ObjectLists.Singleton;
    }

    public IItemEffect getEffect() {
        return effects.get(0);
    }

    @NotNull
    public List<IItemEffect> getEffects() {
        return effects;
    }

    private <T> Optional<T> singleEffect(Predicate<? super IItemEffect> filter, Class<T> clasz) {
        return isSingleton() ? CommonUtils.cast(Optional.of(getEffect()).filter(filter)) : Optional.empty();
    }

    public void initialize(ItemStack item) {
        singleEffect(
                ItemInitialize.class::isInstance,
                ItemInitialize.class
        ).ifPresent(i -> i.accept(item));
    }

    public void mobHit(LivingEntity entity) {
        singleEffect(
                MobHitEffect.class::isInstance,
                MobHitEffect.class
        ).ifPresent(i -> i.accept(entity));
    }

    @Nullable
    public AddAttackData getAddAttackData() {
        return singleEffect(AddAttackData.class::isInstance, AddAttackData.class).orElse(null);
    }

    @Nullable
    public RaisePower getRaisePower() {
        return singleEffect(RaisePower.class::isInstance, RaisePower.class).orElse(null);
    }

    @NotNull
    public List<RandValue> getRandValues() {
        if (getEffect() instanceof RandValue) {
            return effects.stream().map(RandValue.class::cast).collect(Collectors.toList());
        }
        return ObjectLists.emptyList();
    }
}
