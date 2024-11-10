package myproj.routeopt.model;

import java.util.*;

public class Vehicle {
    private int id;
    private int capacity;
    private int currentLoad;
    private List<Integer> route;
    private Map<Integer, Integer> deliveryLoads;

    public Vehicle(int id, int capacity) {
        this.id = id;
        this.capacity = capacity;
        this.currentLoad = 0;
        this.route = new ArrayList<>();
        this.deliveryLoads = new HashMap<>();
    }

    public void addDelivery(int deliveryPointId, int demand) {
        if (currentLoad + demand > capacity) {
            throw new IllegalStateException("Adding this delivery would exceed vehicle capacity");
        }
        if (!route.contains(deliveryPointId)) {  // Only add if not already in route
            route.add(deliveryPointId);
        }
        deliveryLoads.put(deliveryPointId, demand);
        currentLoad += demand;
    }

    public void resetRoute() {
        route.clear();
        // Don't clear loads - we want to preserve them
    }

    public void setRoute(List<Integer> newRoute) {
        // Validate that new route contains all delivery points
        for (Integer deliveryPoint : deliveryLoads.keySet()) {
            if (!newRoute.contains(deliveryPoint)) {
                throw new IllegalArgumentException("New route must contain all assigned delivery points");
            }
        }
        route = new ArrayList<>(newRoute);
    }

    public int getId() { return id; }
    public int getCapacity() { return capacity; }
    public int getCurrentLoad() { return currentLoad; }
    public List<Integer> getRoute() { return route; }
    public Map<Integer, Integer> getDeliveryLoads() { return deliveryLoads; }
}