package net.firiz.renewatelier.characteristic.datas;

@Deprecated(since = "CharacteristicInt以外の要素は全て専用クラスを設けるためCharacteristicArrayは一時的なクラスとする")
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
