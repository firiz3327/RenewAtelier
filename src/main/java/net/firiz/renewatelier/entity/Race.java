package net.firiz.renewatelier.entity;

import org.bukkit.entity.EntityType;

public enum Race {
    UNKNOWN("不明"), // original
    BEAST("猛獣"),
    DRAGON("ドラゴン"),
    ELEMENTAL("精霊"),
    ALCHEMY("錬金体"),
    AJIN("亜人"),
    PUNI("プニ"),
    MAGIC("魔法生物"),
    UNDEAD("不死生物"),
    DEMON("魔族"),
    GOD("神族"),
    HUMAN("人間"),
    PLANT("植物"),
    ANIMAL("温厚動物"), // original
    INSECT("虫"), // original
    FISH("魚介"), // original
    ;

    private final String name;

    Race(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Race getRace(EntityType type) {
        final Race race;
        switch (type) {
            case ZOMBIE: // ゾンビ
            case ZOMBIE_VILLAGER:
            case HUSK:
            case DROWNED:
            case GIANT: // 未使用MOB
            case ZOMBIE_HORSE: // 未使用MOB
            case SKELETON: // スケルトン
            case WITHER_SKELETON:
            case STRAY:
            case SKELETON_HORSE:
            case CREEPER:
            case WITHER:
            case PHANTOM: // other
            case GHAST: // バニラではアンデッドではないが、アトリエの幽霊に該当する為追加
            case VEX:
            case ZOMBIFIED_PIGLIN: // v1.16
            case ZOGLIN: // v1.16
                race = UNDEAD;
                break;
            case PLAYER: // 人間
            case VILLAGER:
            case WANDERING_TRADER:
                race = HUMAN;
                break;
            case WITCH:
            case EVOKER: // 森の洋館
            case PILLAGER: // 襲撃者
            case VINDICATOR:
            case ILLUSIONER: // 未使用MOB
            case PIGLIN: // v1.16
//            case PIGLIN_BRUTE: // v1.16.2
                race = AJIN;
                break;
            case SPIDER: // 虫
            case CAVE_SPIDER:
            case SILVERFISH:
            case BEE:
                race = INSECT;
                break;
            case ENDERMAN: // magic
            case ENDERMITE:
            case SHULKER:
            case BLAZE:
            case SNOWMAN:
            case IRON_GOLEM:
            case STRIDER: // v1.16
                race = MAGIC;
                break;
            case SLIME: // プニ
            case MAGMA_CUBE:
                race = PUNI;
                break;
            case COD: // 魚
            case PUFFERFISH:
            case SALMON:
            case TROPICAL_FISH:
            case ELDER_GUARDIAN:
            case GUARDIAN:
            case SQUID:
            case TURTLE:
            case DOLPHIN:
                race = FISH;
                break;
            case CAT: // 動物
            case OCELOT:
            case PARROT:
            case CHICKEN:
            case COW:
            case MUSHROOM_COW:
            case PIG:
            case FOX:
            case SHEEP:
            case HORSE:
            case DONKEY:
            case MULE:
            case RABBIT:
            case LLAMA:
            case PANDA:
            case BAT:
                race = ANIMAL;
                break;
            case WOLF: // 猛獣
            case POLAR_BEAR:
            case HOGLIN: // v1.16
                race = BEAST;
                break;
            case ENDER_DRAGON: // ドラゴン
                race = DRAGON;
                break;
            default: // 該当なし
                race = UNKNOWN;
                break;
        }
        return race;
    }

    public boolean hasVanillaType(EntityType type) {
        return this == getRace(type);
    }
}