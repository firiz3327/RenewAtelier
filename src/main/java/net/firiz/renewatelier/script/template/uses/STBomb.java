package net.firiz.renewatelier.script.template.uses;

import net.firiz.renewatelier.script.template.balls.BallMaterialEffect;
import net.firiz.renewatelier.script.template.balls.STBallEffect;
import net.firiz.renewatelier.script.template.balls.STDataBallEffect;
import net.firiz.renewatelier.utils.Chore;
import org.bukkit.configuration.ConfigurationSection;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class STBomb extends STUse {

    /*
    script-template:
        id: bomb
        damage: 100
        randomMax: 1.2
        randomMin: 0.8
        velocity: x * 1.2
        ballEffect1:
            type: item
            data: xxx:1500  // item, item_crack, block_crack, block_dust and falling_dust only
        ballEffect2:
            type: redstone
            data: 0,0,255 // select rgb color - redstone, spell_mob and spell_mob_ambient only
            count: 3
        ballEffect3:
            type: note
            data: 6 // red note
     */

    private enum Balls {
        BallMaterialEffect(BallMaterialEffect.class, "item", "item_crack", "block_crack", "block_dust", "falling_dust");

        private final Class<?> clasz;
        private final String[] ids;

        Balls(Class<?> clasz, String... ids) {
            this.clasz = clasz;
            this.ids = ids;
        }

        public static STBallEffect getEffect(String id, int count) {
            for (Balls b : values()) {
                for (String s : b.ids) {
                    if (s.equalsIgnoreCase(id)) {
                        try {
                            return (STBallEffect) b.clasz.getConstructor(String.class, Integer.class).newInstance(id, count);
                        } catch (InstantiationException e) {
                            Chore.logWarning(e);
                        } catch (IllegalAccessException e) {
                            Chore.logWarning(e);
                        } catch (InvocationTargetException e) {
                            Chore.logWarning(e);
                        } catch (NoSuchMethodException e) {
                            Chore.logWarning(e);
                        }
                    }
                }
            }
            return null;
        }
    }

    private final double damage;
    private final double rMax;
    private final double rMin;
    private final String velocityFormula;
    private final List<STBallEffect> ballEffects;

    public STBomb(ConfigurationSection config) {
        super("bomb", config.contains("useCount") ? config.getInt("useCount") : 1);
        this.damage = config.contains("damage") ? config.getDouble("damage") : 1;
        this.rMax = config.contains("random_max") ? config.getDouble("random_max") : 1;
        this.rMin = config.contains("random_min") ? config.getDouble("random_min") : 1;
        this.velocityFormula = config.getString("velocity");

        ballEffects = new ArrayList<>();
        for (int i = 1;; i++) {
            if (config.contains("ballEffect" + i)) {
                final ConfigurationSection section = config.getConfigurationSection("ballEffect" + i);
                section.getKeys(false).stream().map(key -> section.getString("type").toUpperCase()).forEach(type -> {
                    final String data = section.getString("data");
                    final int count = section.getInt("count");
                    final STBallEffect effect = Balls.getEffect(type, count);
                    if (effect instanceof STDataBallEffect) {
                        ((STDataBallEffect) effect).setData(data);
                    }
                });
                continue;
            }
            break;
        }

//        final MapVariable<String, Long> varMap = new MapVariable<String, Long>(String.class, Long.class);
//        varMap.put("x", getVelocity());
//        final Expression exp = ExpRuleFactory.getDefaultRule().parse(str);
//        exp.setVariable(varMap);
    }

    @Override
    public void script() {

    }

}
