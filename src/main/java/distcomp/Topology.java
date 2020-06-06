package distcomp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Topology {
    private static Random rand;

    public static Map<String, Map<String, Integer>> generateTopologyMap(List<String> nodes) {
        Map<String, Map<String, Integer>> map = new HashMap<>();
        for (String node : nodes) {
            rand = new Random();
            int connections = rand.nextInt(nodes.size() - 2) + 1;
            Map<String, Integer> randNodes = new HashMap<>();
            for (int i = 0; i < connections; i++) {
                rand = new Random();
                String s = nodes.get(rand.nextInt(nodes.size()));
                int weight = 1;
                if (!s.equals(node) && !randNodes.containsKey(s))
                    randNodes.put(s, weight);
                else
                    i--;
            }
            map.put(node, randNodes);
        }
        displayTopologyMap(map);
        return map;
    }

    public static Map<String, Map<String, Integer>> generateCriticalCoord(List<String> nodes) {
        Map<String, Map<String, Integer>> map = new HashMap<>();
        int weight = 1;
        rand = new Random();
        String coord = nodes.get(rand.nextInt(nodes.size()));
        String critical = coord;
        do {
            critical = nodes.get(rand.nextInt(nodes.size()));
        } while (critical.equals(coord));

        Map<String, Integer> coordNodes = new HashMap<>();
        for (String node : nodes) {
            if (!node.equals(coord)) {
                coordNodes.put(node, weight);
            }
        }
        Map<String, Integer> criticalNodes = new HashMap<>();
        for (String node : nodes) {
            if (!node.equals(critical)) {
                criticalNodes.put(node, weight);
            }
        }
        map.put(coord, coordNodes);
        map.put(critical, criticalNodes);

        for (String node : nodes) {
            Map<String, Integer> connectedToCoord = new HashMap<>();
            if (!node.equals(coord) && !node.equals(critical)) {
                connectedToCoord.put(coord, weight);
                connectedToCoord.put(critical, weight);
                map.put(node, connectedToCoord);
            }
        }
        displayTopologyMap(map);
        return map;
    }

    private static void displayTopologyMap(Map<String, Map<String, Integer>> map) {
        for (Map.Entry<String, Map<String, Integer>> mapEntry : map.entrySet()) {
            String n = mapEntry.getKey();
            Map<String, Integer> visual = mapEntry.getValue();
            System.out.print(n + ": ");
            for (Map.Entry<String, Integer> entry : visual.entrySet()) {
                System.out.print(entry.getKey() + " (" + entry.getValue() + "), ");
            }
            System.out.println();
        }
    }
}
