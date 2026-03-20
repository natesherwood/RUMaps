package rumaps;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * The MapPanel class is used by the Driver class to create the main map panel in the application.
 * 
 * @author Vian Miranda
 * @author Anna Lu
 */
public class MapPanel extends JPanel {
    private final int WIDTH = 720;
    private final int HEIGHT = 677;

    private final Color backgroundColor = Color.BLACK;

    public final Color minimizeIntersectionPathColor = Color.red;
    private final Color hoverMinimizeIntersectionPathColor = new Color(255, 0, 0, 125);

    public final Color fastestPathColor = Color.blue;
    private final Color hoverFastestPathColor = new Color(0, 0, 255, 125);

    private final Color combinedPathColor = new Color(189, 0, 189);
    private final Color hoverCombinedPathColor = new Color(189, 0, 189, 125);

    private final Color defaultBlockColor = Color.WHITE;
    private final Color hoverDefaultBlockColor = new Color(210,212,213, 150);

    private Driver driver;
    private String[] imagePaths;
    private Image satelliteImage;
    private Image overlayImage;
    private double overlayTransparency;
    private boolean networkVisualsActivated;

    private List<Block> blocks;
    private HashSet<Block> highlightedMinimizeIntersectionPath;
    private boolean highlightedMinimizeIntersectionPathActivated;
    private HashSet<Block> highlightedFastestPath;
    private boolean highlightedFastestPathActivated;
    private Block highlightedBlock;

    private List<Intersection> intersections;
    private Intersection selectedStartIntersection;
    private Intersection selectedEndIntersection;
    private Intersection highlightedIntersection;

    public MapPanel(Driver driver, String[] imagePaths, double overlayTransparency) {
        this.driver = driver;
        
        setBackground(backgroundColor);
        this.imagePaths = imagePaths;
        satelliteImage = new ImageIcon(imagePaths[0]).getImage();
        overlayImage = new ImageIcon(imagePaths[1]).getImage();
        this.overlayTransparency = overlayTransparency;
        networkVisualsActivated = true;
        
        blocks = new ArrayList<>();
        highlightedMinimizeIntersectionPath = new HashSet<>();
        highlightedMinimizeIntersectionPathActivated = true;
        highlightedFastestPath = new HashSet<>();
        highlightedFastestPathActivated = true;

        intersections = new ArrayList<>();
        mouseListener();
    }

    public MapPanel(String[] imagePaths, double overlayTransparency) {
        this(null, imagePaths, overlayTransparency);
    }

    /* Map Visuals */
    public void enableSatellite() {
        satelliteImage = new ImageIcon(imagePaths[0]).getImage();
        repaint();
    }

    public void disableSatellite() {
        satelliteImage = null;
        repaint();
    }

    public void changeTransparency(double transparancy) {
        overlayTransparency = transparancy;
        repaint();
    }

    public void toggleNetworkVisuals(boolean activate) {
        this.networkVisualsActivated = activate;
        repaint();
    } 

    /* Block Visuals */
    public void addBlock(Block block) {
        blocks.add(block);
        repaint();
    }

    public List<Block> getBlocks() {
        return blocks;
    }
    
    public void highlightBlock(Block block) {
        highlightedBlock = block;
        if (driver != null) driver.updateStreetInfo(highlightedBlock);
        revalidate();
        repaint();
    }

    public void highlightMinimizeIntersectionPath(List<Intersection> path) {
        HashSet<Block> blocksInPath = new HashSet<>();
        for (int i = 0; i < path.size() - 1; i++) {
            Intersection current = path.get(i);
            Intersection next = path.get(i + 1);
    
            // Finds the block connecting the current and next intersections
            Block block = driver.getMaps().getRutgers().adj(driver.getMaps().getRutgers().findIntersection(current.getCoordinate().getX(), current.getCoordinate().getY()));
            while (block != null) {
                if ((block.getFirstEndpoint().equals(current) && block.getLastEndpoint().equals(next)) ||
                    (block.getFirstEndpoint().equals(next) && block.getLastEndpoint().equals(current))) {
                    blocksInPath.add(block);
                    break;
                }
                block = block.getNext();
            }
        }
    
        highlightedMinimizeIntersectionPath = blocksInPath;
        repaint();
    }

    public void toggleMinimizeIntersectionPath(boolean activate) {
        this.highlightedMinimizeIntersectionPathActivated = activate;
        repaint();
    }

    public void highlightedFastestPath (List<Intersection> path) {
        HashSet<Block> blocksInPath = new HashSet<>();
        for (int i = 0; i < path.size() - 1; i++) {
            Intersection current = path.get(i);
            Intersection next = path.get(i + 1);
    
            // Finds the block connecting the current and next intersections
            Block block = driver.getMaps().getRutgers().adj(driver.getMaps().getRutgers().findIntersection(current.getCoordinate().getX(), current.getCoordinate().getY()));
            while (block != null) {
                if ((block.getFirstEndpoint().equals(current) && block.getLastEndpoint().equals(next)) ||
                    (block.getFirstEndpoint().equals(next) && block.getLastEndpoint().equals(current))) {
                    blocksInPath.add(block);
                    break;
                }
                block = block.getNext();
            }
        }
        highlightedFastestPath = new HashSet<>(blocksInPath);
        repaint();
    }

    public void toggleFastestPath(boolean activate) {
        this.highlightedFastestPathActivated = activate;
        repaint();
    }

    /* Intersection Visuals */
    public void addIntersection(Intersection intersection) {
        intersections.add(intersection);;
        repaint();
    }

    public void highlightIntersection(Intersection intersection) {
        highlightedIntersection = intersection;
        repaint();
    }

    public void selectIntersection(Intersection intersection) {
        if (intersection == null) {
            selectedStartIntersection = null;
            selectedEndIntersection = null;
        } else {
            if (selectedStartIntersection != null && selectedEndIntersection != null) {
                selectedStartIntersection = null;
                selectedEndIntersection = null;
            }
            if (selectedStartIntersection == null) {
                selectedStartIntersection = intersection;
            } else if (selectedEndIntersection == null) {
                if (selectedStartIntersection.equals(intersection)) {
                    return;
                }
                selectedEndIntersection = intersection;
            }
        }
        if (driver != null) driver.updatePathInfo(selectedStartIntersection, selectedEndIntersection);
    }

    private void mouseListener() {
        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                final int x = e.getX();
                final int y = e.getY();
                Block newHighlightedBlock = null;
                for (Block block : blocks) {
                    BasicStroke stroke = new BasicStroke(Math.max(10, block.getRoadSize() * 2));
                    Shape strokedShape = stroke.createStrokedShape(block.getPath());
                    
                    if (strokedShape.contains(x, y)) {
                        newHighlightedBlock = block;
                        break;
                    }
                }
                if (newHighlightedBlock != highlightedBlock) {
                    highlightBlock(newHighlightedBlock);
                }

                Intersection newHighlightedIntersection = null;
                for (Intersection intersection : intersections) {
                    if (Math.abs(intersection.getCoordinate().getX() - x) <= 5 &&
                        Math.abs(intersection.getCoordinate().getY() - y) <= 5) {
                        newHighlightedIntersection = intersection;
                        break;
                    }
                }

                if (newHighlightedIntersection != highlightedIntersection) {
                    highlightIntersection(newHighlightedIntersection);
                }
            }
        });

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    // Left mouse button was clicked
                    int x = e.getX();
                    int y = e.getY();

                    Intersection newSelectedIntersection = null;
                    for (Intersection intersection : intersections) {
                        if (Math.abs(intersection.getCoordinate().getX() - x) <= 5 &&
                            Math.abs(intersection.getCoordinate().getY() - y) <= 5) {
                            newSelectedIntersection = intersection;
                            break;
                        }
                    }

                    if (newSelectedIntersection != null) {
                        selectIntersection(newSelectedIntersection);
                    }
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    // Right mouse button was clicked
                    selectIntersection(null);
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(satelliteImage, 0, 0, WIDTH, HEIGHT, this);
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) overlayTransparency));
        g2d.drawImage(overlayImage, 0, 0, WIDTH, HEIGHT, this);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        
        if (networkVisualsActivated) {
            for (Block block : blocks) {
                if ((highlightedMinimizeIntersectionPathActivated && highlightedMinimizeIntersectionPath.contains(block)) ||
                    (highlightedFastestPathActivated && highlightedFastestPath.contains(block))) 
                    continue;

                g2d.setStroke(new BasicStroke(block.getRoadSize()));
                if (block.equals(highlightedBlock)) {
                    g2d.setColor(hoverDefaultBlockColor);     
                } else {
                    g2d.setColor(defaultBlockColor);
                }
                g2d.draw(block.getPath());
            }
        }

        if (highlightedMinimizeIntersectionPathActivated) {
            for (Block block : highlightedMinimizeIntersectionPath) {
                if (highlightedFastestPathActivated && highlightedFastestPath.contains(block)) 
                    continue;
                if (block.equals(highlightedBlock))
                    g2d.setColor(hoverMinimizeIntersectionPathColor);
                else 
                    g2d.setColor(minimizeIntersectionPathColor);
                g2d.setStroke(new BasicStroke(block.getRoadSize()));
                g2d.draw(block.getPath());
            }
        }

        if (highlightedFastestPathActivated) {
            for (Block block : highlightedFastestPath) {
                if (highlightedMinimizeIntersectionPathActivated && highlightedMinimizeIntersectionPath.contains(block)) { 
                    if (block.equals(highlightedBlock))
                        g2d.setColor(hoverCombinedPathColor);
                    else
                        g2d.setColor(combinedPathColor);
                } else if (block.equals(highlightedBlock))
                    g2d.setColor(hoverFastestPathColor);
                else 
                    g2d.setColor(fastestPathColor);
                g2d.setStroke(new BasicStroke(block.getRoadSize()));
                g2d.draw(block.getPath());
            }
        }
        if (networkVisualsActivated && highlightedBlock != null) {
            g2d.setStroke(new BasicStroke(highlightedBlock.getRoadSize()));
            g2d.setColor(hoverDefaultBlockColor);
            g2d.draw(highlightedBlock.getPath());
        }
        // Separate from first networkVisualsActivated if clause so that intersection points lie on top of the blocks
        if (networkVisualsActivated) {
            for (Intersection intersection : intersections) {
                g2d.setColor(new Color(255, 255, 255, 210));
                g2d.fillOval(intersection.getCoordinate().getX() - 5, intersection.getCoordinate().getY() - 5, 10, 10);
                if (intersection.equals(highlightedIntersection)) {
                    if (selectedStartIntersection == intersection || selectedEndIntersection == intersection)
                        g2d.setColor(new Color(15, 83, 255, 150));            
                    else 
                        g2d.setColor(new Color(0,0,0, 100));
                } else if (selectedStartIntersection == intersection || selectedEndIntersection == intersection) {
                    g2d.setColor(new Color(15, 83, 255));
                    g2d.fillOval(intersection.getCoordinate().getX() - 4, intersection.getCoordinate().getY() - 4, 8, 8);
                    continue;
                } else {
                    g2d.setColor(Color.BLACK);
                }
                g2d.fillOval(intersection.getCoordinate().getX() - 4, intersection.getCoordinate().getY() - 4, 8, 8);
                g2d.setColor(Color.WHITE);
                g2d.fillOval(intersection.getCoordinate().getX() - 1, intersection.getCoordinate().getY() - 1, 2, 2);
            }
        }


    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(WIDTH, HEIGHT);
    }
}
