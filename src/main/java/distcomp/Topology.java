package distcomp;

import java.util.*;

public class Topology {
    private static Random rand;

    public static Map<String, Map<String, Integer>> generateTopologyMap(List<String> nodes) {
        Map<String, Map<String, Integer>> map = new HashMap<>();
        for (String node : nodes) {
            rand = new Random();
            int connections = rand.nextInt(nodes.size() - 2) + 1 ;
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
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            String n = (String) pair.getKey();
            Map<String, Integer> visual = (Map<String, Integer>) pair.getValue();
            System.out.print(n + ": ");
            Iterator itr = visual.entrySet().iterator();
            while (itr.hasNext()) {
                Map.Entry v = (Map.Entry) itr.next();
                System.out.print(v.getKey() + " (" + v.getValue() + "), ");
            }
            System.out.println();
        }
        return map;
    }
}
