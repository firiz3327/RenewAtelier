package net.firiz.renewatelier.alchemy.kettle;

import it.unimi.dsi.fastutil.objects.*;
import net.firiz.renewatelier.characteristic.Characteristic;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class KettleCharacteristicManager {

    private final List<Characteristic> characteristics = new ObjectArrayList<>();
    private final List<Characteristic> activeCharacteristics = new ObjectArrayList<>();
    private final List<Collection<Characteristic>> activeAll = new ObjectArrayList<>();
    private final ObjectSet<Characteristic> catalystCharacteristics = new ObjectLinkedOpenHashSet<>();

    public KettleCharacteristicManager() {
        activeAll.add(activeCharacteristics);
        activeAll.add(catalystCharacteristics);
    }

    public List<Characteristic> getAllActiveCharacteristics() {
        return activeAll.stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    public boolean addCharacteristic(Characteristic characteristic) {
        return characteristics.add(characteristic);
    }

    public List<Characteristic> getCharacteristics() {
        return characteristics;
    }

    public boolean addCatalystCharacteristic(Characteristic characteristic) {
        return catalystCharacteristics.add(characteristic);
    }

    public boolean removeCatalystCharacteristic(Characteristic characteristic) {
        return catalystCharacteristics.remove(characteristic);
    }

    public boolean removeActiveCharacteristic(Characteristic characteristic) {
        return activeCharacteristics.remove(characteristic);
    }

    public boolean addActiveCharacteristic(Characteristic characteristic) {
        return activeCharacteristics.add(characteristic);
    }

    public void clearActiveCharacteristics() {
        activeCharacteristics.clear();
    }

    public boolean hasActiveCharacteristic(Characteristic characteristic) {
        return activeCharacteristics.contains(characteristic);
    }

    public List<Characteristic> getActiveCharacteristics() {
        return activeCharacteristics;
    }

}
