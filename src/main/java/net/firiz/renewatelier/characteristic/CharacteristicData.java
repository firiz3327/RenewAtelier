package net.firiz.renewatelier.characteristic;

import it.unimi.dsi.fastutil.objects.ObjectIntImmutablePair;

import java.util.List;

public record CharacteristicData(Characteristic characteristic, int value) implements ICharacteristic {
    @Override
    public void add(List<ObjectIntImmutablePair<Characteristic>> cs) {
        cs.add(ObjectIntImmutablePair.of(characteristic, value));
    }
}
