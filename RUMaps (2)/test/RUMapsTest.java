package test;

import static org.junit.Assert.*;
import org.junit.*;
import rumaps.*;
import java.util.ArrayList;

/**
 * This is an optional JUnit test class for the RUMaps class.
 * You can implement the test cases below to verify the functionality of the RUMaps class. 
 */
public class RUMapsTest {

    // All tests will use the Busch.in input file since it is smaller and easier to debug
    private static final String TEST_FILE = "Busch.in"; 
     
    @Test
    public void testInitializeBlocksAndIntersections() {
        RUMaps testRUMaps = new RUMaps(TEST_FILE); 
        Network testNetwork = testRUMaps.getRutgers();

        Intersection[] intersections = testNetwork.getIntersections();
        Block[] blocks = testNetwork.getAdjacencyList();
        // Add assertions to verify (at least some of) the behaviour of initializeBlocks and initializeIntersections  

    }

    @Test
    public void testBlockLength() { 
        RUMaps testRUMaps = new RUMaps(TEST_FILE); 
        Network testNetwork = testRUMaps.getRutgers();
 
        Block[] blocks = testNetwork.getAdjacencyList();
        // Add assertions to verify the behavior of blockLength 
        
    }

    @Test
    public void testReachableIntersections() { 
        RUMaps testRUMaps = new RUMaps(TEST_FILE);
        Network testNetwork = testRUMaps.getRutgers();

        Intersection[] intersections = testNetwork.getIntersections(); 
        // Add assertions to verify the behavior of reachableIntersections 

    }

    @Test
    public void testMinimizeIntersections() { 
        RUMaps testRUMaps = new RUMaps(TEST_FILE); 
        Network testNetwork = testRUMaps.getRutgers();

        Intersection[] intersections = testNetwork.getIntersections(); 
        // Add assertions to verify the behavior of minimizeIntersections  

    }

    @Test
    public void testFastestPath() { 
        RUMaps testRUMaps = new RUMaps(TEST_FILE); 
        Network testNetwork = testRUMaps.getRutgers();

        Intersection[] intersections = testNetwork.getIntersections(); 
        // Add assertions to verify the behavior of fastestPath 

    }

    @Test
    public void testPathInformation() { 
        RUMaps testRUMaps = new RUMaps(TEST_FILE); 
        Network testNetwork = testRUMaps.getRutgers();

        Intersection[] intersections = testNetwork.getIntersections(); 
        // Add assertions to verify the behavior of pathInformation 

    }
}
