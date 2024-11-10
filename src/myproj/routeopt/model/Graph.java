package myproj.routeopt.model;

import java.util.Arrays;

public class Graph {
    private int vertices;
    private int[][] distanceMatrix;

    public Graph(int vertices) {
        this.vertices = vertices;
        distanceMatrix = new int[vertices][vertices];
        for (int i = 0; i < vertices; i++) {
            Arrays.fill(distanceMatrix[i], Integer.MAX_VALUE / 2);
        }
    }

    public void addEdge(int u, int v, int weight) {
        distanceMatrix[u][v] = weight;
        distanceMatrix[v][u] = weight;
    }

    public int getDistance(int u, int v) {
        return distanceMatrix[u][v];
    }

    public int getVertices() { return vertices; }
}
