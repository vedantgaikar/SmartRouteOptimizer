package myproj.routeopt.controller;

import myproj.SmartRouteOptimization;
import myproj.routeopt.model.DeliveryPoint;
import myproj.routeopt.model.Graph;
import myproj.routeopt.model.Vehicle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainController {
    private SmartRouteOptimization system;
    private List<DeliveryPoint> deliveryPoints;
    private List<Vehicle> vehicles;

    public MainController() {
        deliveryPoints = new ArrayList<>();
        vehicles = new ArrayList<>();
    }

    public void initializeSystem(int locations, int depot) {
        Graph cityGraph = new Graph(locations);
        system = new SmartRouteOptimization(cityGraph, depot);
    }

    public void addEdge(int u, int v, int weight) {
        system.getCityGraph().addEdge(u, v, weight);
    }

    public void addDeliveryPoint(int id, int demand, int timeStart, int timeEnd) {
        DeliveryPoint dp = new DeliveryPoint(id, demand, timeStart, timeEnd);
        deliveryPoints.add(dp);
        system.addDeliveryPoint(id, demand, timeStart, timeEnd);
        System.out.println("Added Delivery Point: " + id + ", Demand: " + demand);
    }

    public void addVehicle(int capacity) {
        int id = vehicles.size();
        Vehicle vehicle = new Vehicle(id, capacity);
        vehicles.add(vehicle);
        system.addVehicle(id, capacity);
        System.out.println("Added Vehicle: " + id + ", Capacity: " + capacity);
    }

    public void optimizeRoutes() throws IllegalStateException {
        // Validate system state
        if (system == null) {
            throw new IllegalStateException("System not initialized");
        }
        if (deliveryPoints.isEmpty()) {
            throw new IllegalStateException("No delivery points added");
        }
        if (vehicles.isEmpty()) {
            throw new IllegalStateException("No vehicles added");
        }

        // Check capacity before attempting optimization
        if (!system.validateCapacity()) {
            throw new IllegalStateException("Total vehicle capacity insufficient for delivery demands. " +
                    "Please add more vehicles or reduce delivery demands.");
        }

        try {
            system.assignDeliveries();
            system.optimizeRoutes();
            printDetailedStatistics();
        } catch (Exception e) {
            System.err.println("Error during optimization: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Updated method to get system statistics
    public Map<String, String> getSystemStats() {
        Map<String, String> stats = new HashMap<>();
        if (system != null) {
            Map<String, Integer> deliveryStats = system.getDeliveryStats();
            stats.put("Total Delivery Points", String.valueOf(deliveryStats.get("totalDeliveryPoints")));
            stats.put("Total Demand", String.valueOf(deliveryStats.get("totalDemand")));
            stats.put("Total Vehicles", String.valueOf(deliveryStats.get("totalVehicles")));
            stats.put("Total Capacity", String.valueOf(deliveryStats.get("totalCapacity")));

            // Add detailed vehicle statistics
            List<Vehicle> vehicles = system.getVehicles();
            for (Vehicle v : vehicles) {
                stats.put("Vehicle_" + v.getId() + "_Load", String.valueOf(v.getCurrentLoad()));
                stats.put("Vehicle_" + v.getId() + "_Route", formatRoute(v.getRoute()));
            }
        }
        return stats;
    }

    // Helper method to format route
    private String formatRoute(List<Integer> route) {
        if (route == null || route.isEmpty()) {
            return "Empty Route";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < route.size(); i++) {
            if (i > 0) {
                sb.append(" -> ");
            }
            sb.append(route.get(i));
        }
        return sb.toString();
    }

    // New method to print detailed statistics
    public void printDetailedStatistics() {
        System.out.println("\nSystem Statistics:");
        Map<String, String> stats = getSystemStats();
        System.out.println("Total Capacity: " + stats.get("Total Capacity"));
        System.out.println("Total Demand: " + stats.get("Total Demand"));
        System.out.println("Total Vehicles: " + stats.get("Total Vehicles"));
        System.out.println("Total Delivery Points: " + stats.get("Total Delivery Points"));

        // Print detailed vehicle information
        List<Vehicle> vehicles = system.getVehicles();
        for (Vehicle v : vehicles) {
            System.out.println(String.format("\nVehicle %d (Capacity: %d, Load: %d):",
                    v.getId(), v.getCapacity(), v.getCurrentLoad()));
            System.out.println("Route:");
            System.out.println("  " + formatRoute(v.getRoute()));
            System.out.println("Delivery Points:");
            v.getDeliveryLoads().forEach((pointId, demand) ->
                    System.out.println(String.format("  Point %d: Demand %d", pointId, demand)));
        }
    }

    public DeliveryPoint getDeliveryPoint(int locationId) {
        for (DeliveryPoint dp : deliveryPoints) {
            if (dp.getId() == locationId) {
                return dp;
            }
        }
        return null; // Return null if no matching delivery point is found
    }


    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public SmartRouteOptimization getSystem() {
        return system;
    }
}