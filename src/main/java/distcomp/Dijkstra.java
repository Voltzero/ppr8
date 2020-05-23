package distcomp;

import java.util.*;
import java.util.stream.Collectors;

public class Dijkstra {

    private final Map<String, Map<String, Integer>> topologyMap;
    private static Dijkstra instance = null;
    private Queue<DistanceToEdge> priorityQ;
    private String[] previous;
    private int[] distances;

    private Dijkstra(Map<String, Map<String, Integer>> topologyMap) {
        this.topologyMap = topologyMap;
    }

    public static synchronized Dijkstra getInstance(Map<String, Map<String, Integer>> topologyMap) {
        if (instance == null) {
            instance = new Dijkstra(topologyMap);
        }
        return instance;
    }

    public synchronized String[] calculateShortestPaths(String sourceNode) {
        previous = new String[topologyMap.size()];
        priorityQ = new PriorityQueue<>(topologyMap.size());
        distances = new int[topologyMap.size()];

        Arrays.fill(distances, Integer.MAX_VALUE);
        distances[getNodeIndex(sourceNode)] = 0;

        priorityQ.offer(new DistanceToEdge(sourceNode, 0));

        while (!priorityQ.isEmpty()) {
            relax(priorityQ.poll());
        }
        System.out.println();
        System.out.println("                     [ A, B, C, D, E, F ]"); // XD
        System.out.println("Shortest paths for " + sourceNode + ": " + Arrays.toString(distances));
        System.out.println("Previous nodes:      " + Arrays.toString(previous));
        System.out.println();
        return previous;
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
                previous[getNodeIndex((entry).getKey())] = v.getEgde();
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
