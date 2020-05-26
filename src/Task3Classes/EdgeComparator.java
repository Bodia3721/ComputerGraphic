 package Task3Classes;

import java.util.Comparator;

public class EdgeComparator implements Comparator<Edge> {
    @Override
    public int compare(Edge o1, Edge o2) {
        int x1 = (int)(o1.p1.getX() + o1.p2.getX());
        int x2 = (int)(o2.p1.getX() + o2.p2.getX());
        return (int)Math.signum(x2-x1);
    }
}