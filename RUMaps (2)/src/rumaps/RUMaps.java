package rumaps;

import java.util.*;

/**
 * This class represents the information that can be attained from the Rutgers University Map.
 * 
 * The RUMaps class is responsible for initializing the network, streets, blocks, and intersections in the map.
 * 
 * @author Vian Miranda
 * @author Anna Lu
 */
public class RUMaps {
    
    private Network rutgers;

    /**
     * **DO NOT MODIFY THIS METHOD**
     */
    public RUMaps(MapPanel mapPanel, String filename) {
        StdIn.setFile(filename);
        int numIntersections = StdIn.readInt();
        int numStreets = StdIn.readInt();
        StdIn.readLine();
        rutgers = new Network(numIntersections, mapPanel);
        ArrayList<Block> blocks = initializeBlocks(numStreets);
        initializeIntersections(blocks);

        for (Block block: rutgers.getAdjacencyList()) {
            Block ptr = block;
            while (ptr != null) {
                ptr.setLength(blockLength(ptr));
                ptr.setTrafficFactor(blockTrafficFactor(ptr));
                ptr.setTraffic(blockTraffic(ptr));
                ptr = ptr.getNext();
            }
        }
    }

    /**
     * **DO NOT MODIFY THIS METHOD**
     */
    public RUMaps(String filename) {
        this(null, filename);
    }

    /**
     * **DO NOT MODIFY THIS METHOD**
     */
    public RUMaps() { 
        
    }

    /**
     * Initializes all blocks, given a number of streets.
     * The file was opened by the constructor - use StdIn to continue reading the file.
     */
    public ArrayList<Block> initializeBlocks(int numStreets) {
        ArrayList<Block> blocks = new ArrayList<>();

        for (int i = 0; i < numStreets; i++) {
            String streetName = StdIn.readLine();
            int numBlocks = StdIn.readInt();
            StdIn.readLine();

            for (int k = 0; k < numBlocks; k++) {
                int blockNumber = StdIn.readInt();
                StdIn.readLine();
                int numPoints = StdIn.readInt();
                StdIn.readLine();
                double roadSize = StdIn.readDouble();
                StdIn.readLine();

                Block block = new Block(roadSize, streetName, blockNumber);

                for (int j = 0; j < numPoints; j++) {
                    String[] parts = StdIn.readLine().trim().split(" ");
                    int x = Integer.parseInt(parts[0]);
                    int y = Integer.parseInt(parts[1]);
                    Coordinate coord = new Coordinate(x, y);
                    if (j == 0) {
                        block.startPoint(coord);
                    } else {
                        block.nextPoint(coord);
                    }
                }

                blocks.add(block);
            }
        }

        return blocks;
    }

    /**
     * Traverses each block and finds the block's start and end points to create intersections.
     * Adds intersections as vertices to the "rutgers" graph if not already present,
     * and adds UNDIRECTED edges to the adjacency list.
     */
    public void initializeIntersections(ArrayList<Block> blocks) {
        if (blocks == null) return;

        for (Block current : blocks) {
            List<Coordinate> points = current.getCoordinatePoints();
            if (points == null || points.isEmpty()) continue;

            Coordinate startCoord = points.get(0);
            Coordinate endCoord   = points.get(points.size() - 1);

            // --- Handle start intersection ---
            int startIndex = rutgers.findIntersection(startCoord);
            Intersection startIntersection;
            if (startIndex == -1) {
                startIntersection = new Intersection(startCoord);
                rutgers.addIntersection(startIntersection);
                startIndex = rutgers.findIntersection(startCoord);
            } else {
                startIntersection = rutgers.getIntersections()[startIndex];
            }
            current.setFirstEndpoint(startIntersection);

            // --- Handle end intersection ---
            int endIndex = rutgers.findIntersection(endCoord);
            Intersection endIntersection;
            if (endIndex == -1) {
                endIntersection = new Intersection(endCoord);
                rutgers.addIntersection(endIntersection);
                endIndex = rutgers.findIntersection(endCoord);
            } else {
                endIntersection = rutgers.getIntersections()[endIndex];
            }
            current.setLastEndpoint(endIntersection);

            // --- Create undirected edges ---
            // Block A: start -> end
            Block blockA = current.copy();
            blockA.setFirstEndpoint(startIntersection);
            blockA.setLastEndpoint(endIntersection);

            // Block B: end -> start (reversed)
            Block blockB = current.copy();
            blockB.setFirstEndpoint(endIntersection);
            blockB.setLastEndpoint(startIntersection);

            rutgers.addEdge(startIndex, blockA);
            rutgers.addEdge(endIndex,   blockB);
        }
    }

    /**
     * Calculates the length of a block by summing distances between all consecutive coordinate points.
     */
    public double blockLength(Block block) {
        ArrayList<Coordinate> points = block.getCoordinatePoints();
        double total = 0.0;
        for (int i = 0; i < points.size() - 1; i++) {
            total += coordinateDistance(points.get(i), points.get(i + 1));
        }
        return total;
    }

    /**
     * Uses a DFS to find the order of all intersections reachable from the given source.
     * Implemented recursively.
     */
    public ArrayList<Intersection> reachableIntersections(Intersection source) {
        ArrayList<Intersection> visited = new ArrayList<>();
        boolean[] marked = new boolean[rutgers.getIntersections().length];
        dfsHelper(source, marked, visited);
        return visited;
    }

    private void dfsHelper(Intersection v, boolean[] marked, ArrayList<Intersection> visited) {
        int index = rutgers.findIntersection(v.getCoordinate());
        if (index == -1 || marked[index]) return;

        marked[index] = true;
        visited.add(v);

        Block block = rutgers.adj(index);
        while (block != null) {
            Intersection neighbor = block.other(v);
            int neighborIndex = rutgers.findIntersection(neighbor.getCoordinate());
            if (neighborIndex != -1 && !marked[neighborIndex]) {
                dfsHelper(neighbor, marked, visited);
            }
            block = block.getNext();
        }
    }

    /**
     * Finds the path with the fewest intersections from start to end using BFS.
     * Returns an empty ArrayList if no path exists.
     */
    public ArrayList<Intersection> minimizeIntersections(Intersection start, Intersection end) {
        int n = rutgers.getIntersections().length;
        boolean[] visited = new boolean[n];
        Intersection[] edgeTo = new Intersection[n];

        int startIndex = rutgers.findIntersection(start.getCoordinate());
        if (startIndex == -1) return new ArrayList<>();

        Queue<Intersection> queue = new Queue<>();
        visited[startIndex] = true;
        queue.enqueue(start);

        while (!queue.isEmpty()) {
            Intersection current = queue.dequeue();
            if (current.equals(end)) break;

            int currentIndex = rutgers.findIntersection(current.getCoordinate());
            Block block = rutgers.adj(currentIndex);
            while (block != null) {
                Intersection neighbor = block.other(current);
                int neighborIndex = rutgers.findIntersection(neighbor.getCoordinate());
                if (neighborIndex != -1 && !visited[neighborIndex]) {
                    visited[neighborIndex] = true;
                    edgeTo[neighborIndex] = current;
                    queue.enqueue(neighbor);
                }
                block = block.getNext();
            }
        }

        int endIndex = rutgers.findIntersection(end.getCoordinate());
        if (!visited[endIndex]) return new ArrayList<>();

        // Reconstruct path by chasing edgeTo from end back to start
        ArrayList<Intersection> path = new ArrayList<>();
        Intersection v = end;
        while (v != null) {
            path.add(v);
            int vIndex = rutgers.findIntersection(v.getCoordinate());
            v = edgeTo[vIndex];
        }
        Collections.reverse(path);
        return path;
    }

    /**
     * Finds the path with the least traffic from start to end using Dijkstra's algorithm.
     * Uses traffic as edge weights. Ties broken by earlier position in fringe (ArrayList).
     * Returns an empty ArrayList if no path exists.
     */
    public ArrayList<Intersection> fastestPath(Intersection start, Intersection end) {
        int n = rutgers.getIntersections().length;
        double[] d    = new double[n];
        boolean[] done = new boolean[n];
        Intersection[] pred = new Intersection[n];

        for (int i = 0; i < n; i++) d[i] = Double.MAX_VALUE;

        int startIndex = rutgers.findIntersection(start.getCoordinate());
        if (startIndex == -1) return new ArrayList<>();

        d[startIndex] = 0.0;
        ArrayList<Intersection> fringe = new ArrayList<>();
        fringe.add(start);

        while (!fringe.isEmpty()) {
            // Extract intersection with minimum cost from fringe (ties: earlier in list)
            Intersection u = null;
            int uIndex = -1;
            double minCost = Double.MAX_VALUE;
            for (Intersection inter : fringe) {
                int idx = rutgers.findIntersection(inter.getCoordinate());
                if (idx != -1 && d[idx] < minCost) {
                    minCost = d[idx];
                    u = inter;
                    uIndex = idx;
                }
            }

            if (u == null) break;
            fringe.remove(u);
            done[uIndex] = true;

            if (u.equals(end)) break;

            Block block = rutgers.adj(uIndex);
            while (block != null) {
                Intersection neighbor = block.other(u);
                int neighborIndex = rutgers.findIntersection(neighbor.getCoordinate());
                if (neighborIndex != -1 && !done[neighborIndex]) {
                    double newCost = d[uIndex] + block.getTraffic();
                    if (newCost < d[neighborIndex]) {
                        d[neighborIndex] = newCost;
                        pred[neighborIndex] = u;
                        if (!fringe.contains(neighbor)) {
                            fringe.add(neighbor);
                        }
                    }
                }
                block = block.getNext();
            }
        }

        int endIndex = rutgers.findIntersection(end.getCoordinate());
        if (d[endIndex] == Double.MAX_VALUE) return new ArrayList<>();

        // Reconstruct path
        ArrayList<Intersection> path = new ArrayList<>();
        Intersection v = end;
        while (v != null) {
            path.add(v);
            int vIndex = rutgers.findIntersection(v.getCoordinate());
            v = pred[vIndex];
        }
        Collections.reverse(path);
        return path;
    }

    /**
     * Calculates total length, average traffic factor, and total traffic for a given path.
     * Returns [totalLength, totalTraffic/totalLength, totalTraffic].
     */
    public double[] pathInformation(ArrayList<Intersection> path) {
        double totalLength  = 0.0;
        double totalTraffic = 0.0;

        for (int i = 0; i < path.size() - 1; i++) {
            Intersection current = path.get(i);
            Intersection next    = path.get(i + 1);

            int currentIndex = rutgers.findIntersection(current.getCoordinate());
            Block block = rutgers.adj(currentIndex);
            while (block != null) {
                if (block.getLastEndpoint().equals(next)) {
                    totalLength  += block.getLength();
                    totalTraffic += block.getTraffic();
                    break;
                }
                block = block.getNext();
            }
        }

        double avgTrafficFactor = (totalLength > 0) ? totalTraffic / totalLength : 0.0;
        return new double[] { totalLength, avgTrafficFactor, totalTraffic };
    }

    /**
     * Calculates the Euclidean distance between two coordinates.
     * PROVIDED - do not modify
     */
    private double coordinateDistance(Coordinate a, Coordinate b) {
        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * **DO NOT MODIFY THIS METHOD**
     */
    public double blockTrafficFactor(Block block) {
        double rand = StdRandom.gaussian(1, 0.2);
        rand = Math.max(rand, 0.5);
        rand = Math.min(rand, 1.5);
        return rand;
    }

    /**
     * PROVIDED METHOD
     */
    public double blockTraffic(Block block) {
        return block.getTrafficFactor() * block.getLength();
    }

    public Network getRutgers() {
        return rutgers;
    }
}