package net.firiz.renewatelier.alchemy.kettle;

import it.unimi.dsi.fastutil.objects.*;
import net.firiz.renewatelier.characteristic.Characteristic;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KettleCharacteristicManager {

    private final Object2BooleanMap<Characteristic> characteristics = new Object2BooleanLinkedOpenHashMap<>();
    private final ObjectSet<Characteristic> catalystCharacteristics = new ObjectLinkedOpenHashSet<>();

    public boolean addCharacteristic(Characteristic characteristic, boolean active) {
        return characteristics.put(characteristic, active);
    }

    public ObjectSet<Characteristic> getCharacteristics() {
        return characteristics.keySet();
    }

    public boolean addCatalystCharacteristic(Characteristic characteristic) {
        return catalystCharacteristics.add(characteristic);
    }

    public boolean removeCatalystCharacteristic(Characteristic characteristic) {
        return catalystCharacteristics.remove(characteristic);
    }

    public void setActiveCharacteristic(Characteristic characteristic, boolean active) {
        if (characteristics.containsKey(characteristic)) {
            characteristics.put(characteristic, active);
        }
    }

    public boolean isActiveCharacteristic(Characteristic characteristic) {
        return characteristics.containsKey(characteristic) && characteristics.getBoolean(characteristic);
    }

    public List<Characteristic> getActiveCharacteristics() {
        return characteristics.object2BooleanEntrySet().stream().filter(Object2BooleanMap.Entry::getBooleanValue).map(Map.Entry::getKey).collect(Collectors.toList());
    }

    public void resetActiveCharacteristic() {
        final ObjectSet<Characteristic> set = characteristics.keySet();
        characteristics.clear();
        set.forEach(characteristic -> characteristics.put(characteristic, false));
    }

}
