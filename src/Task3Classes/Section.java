package Task3Classes;

import java.util.ArrayList;

public class Section {
    public int down_y;
    public int up_y;
    public ArrayList<Edge> edges;
    public Section(int down_y, int up_y) {
        this.down_y = down_y;
        this.up_y = up_y;
        this.edges = new ArrayList<>();
    }
}
