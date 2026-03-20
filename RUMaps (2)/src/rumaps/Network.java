package rumaps;

public class Network {
    // Stores vertices
    private Intersection[] intersections;
    // Stores edges: adj[i] refers to the edges of vertex in intersections[i]
    private Block[] adj; 
    private int nextIndex;

    private MapPanel mapPanel;

    public Network(int size) {
        intersections = new Intersection[size];
        adj = new Block[size];
    }

    public Network(int size, MapPanel mapPanel) {
        intersections = new Intersection[size];
        adj = new Block[size];
        this.mapPanel = mapPanel;
    }

    /**
     * Finds the index of an intersection in the intersections array.
     * @param c the coordinate which may be stored in an intersection
     * @return the intersection index or -1 if nonexistent
     */
    public int findIntersection(Coordinate c) {
        return findIntersection(c.getX(), c.getY());
    }

    /**
     * Finds the index of an intersection in the intersections array.
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return the intersection index or -1 if nonexistent
     */
    public int findIntersection(int x, int y) {
        for (int i = 0; i < intersections.length; i++) {
            if (intersections[i] != null && intersections[i].getCoordinate().getX() == x && intersections[i].getCoordinate().getY() == y) {
                return i;
            }
        }
        return -1; // Not found
    }

    /**
     * Adds an intersection to the next open space in the intersections
     * array. Assumes the intersections array is correctly sized.
     * @param res the intersection to add
     */
    public void addIntersection(Intersection res) {
        intersections[nextIndex] = res;
        nextIndex++;
        if (mapPanel != null) mapPanel.addIntersection(res);
    }

    /**
     * Adds an edge (block) to the adjacency list of the given index.
     * ONLY adds in ONE direction (a->b). 
     * @param index the index to add at
     * @param toAdd the block to add
     */
    public void addEdge(int index,Block toAdd) {
        if (adj[index] == null) {
            adj[index] = toAdd;
            if (mapPanel != null){
                mapPanel.addBlock(toAdd);
            }
        } else {
            Block current = adj[index];
            while (current.getNext() != null) {
                current = current.getNext();
            }
            current.setNext(toAdd);
            if (mapPanel != null){
                mapPanel.addBlock(toAdd);
            }
        }
    }

    /**
     * Returns edges incident to a vertex at a given index.  
     * @param index the given index of the vertex
     * @return the front Block reference at this index
     */
    public Block adj(int index) {
        return adj[index];
    }

    public Intersection[] getIntersections() {
        return intersections;
    }

    public void setIntersections(Intersection[] arr) {
        this.intersections = arr;
    }

    public Block[] getAdjacencyList() {
        return adj;
    }

    public void setAdj(Block[] arr) {
        this.adj = arr;
    }

    public int getNextIndex() {
        return nextIndex;
    }

    public void setNextIndex(int nextIndex) {
        this.nextIndex = nextIndex;
    }
}
