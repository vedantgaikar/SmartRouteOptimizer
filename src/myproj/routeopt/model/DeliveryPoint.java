package myproj.routeopt.model;

public class DeliveryPoint {
    private int id;
    private int demand;
    private int timeWindowStart;
    private int timeWindowEnd;

    public DeliveryPoint(int id, int demand, int timeWindowStart, int timeWindowEnd) {
        this.id = id;
        this.demand = demand;
        this.timeWindowStart = timeWindowStart;
        this.timeWindowEnd = timeWindowEnd;
    }

    // Add getters and setters
    public int getId() { return id; }
    public int getDemand() { return demand; }
    public int getTimeWindowStart() { return timeWindowStart; }
    public int getTimeWindowEnd() { return timeWindowEnd; }

    @Override
    public String toString() {
        return "Location " + id + " (Demand: " + demand + ")";
    }
}
