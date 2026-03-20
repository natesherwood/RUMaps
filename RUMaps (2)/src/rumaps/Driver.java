package rumaps;

import javax.swing.*;
import java.awt.*;
import java.util.*;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

/**
 * **RUN THIS FILE TO RUN THE APPLICATION**
 * 
 * The Driver class represents the main entry point of the RUMaps application.
 * It creates the main window, initializes the map panel, and handles user interactions.
 *
 * @author Vian Miranda
 * @author Anna Lu
 */
public class Driver {
    /**
     * NOTE TO STUDENTS: This is the seed which the Driver uses when setting the 
     * traffic factor. Feel free to change the seed to your choice, but remember 
     * that changing the seed will result in different outputs.
     */
    private final static int SEED = 2024;
    private String networkPath = "Busch.in";
    private final String BUSCH_PATH = "Busch.in";
    private final String COORDINATES_PATH = "AllCampuses.in";
    private final String[] imagePaths = {"assets/satellite.png", "assets/overlay.png"};
    private final double defaultOverlayTransparency = 0.125;
    private final String defaultStreetInfoString = "Hover over a block to see its street name and block number";

    private JFrame window;
    private MapPanel mapPanel;
    private RUMaps rumaps;

    private JLabel streetInfoLabel;
    private JLabel blockLengthLabel;
    private JLabel blockTrafficFactorLabel;
    private JLabel blockTrafficLabel;

    private String defaultBlockLengthValue;
    private String defaultBlockTrafficFactorValue;
    private String defaultBlockTrafficValue;

    private JLabel minIntersectionsLabel;
    private JLabel fastestPathLabel;
    private JLabel dfsLabel;


    private Driver() {
        StdRandom.setSeed(SEED);

        window = new JFrame();
        window.setLayout(new BorderLayout());
        window.setTitle("RU Maps");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mapPanel = new MapPanel(this, imagePaths, defaultOverlayTransparency) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(720, 677);
            }
        };
        
        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.setBackground(Color.DARK_GRAY);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        wrapperPanel.add(mapPanel, gbc);
        
        JScrollPane scrollPane = new JScrollPane(wrapperPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getViewport().setViewPosition(new Point(0, 0));
        
        scrollPane.setPreferredSize(new Dimension(600, 480));
        
        window.add(scrollPane, BorderLayout.CENTER);
        

        /* Control Panel */
        ToolTipManager.sharedInstance().setDismissDelay(60000);
        JPanel controlPanel = makeControlPanel();
        window.add(controlPanel, BorderLayout.SOUTH);

        window.setResizable(true);
        window.setVisible(true);
        window.pack();
    }

    private JPanel makeControlPanel() {
        JPanel container = new JPanel();
        container.setLayout(new BorderLayout());

        rumaps = new RUMaps(mapPanel, networkPath);

        JPanel streetInfo = makeStreetInfo();
        container.add(streetInfo, BorderLayout.NORTH);

        JPanel buttonPanel = makeMapOverlaySelect();
        container.add(buttonPanel, BorderLayout.SOUTH);

        return container;
    }

    private JPanel makeStreetInfo() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1));

        streetInfoLabel = new JLabel(defaultStreetInfoString, SwingConstants.CENTER);
        JPanel blockInfoPanel = makeBlockInfo();
        JPanel pathInfoPanel = makePathInfo();

        panel.add(streetInfoLabel);
        panel.add(blockInfoPanel);
        panel.add(pathInfoPanel);

        return panel;
    }

    private JPanel makeBlockInfo() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1));

        blockLengthLabel = new JLabel();
        blockLengthLabel.setHorizontalAlignment(SwingConstants.CENTER);

        blockTrafficFactorLabel = new JLabel();
        blockTrafficFactorLabel.setHorizontalAlignment(SwingConstants.CENTER);

        blockTrafficLabel = new JLabel();
        blockTrafficLabel.setHorizontalAlignment(SwingConstants.CENTER);

        defaultBlockLengthValue = "N/A";
        defaultBlockTrafficFactorValue = "N/A";
        defaultBlockTrafficValue = "N/A";

        updateStreetInfo(null);

        panel.add(blockLengthLabel);
        panel.add(blockTrafficFactorLabel);
        panel.add(blockTrafficLabel);

        return panel;
    }

    private JPanel makePathInfo() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1));

        minIntersectionsLabel = new JLabel();
        minIntersectionsLabel.setHorizontalAlignment(SwingConstants.CENTER);

        fastestPathLabel = new JLabel();
        fastestPathLabel.setHorizontalAlignment(SwingConstants.CENTER);

        dfsLabel = new JLabel();
        dfsLabel.setHorizontalAlignment(SwingConstants.CENTER); 

        updatePathInfo(null, null);
        panel.add(dfsLabel);

        panel.add(minIntersectionsLabel);
        panel.add(fastestPathLabel);

        return panel;
    }

    private void switchMap(MapPanel newMapPanel) {
        Container parent = mapPanel.getParent();
        
        parent.remove(mapPanel);
        
        mapPanel = newMapPanel;
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        parent.add(mapPanel, gbc);
    
        window.revalidate();
        window.repaint();
    }

    private JPanel makeMapOverlaySelect() {
        JPanel completePanel = new JPanel();
        completePanel.setLayout(new GridLayout(3, 1));
        JLabel overlayLabel = new JLabel("Map Settings", SwingConstants.CENTER);
        overlayLabel.setFont(new Font("Arial", Font.BOLD, 14));
        completePanel.add(overlayLabel, BorderLayout.NORTH);

        JPanel networkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        ButtonGroup networkGroup = new ButtonGroup();
        JRadioButton buschButton = new JRadioButton("Busch", true);
        JRadioButton coordinatesButton = new JRadioButton("All Campuses", false);
        networkGroup.add(buschButton);
        networkGroup.add(coordinatesButton);

        coordinatesButton.addActionListener(e -> {
            if (coordinatesButton.isSelected()) {
                MapPanel newMapPanel = new MapPanel(this, imagePaths, defaultOverlayTransparency) {
                    @Override
                    public Dimension getPreferredSize() {
                        return new Dimension(720, 677);
                    }
                };
                networkPath = COORDINATES_PATH;
                rumaps = new RUMaps(newMapPanel, networkPath);
                switchMap(newMapPanel);
            }
        });
        
        buschButton.addActionListener(e -> {
            if (buschButton.isSelected()) {
                MapPanel newMapPanel = new MapPanel(this, imagePaths, defaultOverlayTransparency) {
                    @Override
                    public Dimension getPreferredSize() {
                        return new Dimension(720, 677);
                    }
                };
                networkPath = BUSCH_PATH;
                StdRandom.setSeed(SEED);
                rumaps = new RUMaps(newMapPanel, networkPath);
                switchMap(newMapPanel);
            }
        });

        networkPanel.add(coordinatesButton);
        networkPanel.add(buschButton);
        completePanel.add(networkPanel);

        JPanel containerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));


        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2,2));

        JCheckBox satellite = new JCheckBox("Satellite", true);
        JCheckBox network = new JCheckBox("Street Network", true);
        JCheckBox minimize = new JCheckBox("Minimize Intersections", true);
        minimize.setForeground(mapPanel.minimizeIntersectionPathColor);
        JCheckBox fast = new JCheckBox("Fastest Path", true);
        fast.setForeground(mapPanel.fastestPathColor);
        JPanel transparencyPanel = makeTransparencySlider();

        satellite.addActionListener(e -> {
            if (satellite.isSelected()) {
                mapPanel.enableSatellite();
            } else {
                mapPanel.disableSatellite();
            }
        });

        network.addActionListener(e -> mapPanel.toggleNetworkVisuals(network.isSelected()));
        minimize.addActionListener(e -> mapPanel.toggleMinimizeIntersectionPath(minimize.isSelected()));
        fast.addActionListener(e -> mapPanel.toggleFastestPath(fast.isSelected()));

        panel.add(satellite);
        panel.add(network);
        panel.add(minimize);
        panel.add(fast);

        containerPanel.add(panel);
        completePanel.add(containerPanel);
        completePanel.add(transparencyPanel);

        return completePanel;
    }

    private JPanel makeTransparencySlider() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        JLabel label = new JLabel("Overlay: " + (int) (defaultOverlayTransparency * 100) + "%");
        JSlider slider = new JSlider(0, 100, (int) (defaultOverlayTransparency * 100));
        slider.addChangeListener(e -> {
            label.setText("Overlay: " + slider.getValue() + "%");
            double transparency = slider.getValue() / 100.0;
            mapPanel.changeTransparency(transparency);
        });

        panel.add(label);
        panel.add(slider);
        return panel;
    }

    public void updateStreetInfo(Block block) {
        if (block == null) {
            streetInfoLabel.setText(defaultStreetInfoString);
            streetInfoLabel.setForeground(new Color(128, 128, 128, 150));
            streetInfoLabel.setFont(new Font("Dialog", Font.ITALIC, 17));

            blockLengthLabel.setText("<html><b>Length:</b> " + defaultBlockLengthValue);
            blockTrafficFactorLabel.setText("<html><b>Traffic Factor:</b> " + defaultBlockTrafficFactorValue);
            blockTrafficLabel.setText("<html><b>Traffic:</b> " + defaultBlockTrafficValue);
        } else {
            streetInfoLabel.setText(String.format("%s (Block %s)", block.getStreetName(), block.getBlockNumber()));
            streetInfoLabel.setForeground(new Color(31, 31, 31, 200));
            streetInfoLabel.setFont(new Font("Dialog", Font.BOLD, 17));

            blockLengthLabel.setText(String.format("<html><b>Length:</b> %.2f", block.getLength()));
            blockTrafficFactorLabel.setText(String.format("<html><b>Traffic Factor:</b> %.3f", block.getTrafficFactor()));
            blockTrafficLabel.setText(String.format("<html><b>Traffic:</b> %.2f", block.getTraffic()));
        }
    }

    public void updatePathInfo(Intersection start, Intersection end) {
        if (start == null && end == null) {
            defaultBlockLengthValue = "N/A";
            defaultBlockTrafficFactorValue = "N/A";
            defaultBlockTrafficValue = "N/A";
            updateStreetInfo(null);

            dfsLabel.setText("<html><b>Reachable Intersections (DFS):</b>  N/A");
            dfsLabel.setToolTipText(null);


            minIntersectionsLabel.setText("<html><b>Minimize Intersections (BFS):</b>  N/A");
            minIntersectionsLabel.setToolTipText(null);

            fastestPathLabel.setText("<html><b>Fastest Path:</b>  N/A");
            fastestPathLabel.setToolTipText(null);


            mapPanel.highlightMinimizeIntersectionPath(new ArrayList<>());
            mapPanel.highlightedFastestPath(new ArrayList<>());
        } else if (start != null && end == null) {
            defaultBlockLengthValue = "N/A";
            defaultBlockTrafficFactorValue = "N/A";
            defaultBlockTrafficValue = "N/A";
            updateStreetInfo(null);

            ArrayList<Intersection> dfsPath = rumaps.reachableIntersections(start);
            String[] formattedDfsPaths = formatDFSPath(dfsPath);
            dfsLabel.setText("<html><b>Reachable Intersections (DFS):</b>  " + formattedDfsPaths[0]);
            dfsLabel.setToolTipText(String.format("<html><span style=\"color:rgb(%s, %s, %s)\"> %s",
                mapPanel.minimizeIntersectionPathColor.getRed(), mapPanel.minimizeIntersectionPathColor.getGreen(), 
                mapPanel.minimizeIntersectionPathColor.getBlue(), formattedDfsPaths[1]));
            dfsLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        StringSelection stringSelection = new StringSelection(formattedDfsPaths[1].replace("<br>", "").replace("<em>", "").replace("<em/>", ""));
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(stringSelection, null); 
                    }
                });
            minIntersectionsLabel.setText("<html><b>Minimize Intersections (BFS):</b>  N/A");
            minIntersectionsLabel.setToolTipText(null);

            fastestPathLabel.setText("<html><b>Fastest Path:</b>  N/A");
            fastestPathLabel.setToolTipText(null);


            mapPanel.highlightMinimizeIntersectionPath(new ArrayList<>());
            mapPanel.highlightedFastestPath(new ArrayList<>());
        }
        else {
            ArrayList<Intersection> dfsPath = rumaps.reachableIntersections(start);
            ArrayList<Intersection> minPath = rumaps.minimizeIntersections(start, end);
            ArrayList<Intersection> fastPath = rumaps.fastestPath(start, end);

            double[] minPathInfo = rumaps.pathInformation(minPath);
            double[] fastPathInfo = rumaps.pathInformation(fastPath);
            defaultBlockLengthValue = String.format(
                "<span style=\"color:rgb(%s,%s,%s);\">%.2f</span>, <span style=\"color:rgb(%s,%s,%s);\">%.2f</span>", 
                mapPanel.minimizeIntersectionPathColor.getRed(), mapPanel.minimizeIntersectionPathColor.getGreen(), 
                mapPanel.minimizeIntersectionPathColor.getBlue(), minPathInfo[0], mapPanel.fastestPathColor.getRed(), 
                mapPanel.fastestPathColor.getGreen(), mapPanel.fastestPathColor.getBlue(), fastPathInfo[0]);
            defaultBlockTrafficFactorValue = String.format(
                "<span style=\"color:rgb(%s,%s,%s);\">%.3f</span>, <span style=\"color:rgb(%s,%s,%s);\">%.3f</span>", 
                mapPanel.minimizeIntersectionPathColor.getRed(), mapPanel.minimizeIntersectionPathColor.getGreen(), 
                mapPanel.minimizeIntersectionPathColor.getBlue(), minPathInfo[1], mapPanel.fastestPathColor.getRed(), 
                mapPanel.fastestPathColor.getGreen(), mapPanel.fastestPathColor.getBlue(), fastPathInfo[1]);
            defaultBlockTrafficValue = String.format(
                "<span style=\"color:rgb(%s,%s,%s);\">%.2f</span>, <span style=\"color:rgb(%s,%s,%s);\">%.2f</span>", 
                mapPanel.minimizeIntersectionPathColor.getRed(), mapPanel.minimizeIntersectionPathColor.getGreen(), 
                mapPanel.minimizeIntersectionPathColor.getBlue(), minPathInfo[2], mapPanel.fastestPathColor.getRed(), 
                mapPanel.fastestPathColor.getGreen(), mapPanel.fastestPathColor.getBlue(), fastPathInfo[2]);

            updateStreetInfo(null);

            String[] formattedDfsPaths = formatDFSPath(dfsPath);
            String[] formattedMinPaths = formatPath(minPath);
            String[] formattedFastPaths = formatPath(fastPath);

            dfsLabel.setText("<html><b>Reachable Intersections (DFS):</b>  " + formattedDfsPaths[0]);
            dfsLabel.setToolTipText(String.format("<html><span style=\"color:rgb(%s, %s, %s)\"> %s",
                mapPanel.minimizeIntersectionPathColor.getRed(), mapPanel.minimizeIntersectionPathColor.getGreen(), 
                mapPanel.minimizeIntersectionPathColor.getBlue(), formattedDfsPaths[1]));
                if (dfsLabel.getMouseListeners().length > 0) {
                    dfsLabel.removeMouseListener(dfsLabel.getMouseListeners()[0]);
                }
                dfsLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        StringSelection stringSelection = new StringSelection(formattedDfsPaths[1].replace("<br>", "").replace("<em>", "").replace("<em/>", ""));
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(stringSelection, null);  
                    }
                });
                minIntersectionsLabel.setText("<html><b>Minimize Intersections (BFS):</b>  " + formattedMinPaths[0]);
                minIntersectionsLabel.setToolTipText(String.format("<html><span style=\"color:rgb(%s, %s, %s)\"> %s",
                    mapPanel.minimizeIntersectionPathColor.getRed(), mapPanel.minimizeIntersectionPathColor.getGreen(), 
                    mapPanel.minimizeIntersectionPathColor.getBlue(), formattedMinPaths[1]));
                if (minIntersectionsLabel.getMouseListeners().length > 0) {
                    minIntersectionsLabel.removeMouseListener(minIntersectionsLabel.getMouseListeners()[0]);
                }
                
                minIntersectionsLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        StringSelection stringSelection = new StringSelection(formattedMinPaths[1].replace("<br>", "").replace("<em>", "").replace("<em/>", ""));
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(stringSelection, null); 
                    }
                });
    
                fastestPathLabel.setText("<html><b>Fastest Path:</b>  " + formattedFastPaths[0]);
                fastestPathLabel.setToolTipText(String.format("<html><span style=\"color:rgb(%s, %s, %s)\"> %s",
                    mapPanel.fastestPathColor.getRed(), mapPanel.fastestPathColor.getGreen(), 
                    mapPanel.fastestPathColor.getBlue(),formattedFastPaths[1]));
                if (fastestPathLabel.getMouseListeners().length > 0) {
                    fastestPathLabel.removeMouseListener(dfsLabel.getMouseListeners()[0]);
                }
                fastestPathLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        StringSelection stringSelection = new StringSelection(formattedFastPaths[1].replace("<br>", "").replace("<em>", "").replace("<em/>", ""));
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(stringSelection, null); 
                    }
                });
    
                mapPanel.highlightMinimizeIntersectionPath(minPath);
                mapPanel.highlightedFastestPath(fastPath);
        }
    }

    private String[] formatDFSPath(ArrayList<Intersection> path) {
        if (path == null || path.size() < 2) {
            return new String[] {"N/A", null};
        }

        StringBuilder sb = new StringBuilder();
        int lineCount = 0, index = 0;

        for (Intersection inter : path) {
            sb.append(inter.getCoordinate().toString() + " ");
            if (index < path.size() - 1) {
                sb.append("-> ");
            }
            index++;
            if (sb.length() / 120 > lineCount) {
                sb.append("<br>");
                lineCount = sb.length() / 120;
            }
        }
        int displayLen = Math.min(50, sb.length());
        sb.append("<br><em>total ").append(index).append(index == 1 ? " intersection (click to copy)</em>" : " intersections (click to copy)</em>");
    
        StringBuilder display = new StringBuilder();
        display.append(sb.substring(0, displayLen)).append("...");
    
        return new String[] {display.toString(), sb.toString()}; 
    }

    private String[] formatPath(ArrayList<Intersection> path) {
        if (path == null || path.size() < 2) {
            return new String[] {"N/A", null};
        }
    
        StringBuilder sb = new StringBuilder();
        int lineCount = 0, index = 0;
    
        for (int i = 0; i < path.size() - 1; i++) {
            Intersection current = path.get(i);
            Intersection next = path.get(i + 1);
    
            // Find the incident block between the current and next intersections
            Block block = rumaps.getRutgers().adj(rumaps.getRutgers().findIntersection(current.getCoordinate().getX(), current.getCoordinate().getY()));
            while (block != null) {
                if ((block.getFirstEndpoint().equals(current) && block.getLastEndpoint().equals(next)) ||
                    (block.getFirstEndpoint().equals(next) && block.getLastEndpoint().equals(current))) {
                    sb.append(block.getStreetName()).append(" (Block ").append(block.getBlockNumber()).append(") ");
                    break;
                }
                block = block.getNext();
            }
    
            index++;
            if (i < path.size() - 2) {
                sb.append("-> ");
            }
    
            if (sb.length() / 120 > lineCount) {
                sb.append("<br>");
                lineCount = sb.length() / 120;
            }
        }
    
        int displayLen = Math.min(50, sb.length());
        sb.append("<br><em>total ").append(index).append(index == 1 ? " intersection (click to copy)</em>" : " intersections (click to copy)</em>");
    
        StringBuilder display = new StringBuilder();
        display.append(sb.substring(0, displayLen)).append("...");
    
        return new String[] {display.toString(), sb.toString()};
    }

    public RUMaps getMaps() {
        return rumaps;
    }

    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.err.println("Cannot set look and feel; falling back on default.");
            }
            new Driver();
        });
    }
}