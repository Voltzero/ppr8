package distcomp;

import java.util.*;

public class Topology {
    private List<String> nodes;
    private Random rand;

    public Topology(List<String> nodes) {
        this.nodes = nodes;
        rand = new Random();
    }

    public Map<String, Map<String, Integer>> generateTopologyMap() {
        Map<String, Map<String, Integer>> map = new HashMap<>();
        for (String node : nodes) {
            int connections = rand.nextInt(nodes.size() - 2) + 1 ;
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
