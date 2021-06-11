package net.firiz.renewatelier.characteristic;

import it.unimi.dsi.fastutil.objects.ObjectIntImmutablePair;

import java.util.List;

public interface ICharacteristic {

    void add(List<ObjectIntImmutablePair<Characteristic>> cs);

}
