package rumaps;

/**
 * The Coordinate class is used to represent a coordinate in the map and stores x and y values.
 * It contains the following attributes:
 * - x value of the coordinate (horizontal, left to right)
 * - y value of the coordinate (vertical, top to bottom)
 * 
 * @author Vian Miranda
 * @author Anna Lu
 */
public class Coordinate {
    private int x;
    private int y;

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
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
        Coordinate other = (Coordinate) obj;
        if (this.hashCode() != other.hashCode())
            return false;
        if (x != other.getX())
            return false;
        if (y != other.getY())
            return false;
        return true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("(").append(x).append(", ").append(y).append(")");

        return sb.toString();
    }
}
