package net.firiz.renewatelier.skills.character.tree;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.firiz.ateliercommonapi.adventure.text.Text;
import net.firiz.renewatelier.skills.character.IPlayerSkillBuilder;
import net.firiz.renewatelier.skills.character.passive.EnumPlayerPassiveSkill;
import net.firiz.renewatelier.utils.minecraft.ItemUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class SkillTree {

    public static final int WIDTH = 32;
    public static final int HEIGHT = 32;
    public static final int CENTER = (WIDTH * HEIGHT / 2) + (WIDTH / 2);

    private final Block[] blocks = new Block[WIDTH * HEIGHT];
    private final List<NodeBlock> nodes = new ObjectArrayList<>();

    public SkillTree(Set<IPlayerSkillBuilder> skills) {
        addNodes(skills, new NodeBlock(Node.ROOT, CENTER, true));
    }

    public void activate(IPlayerSkillBuilder skill) {
        final Optional<NodeBlock> nodeBlock = nodes.stream().filter(node -> node.node.skill == skill).findAny();
        nodeBlock.ifPresent(block -> block.active = true);
    }

    public void deactivate(IPlayerSkillBuilder skill) {
        final Optional<NodeBlock> nodeBlock = nodes.stream().filter(node -> node.node.skill == skill).findAny();
        nodeBlock.ifPresent(block -> block.active = false);
    }

    public Block[] getBlocks() {
        return blocks;
    }

    private void addNodes(Set<IPlayerSkillBuilder> skills, NodeBlock block) {
        block.node.children.forEach((key, childNode) -> {
            int pos = block.pos;
            switch (childNode.direction) {
                case UP:
                    pos -= WIDTH * 2;
                    break;
                case DOWN:
                    pos += WIDTH * 2;
                    break;
                case LEFT:
                    pos -= 2;
                    break;
                case RIGHT:
                    pos += 2;
                    break;
                default:
                    return;
            }
            final NodeBlock next = new NodeBlock(childNode, pos, skills.contains(childNode.skill));
            addNodes(skills, next);
        });
    }

    private static ItemStack i(String name, int customModelData) {
        return ItemUtils.unavailableItem(Material.BARRIER, customModelData, Component.text(name));
    }

    private static ItemStack e(ItemStack item) {
        final ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.LUCK, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        return item;
    }

    public interface Block {
        default ItemStack createItem() {
            return null;
        }
    }

    static class Line implements Block {
        static final ItemStack line1 = i("-", 3);
        static final ItemStack line1Active = i("-", 4);
        static final ItemStack line2 = i("-", 5);
        static final ItemStack line2Active = i("-", 6);
        boolean active;
        boolean side;

        public Line(boolean active, boolean side) {
            this.active = active;
            this.side = side;
        }

        @Override
        public ItemStack createItem() {
            final ItemStack item;
            if (side) {
                item = active ? line2Active : line2;
            } else {
                item = active ? line1Active : line1;
            }
            return item.clone();
        }
    }

    class NodeBlock implements Block {
        @NotNull
        final Node node;
        final int pos;
        boolean active;

        NodeBlock(@NotNull Node node, int pos) {
            this(node, pos, false);
        }

        NodeBlock(@NotNull Node node, int pos, boolean active) {
            this.node = node;
            this.pos = pos;
            this.active = active;
            blocks[pos] = this;
            nodes.add(this);
            refreshLine();
        }

        @Override
        public ItemStack createItem() {
            final ItemStack item = node.iconItem.clone();
            return active && node != Node.ROOT ? e(item) : item;
        }

        void refreshLine() {
            if (node.parent != null) {
                int linePos = pos;
                boolean side;
                switch (node.direction) {
                    case UP:
                        linePos += WIDTH;
                        side = false;
                        break;
                    case DOWN:
                        linePos -= WIDTH;
                        side = false;
                        break;
                    case LEFT:
                        linePos += 1;
                        side = true;
                        break;
                    case RIGHT:
                        linePos -= 1;
                        side = true;
                        break;
                    default:
                        return;
                }
                blocks[linePos] = new Line(active, side);
            }
        }
    }

    enum Node {
        ROOT(null, 0, EnumPlayerPassiveSkill.ROOT, Direction.NONE, "錬金術の基礎", 7),
        FLAM(ROOT, 200, EnumPlayerPassiveSkill.FLAM, Direction.LEFT, "フラム", 7),
        NEUTRALIZATION(ROOT, 200, EnumPlayerPassiveSkill.NEUTRALIZATION, Direction.RIGHT, "中和剤", 8),
        INGOT(NEUTRALIZATION, 200, EnumPlayerPassiveSkill.INGOT, Direction.RIGHT, "インゴットセット", 9);

        @Nullable
        final Node parent;
        final int requiredPoint;
        @NotNull
        final IPlayerSkillBuilder skill;
        @NotNull
        final ItemStack iconItem;
        @NotNull
        final Direction direction;
        @NotNull
        final Map<Direction, Node> children;

        Node(@Nullable Node parent, int requiredPoint, @NotNull IPlayerSkillBuilder skill, @NotNull Direction direction, @Nullable String name, int customModelData) {
            this(parent, requiredPoint, skill, direction, i(name, customModelData));
        }

        Node(@Nullable Node parent, int requiredPoint, @NotNull IPlayerSkillBuilder skill, @NotNull Direction direction, @NotNull ItemStack item) {
            this.parent = parent;
            this.requiredPoint = requiredPoint;
            this.skill = skill;
            this.iconItem = item;
            this.direction = direction;
            this.children = new Object2ObjectOpenHashMap<>();
            if (this.parent != null) {
                if (this.parent.children.containsKey(direction)) {
                    throw new IllegalArgumentException("The node already exists in that orientation.");
                }
                this.parent.children.put(direction, this);
            }
        }

    }

    enum Direction {
        NONE,
        LEFT,
        RIGHT,
        UP,
        DOWN
    }

}
