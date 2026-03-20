package rumaps;

import java.util.*;

/**
 * The Intersection class is used to represent a street intersection.
 * It contains the following attributes:
 * - coordinate - coordinate of the intersection
 * - blocks - list of intersecting blocks
 * 
 * @author Vian Miranda
 * @author Anna Lu
 */
public class Intersection {
    private Coordinate coordinate;

    public Intersection(Coordinate coordinate) {
        this.coordinate = coordinate;   
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((coordinate == null) ? 0 : coordinate.hashCode());
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
        Intersection other = (Intersection) obj;
        if (coordinate == null) {
            if (other.coordinate != null)
                return false;
        } else if (!coordinate.equals(other.coordinate))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "[coordinate=" + coordinate.toString() + "]";
    }
}

