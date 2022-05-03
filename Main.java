import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Main {

    // input
    public static final String input =
            "██████████\n" +
                    "████3   1█\n" +
                    "████z███ █\n" +
                    "x      y2█\n" +
                    "████ █████\n" +
                    "██████████";

    // input split so that each line is a new entry in a String array
    public static String[] splitInput;

    // alphabet for matching letters to numbers
    public static String alphabet = "abcdefghijklmnopqrstuvwxyz";

    // a 2D grid of all the walls
    public static boolean[][] grid; // true = wall, false == air

    // width of grid
    public static int width;

    // height of grid
    public static int height;

    // number of entities (saved in memory for later faster access)
    public static int entityCount;

    // maximum number of movements required to go from any point on the grid to another
    // this will be used later on
    public static double maxMovements;

    // given an entityID (which is just its target's number - 1) that is used as an index we can find all this info
    public static ArrayList<Coord> entityToTargetCoords;  // gets an entity's target's coords
    public static ArrayList<Coord> entityToStartCoords; // gets the coords of where the entity starts
    public static ArrayList<HashMap<Coord, Integer>> entityToDistanceMap; // gives us a hashMap that tells us how far a given coordinate is to the target

    // an arrow for use when printing output
    public static String arrow;

    // statistics
    public static int loops = 0;
    public static long timeStarted = 0L;

    // too much code to fit into one class, so i chuck the pathfinding business into another one
    public static Pathfinder pathfinder;

    public static void main(String[] args) {
        // so its easier to work with
        splitInput = input.split("\n");

        // self explanatory
        width = splitInput[0].length();
        height = splitInput.length;

        // see method for more notes
        scanInput();
        generateDistMaps();
        createArrow();

        // for debugging
//        printDistMap(1);

        // gotta actually create the pathfinder and does simple setup
        pathfinder = new Pathfinder();
        pathfinder.findPath();

        // marking the time at which we began the algorithm
        timeStarted = System.currentTimeMillis();

        // this is where it iterates the algorithm
        loop:
        while (true) {
            loops++;

            // see method for more info
            switch (pathfinder.iterate()) {
                case 1:
                    break loop;
                case 2:
                    System.out.println("FAILED");
                    break loop;
                case 0:
            }
        }

        // self explanatory
        printSolution(pathfinder.currentNode);
    }

    // math for creating the little arrow in between the steps of the path
    public static void createArrow() {
        int spaces = (width - 1) / 2;
        int arrows = 2 - width % 2;
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        for (int i = 0; i < spaces; i++) {
            sb.append(" ");
        }
        for (int i = 0; i < arrows; i++) {
            sb.append("v");
        }
        sb.append("\n");
        arrow = sb.toString();
    }

    // prints the solution, given the final node
    public static void printSolution(final Node finalNode) {
        Node prevNode = finalNode;

        // the path is a list of list of entity coordinates
        ArrayList<ArrayList<Coord>> path = new ArrayList<>();

        // adds all of the finalNode's previous nodes' entity positions to the list
        while (prevNode.previous != null) {
            prevNode = prevNode.previous;
            path.add(0, prevNode.entityCoords);
        }

        // prints them all out
        for (ArrayList<Coord> coordArray : path) {
            printGrid(coordArray);
            System.out.println(arrow);
        }

        // prints out the actual final node
        printGrid(finalNode.entityCoords);

        // show stats
        System.out.println();
        System.out.println("Total movements: " + path.size() + ", Loops/states considered: " + loops + ", Time taken: " + (System.currentTimeMillis() - timeStarted) + "ms");
    }

    // scans the input and generates data and stuff
    public static void scanInput() {
        // creates grid
        grid = new boolean[width][height];

        // code that figures locations and matches entities to targets
        HashMap<Character, Coord> numbers = new HashMap<>();
        HashMap<Character, Coord> letters = new HashMap<>();
        int y = 0;
        for (String string : splitInput) {
            int x = 0;
            for (char c : string.toCharArray()) {
                grid[x][y] = c == '█';
                if (Character.isAlphabetic(c)) {
                    letters.put(c, new Coord(x, y));
                }
                if (Character.isDigit(c)) {
                    numbers.put(c, new Coord(x, y));
                }
                x++;
            }
            y++;
        }

        entityCount = numbers.size();
        entityToStartCoords = new ArrayList<>(Arrays.asList(new Coord[entityCount]));
        entityToTargetCoords = new ArrayList<>(Arrays.asList(new Coord[entityCount]));
        alphabet = alphabet.substring(26 - entityCount);
        for (Map.Entry<Character, Coord> entry : numbers.entrySet()) {
            int number = Character.getNumericValue(entry.getKey());
            char entity = alphabet.charAt(number - 1);
            entityToTargetCoords.set(number - 1, entry.getValue());
            entityToStartCoords.set(number - 1, letters.get(entity));
        }
    }

    // creates the maps used in calculating heuristics
    public static void generateDistMaps() {
        entityToDistanceMap = new ArrayList<>();

        // for each entity we make a new distance map
        for (int i = 0; i < entityCount; i++) {
            DistMapBuilder builder = new DistMapBuilder(entityToStartCoords.get(i), entityToTargetCoords.get(i));
            entityToDistanceMap.add(builder.buildDistanceMap());
        }

        // calculate the maximum number of movements an entity needs to take from one cell to another
        // used when calculating heuristic
        for (HashMap<Coord, Integer> map : entityToDistanceMap) {
            for (Integer integer : map.values()) {
                maxMovements = Math.max(integer, maxMovements);
            }
        }
    }

    // prints the grid
    public static void printGrid(ArrayList<Coord> enemyCoords) {
        for (int y = 0; y < height; y++) {
            xloop:
            for (int x = 0; x < width; x++) {
                if (grid[x][y]) {
                    System.out.print("█");
                    continue;
                }
                for (int i = 0; i < entityCount; i++) {
                    boolean flag1 = enemyCoords.get(i).equals(new Coord(x, y));
                    boolean flag2 = entityToTargetCoords.get(i).equals(new Coord(x, y));

                    // capitalises entity if they are sitting on the target. IMO improves upon the output and its readability
                    if (flag1 && flag2) {
                        System.out.print(Character.toUpperCase(alphabet.charAt(i)));
                        continue xloop;
                    }
                    if (flag1) {
                        System.out.print(alphabet.charAt(i));
                        continue xloop;
                    }
                    if (flag2) {
                        System.out.print(i + 1);
                        continue xloop;
                    }
                }
                System.out.print(" ");
            }
            System.out.println();
        }
    }

    // for troubleshooting
    public static void printDistMap(int enemyID) {
        HashMap<Coord, Integer> map = entityToDistanceMap.get(enemyID);
        for (int y = 0; y < height; y++) {
            xloop:
            for (int x = 0; x < width; x++) {
                if (grid[x][y]) {
                    System.out.print("█");
                    continue;
                }
                if (map.containsKey(new Coord(x, y))) {
                    System.out.print(map.get(new Coord(x, y)));
                    continue xloop;
                }
            }
            System.out.println("");
        }
        System.out.println();
    }

    // also for troubleshooting
    public static String getString() {
        return "Main{" +
                "alphabet='" + alphabet + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", entityCount=" + entityCount +
                ", maxMovements=" + maxMovements +
                ", arrow='" + arrow + '\'' +
                ", loops=" + loops +
                ", timeTaken=" + timeStarted +
                '}';
    }
}
