package net.firiz.renewatelier.script.template.balls;

import net.firiz.renewatelier.utils.Chore;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class BallMaterialEffect extends STDataBallEffect<ItemStack> {

    private ItemStack data;

    public BallMaterialEffect(String id, int count) {
        super(id, count);
    }

    @Override
    public void setData(String data) {
        if (data.contains(",")) {
            this.data = new ItemStack(Chore.getMaterial(data));
        } else {
            final String[] datas = data.split(",");
            this.data = Chore.createCustomModelItem(Chore.getMaterial(datas[0]), 1, Integer.parseInt(datas[0]));
        }
    }

    @Override
    public ItemStack getData() {
        return data;
    }

    @Override
    public void effect(Location loc) {
    }


}
