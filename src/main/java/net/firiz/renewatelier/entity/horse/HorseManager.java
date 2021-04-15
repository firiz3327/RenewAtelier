package net.firiz.renewatelier.entity.horse;

import net.firiz.renewatelier.AtelierPlugin;
import net.firiz.renewatelier.inventory.item.json.HorseSaddle;
import net.firiz.renewatelier.utils.Randomizer;
import net.firiz.renewatelier.utils.java.CollectionUtils;
import net.firiz.renewatelier.utils.minecraft.EntityUtils;
import net.firiz.renewatelier.utils.minecraft.ItemUtils;
import net.firiz.renewatelier.version.entity.horse.NMSAtelierHorse;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.spigotmc.event.entity.EntityMountEvent;

import java.util.Optional;

public enum HorseManager {
    INSTANCE;

    public void onMount(EntityMountEvent e, Horse horse, Player player) {
        if (horse.getOwner() != null && horse.getOwner().getUniqueId() != player.getUniqueId()) {
            e.setCancelled(true);
        }
    }

    public void onTame(Player tamer, Horse horse) {
        int tierPoint = Randomizer.rand(0, 5);
        switch (horse.getStyle()) {
            case NONE:
                tierPoint += 3;
                break;
            case WHITE:
                tierPoint += 2;
                break;
            default:
                tierPoint++;
                break;
        }
        switch (horse.getColor()) {
            case WHITE:
            case CHESTNUT:
                tierPoint++;
                break;
            case BROWN:
                tierPoint--;
                break;
            default:
                break;
        }
        final boolean female = Randomizer.nextBoolean();
        tierPoint -= female ? 1 : 0;
        final HorseTier tier;
        if (tierPoint <= 2) { // ~ 2
            tier = HorseTier.TIER_1;
        } else if (tierPoint <= 4) { // 3 ~ 4
            tier = HorseTier.TIER_2;
        } else if (tierPoint <= 7) { // 5 ~ 7
            tier = HorseTier.TIER_3;
        } else { // 8 ~
            tier = HorseTier.TIER_4;
        }
        final HorseSaddle saddle = new HorseSaddle(female, tier, Randomizer.rand(0, 3), 0, horse.getColor().ordinal(), horse.getStyle().ordinal());
        final ItemStack itemSaddle = HorseSaddle.createSaddle(saddle);
        ItemUtils.addItem(tamer, itemSaddle);
        horse.remove();
        new NMSAtelierHorse(tamer, tamer.getLocation().getWorld(), itemSaddle).spawn(tamer);
    }

    public void interactSaddle(Player player, Location location, ItemStack saddle) {
        if (player.getVehicle() != null) {
            final Object handle = EntityUtils.getHandle(player.getVehicle());
            if (handle instanceof NMSAtelierHorse) {
                final NMSAtelierHorse horse = (NMSAtelierHorse) handle;
                horse.boost();
            }
        } else {
            new NMSAtelierHorse(player, location.getWorld(), saddle).spawn(player);
        }
    }

    public void interactHorse(Player player, Horse horse) {
        if (horse.getOwner() != null && horse.getOwner().getUniqueId() != player.getUniqueId()) {
            final Object handle = EntityUtils.getHandle(horse);
            if (handle instanceof NMSAtelierHorse) {
                final NMSAtelierHorse atelierHorse = (NMSAtelierHorse) handle;
                if (atelierHorse.getHorseSaddle().hasSkill(EnumHorseSkill.TWO_SEATER) && !atelierHorse.hasTwoSeaterRider()) {
                    Bukkit.getScheduler().runTask(AtelierPlugin.getPlugin(), () -> atelierHorse.startTwoSeater(player));
                }
            }
        }
    }

    public ItemStack mating(ItemStack femaleItem, HorseSaddle female, HorseSaddle male) {
        final HorseTier femaleTier = female.getTier();
        final HorseTier maleTier = male.getTier();

        final HorseSkillList highSkillList;
        final HorseTier highTier;
        final HorseSaddle saddle;
        if (femaleTier == maleTier) {
            saddle = Randomizer.nextDouble() > 0.6 ? female : male;
            highTier = saddle.getTier();
        } else {
            saddle = femaleTier.getTier() > maleTier.getTier() ? female : male;
            highTier = femaleTier.getTier() > maleTier.getTier() ? femaleTier : maleTier;
        }
        highSkillList = saddle.getHorseSkills();
        int rank = highTier.getTier();
        if (Randomizer.percent(highTier.getRankUpPercent())) {
            rank++;
        } else if (Randomizer.percent(highTier.getRankDownPercent())) {
            rank--;
        }

        final HorseSkillList skillList = new HorseSkillList();
        highSkillList.entrySet().forEach(entry -> {
            if (Randomizer.percent(80)) {
                skillList.put(entry.getSkill(), (int) (entry.getLevel() / Math.max(0.1, Randomizer.nextDouble() * 3)));
            }
        });

        female.setMatingCount(female.getMatingCount() + 1);
        female.refreshMatingTime();
        female.writeItem(femaleItem, true);
        final Optional<HorseTier> nextTier = HorseTier.searchTier(rank);
        return HorseSaddle.createSaddle(
                Randomizer.nextBoolean(),
                nextTier.orElse(highTier),
                0,
                CollectionUtils.getRandomValue(Horse.Color.values()).ordinal(),
                CollectionUtils.getRandomValue(Horse.Style.values()).ordinal(),
                skillList
        );
    }

    public void swap(PlayerSwapHandItemsEvent event, Player player, ItemStack saddle) {
    }

    public void checkHorse(Player player) {
        final Entity vehicle = player.getVehicle();
        if (vehicle instanceof Horse && NMSAtelierHorse.hasKey(vehicle)) {
            vehicle.remove();
        }
    }

}
