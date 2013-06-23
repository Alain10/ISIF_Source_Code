package ca.ott.al.starmap.core.map;

public class Star extends StarMapObject {
    /**
     * 
     */
    private static final long serialVersionUID = -4952370666457453461L;

    public enum Type {
        B, A, F, G, K, M
    }

    private Type type;
    private int subtype; // range 0 to 9

    public Star(Type type, int subtype) {
        this.type = type;
        this.subtype = subtype;
    }

    public Type getType() {
        return type;
    }

    public int getSubtype() {
        return subtype;
    }

}
