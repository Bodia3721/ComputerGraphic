package Task3Classes;

import javafx.geometry.Point2D;

public class Edge {
    public Point2D p1;
    public Point2D p2;
    public int piece;
    public Edge(Point2D p1, Point2D p2, int piece) {
        this.p1 = new Point2D(p1.getX(), p1.getY());
        this.p2 = new Point2D(p2.getX(), p2.getY());
        this.piece = piece;
    }
}