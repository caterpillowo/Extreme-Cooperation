import java.util.ArrayList;

public class Node {
    // a snapshot of the coordinates of all the entities in target number order (as is used in every other list of entity something in this program)
    public final ArrayList<Coord> entityCoords;

    // stuff to keep track of
    public Node previous;
    public int moveCount;
    public int heapIndex;
    public boolean closed = false; // to save memory, we keep a map of coords -> node so that we dont get overrun by nodes. also saves us from using an actual closedset
    public double totalCost = -1;

    // this isnt used
    public Node(ArrayList<Coord> entityCoords, Node previous) {
        this.entityCoords = entityCoords;
        this.previous = previous;
        this.moveCount = previous.moveCount + 1;
        this.heapIndex = -1;
    }

    public Node(ArrayList<Coord> entityCoords) {
        this.entityCoords = entityCoords;
        this.previous = null;
        this.moveCount = 0;

        // index -1 is obviously impossible so this means that the node hasnt been heapified yet
        this.heapIndex = -1;

    }

    // sets the cost
    public void setCost() {
        totalCost = moveCount + calcHeuristic(); // TODO: make a map of all grid coords to distance to target

    }

    // duh
    public double getCost() {
        return totalCost;
    }

    // calculates heuristic (or hCost)
    public double calcHeuristic() {
        // the maximum distance an entity is from the target
        int maxDist = 0;

        // the total distance of entities from targets
        double totalDist = 0;
        for (int i = 0; i < entityCoords.size(); i++) {
            maxDist = Math.max(distanceToTarget(i), maxDist);
            totalDist += distanceToTarget(i);
        }

        // duh
        double averageDistance = totalDist / entityCoords.size();

        // as a tiebreaker, we add the average distance of all the entities to their targets divided by the maximum possible distance of an entity from its target
        // since the average distance is basically guranteed to be smaller than the maximum distance this number will always be less than 1,
        // so it will never become more significant that distance travelled and an entity's distance from its target
        // so the integer part of the number is its distancetravelled, and the decimal part is the average distance travelled that acts as a tiebreaker
        // this ensures that the heuristic is admissible and will return the shortest path
        // this is also the best heuristic i could come up with that is admissible as well as being consistent (i think?)
        return maxDist + (averageDistance / Main.maxMovements);
    }


    public int distanceToTarget(int entityID) {
        // gets an entity's distance to its target in the context of this node from the distance maps we made earlier
        return Main.entityToDistanceMap.get(entityID).get(entityCoords.get(entityID));
    }

    // take a guess
    public boolean hasReachedTarget() {
        for (int entityID = 0; entityID < entityCoords.size(); entityID++) {
            Coord entity = entityCoords.get(entityID);
            Coord target = Main.entityToTargetCoords.get(entityID);
            if (!entity.equals(target))
                return false;
        }
        return true;
    }

    // for debugging
    @Override
    public String toString() {
        return "Node{" +
                ", moveCount=" + moveCount +
                ", heapIndex=" + heapIndex +
                '}';
    }

    // compares location of entities instead of memory and ignores other fields
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return entityCoords.equals(node.entityCoords);
    }

    @Override
    public int hashCode() {
        return entityCoords.hashCode();
    }
}
