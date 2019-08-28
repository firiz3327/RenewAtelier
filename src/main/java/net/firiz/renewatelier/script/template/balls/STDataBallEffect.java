package net.firiz.renewatelier.script.template.balls;

import org.bukkit.Location;

public abstract class STDataBallEffect<T> extends STBallEffect {

    public STDataBallEffect(String id, int count) {
        super(id, count);
    }

    abstract public void setData(String data);

    abstract public T getData();

    @Override
    abstract public void effect(Location loc);

}
