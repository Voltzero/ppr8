package distcomp;

public class DistanceToEdge implements Comparable<DistanceToEdge> {
    private String egde;
    private int distance;

    public DistanceToEdge(String egde, int distance) {
        this.egde = egde;
        this.distance = distance;
    }

    public void setEgde(String egde) {
        this.egde = egde;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getEgde() {
        return egde;
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public int compareTo(DistanceToEdge o) {
        return Integer.compare(distance, o.getDistance());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DistanceToEdge other = (DistanceToEdge) obj;
        if (distance != other.getDistance())
            return false;
        if (egde.equals(other.getEgde()))
            return false;
        return true;

    }
}
