package distcomp;

import java.util.*;

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

    public List<ArrayList<String>> calculateShortestPaths(String sourceNode) {
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
        System.out.println(Arrays.toString(distances));
        System.out.println(Arrays.toString(egdes));

        return routeTracking;
    }

    private void relax(DistanceToEdge v) {
        for (Map.Entry<String, Integer> entry : topologyMap.get(v.getEgde()).entrySet()) {
            int vertex = getNodeIndex((String) ((Map.Entry) entry).getKey());
            int w = (Integer) ((Map.Entry) entry).getValue();

            if (distances[vertex] > (distances[getNodeIndex(v.getEgde())] + w)) {
                distances[vertex] = (distances[getNodeIndex(v.getEgde())] + w);
                egdes[vertex] = (String) ((Map.Entry) entry).getKey();
                routeTracking.get(getNodeIndex((String) ((Map.Entry) entry).getKey())).add(v.getEgde());
                DistanceToEdge dte = new DistanceToEdge((String) ((Map.Entry) entry).getKey(), distances[vertex]);
                priorityQ.remove(dte);
                priorityQ.offer(dte);
            }
        }
    }

    private int getNodeIndex(String node) {
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
}
