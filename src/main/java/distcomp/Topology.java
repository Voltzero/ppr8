package distcomp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Topology {
    private static List<String> nodes;
    private static Random rand;

    public Topology(List<String> nodes) {
        Topology.nodes = nodes;
        rand = new Random();
    }

    public static Map<String, Map<String, Integer>> generateTopologyMap() {
        Map<String, Map<String, Integer>> map = new HashMap<>();
        for (String node : nodes) {
            int connections = rand.nextInt(nodes.size() - 2);
            Map<String, Integer> randNodes = new HashMap<>();
            for (int i = 0; i < connections; i++) {
                String s = nodes.get(rand.nextInt(nodes.size()));
                int weight = rand.nextInt(9) + 1;
                if (!s.equals(node) && !randNodes.containsKey(s))
                    randNodes.put(s, weight);
                else
                    i--;
            }
            map.put(node, randNodes);
        }
        return map;
    }
}
