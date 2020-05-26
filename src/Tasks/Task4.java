package Tasks;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.Random;

public class Task4 implements Task {
    private static final int HEIGHT = 700;
    private static final int WIDTH = 1300;
    private static final int POINT_RADIUS = 8;
    private static final int BORDER_DISTANCE_H = 300;
    private static final int BORDER_DISTANCE_W = 500;
    private static final int POINT_COUNT = 15;

    private Group item_group;
    private Tree intervalTree;

    private Point2D pressed_point1;
    private Point2D pressed_point2;

    public Task4() {
        this.Setup();
    }

    private void Setup() {
        pressed_point1 = null;
        pressed_point2 = null;
        item_group = new Group();

        ArrayList<Point2D> shape = getShape();
        for (Point2D p : shape) {
            DrawPoint(p, Color.RED);
        }
        MakeTree(shape);
    }
    @Override
    public Scene getScene() {
        Scene scene = new Scene(item_group, WIDTH, HEIGHT);
        scene.setOnMouseClicked(event -> {
            pressed_point2 = pressed_point1;
            pressed_point1 = new Point2D(event.getX(), HEIGHT - event.getY());
            DrawPoint(pressed_point1, Color.BLUE);
            if (pressed_point1 != null && pressed_point2 != null)
                FindArea();
        });
        return scene;
    }

    private ArrayList<Point2D> getShape() {
        Random random = new Random();
        int count = (random.nextInt() % POINT_COUNT) + POINT_COUNT/2;
        if (count < 5) count = 5;
        ArrayList<Point2D> res = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Point2D point = new Point2D(  (WIDTH  * 2 / 3) + (random.nextInt() % BORDER_DISTANCE_W ) - (BORDER_DISTANCE_W >> 1),
                    (HEIGHT * 2 / 3) + (random.nextInt() % BORDER_DISTANCE_H ) - (BORDER_DISTANCE_H >> 1));
            res.add(point);
        }
        return res;
    }
    private void DrawPoint(Point2D point, Color color) {
        Circle circle = new Circle(POINT_RADIUS);
        circle.setFill(color);
        circle.setTranslateX(point.getX());
        circle.setTranslateY(HEIGHT - point.getY());
        item_group.getChildren().add(circle);
    }
    private void DrawLine(int x1, int y1, int x2, int y2, Color color) {
        Line l = new Line(x1, HEIGHT - y1, x2, HEIGHT - y2);
        l.setStroke(color);
        item_group.getChildren().add(l);
    }

    private void RecursiveBuildY(Node node, ArrayList<Point2D> sub_shape, boolean left_bottom) {
        Point2D left = sub_shape.get(0), right = sub_shape.get(0);
        for (Point2D point : sub_shape) {
            if (point.getX() < left.getX())
                left = point;
            if (point.getX() > right.getX())
                right = point;
        }
        int middleX = (int) ((left.getX() + right.getX())/2);

        Point2D nearest = sub_shape.get(0);
        for (Point2D point : sub_shape) {
            if ( Math.abs((int)point.getX() - middleX) < Math.abs((int)nearest.getX() - middleX))
                nearest = point;
        }

        ArrayList<Point2D> leftSide = new ArrayList<>();
        ArrayList<Point2D> rightSide = new ArrayList<>();
        for (Point2D point : sub_shape) {
            if (nearest == point) continue;
            if (point.getX() < nearest.getX())
                leftSide.add(point);
            if (point.getX() > nearest.getX())
                rightSide.add(point);
        }
        node.point = nearest;

        if (node.parent != null) {
            if (left_bottom)
                DrawLine((int) nearest.getX(), -HEIGHT, (int)nearest.getX(), (int) node.parent.point.getY(), Color.GREEN);
            else
                DrawLine((int) nearest.getX(), (int) node.parent.point.getY(), (int)nearest.getX(), HEIGHT, Color.GREEN);
        } else
            DrawLine((int) nearest.getX(), -HEIGHT, (int)nearest.getX(), HEIGHT, Color.GREEN);

        node.leftInterval = (int) left.getY();
        node.rightInterval = (int) right.getY();

        if (!leftSide.isEmpty()) {
            node.leftChild = new Node();
            node.leftChild.parent = node;
            RecursiveBuildX(node.leftChild, leftSide, true);
        }
        if (!rightSide.isEmpty()) {
            node.rightChild = new Node();
            node.rightChild.parent = node;
            RecursiveBuildX(node.rightChild, rightSide, false);
        }
    }
    private void RecursiveBuildX(Node node, ArrayList<Point2D> sub_shape, boolean left_bottom) {

        Point2D bottom = sub_shape.get(0), top = sub_shape.get(0);
        for (Point2D point : sub_shape) {
            if (point.getY() < bottom.getY())
                bottom = point;
            if (point.getY() > top.getY())
                top = point;
        }
        int middleY = (int) ((bottom.getY() + top.getY())/2);

        Point2D nearest = sub_shape.get(0);
        for (Point2D point : sub_shape) {
            if ( Math.abs((int)point.getY() - middleY) < Math.abs((int)nearest.getY() - middleY))
                nearest = point;
        }
        node.point = nearest;

        ArrayList<Point2D> bottomSide = new ArrayList<>();
        ArrayList<Point2D> topSide = new ArrayList<>();
        for (Point2D point : sub_shape) {
            if (nearest == point) continue;
            if (point.getY() < nearest.getY())
                bottomSide.add(point);
            if (point.getY() > nearest.getY())
                topSide.add(point);
        }

        if (node.parent != null) {
            if (left_bottom)
                DrawLine(-WIDTH, (int) nearest.getY(), (int) node.parent.point.getX(), (int)nearest.getY(), Color.GREEN);
            else
                DrawLine((int) node.parent.point.getX(), (int) nearest.getY(), WIDTH, (int)nearest.getY(), Color.GREEN);
        } else
            DrawLine(-WIDTH, (int) nearest.getY(), WIDTH, (int)nearest.getY(), Color.GREEN);

        node.leftInterval = (int) bottom.getY();
        node.rightInterval = (int) top.getY();

        if (!bottomSide.isEmpty()) {
            node.leftChild = new Node();
            node.leftChild.parent = node;
            RecursiveBuildY(node.leftChild, bottomSide, true);
        }
        if (!topSide.isEmpty()) {
            node.rightChild = new Node();
            node.rightChild.parent = node;
            RecursiveBuildY(node.rightChild, topSide, false);
        }
    }
    private void MakeTree(ArrayList<Point2D> shape) {
        intervalTree = new Tree();
        RecursiveBuildY(intervalTree.root, shape, false);
    }

    private boolean RecursiveSearchY(int left, int right, int top, int bottom, Node node) {
        boolean gone = false;
        if (node.leftChild != null && left < node.leftInterval) {
            gone = RecursiveSearchX(left, (int) node.point.getX(), top, bottom, node.leftChild);
        }
        if (node.rightChild != null && node.rightInterval < right) {
            gone |= RecursiveSearchX((int) node.point.getX(), right, top, bottom, node.rightChild);
        }
        if (!gone) {
            DrawPoint(node.point, Color.GREEN);
            return false;
        }
        return true;
    }
    private boolean RecursiveSearchX(int left, int right, int bottom, int top, Node node) {
        boolean gone = false;
        if (node.leftChild != null && bottom < node.leftInterval) {
            gone = RecursiveSearchY(left, right, bottom, (int) node.point.getY(), node.leftChild);
        }
        if (node.rightChild != null && node.rightInterval < top) {
            gone = RecursiveSearchY(left, right, (int) node.point.getY(), top, node.rightChild);
        }
        if (!gone) {
            DrawPoint(node.point, Color.GREEN);
            return false;
        }
        return true;
    }
    private void FindArea() {
        RecursiveSearchY((int) pressed_point1.getX(), (int) pressed_point2.getX(), (int) pressed_point1.getY(), (int) pressed_point2.getY(), intervalTree.root);
    }
}

class Node {
    Point2D point;
    int leftInterval;
    int rightInterval;

    Node leftChild;
    Node rightChild;
    Node parent;
}
class Tree {

    Node root;
    Tree() {
        root = new Node();
        root.parent = null;
    }
}
