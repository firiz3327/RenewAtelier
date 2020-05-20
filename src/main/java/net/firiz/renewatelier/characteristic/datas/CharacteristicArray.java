package net.firiz.renewatelier.characteristic.datas;

/**
 * @deprecated CharacteristicInt以外の要素は全て専用クラスを設けるためCharacteristicArrayは一時的なクラスとする
 */
@Deprecated(forRemoval = true)
public class CharacteristicArray implements CharacteristicData {

    private final String[] x;

    @Deprecated
    public CharacteristicArray(String[] x) {
        this.x = x;
    }

    @Deprecated
    public String[] getX() {
        return x;
    }
}
