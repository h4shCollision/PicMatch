package h4shCollision.picmatch;

public class Block extends Point {

    private int content;

    Block(int a, int b) {
        super(a, b);
        content = 0;
    }

    Block(int a, int b, int c) {
        super(a, b);
        content = c;
    }

    Block(Point p, int c) {
        super(p);
        this.content = c;
    }

    public boolean isEmpty() {
        return content == 0;
    }

    public int getContent() {
        return content;
    }

    public void remove() {
        setContent(0);
    }

    void setX(int n) {
        x = n;
    }

    void setY(int n) {
        y = n;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Block)) return false;
        if (!super.equals(o)) return false;
        Block block = (Block) o;
        return content == block.content;
    }

    @Override
    public String toString() {
        return super.toString() + ": " + content;
    }

    void setContent(int c) {
        content = c;
    }

    void setLocation(Point p) {
        setX(p.x);
        setY(p.y);
    }
}
