package net.firiz.renewatelier.alchemy.material;

/**
 *
 * @author firiz
 */
public final class MaterialSizeData {

    private final MaterialSize size;
    private final int rotate;

    public MaterialSizeData(MaterialSize size, int rotate) {
        this.size = size;
        this.rotate = rotate;
    }
    
    public int[] getSize() {
        return size.getSize(rotate);
    }

}
