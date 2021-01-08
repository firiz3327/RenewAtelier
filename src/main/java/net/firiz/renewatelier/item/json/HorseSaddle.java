package net.firiz.renewatelier.item.json;

import com.google.gson.annotations.Expose;
import net.firiz.renewatelier.constants.GameConstants;
import net.firiz.renewatelier.entity.horse.HorseSkillList;
import net.firiz.renewatelier.entity.horse.HorseTier;
import net.firiz.renewatelier.entity.horse.EnumHorseSkill;
import net.firiz.renewatelier.utils.CommonUtils;
import net.firiz.renewatelier.utils.Randomizer;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
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

        final String speed = ChatColor.GREEN + "+" + CommonUtils.scaleString(tier.getSpeed(level) - tier.getSpeed(oldLevel), 4, RoundingMode.HALF_UP);
        final String jump = ChatColor.GREEN + "+" + CommonUtils.scaleString(tier.getJump(level) - tier.getJump(oldLevel), 4, RoundingMode.HALF_UP);
        player.sendMessage(ChatColor.GRAY + "あなたの馬が " + ChatColor.GREEN + level + "Lv" + ChatColor.GRAY + "にレベルアップしました！");
        player.sendMessage(ChatColor.GRAY + "移動速度" + speed + ChatColor.GRAY + ", ジャンプ力" + jump);
        if (isMaxLevel()) {
            player.sendMessage(ChatColor.GREEN + "あなたの馬は最大レベルに到達しました！");
        }
        if (newSkill != null) {
            player.sendMessage(ChatColor.GREEN + "新しく " + newSkill.getName() + " を覚えました！");
        }
        if (lvUpSkill != null) {
            player.sendMessage(ChatColor.GREEN + lvUpSkill.getName() + " のレベルが上がりました！");
        }
    }

    public boolean isMaxLevel() {
        return tier.getMaxLevel() <= level;
    }

    public void writeItem(@NotNull final ItemStack item, boolean refreshLore) {
        final ItemMeta meta = item.getItemMeta();
        if (refreshLore) {
            final List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "ランク: " + ChatColor.WHITE + tier.getTier());
            lore.add(ChatColor.GRAY + "性別: " + ChatColor.WHITE + (female ? "牝馬♀" : "牡馬♂"));
            lore.add(ChatColor.GRAY + "レベル: " + ChatColor.WHITE + level + " / " + tier.getMaxLevel());
            lore.add(ChatColor.GRAY + "移動速度: " + ChatColor.WHITE + CommonUtils.scaleString(tier.getSpeed(level), 2, RoundingMode.HALF_UP));
            lore.add(ChatColor.GRAY + "ジャンプ力: " + ChatColor.WHITE + CommonUtils.scaleString(tier.getJump(level), 2, RoundingMode.HALF_UP));
            if (!horseSkills.isEmpty()) {
                lore.add(ChatColor.GRAY + "スキル:");
                horseSkills.entrySet().forEach(
                        entry -> lore.add(ChatColor.WHITE + "- " + entry.getSkill().getName() + " : " + ChatColor.GREEN + entry.getLevel() + " / " + entry.getSkill().getMaxLevel())
                );
            }
            meta.setLore(lore);
        }
        final String json = JsonFactory.toJson(this);
        meta.getPersistentDataContainer().set(
                persistentDataKey,
                PersistentDataType.STRING,
                json
        );
        item.setItemMeta(meta);
    }

    public static ItemStack createSaddle(boolean female, HorseTier tier, int level, int color, int style) {
        return createSaddle(new HorseSaddle(female, tier, level, 0, color, style));
    }

    public static ItemStack createSaddle(HorseSaddle saddle) {
        final ItemStack item = new ItemStack(Material.SADDLE);
        saddle.writeItem(item, true);
        return item;
    }

    public static boolean has(@Nullable final ItemStack item) {
        return item != null && item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().has(
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
