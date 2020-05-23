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
                int weight = rand.nextInt(9) + 1;
                if (!s.equals(node) && !randNodes.containsKey(s))
                    randNodes.put(s, weight);
                else
                    i--;
            }
            map.put(node, randNodes);
        }
        for (Map.Entry<String, Map<String, Integer>> mapEntry : map.entrySet()) {
            String n = mapEntry.getKey();
            Map<String, Integer> visual = mapEntry.getValue();
            System.out.print(n + ": ");
            for (Map.Entry<String, Integer> entry : visual.entrySet()) {
                System.out.print(entry.getKey() + " (" + entry.getValue() + "), ");
            }
            System.out.println();
        }
        return map;
    }
}
