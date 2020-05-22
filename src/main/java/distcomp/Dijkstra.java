package distcomp;

import java.util.Arrays;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class Dijkstra {

    private Map<String, Map<String, Integer>> topologyMap;
    private static Dijkstra instance = null;
    private Queue<String> priorityQ;
    int[] distances;
    String[] egdes;

    private Dijkstra(Map<String, Map<String, Integer>> topologyMap) {
        this.topologyMap = topologyMap;
    }

    public static synchronized Dijkstra getInstance(Map<String, Map<String, Integer>> topologyMap) {
        if (instance == null) {
            instance = new Dijkstra(topologyMap);
        }
        return instance;
    }

    public int calculateShortestPath(String sourceNode, String destinationNode) {
        priorityQ = new PriorityQueue<>(topologyMap.size());
        distances = new int[topologyMap.size()];
        egdes = new String[topologyMap.size()];
        Arrays.fill(distances, Integer.MAX_VALUE);
        distances[getNodeIndex(sourceNode)] = 0;

        priorityQ.offer(sourceNode);

        while (!priorityQ.isEmpty()) {
            relax(priorityQ.poll());
        }

        return 0;
    }

    private void relax(String v) {
        for (Map.Entry<String, Integer> entry : topologyMap.get(v).entrySet()) {
            int vertex = getNodeIndex((String) ((Map.Entry) entry).getKey());
            int w = (Integer) ((Map.Entry) entry).getValue();

            if(distances[vertex] > (distances[getNodeIndex(v)] + w)){
                distances[vertex] = (distances[getNodeIndex(v)] + w);
                egdes[vertex] = (String) ((Map.Entry) entry).getKey();

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
