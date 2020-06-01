package distcomp;

public class MaxID {
    private String nodeID;
    private int nodeLvl;

    public MaxID() {
    }

    public MaxID(String nodeID, int nodeLvl) {
        this.nodeID = nodeID;
        this.nodeLvl = nodeLvl;
    }

    public void setNodeID(String nodeID) {
        this.nodeID = nodeID;
    }

    public void setNodeLvl(int nodeLvl) {
        this.nodeLvl = nodeLvl;
    }

    public String getNodeID() {
        return nodeID;
    }

    public int getNodeLvl() {
        return nodeLvl;
    }

    @Override
    public boolean equals(Object obj) {
        return (((MaxID) obj).getNodeID().equals(this.nodeID) && ((MaxID) obj).getNodeLvl() == this.nodeLvl);
    }
}
