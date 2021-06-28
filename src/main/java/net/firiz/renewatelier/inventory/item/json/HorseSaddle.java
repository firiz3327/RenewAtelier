package net.firiz.renewatelier.inventory.item.json;

import com.google.gson.annotations.Expose;
import net.firiz.ateliercommonapi.adventure.text.C;
import net.firiz.ateliercommonapi.adventure.text.Lore;
import net.firiz.ateliercommonapi.adventure.text.Text;
import net.firiz.renewatelier.constants.GameConstants;
import net.firiz.renewatelier.entity.horse.HorseSkillList;
import net.firiz.renewatelier.entity.horse.HorseTier;
import net.firiz.renewatelier.entity.horse.EnumHorseSkill;
import net.firiz.renewatelier.language.Lang;
import net.firiz.renewatelier.server.json.JsonFactory;
import net.firiz.renewatelier.utils.CommonUtils;
import net.firiz.renewatelier.utils.Randomizer;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class HorseSaddle {

    private static final NamespacedKey persistentDataKey = CommonUtils.createKey("horseLevel");

    @Expose
    private final boolean female;
    @Expose
    private final HorseTier tier;
    @Expose
    private int level;
    @Expose
    private int exp;

    @Expose
    private int matingCount;
    @Expose
    private long matingTime;

    @Expose
    private int color; // Horse.Color.original
    @Expose
    private int style; // Horse.Style.original

    @Expose
    private final HorseSkillList horseSkills = new HorseSkillList();

    public HorseSaddle(boolean female, HorseTier tier, int level, int exp, int color, int style) {
        this.female = female;
        this.tier = tier;
        this.level = level;
        this.exp = exp;
        this.color = color;
        this.style = style;
    }

    public HorseSaddle(boolean female, HorseTier tier, int level, int exp, int color, int style, HorseSkillList horseSkills) {
        this.female = female;
        this.tier = tier;
        this.level = level;
        this.exp = exp;
        this.color = color;
        this.style = style;
        horseSkills.entrySet().forEach(entry -> horseSkills.put(entry.getSkill(), entry.getLevel()));
    }

    public boolean isFemale() {
        return female;
    }

    public HorseTier getTier() {
        return tier;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getExp() {
        return exp;
    }

    public boolean addExp(int exp, ItemStack saddle, Player player) {
        if (isMaxLevel()) {
            return false;
        }
        this.exp += exp;
        final int oldLevel = this.level;
        boolean levelUp = false;
        while (true) {
            final long reqExp = GameConstants.HORSE_REQ_EXPS[level];
            if (isMaxLevel() || reqExp > this.exp) {
                break;
            }
            this.exp -= reqExp;
            this.level++;
            levelUp = true;
        }
        if (levelUp) {
            levelUp(saddle, player, oldLevel);
        }
        return levelUp;
    }

    public int getMatingCount() {
        return matingCount;
    }

    public void setMatingCount(int matingCount) {
        this.matingCount = matingCount;
    }

    public long getMatingTime() {
        return matingTime;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public boolean hasSkill(EnumHorseSkill skill) {
        return horseSkills.containsKey(skill);
    }

    public int getSkillLevel(EnumHorseSkill skill) {
        return horseSkills.getInt(skill);
    }

    public void setSkillLevel(EnumHorseSkill skill, int skillLevel) {
        if (skillLevel > 0) {
            horseSkills.put(skill, skillLevel);
        } else {
            horseSkills.remove(skill);
        }
    }

    public HorseSkillList getHorseSkills() {
        return horseSkills;
    }

    private void levelUp(ItemStack saddle, Player player, int oldLevel) {
        // horseSkill レベルアップ
        EnumHorseSkill newSkill = null;
        EnumHorseSkill lvUpSkill = null;
        if (!horseSkills.isEmpty() && Randomizer.percent(20)) {
            final EnumHorseSkill horseSkill = horseSkills.random();
            final int skillLv = horseSkills.getInt(horseSkill);
            if (horseSkill.getMaxLevel() > skillLv) {
                horseSkills.put(horseSkill, skillLv + 1);
                lvUpSkill = horseSkill;
            }
        }
        // horseSkill 付与
        if (level % 5 == 0 && Randomizer.nextBoolean()) {
            final EnumHorseSkill skill = EnumHorseSkill.random(tier);
            if (!horseSkills.containsKey(skill)) {
                newSkill = skill;
                horseSkills.put(newSkill, 1);
            }
        }

        writeItem(saddle, true);
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);

        final Component speed = Text.of("+" + scaleString(tier.getSpeed(level) - tier.getSpeed(oldLevel)), C.GREEN);
        final Component jump = Text.of("+" + scaleString(tier.getJump(level) - tier.getJump(oldLevel)), C.GREEN);
        player.sendMessage(Lang.HORSE_LEVELUP.get(level + "Lv"));
        player.sendMessage(Lang.HORSE_LEVELUP_STATUSUP.get(speed, jump));
        if (isMaxLevel()) {
            player.sendMessage(Lang.HORSE_LEVELUP_MAX.get());
        }
        if (newSkill != null) {
            player.sendMessage(Lang.HORSE_LEVELUP_SKILL_NEW.get(newSkill.getName()));
        }
        if (lvUpSkill != null) {
            player.sendMessage(Lang.HORSE_LEVELUP_SKILL_LVUP.get(lvUpSkill.getName()));
        }
    }

    public boolean isMaxLevel() {
        return tier.getMaxLevel() <= level;
    }

    public void writeItem(@NotNull final ItemStack item, boolean refreshLore) {
        final ItemMeta meta = item.getItemMeta();
        meta.setCustomModelData(female ? 1 : 2);
        if (refreshLore) {
            final Lore lore = new Lore();
            lore.add(Lang.HORSE_SADDLE_LORE_DESC_RANK.get(tier.getTier()));
            lore.add(Lang.HORSE_SADDLE_LORE_DESC_GENDER.get(female ? Lang.HORSE_GENDER_FEMALE : Lang.HORSE_GENDER_MALE));
            lore.add(Lang.HORSE_SADDLE_LORE_DESC_SPEED.get(scaleString(tier.getSpeed(level))));
            lore.add(Lang.HORSE_SADDLE_LORE_DESC_JUMP.get(scaleString(tier.getJump(level))));
            if (!horseSkills.isEmpty()) {
                lore.add(Lang.HORSE_SADDLE_LORE_DESC_SKILL.get());
                horseSkills.entrySet().forEach(
                        entry -> lore.add("- ").color(C.WHITE)
                                .append(entry.getSkill().getName()).color(C.WHITE)
                                .append(" : ").color(C.WHITE)
                                .append(entry.getLevel() + " / " + entry.getSkill().getMaxLevel()).color(C.GREEN)
                );
            }
            if (female && matingCount > 0) {
                lore.nextLine();
                lore.add(Lang.HORSE_SADDLE_LORE_DESC_MATING.get(matingCount + " / " + GameConstants.HORSE_MATING_MAX_COUNT));
                if (matingCount < GameConstants.HORSE_MATING_MAX_COUNT) {
                    lore.add(Lang.HORSE_SADDLE_LORE_DESC_MATING_TIME.get(matingTimeString()));
                }
            }
            meta.lore(lore);
        }
        final String json = JsonFactory.toJson(this);
        meta.getPersistentDataContainer().set(
                persistentDataKey,
                PersistentDataType.STRING,
                json
        );
        item.setItemMeta(meta);
    }

    public String scaleString(double val) {
        return CommonUtils.scaleString(val * 1000, 0, RoundingMode.HALF_UP);
    }

    public void refreshMatingTime() {
        this.matingTime = LocalDateTime.now().withSecond(0).toEpochSecond(ZoneOffset.UTC);
    }

    private String matingTimeString() {
        final LocalDateTime time = LocalDateTime.ofEpochSecond(matingTime, 0, ZoneOffset.UTC).plusHours(GameConstants.HORSE_MATING_REQUIRE_HOUR);
        final ZonedDateTime zonedTime = time.atZone(ZoneId.of("Asia/Tokyo"));
        return DateTimeFormatter.ofPattern("MM/dd HH:mm").format(zonedTime);
    }

    public boolean availableMatingTime() {
        final LocalDateTime now = LocalDateTime.now().withSecond(0);
        final LocalDateTime time = LocalDateTime.ofEpochSecond(matingTime, 0, ZoneOffset.UTC).plusHours(GameConstants.HORSE_MATING_REQUIRE_HOUR);
        return now.isAfter(time);
    }

    public static ItemStack createSaddle(boolean female, HorseTier tier, int level, int color, int style) {
        return createSaddle(new HorseSaddle(female, tier, level, 0, color, style));
    }

    public static ItemStack createSaddle(boolean female, HorseTier tier, int level, int color, int style, HorseSkillList skillList) {
        return createSaddle(new HorseSaddle(female, tier, level, 0, color, style, skillList));
    }

    public static ItemStack createSaddle(HorseSaddle saddle) {
        final ItemStack item = new ItemStack(Material.SADDLE);
        saddle.writeItem(item, true);
        return item;
    }

    public static boolean has(@Nullable final ItemStack item) {
        return item != null && item.getType() != Material.AIR && item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().has(
                persistentDataKey,
                PersistentDataType.STRING
        );
    }

    @NotNull
    public static HorseSaddle load(@NotNull final ItemStack item) {
        if (Objects.requireNonNull(item).hasItemMeta()) {
            final PersistentDataContainer persistentDataContainer = item.getItemMeta().getPersistentDataContainer();
            if (persistentDataContainer.has(persistentDataKey, PersistentDataType.STRING)) {
                final String json = persistentDataContainer.get(persistentDataKey, PersistentDataType.STRING);
                return JsonFactory.fromJson(json, HorseSaddle.class);
            }
        }
        throw new IllegalArgumentException("not saddle item.");
    }
}
