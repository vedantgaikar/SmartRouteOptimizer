package myproj.routeopt.view;

import myproj.SmartRouteOptimization;
import myproj.routeopt.controller.MainController;
import myproj.routeopt.model.Vehicle;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class MainView extends JFrame {
    private MainController controller;
    private JTextArea routeDisplay;
    private Color backgroundColor = new Color(204, 255, 204); // Light green
    private Font primaryFont = new Font("Apercu", Font.PLAIN, 14);
    private Color primaryColor = new Color(0, 4, 40); // Green
    private Color secondaryColor = new Color(139, 153, 223); // Light orange

    public MainView() {
        controller = new MainController();
        setupUI();
        setLocationRelativeTo(null); // Center the window on screen
    }

    private void setupUI() {
        setTitle("Route Optimization System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE); // Set background color

        mainPanel.add(createInitializationSection());
        mainPanel.add(createDeliverySection());
        mainPanel.add(createVehicleSection());
        mainPanel.add(createRouteDisplaySection());

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createInitializationSection() {
        JPanel panel = new JPanel(new FlowLayout());
        stylePanel(panel, "System Initialization");

        JTextField locationCount = createStyledTextField("Number of locations");
        JTextField depotLocation = createStyledTextField("Depot location");
        JTextField edgeCount = createStyledTextField("Number of edges");

        JButton initButton = createStyledButton("Initialize System");
        initButton.addActionListener(e -> {
            try {
                int locations = Integer.parseInt(locationCount.getText());
                int depot = Integer.parseInt(depotLocation.getText());
                controller.initializeSystem(locations, depot);

                int edges = Integer.parseInt(edgeCount.getText());
                for (int i = 0; i < edges; i++) {
                    showEdgeInputDialog();
                }
            } catch (NumberFormatException ex) {
                showError("Please enter valid numbers");
            }
        });

        panel.add(locationCount);
        panel.add(depotLocation);
        panel.add(edgeCount);
        panel.add(initButton);
        return panel;
    }

    private JPanel createDeliverySection() {
        JPanel panel = new JPanel(new FlowLayout());
        stylePanel(panel, "Add Delivery Points");

        JTextField dpId = createStyledTextField("Location ID");
        JTextField dpDemand = createStyledTextField("Demand");
        JTextField dpTimeStart = createStyledTextField("Time Window Start");
        JTextField dpTimeEnd = createStyledTextField("Time Window End");

        JButton addDeliveryButton = createStyledButton("Add Delivery Point");
        addDeliveryButton.addActionListener(e -> {
            try {
                int id = Integer.parseInt(dpId.getText());
                int demand = Integer.parseInt(dpDemand.getText());
                int timeStart = Integer.parseInt(dpTimeStart.getText());
                int timeEnd = Integer.parseInt(dpTimeEnd.getText());

                controller.addDeliveryPoint(id, demand, timeStart, timeEnd);
                clearFields(dpId, dpDemand, dpTimeStart, dpTimeEnd);
            } catch (NumberFormatException ex) {
                showError("Please enter valid numbers");
            }
        });

        panel.add(dpId);
        panel.add(dpDemand);
        panel.add(dpTimeStart);
        panel.add(dpTimeEnd);
        panel.add(addDeliveryButton);
        return panel;
    }

    private JPanel createVehicleSection() {
        JPanel panel = new JPanel(new FlowLayout());
        stylePanel(panel, "Add Vehicles");

        JTextField vehicleCapacity = createStyledTextField("Vehicle Capacity");
        JButton addVehicleButton = createStyledButton("Add Vehicle");

        addVehicleButton.addActionListener(e -> {
            try {
                int capacity = Integer.parseInt(vehicleCapacity.getText());
                controller.addVehicle(capacity);
                vehicleCapacity.setText("");
            } catch (NumberFormatException ex) {
                showError("Please enter a valid capacity");
            }
        });

        panel.add(vehicleCapacity);
        panel.add(addVehicleButton);
        return panel;
    }

    private JPanel routeDisplayPanel;

    private JPanel createRouteDisplaySection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Routes"));

        routeDisplayPanel = new JPanel();
        routeDisplayPanel.setLayout(new BoxLayout(routeDisplayPanel, BoxLayout.Y_AXIS));

        JButton optimizeButton = new JButton("Optimize Routes");
        optimizeButton.addActionListener(e -> {
            controller.optimizeRoutes();
            updateRouteDisplay();
        });

        panel.add(optimizeButton, BorderLayout.NORTH);
        panel.add(new JScrollPane(routeDisplayPanel), BorderLayout.CENTER);

        return panel;
    }


    private void showEdgeInputDialog() {
        JTextField fromLocation = new JTextField(5);
        JTextField toLocation = new JTextField(5);
        JTextField weight = new JTextField(5);

        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("From Location:"));
        panel.add(fromLocation);
        panel.add(new JLabel("To Location:"));
        panel.add(toLocation);
        panel.add(new JLabel("Weight:"));
        panel.add(weight);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Edge", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                int from = Integer.parseInt(fromLocation.getText());
                int to = Integer.parseInt(toLocation.getText());
                int w = Integer.parseInt(weight.getText());
                controller.addEdge(from, to, w);
            } catch (NumberFormatException ex) {
                showError("Please enter valid numbers");
            }
        }
    }

    private void updateRouteDisplay() {
        routeDisplayPanel.removeAll();
        try {
            StringBuilder displayText = new StringBuilder();
            SmartRouteOptimization system = controller.getSystem();
            Map<String, String> stats = controller.getSystemStats();
            displayText.append("System Statistics:\n");
            displayText.append(String.format("Total Capacity: %s\n", stats.get("Total Capacity")));
            displayText.append(String.format("Total Demand: %s\n", stats.get("Total Demand")));
            displayText.append(String.format("Total Vehicles: %s\n", stats.get("Total Vehicles")));
            displayText.append(String.format("Total Delivery Points: %s\n\n", stats.get("Total Delivery Points")));

            for (Vehicle vehicle : system.getVehicles()) {
                RoutePanel routePanel = new RoutePanel(
                        vehicle.getId(),
                        vehicle.getCapacity(),
                        vehicle.getCurrentLoad(),
                        vehicle.getRoute()
                );
                routeDisplayPanel.add(routePanel);
            }

            routeDisplayPanel.revalidate();
            routeDisplayPanel.repaint();

        } catch (IllegalStateException e) {
            showError("Optimization Error", e.getMessage());
        }
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(primaryColor);
        button.setForeground(Color.WHITE);
        button.setFont(primaryFont);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(secondaryColor, 1),
                BorderFactory.createEmptyBorder(8, 16, 8, 16) // Adjust padding here
        ));
        return button;
    }


    private JTextField createStyledTextField(String title) {
        JTextField textField = new JTextField(10);
        textField.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(secondaryColor), title));
        textField.setFont(primaryFont);
        return textField;
    }

    private void stylePanel(JPanel panel, String title) {
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(secondaryColor), title));
    }

    private String formatRoute(List<Integer> route) {
        StringBuilder routeStr = new StringBuilder("Depot");
        int stopNumber = 1;
        for (int locationId : route) {
            routeStr.append(String.format(" -> Stop %d: Location %d", stopNumber++, locationId));
        }
        routeStr.append(" -> Depot");
        return routeStr.toString();
    }

    private void showError(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void clearFields(JTextField... fields) {
        for (JTextField field : fields) {
            field.setText("");
        }
    }
}
