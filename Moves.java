public enum Moves {
    // im still not quite sure how enums work in java
    // each enum is given an x and y offset
    // very self explanatory
    UP(0, 1),
    DOWN(0, -1),
    LEFT(-1, 0),
    RIGHT(1, 0),
    NONE(0, 0);

    public final int xOffset;
    public final int yOffset;

    Moves(int x, int y) {
        xOffset = x;
        yOffset = y;
    }

}
