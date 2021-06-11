package net.firiz.renewatelier.skills.character;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.firiz.renewatelier.entity.player.Char;
import net.firiz.renewatelier.inventory.item.json.AlchemyItemStatus;
import net.firiz.renewatelier.skills.character.passive.EnumPlayerPassiveSkill;
import net.firiz.renewatelier.skills.character.skill.bow.BowCharSkill;
import net.firiz.renewatelier.skills.character.skill.CharSkill;
import net.firiz.renewatelier.skills.character.skill.EnumPlayerSkill;
import net.firiz.renewatelier.skills.character.tree.SkillTree;
import net.firiz.renewatelier.sql.SQLManager;
import net.firiz.renewatelier.utils.java.CollectionUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PlayerSkillManager {

    private final Player player;
    private final Set<EnumPlayerSkill> skills = new ObjectArraySet<>();
    private final Set<EnumPlayerPassiveSkill> passiveSkills = new ObjectArraySet<>();

    private final Map<EnumPlayerSkill.Weapon, EnumPlayerSkill> selectedSkills = new Object2ObjectOpenHashMap<>();

    // initialized
    private Char character;
    private SkillTree skillTree;

    private CharSkill skill;

    public PlayerSkillManager(Player player) {
        this.player = player;
    }

    public void init(Char character, List<IPlayerSkillBuilder> skills) {
        this.character = character;
        this.character.getCharStats().resetPassiveBuff();
        this.skills.clear();
        skills.forEach(skillBuilder -> {
            if (skillBuilder instanceof EnumPlayerSkill) {
                this.skills.add((EnumPlayerSkill) skillBuilder);
            } else if (skillBuilder instanceof EnumPlayerPassiveSkill) {
                this.passiveSkills.add((EnumPlayerPassiveSkill) skillBuilder);
                ((EnumPlayerPassiveSkill) skillBuilder).createSkill(this.character).fire();
            }
        });
        if (!passiveSkills.contains(EnumPlayerPassiveSkill.ROOT)) {
            this.passiveSkills.add(EnumPlayerPassiveSkill.ROOT);
            EnumPlayerPassiveSkill.ROOT.createSkill(this.character).fire();
        }
        this.skillTree = new SkillTree(CollectionUtils.addAll(new ObjectArraySet<>(), this.skills, this.passiveSkills));
    }

    public void activateSkill(@NotNull final EnumPlayerSkill skill) {
        Objects.requireNonNull(skill);
        skills.add(skill);
        activateISkill(skill);
    }

    public void activateSkill(@NotNull final EnumPlayerPassiveSkill skill) {
        Objects.requireNonNull(skill);
        passiveSkills.add(skill);
        activateISkill(skill);
    }

    private void activateISkill(@NotNull final IPlayerSkillBuilder skillBuilder) {
        skillTree.activate(skillBuilder);
        SQLManager.INSTANCE.insert(
                "skills",
                new String[]{"userId", "skill", "isPassive"},
                new Object[]{character.getId(), skillBuilder.getName(), skillBuilder.isPassive()}
        );
    }

    public SkillTree getSkillTree() {
        return skillTree;
    }

    public Set<EnumPlayerSkill> getSkills() {
        return Collections.unmodifiableSet(skills);
    }

    public Set<EnumPlayerPassiveSkill> getPassiveSkills() {
        return Collections.unmodifiableSet(passiveSkills);
    }

    public void selectSkill(final EnumPlayerSkill.Weapon weapon, final EnumPlayerSkill weaponSkill) {
        selectedSkills.put(weapon, weaponSkill);
    }

    public void fireSkill(@Nullable AlchemyItemStatus itemStatus) {
        if (itemStatus != null) {
            final ItemStack item = itemStatus.getItemStack();
            final EnumPlayerSkill.Weapon weapon = EnumPlayerSkill.Weapon.searchWeapon(item);
            final EnumPlayerSkill weaponSkill = selectedSkills.get(weapon);
            if (weaponSkill != null) {
                fireSkill(weaponSkill, itemStatus);
            }
        }
    }

    public void fireSkill(EnumPlayerSkill enumPlayerSkill, @Nullable AlchemyItemStatus itemStatus) {
        if (itemStatus != null && (skill == null || skill.isDie())) {
            if (enumPlayerSkill.consumeMp(character)) {
                skill = enumPlayerSkill.createSkill(player, itemStatus);
                skill.fire();
            } else {
                player.sendMessage(Component.text("MPが足りません。"));
            }
        }
    }

    public void shoot(Projectile base) {
        if (skill instanceof BowCharSkill && !skill.isDie()) {
            ((BowCharSkill) skill).shoot(base);
        }
    }

    @Nullable
    public CharSkill getNowSkill() {
        return skill;
    }

    public void update() {
        if (skill != null) {
            skill.die();
        }
    }

}
