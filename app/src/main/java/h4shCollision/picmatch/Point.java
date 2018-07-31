package h4shCollision.picmatch;

public class Point {
    protected int x, y;

    Point(int a, int b) {
        x = a;
        y = b;
    }

    Point(Point p) {
        y = p.getY();
        x = p.getX();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point)) return false;

        Point point = (Point) o;

        if (x != point.x) return false;
        if (y != point.y) return false;

        return true;
    }

    @Override
    public Point clone() {
        return new Point(x, y);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return x + ", " + y;
    }
}
