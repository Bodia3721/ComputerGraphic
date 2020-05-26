package Tasks;

import Task3Classes.Edge;
import Task3Classes.Section;
import Task3Classes.EdgeComparator;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.Collections;

public class Task3 implements Task {

    private static final int HEIGHT = 700;
    private static final int WIDTH = 1300;
    private static final int POINT_RADIUS = 6;

    private Group item_group;
    private Button start_button;
    private boolean draw_mode;

    ArrayList<ArrayList<Point2D>> pieces_array;
    Point2D last_point;
    ArrayList<Section> sections;

    public Task3() {
        this.Setup();
    }
    @Override
    public Scene getScene() {
        Group main_group = new Group();
        main_group.getChildren().addAll(item_group, start_button);
        Scene scene = new Scene(main_group, WIDTH, HEIGHT);
        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEvent ->
        {
            if ((int)mouseEvent.getX() < 50 && (int)mouseEvent.getSceneY() < 50) return;
            last_point = new Point2D((int)mouseEvent.getX(), HEIGHT - (int)mouseEvent.getSceneY());
            if (mouseEvent.getButton() == MouseButton.PRIMARY) LeftMouseClick();
            else RightMouseClick();
        } );
        return scene;
    }

    private void Setup() {
        item_group = new Group();

        draw_mode = true;
        start_button = new Button("Start");
        start_button.setOnAction( handler -> FinishDraw());
        pieces_array = new ArrayList<>();
        pieces_array.add(new ArrayList<>());
    }
    private void FinishDraw() {
        start_button.setDisable(true);
        draw_mode = false;
        for (int i = 0; i < pieces_array.size(); i++) {
            if (pieces_array.get(i).size() < 3 ) {
                pieces_array.remove(i);
                i--;
            }
        }
        PrepareMethod();
        DrawPreparings();
    }
    private void LeftMouseClick() {
        if (draw_mode) MouseDraw();
        else ChoosePoint();
    }
    private void RightMouseClick() {
        if (!draw_mode) return;
        pieces_array.set(pieces_array.size()-1, JarvisAlgorithm(pieces_array.get(pieces_array.size()-1)));
        pieces_array.add(new ArrayList<>());
        DrawPieces();
    }

    private void MouseDraw() {
        pieces_array.get(pieces_array.size()-1).add(last_point);
        DrawPieces();
    }
    private void ChoosePoint() {
        StringMethod(last_point);
    }

    private Point2D SmallestPoint(ArrayList<Point2D> shape) {
        Point2D p = shape.get(0);
        for (Point2D point2D : shape)
            if (point2D.getY() < p.getY())
                p = point2D;
            else if (point2D.getY() == p.getY() && point2D.getX() < p.getX())
                p = point2D;
        return p;
    }
    private double CosBetweenVectors(Point2D a, Point2D b, Point2D c, Point2D d) {
        Point2D x = new Point2D(b.getX() - a.getX(), b.getY() - a.getY());
        Point2D y = new Point2D(d.getX() - c.getX(), d.getY() - c.getY());
        double m = x.getX()*y.getX() + x.getY()*y.getY();
        double n = Math.sqrt(Math.pow(x.getX(), 2) + Math.pow(x.getY(), 2)) * Math.sqrt(Math.pow(y.getX(), 2) + Math.pow(y.getY(), 2));
        return ((m*1000)/n);
    }
    private ArrayList<Point2D> JarvisAlgorithm(ArrayList<Point2D> shape) {
        shape = (ArrayList<Point2D>) shape.clone();
        ArrayList<Point2D> new_shape = new ArrayList<>();
        new_shape.add(SmallestPoint(shape));

        Point2D p = null;
        double cs = -2;
        for (Point2D i : shape) {
            if (new_shape.get(0).equals(i)) continue;
            if (p == null) p = i;
            else {
                double d = CosBetweenVectors(new_shape.get(0), new Point2D(i.getX(), new_shape.get(0).getY()), new_shape.get(0), i);
                if (cs < d) {
                    cs = d;
                    p = i;
                }
            }
        }
        new_shape.add(p);
        shape.remove(p);

        if (new_shape.get(1).getX() < new_shape.get(0).getX()) {
            p = new_shape.get(0);
            new_shape.set(0, new_shape.get(1));
            new_shape.set(1, p);
        }

        do {
            if (shape.isEmpty()) {
                System.out.println("Empty set");
                break;
            }
            p = new_shape.get(0);
            cs = CosBetweenVectors(new_shape.get(new_shape.size()-2), new_shape.get(new_shape.size()-1), new_shape.get(new_shape.size()-1), p);
            for (Point2D i : shape) {
                if (p.equals(i)) continue;
                double d = CosBetweenVectors(new_shape.get(new_shape.size() - 2), new_shape.get(new_shape.size() - 1), new_shape.get(new_shape.size() - 1), i);
                if (d > cs) {
                    p = i;
                    cs = d;
                }
            }
            if (p.equals(new_shape.get(0))) break;
            new_shape.add(p);
            shape.remove(p);
        } while (true);

        return new_shape;
    }

    private void DrawShape(ArrayList<Point2D> shape, Color color) {
        for (int i = 0; i < shape.size(); i++) {
            int j = (i+1) % shape.size();
            Line l = new Line(shape.get(i).getX(), HEIGHT - shape.get(i).getY(), shape.get(j).getX(), HEIGHT - shape.get(j).getY());
            l.setStroke(color);
            item_group.getChildren().add(l);
        }
    }
    private void DrawPieces() {
        item_group.getChildren().clear();
        for (ArrayList<Point2D> piece : pieces_array) {
            DrawShape(piece, Color.BLACK);
        }
    }
    private void DrawPoint(Point2D point, Color color) {
        Circle circle = new Circle(POINT_RADIUS);
        circle.setFill(color);
        circle.setTranslateX(point.getX());
        circle.setTranslateY(HEIGHT - point.getY());
        item_group.getChildren().add(circle);
    }
    private void DrawPreparings() {
        Line l1 = new Line(0, HEIGHT - sections.get(0).down_y, WIDTH, HEIGHT - sections.get(0).down_y);
        l1.setStroke(Color.GREEN);
        item_group.getChildren().add(l1);
        for (Section section : sections) {
            Line l = new Line(0, HEIGHT - section.up_y, WIDTH, HEIGHT - section.up_y);
            l.setStroke(Color.GREEN);
            item_group.getChildren().add(l);
        }
    }
    private void DrawSection(Section section) {
        for (Edge edge : section.edges) {
            Line l = new Line(edge.p1.getX(), HEIGHT - edge.p1.getY(), edge.p2.getX(), HEIGHT - edge.p2.getY());
            l.setStroke(Color.RED);
            item_group.getChildren().add(l);
        }
    }
    private void DrawEdge(Edge edge) {
        Line l = new Line(edge.p1.getX(), HEIGHT - edge.p1.getY(), edge.p2.getX(), HEIGHT - edge.p2.getY());
        l.setStroke(Color.BLUE);
        item_group.getChildren().add(l);
    }

    private boolean IntersectSection(Section section, Point2D p1, Point2D p2) {
        Point2D _p1 = new Point2D(p1.getX(), p1.getY());
        Point2D _p2 = new Point2D(p2.getX(), p2.getY());
        if (_p2.getY() < _p1.getY()) {
            Point2D p = _p1;
            _p1 = _p2;
            _p2 = p;
        }
        if (_p1.getY() <= section.down_y && _p2.getY() >= section.up_y) return true;
        return false;
    }
    private void CutEdgeInSection(Section section) {
        for (Edge edge : section.edges) {
            double koeff_down = (edge.p2.getY() - edge.p1.getY()) / Math.abs(section.down_y - edge.p1.getY());
            double koeff_up   = (edge.p2.getY() - edge.p1.getY()) / Math.abs(section.up_y   - edge.p1.getY());
            int dist_down = (int)((edge.p2.getX() - edge.p1.getX()) / koeff_down);
            int dist_up   = (int)((edge.p2.getX() - edge.p1.getX()) / koeff_up);
            double old_x = edge.p1.getX();
            edge.p1 = new Point2D(old_x + dist_down, section.down_y);
            edge.p2 = new Point2D(old_x + dist_up,   section.up_y);
        }
    }
    private void PrepareMethod() {
        ArrayList<Integer> y_coordinats = new ArrayList<Integer>();
        for (ArrayList<Point2D> arr_p : pieces_array)
            for (Point2D p : arr_p)
                y_coordinats.add((int)p.getY());
        Collections.sort(y_coordinats);

        sections = new ArrayList<>();
        sections.add(new Section(0, y_coordinats.get(0)));
        for (int i = 0; i < y_coordinats.size()-1; i++)
            sections.add(new Section(y_coordinats.get(i), y_coordinats.get(i+1)));
        sections.add(new Section(y_coordinats.get(y_coordinats.size()-1), HEIGHT));

        for (Section section : sections) {
            for (int i = 0; i < pieces_array.size(); i++) {
                for (int j = 0; j < pieces_array.get(i).size(); j++) {
                    Point2D p1 = pieces_array.get(i).get(j);
                    Point2D p2 = pieces_array.get(i).get((j + 1) % pieces_array.get(i).size());
                    if (IntersectSection(section, p1, p2)) {
                        if (p1.getY() < p2.getY())
                             section.edges.add(new Edge(p1, p2, i));
                        else section.edges.add(new Edge(p2, p1, i));
                    }
                }
            }
            CutEdgeInSection(section);
            section.edges.sort(new EdgeComparator());
        }
    }

    private boolean InsideSection(Point2D p, Section section) {
        return section.down_y <= p.getY() && p.getY() <= section.up_y;
    }
    private int rotate(Point2D a, Point2D b, Point2D c) {
        return (int)Math.signum((b.getX()-a.getX())*(c.getY()-b.getY())-(b.getY()-a.getY())*(c.getX()-b.getX()));
    }
    private void StringMethod(Point2D current_point) {
        DrawPieces();
        DrawPoint(current_point, Color.RED);
        DrawPreparings();

        int l = 0;
        int r = sections.size() - 1;
        while (l < r) {
            int m = (l+r)/2;
            if (InsideSection(current_point, sections.get(m))) {
                l = r = m;
            } else if (current_point.getY() < sections.get(m).down_y) {
                r = m;
            } else if (current_point.getY() > sections.get(m).up_y) {
                l = m+1;
            } else
                System.out.println("ERRROOOOR BLYAT\'!");
        }

        Section section = sections.get(l);
        DrawSection(section);

        l = 0;
        r = section.edges.size() - 1;
        if (rotate(section.edges.get(l).p1, section.edges.get(l).p2, current_point) < 0) {
            r = l;
        } else if (rotate(section.edges.get(r).p1, section.edges.get(r).p2, current_point) > 0) {
            l = r;
        }
        else
        while (l + 1 < r) {
            int m = (l+r)/2;
            int pos = rotate(section.edges.get(m).p1, section.edges.get(m).p2, current_point);
            if (pos > 0) {
                l = m;
            } else if (pos < 0) {
                r = m;
            } else System.out.println("Ninada tak!");
        }
        DrawEdge(section.edges.get(l));
        DrawEdge(section.edges.get(r));
    }
}
