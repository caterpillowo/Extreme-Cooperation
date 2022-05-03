import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

// the important one
public class Pathfinder {
    // open set
    public BinaryHeapOpenSet openSet;

    // a map of a list of entity locations to a node
    public HashMap<Integer, Node> nodeMap;

    // didnt i already make this before? i think im stupid
    public long timeStart = 0L;

    // the node that is selected and removed from the top of the openset
    public Node currentNode;

    public Pathfinder() {
        openSet = new BinaryHeapOpenSet();
        nodeMap = new HashMap<>();
    }

    // gets the node for a given list of entity coordinates
    // creates a new node if necessary
    public Node getNode(ArrayList<Coord> coordArray) {
        int hash = coordArray.hashCode();
        Node node = nodeMap.get(hash);
        if (node == null) {
            node = new Node(coordArray);
            nodeMap.put(hash, node);
        }
        return node;
    }


    public int iterate() { // 0 = null, 1 = success, 2 = fail
        if (openSet.size() == 0) // openSet is empty, so there is no possible path. this will usually take forever
            return 2;
        // takes the top of the openSet and removes it
        currentNode = openSet.peekAndRemove();

        // sets it as closed
        currentNode.closed = true;

        // duh
        if (currentNode.hasReachedTarget()) {
            return 1;
        }

        // see method
        ArrayList<ArrayList<Moves>> validMovesList = getValidMoves();

        // creates an index which will be used to get the permutations of valid moves
        int[] indexes = new int[Main.entityCount];

        int pointer = 0;
        outerloop:
        while (true) {

            // probably overcomplex code that loops through every permutation of the valid moves for each entity
            // it increases the first cell in indexes by 1 and will carry it over if it exceeds the size of validMoves for that entity
            // if it reaches the end of indexes then it means we have finished every permutation

            // basically imagine counting normally except each digit is in a different base
            while (indexes[pointer] == validMovesList.get(pointer).size()) {
                indexes[pointer] = 0;
                pointer++;
                if (pointer == indexes.length) {
                    break outerloop;
                }
                indexes[pointer]++;
            }
            pointer = 0;

            // this will become the post-move list of enemy coords
            ArrayList<Coord> enemyCoords = new ArrayList<>();

            // messy code that applies a movement to each coordinate
            for (int entityID = 0; entityID < indexes.length; entityID++) {
                int validMoveIndex = indexes[entityID];
                enemyCoords.add(currentNode.entityCoords.get(entityID).offset(
                        validMovesList.get(entityID)
                                .get(validMoveIndex)));
            }

            // increments the first cell of indexes
            // we do this after all of the stuff above or else we start at index 1 instead of 0
            indexes[pointer]++;

            // we get the node with the matching coordinates, or create one if it doesnt exist
            // this acts as a closedSet and lets us have nodes that arent in openSet
            Node node = getNode(enemyCoords);

            // skip if closed
            if (node.closed) {
                continue;
            }

            // skip if entity collides with another entity
            if (entityCollisionExists(enemyCoords, currentNode.entityCoords)) {
                node.closed = true;
                nodeMap.put(enemyCoords.hashCode(), node);
                continue;
            }

            // this means the node isnt in openSet so we add it
            if (node.heapIndex == -1) {
                node.moveCount = currentNode.moveCount + 1;
                node.previous = currentNode;
                node.setCost();
                openSet.add(node);
            }

            // this means the node is in openSet, but we have found a faster way to get to those positions so we update it
            if (node.moveCount > currentNode.moveCount + 1) {
                node.moveCount = currentNode.moveCount + 1;
                node.previous = currentNode;
                node.setCost();
                openSet.update(node);
            }
        }
        return 0;
    }

    // gets a list of a list of valid moves for entities in entityID order
    public ArrayList<ArrayList<Moves>> getValidMoves() {
        ArrayList<ArrayList<Moves>> validMovesList = new ArrayList<>();
        for (int entityID = 0; entityID < Main.entityCount; entityID++) {
            ArrayList<Moves> validMoves = getValidMoves(currentNode.entityCoords.get(entityID));
            validMovesList.add(validMoves);
        }
        return validMovesList;
    }


    // basic setup
    public void findPath() {
        // creates the starting node and adds it to things
        Node node = getNode(Main.entityToStartCoords);
        openSet.add(node);
        timeStart = System.currentTimeMillis();
    }

    // checks if there are any entity collisions happening
    public boolean entityCollisionExists(ArrayList<Coord> coords, ArrayList<Coord> prevCoords) {
        // check for collisions within current coords
        // hashSets are used because it lets us do this in O(n) time
        HashSet<Coord> hashSet = new HashSet<>();
        for (int i = 0; i < coords.size(); i++) {
            if (!hashSet.add(coords.get(i)))
                return true;
        }

        // check for entities moving through each other
        hashSet = new HashSet<>();
        if (!hashSet.addAll(prevCoords)) {  // this should never return false
            System.out.println(prevCoords.get(-1));
        }
        for (int i = 0; i < coords.size(); i++) {
            if (!hashSet.add(coords.get(i))) {
                if (!coords.get(i).equals(prevCoords.get(i)))
                    return true;
            }
        }
        return false;
    }

    // gets moves that dont send us off the grid or into a wall
    // we take other entities into account later
    public ArrayList<Moves> getValidMoves(Coord entity) {
        ArrayList<Moves> validMoves = new ArrayList();
        for (Moves move : Moves.values()) {
            Coord offsetCoord = entity.offset(move);
            if (offsetCoord.isOutOfBounds() || offsetCoord.isInWall())
                continue;
            validMoves.add(move);
        }
        return validMoves;
    }

}
