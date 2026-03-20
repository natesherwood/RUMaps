package rumaps;

import java.awt.geom.Path2D;
import java.util.*;

/**
 * The Block class is used to represent a street block in the map and stores information such as the start point, end point, length, traffic factor, traffic, street name, and block number.
 * It is also used to draw the block to the map GUI. It contains the following attributes:
 * - path - purely for GUI 
 * - coordinatePoints - coordinates of the block (inclusive of start and end points, which are coords of intersection A and B) 
 * - roadSize - size of the stroke 
 * - intersecetionA - starting (can also be considered end) intersection of the block 
 * - intersecetionB - ending (can also be considered start) intersection of the block 
 * - length - length of the block 
 * - trafficFactor - traffic factor of the block 
 * - traffic - traffic of the block 
 * - streetName - name of the street block is on
 * - blockNumber - number of the block 
 * 
 * @author Vian Miranda
 * @author Anna Lu
 */
public class Block {
    private Path2D path;
    private ArrayList<Coordinate> coordinatePoints;
    private float roadSize;

    private Intersection firstEndpoint;
    private Intersection lastEndpoint;

    private double length;
    private double trafficFactor;
    private double traffic;

    private String streetName;
    private int blockNumber;

    private Block next; // for linked list implementation

    /**
     * Constructor for Block class
     * 
     * Must set roadSize after creating a new Block object
     */
    public Block() {
        path = new Path2D.Double();
    }
    
    /**
     * Constructor for Block class
     * 
     * @param roadSize - size of the stroke (don't have to set after creating object)
     */
    public Block(double roadSize) {
        this();
        this.roadSize = (float) roadSize;
    }

    /**
     * Constructor for Block class
     * 
     * @param roadSize - size of the stroke (don't have to set after creating object)
     */
    public Block(double roadSize, String streetName, int blockNumber) {
        this();
        this.roadSize = (float) roadSize;
        this.streetName = streetName;
        this.blockNumber = blockNumber;
    }

    /**
     * Creates a deep copy of this Block. 
     * @return a deep copy with the same attributes as this Block
     */
    public Block copy() {
        Block copy = new Block();
        copy.path = (Path2D) this.path.clone();
        copy.coordinatePoints = new ArrayList<>(this.coordinatePoints);
        copy.roadSize = this.roadSize;
        copy.firstEndpoint = this.firstEndpoint;
        copy.lastEndpoint = this.lastEndpoint;
        copy.length = this.length;
        copy.trafficFactor = this.trafficFactor;
        copy.traffic = this.traffic;
        copy.streetName = this.streetName;
        copy.blockNumber = this.blockNumber;
        return copy;
    }

    /* GUI attributes */
    public void startPoint(Coordinate coordinate) {
        coordinatePoints = new ArrayList<>();
        coordinatePoints.add(coordinate);
        path.moveTo(coordinate.getX(), coordinate.getY());
    }

    public void nextPoint(Coordinate coordinate) {
        coordinatePoints.add(coordinate);
        path.lineTo(coordinate.getX(), coordinate.getY());
    }

    public Path2D getPath() {
        return path;
    }

    public void setRoadSize(double roadSize) {
        this.roadSize = (float) roadSize;
    }

    public float getRoadSize() {
        return roadSize;
    }

    /* Measurable Attributes */
    public ArrayList<Coordinate> getCoordinatePoints() {
        return coordinatePoints;
    }

    public void setFirstEndpoint(Intersection firstEndpoint) {
        this.firstEndpoint = firstEndpoint;
    }

    public Intersection getFirstEndpoint() {
        return firstEndpoint;
    }

    public Intersection other(Intersection vertex) {
        if      (vertex.equals(firstEndpoint)) return lastEndpoint;
        else if (vertex.equals(lastEndpoint)) return firstEndpoint;
        else throw new IllegalArgumentException("Illegal endpoint");
    }

    public void setLastEndpoint(Intersection lastEndpoint) {
        this.lastEndpoint = lastEndpoint;
    }

    public Intersection getLastEndpoint() {
        return lastEndpoint;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public double getLength() {
        return length;
    }

    public void setTrafficFactor(double trafficFactor) {
        this.trafficFactor = trafficFactor;
    }

    public double getTrafficFactor() {
        return trafficFactor;
    }

    public void setTraffic(double traffic) {
        this.traffic = traffic;
    }

    public double getTraffic() {
        return traffic;
    }

    /* Nominal Attributes */
    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setBlockNumber(int blockNumber) {
        this.blockNumber = blockNumber;
    }

    public int getBlockNumber() {
        return blockNumber;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        result = prime * result + ((coordinatePoints == null) ? 0 : coordinatePoints.hashCode());
        result = prime * result + Float.floatToIntBits(roadSize);
        long temp;
        temp = Double.doubleToLongBits(length);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(trafficFactor);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(traffic);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((streetName == null) ? 0 : streetName.hashCode());
        result = prime * result + blockNumber;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Block other = (Block) obj;
        if (path == null) {
            if (other.path != null)
                return false;
        }
        if (coordinatePoints == null) {
            if (other.coordinatePoints != null)
                return false;
        } else if (!coordinatePoints.equals(other.coordinatePoints))
            return false;
        if (Float.floatToIntBits(roadSize) != Float.floatToIntBits(other.roadSize))
            return false;
        if (Double.doubleToLongBits(length) != Double.doubleToLongBits(other.length))
            return false;
        if (Double.doubleToLongBits(trafficFactor) != Double.doubleToLongBits(other.trafficFactor))
            return false;
        if (Double.doubleToLongBits(traffic) != Double.doubleToLongBits(other.traffic))
            return false;
        if (streetName == null) {
            if (other.streetName != null)
                return false;
        } else if (!streetName.equals(other.streetName))
            return false;
        if (blockNumber != other.blockNumber)
            return false;
        return true;
    }


    @Override
    public String toString() {
        return "Block [blockNumber=" + blockNumber+", streetName=" + streetName
                +", coordinatePoints=" + coordinatePoints.toString() + ", roadSize=" + roadSize
                + ", firstEndpoint=" + (firstEndpoint == null ? "null" : firstEndpoint.toString()) + ", lastEndpoint=" + (lastEndpoint == null ? "null" : lastEndpoint.toString()) + ", length=" + length
                + ", trafficFactor=" + trafficFactor + ", traffic=" + traffic +  "]";
    }

    public Block getNext() {
        return next;
    }
    public void setNext(Block next) {
        this.next = next;
    }
}
