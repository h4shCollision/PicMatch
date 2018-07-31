package h4shCollision.picmatch;

import java.util.ArrayList;

public class PointGroup {
    int number;
    ArrayList<Block> points;

    PointGroup(int n) {
        number = n;
        points=new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PointGroup that = (PointGroup) o;

        if (number != that.number) return false;

        return true;
    }

    public boolean remove(Block p){
        return points.remove(p);
    }
}
