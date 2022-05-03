import java.util.Objects;

public class Coord {

    // this is an object that stores an x and y value.
    // mutable coordinates might do funny things so to be safe they are final
    public final int x, y;

    // constructor!!
    public Coord(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // random skipping occurs sometimes tryna figure it out
    // this method doesnt do anything in the actual agorithm
    public int distanceToCoord(Coord target) {
        return Math.abs(x - target.x) + Math.abs(y - target.y);
    }

    // isnt used but you can figure out what it does
    public Coord offset(int x, int y) {
        return new Coord(this.x + x, this.y + y);
    }

    // given our Moves, this will apply the move to the coordinate. Moves have an x offset and a y offset. See Moves.java for more
    public Coord offset(Moves move) {
        return new Coord(this.x + move.xOffset, this.y + move.yOffset);
    }

    // cant be having the coordinate be outside of the grid
    public boolean isOutOfBounds() {
        return this.x < 0 || this.x >= Main.width || this.y < 0 || this.y >= Main.height;
    }

    // this is to check if the coordinate is in a wall. this means that we cant go there
    public boolean isInWall() {
        return Main.grid[x][y];
    }

    // for use in hashmaps. usually objects are compared by their address in the computer's memory.
    // this would suck because randomCoord.equals(new Coord(x, y)) would never return true
    // so here im overriding it so that it just compares the x and y values
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coord coord = (Coord) o;
        return x == coord.x &&
                y == coord.y;
    }

    // this basically returns the coord's index in the grid if we numbered each cell from left to right top to bottom
    // this means that we will never have a hash collision (yay) cuz every coord is unique
    // this also means larger inputs will give very large hashcodes and use up lots of memory but the test case is like
    // 7x7 it doesnt matter
    @Override
    public int hashCode() {
        return Main.width * x + y;
    }

    // debugging purposes. nobody cares about memory address
    @Override
    public String toString() {
        return "Coord{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    // this isnt used
    public static int hashArray(Coord[] coords) {
        int total = 0;
        for (int i = 0; i < coords.length; i++) {
            total += coords[i].hashCode() * (Math.pow(i, Main.width));
        }
        return total;
    }

    // this isnt used
    public static int hash(int x, int y) {
        Objects.hash(x, y);
        return Main.width * x + y;
    }

    // this isnt used
    public static Coord fromHash(int hashCode) {
        return new Coord(hashCode / Main.width, hashCode % Main.width);
    }


}
