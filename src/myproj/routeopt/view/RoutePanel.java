package myproj.routeopt.view;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RoutePanel extends JPanel {
    private List<Integer> route;
    private int vehicleId;
    private int vehicleCapacity;
    private int vehicleLoad;

    public RoutePanel(int vehicleId, int vehicleCapacity, int vehicleLoad, List<Integer> route) {
        this.vehicleId = vehicleId;
        this.vehicleCapacity = vehicleCapacity;
        this.vehicleLoad = vehicleLoad;
        this.route = route;
        setPreferredSize(new Dimension(600, 150));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int x = 20; // Starting x position
        int y = 60; // y position for route

        // Draw Vehicle Info at the top
        g2.drawString(String.format("Vehicle %d (Capacity: %d, Load: %d)", vehicleId, vehicleCapacity, vehicleLoad), x, 20);

        // Draw depot at the start
        drawLocation(g2, x, y, "Depot", Color.LIGHT_GRAY);
        x += 100;

        // Draw each location in the route
        int stopNumber = 1;
        for (Integer locationId : route) {
            drawArrow(g2, x - 50, y + 10, x, y + 10);
            drawLocation(g2, x, y, "Stop " + stopNumber + ": " + locationId, Color.CYAN);
            x += 100;
            stopNumber++;
        }

        // Draw arrow back to the depot
        drawArrow(g2, x - 50, y + 10, x, y + 10);
        drawLocation(g2, x, y, "Depot", Color.LIGHT_GRAY);
    }

    private void drawLocation(Graphics2D g2, int x, int y, String label, Color color) {
        g2.setColor(color);
        g2.fillRoundRect(x, y, 80, 30, 10, 10);
        g2.setColor(Color.BLACK);
        g2.drawRoundRect(x, y, 80, 30, 10, 10);
        g2.drawString(label, x + 10, y + 20);
    }

    private void drawArrow(Graphics2D g2, int x1, int y1, int x2, int y2) {
        g2.setColor(Color.BLACK);
        g2.drawLine(x1, y1, x2, y2);
        int arrowSize = 6;
        int dx = x2 - x1, dy = y2 - y1;
        double D = Math.sqrt(dx * dx + dy * dy);
        double xm = D - arrowSize, xn = xm, ym = arrowSize, yn = -arrowSize, x;
        double sin = dy / D, cos = dx / D;

        x = xm * cos - ym * sin + x1;
        ym = xm * sin + ym * cos + y1;
        xm = x;

        x = xn * cos - yn * sin + x1;
        yn = xn * sin + yn * cos + y1;
        xn = x;

        int[] xpoints = {x2, (int) xm, (int) xn};
        int[] ypoints = {y2, (int) ym, (int) yn};

        g2.fillPolygon(xpoints, ypoints, 3);
    }
}
