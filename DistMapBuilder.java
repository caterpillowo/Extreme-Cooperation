import java.util.*;

/*
    gives us a map of every open cell to its distance to the target for a given entity
    used for heuristic
    every entity has a different target and is given its own map
    for target located at 0, it will look like this

        ██████████
        ████65432█
        ████5███1█
        876543210█
        ████5█████
        ██████████

*/
public class DistMapBuilder {

    public Coord start;
    public Coord finish;

    public DistMapBuilder(Coord start, Coord finish) {
        this.start = start;
        this.finish = finish;
    }

    // not copy and pasting BinaryHeapOpenSet so we will use the inferior PriorityQueue that java gives us (sad)
    public PriorityQueue<Node> openSet;

    // very quick and dirty
    public HashMap<Coord, Integer> buildDistanceMap() {
        // comparator because thats what priorityqueue uses
        Comparator nodeComparator = new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                Node node1 = (Node) o1;
                Node node2 = (Node) o2;
                // returns -1 if node1.distanceTravelled is smaller and +1 if node2.distanceTravelled is smaller
                // its just how comparators work in java
                return Double.compare(node1.distanceTravelled, node2.distanceTravelled);
            }
        };
        // this is the openSet
        openSet = new PriorityQueue(nodeComparator);

        // this will act as a closedSet while also building the distanceMap at the same time
        HashMap<Coord, Integer> distanceMap = new HashMap<>();

        // add the starting node
        openSet.add(new Node(finish, null, 0));

        // add the starting node to the closedSet
        distanceMap.put(finish, 0);

        // openSet becomes empty when every cell is either a wall or already in closedSet. this is when when we want to stop
        while (!openSet.isEmpty()) {
            // remove() actually returns the node at the top of the priorityQueue and removes it. the name is a bit strange
            // ask java
            Node currentNode = openSet.remove();

            // for this node, we need to look at all the places it could move to
            // for those non-java uses it basically means "for move of type Moves in set movingMoves"
            // iterates through them

            for (Moves move : movingMoves) {
                // we apply the move's offset to the coordinate
                Coord offsetCoord = currentNode.coord.offset(move);

                // read the names of the methods and you can guess why we want to skip these straight away
                if (offsetCoord.isOutOfBounds() || offsetCoord.isInWall())
                    continue;

                // this coordinate is already in the distanceMap which is our closedSet.
                // closed coords already have the shortest distance. no point in looking at this
                // this is why we override hashCode() and equals() in Coord
                if (distanceMap.containsKey(offsetCoord))
                    continue;

                // well it looks like this spot is not yet in our distance map
                // we add it to openSet so it can be evaluated later for its neighbours
                openSet.add(new Node(offsetCoord, currentNode, currentNode.distanceTravelled + 1));

                // add it to the distanceMap because its closed
                distanceMap.put(offsetCoord, currentNode.distanceTravelled + 1);
            }
        }

        // return the completed distanceMap
        return distanceMap;
    }

    // great name. difference between this and Moves.values() is that we leave out Moves.NONE
    public static Set<Moves> movingMoves = EnumSet.of(Moves.UP, Moves.DOWN, Moves.LEFT, Moves.RIGHT);

    // node object used in algorithm
    class Node {

        // constructur
        public Node(Coord coord, Node previous, int distanceTravelled) {
            this.coord = coord;
            this.previous = previous;
            this.distanceTravelled = distanceTravelled;
        }

        // we need these things
        public Coord coord;

        // pointer to the previous node so we know the distance its already travelled
        public Node previous;

        // the distance the node has travelled
        public int distanceTravelled;


        // override stuff
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return Objects.equals(coord, node.coord);
        }

        // yes
        @Override
        public int hashCode() {
            return Objects.hash(coord);
        }
    }
}
