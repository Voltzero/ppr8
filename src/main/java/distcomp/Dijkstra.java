package distcomp;

import java.util.*;
import java.util.stream.Collectors;

public class Dijkstra {

    private Map<String, Map<String, Integer>> topologyMap;
    private static Dijkstra instance = null;
    private Queue<DistanceToEdge> priorityQ;
    private List<ArrayList<String>> routeTracking;
    private int[] distances;
    private String[] egdes;

    private Dijkstra(Map<String, Map<String, Integer>> topologyMap) {
        this.topologyMap = topologyMap;
    }

    public static synchronized Dijkstra getInstance(Map<String, Map<String, Integer>> topologyMap) {
        if (instance == null) {
            instance = new Dijkstra(topologyMap);
        }
        return instance;
    }

    public synchronized List<ArrayList<String>> calculateShortestPaths(String sourceNode) {
        routeTracking = new ArrayList<>();
        for (int i = 0; i < topologyMap.size(); i++)
            routeTracking.add(new ArrayList<>());
        priorityQ = new PriorityQueue<>(topologyMap.size());
        distances = new int[topologyMap.size()];
        egdes = new String[topologyMap.size()];

        Arrays.fill(distances, Integer.MAX_VALUE);
        distances[getNodeIndex(sourceNode)] = 0;

        priorityQ.offer(new DistanceToEdge(sourceNode, 0));

        routeTracking.get(getNodeIndex(sourceNode)).add(sourceNode);

        while (!priorityQ.isEmpty()) {
            relax(priorityQ.poll());
        }
        System.out.println();
        System.out.println("                       [ A, B, C, D, E, F ]");
        System.out.println("Shortest paths for " + sourceNode + ":   " + Arrays.toString(distances));
        System.out.println();
        return routeTracking;
    }

    private void relax(DistanceToEdge v) {
        Map<String, Integer> sorted = topologyMap.get(v.getEgde()).entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
        for (Map.Entry<String, Integer> entry : sorted.entrySet()) {
            int vertex = getNodeIndex((entry).getKey());
            int w = (entry).getValue();

            if (distances[vertex] > (distances[getNodeIndex(v.getEgde())] + w)) {
                distances[vertex] = (distances[getNodeIndex(v.getEgde())] + w);
                egdes[vertex] = (entry).getKey();
                routeTracking.get(getNodeIndex((entry).getKey())).add(v.getEgde());
                DistanceToEdge dte = new DistanceToEdge((entry).getKey(), distances[vertex]);
                priorityQ.remove(dte);
                priorityQ.offer(dte);
            }
        }
    }

    public static int getNodeIndex(String node) {
        switch (node) {
            case "A":
                return 0;
            case "B":
                return 1;
            case "C":
                return 2;
            case "D":
                return 3;
            case "E":
                return 4;
            case "F":
                return 5;
            default:
                return -1;
        }
    }

    public static String getNodeIDfromIndex(int nodeID) {
        switch (nodeID) {
            case 0:
                return "A";
            case 1:
                return "B";
            case 2:
                return "C";
            case 3:
                return "D";
            case 4:
                return "E";
            case 5:
                return "F";
            default:
                return "x";
        }
    }


}
