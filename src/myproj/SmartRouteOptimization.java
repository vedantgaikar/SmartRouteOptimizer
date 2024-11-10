package myproj;

import myproj.routeopt.model.*;

import myproj.routeopt.model.DeliveryPoint;
import myproj.routeopt.model.Graph;
import myproj.routeopt.model.Vehicle;

import java.util.*;

public class SmartRouteOptimization {
    private Graph cityGraph;
    private List<DeliveryPoint> deliveryPoints;
    private List<Vehicle> vehicles;
    private int depotLocation;

    public SmartRouteOptimization(Graph cityGraph, int depotLocation) {
        this.cityGraph = cityGraph;
        this.depotLocation = depotLocation;
        this.deliveryPoints = new ArrayList<>();
        this.vehicles = new ArrayList<>();
    }

    // Check if total capacity is sufficient
    public boolean validateCapacity() {
        int totalDemand = deliveryPoints.stream()
                .mapToInt(DeliveryPoint::getDemand)
                .sum();

        int totalCapacity = vehicles.stream()
                .mapToInt(Vehicle::getCapacity)
                .sum();

        return totalCapacity >= totalDemand;
    }
    public void assignDeliveries() throws IllegalStateException {
        if (deliveryPoints.isEmpty()) {
            throw new IllegalStateException("No delivery points added");
        }
        if (vehicles.isEmpty()) {
            throw new IllegalStateException("No vehicles added");
        }
        if (!validateCapacity()) {
            throw new IllegalStateException("Total vehicle capacity insufficient for total demand");
        }

        // Reset all vehicles but preserve their capacity
        for (Vehicle vehicle : vehicles) {
            vehicle.resetRoute();
        }

        // Sort delivery points by demand (largest first)
        List<DeliveryPoint> sortedPoints = new ArrayList<>(deliveryPoints);
        sortedPoints.sort((dp1, dp2) -> Integer.compare(dp2.getDemand(), dp1.getDemand()));

        // Try to assign each delivery point
        for (DeliveryPoint dp : sortedPoints) {
            Vehicle bestVehicle = null;
            int maxRemainingCapacity = -1;

            for (Vehicle vehicle : vehicles) {
                int remainingCapacity = vehicle.getCapacity() - vehicle.getCurrentLoad();
                if (remainingCapacity >= dp.getDemand() && remainingCapacity > maxRemainingCapacity) {
                    bestVehicle = vehicle;
                    maxRemainingCapacity = remainingCapacity;
                }
            }

            if (bestVehicle == null) {
                throw new IllegalStateException(
                        String.format("Unable to assign delivery point %d (demand: %d) - no vehicle has sufficient remaining capacity",
                                dp.getId(), dp.getDemand())
                );
            }

            bestVehicle.addDelivery(dp.getId(), dp.getDemand());
            System.out.println(String.format("Assigned delivery point %d (demand: %d) to vehicle %d",
                    dp.getId(), dp.getDemand(), bestVehicle.getId()));
        }
    }

    public void optimizeRoutes() {
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getDeliveryLoads().isEmpty()) {
                // If no deliveries assigned, just set route as depot->depot
                List<Integer> emptyRoute = new ArrayList<>();
                emptyRoute.add(depotLocation);
                emptyRoute.add(depotLocation);
                vehicle.setRoute(emptyRoute);
                continue;
            }

            Set<Integer> deliveryPoints = new HashSet<>(vehicle.getDeliveryLoads().keySet());
            List<Integer> optimizedRoute = new ArrayList<>();
            optimizedRoute.add(depotLocation);

            // Current location starts at depot
            int currentLocation = depotLocation;

            // Build route using nearest neighbor
            while (!deliveryPoints.isEmpty()) {
                int nearest = findNearestLocation(currentLocation, new ArrayList<>(deliveryPoints));
                optimizedRoute.add(nearest);
                deliveryPoints.remove(nearest);
                currentLocation = nearest;
            }

            // Return to depot
            optimizedRoute.add(depotLocation);

            // Set the optimized route
            vehicle.setRoute(optimizedRoute);

            System.out.println(String.format("Optimized route for vehicle %d: %s",
                    vehicle.getId(), optimizedRoute));
        }
    }

    // Add this debug method
    public void printSystemState() {
        System.out.println("\nSystem State:");
        System.out.println("Delivery Points: " + deliveryPoints.size());
        for (DeliveryPoint dp : deliveryPoints) {
            System.out.println(String.format("  Point %d: Demand %d", dp.getId(), dp.getDemand()));
        }

        System.out.println("\nVehicles: " + vehicles.size());
        for (Vehicle v : vehicles) {
            System.out.println(String.format(
                    "  Vehicle %d: Capacity %d, Current Load %d",
                    v.getId(), v.getCapacity(), v.getCurrentLoad()));
            System.out.println("    Route: " + v.getRoute());
            System.out.println("    Delivery Loads: " + v.getDeliveryLoads());
        }
    }

    // Helper method to find nearest location from current position
    private int findNearestLocation(int currentLocation, List<Integer> remainingLocations) {
        int nearestLocation = remainingLocations.get(0);
        int shortestDistance = cityGraph.getDistance(currentLocation, nearestLocation);

        for (int location : remainingLocations) {
            int distance = cityGraph.getDistance(currentLocation, location);
            if (distance < shortestDistance) {
                shortestDistance = distance;
                nearestLocation = location;
            }
        }

        return nearestLocation;
    }
    public void addDeliveryPoint(int id, int demand, int timeStart, int timeEnd) {
        deliveryPoints.add(new DeliveryPoint(id, demand, timeStart, timeEnd));
    }

    public void addVehicle(int id, int capacity) {
        vehicles.add(new Vehicle(id, capacity));
    }

    // Getters for accessing the internal state
    public Graph getCityGraph() {
        return cityGraph;
    }

    public List<DeliveryPoint> getDeliveryPoints() {
        return Collections.unmodifiableList(deliveryPoints);
    }

    public List<Vehicle> getVehicles() {
        return Collections.unmodifiableList(vehicles);
    }

    public int getDepotLocation() {
        return depotLocation;
    }
    // Add method to get delivery statistics
    public Map<String, Integer> getDeliveryStats() {
        Map<String, Integer> stats = new HashMap<>();

        int totalDemand = deliveryPoints.stream()
                .mapToInt(DeliveryPoint::getDemand)
                .sum();

        int totalCapacity = vehicles.stream()
                .mapToInt(Vehicle::getCapacity)
                .sum();

        stats.put("totalDeliveryPoints", deliveryPoints.size());
        stats.put("totalDemand", totalDemand);
        stats.put("totalVehicles", vehicles.size());
        stats.put("totalCapacity", totalCapacity);

        // Add current loads
        vehicles.forEach(v ->
                stats.put("vehicle_" + v.getId() + "_load", v.getCurrentLoad())
        );

        return stats;
    }


    // Add this helper method to get detailed route information
    public List<String> getDetailedRouteInfo() {
        List<String> routeInfo = new ArrayList<>();
        for (Vehicle vehicle : vehicles) {
            StringBuilder info = new StringBuilder();
            info.append(String.format("Vehicle %d (Capacity: %d, Current Load: %d):\n",
                    vehicle.getId(), vehicle.getCapacity(), vehicle.getCurrentLoad()));
            info.append("Route: ");
            info.append(vehicle.getRoute().toString());
            info.append("\nDelivery Loads: ");
            info.append(vehicle.getDeliveryLoads().toString());
            routeInfo.add(info.toString());
        }
        return routeInfo;
    }
}
