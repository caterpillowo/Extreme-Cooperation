// not my code. i could use java's built in PriorityQueue, but i already had this implementation handy from another project i was working on
// this is a binary heap open set its in the name
// min heap that is used to find the open node with the lowest cost
import java.util.Arrays;

/**
 * A binary heap implementation of an open set. This is the one used in the AStarPathFinder.
 *
 * @author leijurv
 */
public final class BinaryHeapOpenSet {

    /**
     * The initial capacity of the heap (2^10)
     */
    private static final int INITIAL_CAPACITY = 1024;

    /**
     * The array backing the heap
     */
    private Node[] array;

    /**
     * The size of the heap
     */
    private int size;

    public BinaryHeapOpenSet() {
        this(INITIAL_CAPACITY);
    }

    public BinaryHeapOpenSet(int size) {
        this.size = 0;
        this.array = new Node[size];
    }

    public int size() {
        return size;
    }

    public void add(Node value) {
        if (size >= array.length - 1) {
            array = Arrays.copyOf(array, array.length << 1);
        }
        size++;
        value.heapIndex = size;
        array[size] = value;
        update(value);
    }

    public void update(Node val) {
        int index = val.heapIndex;
        int parentInd = index >>> 1;
        double cost = val.getCost();
        Node parentNode = array[parentInd];
        while (index > 1 && parentNode.getCost() > cost) {
            array[index] = parentNode;
            array[parentInd] = val;
            val.heapIndex = parentInd;
            parentNode.heapIndex = index;
            index = parentInd;
            parentInd = index >>> 1;
            parentNode = array[parentInd];
        }
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public Node peekAndRemove() {
        if (size == 0) {
            throw new IllegalStateException();
        }
        Node result = array[1];
        Node val = array[size];
        array[1] = val;
        val.heapIndex = 1;
        array[size] = null;
        size--;
        result.heapIndex = -1;
        if (size < 2) {
            return result;
        }
        int index = 1;
        int smallerChild = 2;
        double cost = val.getCost();
        do {
            Node smallerChildNode = array[smallerChild];
            double smallerChildCost = smallerChildNode.getCost();
            if (smallerChild < size) {
                Node rightChildNode = array[smallerChild + 1];
                double rightChildCost = rightChildNode.getCost();
                if (smallerChildCost > rightChildCost) {
                    smallerChild++;
                    smallerChildCost = rightChildCost;
                    smallerChildNode = rightChildNode;
                }
            }
            if (cost <= smallerChildCost) {
                break;
            }
            array[index] = smallerChildNode;
            array[smallerChild] = val;
            val.heapIndex = smallerChild;
            smallerChildNode.heapIndex = index;
            index = smallerChild;
        } while ((smallerChild <<= 1) <= size);
        return result;
    }
}