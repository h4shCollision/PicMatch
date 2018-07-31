package h4shCollision.picmatch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class GridContent {
    ArrayList<PointGroup> groups;
    Block[][] content;
    private int sizeh,sizev,gravity,dynamicG,total;
    int timeLim=60;
    private boolean[][] grid;
    private int[][] types;
    boolean countdown =true;

    public GridContent(InputStream is) {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        try {
            String[] st = br.readLine().split(" ");
            sizeh = Integer.parseInt(st[0]);
            sizev = Integer.parseInt(st[1]);
            gravity = Integer.parseInt(br.readLine());
            int n = Integer.parseInt(br.readLine());
            types = new int[n][2];
            for (int i = 0; i < n; i++) {
                st = br.readLine().split(" ");
                types[i][0] = Integer.parseInt(st[0]);
                types[i][1] = Integer.parseInt(st[1]);
                total += types[i][1];
            }
            grid = new boolean[sizeh][sizev];
            for (int i = 0; i < sizev; i++) {
                st = br.readLine().split(" ");
                for (int j = 0; j < sizeh; j++) {
                    grid[j][i] = st[j].equals("1");
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        content = new Block[sizeh][sizev];
        groups = new ArrayList<>();
        for (int i = 0; i < types.length; i++) {
            groups.add(new PointGroup(types[i][0]));
        }
        int[] numberArray = new int[total];
        int c = 0;
        for (int i = 0; i < types.length; i++) {
            Arrays.fill(numberArray, c, c + types[i][1], types[i][0]);
            c += types[i][1];
        }
        c = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j]) {
                    Block b = new Block(i, j, numberArray[c]);
                    setBlockAt(b, b);
                    getPointGroup(numberArray[c]).points.add(b);
                    c++;
                } else {
                    setBlockAt(new Point(i, j), new Block(i, j));
                }
            }
        }
        if (gravity == Gravity.DYNAMIC) {
            dynamicG = Gravity.DOWN;
        } else {
            dynamicG = gravity;
        }
    }

    public int getSizev() {
        return sizev;
    }

    public int getSizeh() {
        return sizeh;
    }

    public int getTotal() {
        return total;
    }

    public void shuffle() {
        int c = 0;
        Block[] blockArray = new Block[total];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j]) {
                    blockArray[c] = content[i][j];
                    c++;
                }
            }
        }
        for (int i = 0; i < blockArray.length; i++) {
            int t = (int) (Math.random() * total);
            swap(blockArray[i], blockArray[t]);
        }
    }

    public Block getBlockAt(Point p) {
        return content[p.getX()][p.getY()];
    }

    private void setBlockAt(Point p, Block b) {
        content[p.getX()][p.getY()] = b;
    }

    void swap(Point a, Point b) {
        Block b1 = getBlockAt(a), b2 = getBlockAt(b);
        Point copy=b1.clone();
        b1.setLocation(b2);
        b2.setLocation(copy);
        setBlockAt(b1, b1);
        setBlockAt(b2, b2);
    }

    Point[] checkValid(Point a, Point b) {
        int[][][] check = new int[2][sizeh][sizev];//0 reach horizontal, 1 reach vertical
        for (int i = 0; i < sizeh; i++) {
            for (int j = 0; j < sizev; j++) {
                check[0][i][j] = 4;
                check[1][i][j] = 4;
            }
        }
        Point[][][] source = new Point[2][sizeh][sizev];
        check[0][a.getX()][a.getY()] = 0;
        check[1][a.getX()][a.getY()] = 0;
        ArrayList<Point> checkV = new ArrayList<>(), checkH = new ArrayList<>();
        checkV.add(a);
        checkH.add(a);
        for (int i = 1; i < 4; i++) {
            ArrayList<Point> vtemp = new ArrayList<>(), htemp = new ArrayList<>();
            for (Point p : checkH) {
                int x = p.getX();
                while (x > 0) {
                    x--;
                    if (check[0][x][p.getY()] > i) {
                        check[0][x][p.getY()] = i;
                        source[0][x][p.getY()] = p;
                        if (content[x][p.getY()].isEmpty())
                            vtemp.add(new Point(x, p.getY()));
                    }
                    if (!content[x][p.getY()].isEmpty()) {
                        break;
                    }
                }
                x = p.getX() + 1;
                while (x < sizeh) {
                    if (check[0][x][p.getY()] > i) {
                        check[0][x][p.getY()] = i;
                        source[0][x][p.getY()] = p;
                        if (content[x][p.getY()].isEmpty())
                            vtemp.add(new Point(x, p.getY()));
                    }
                    if (!content[x][p.getY()].isEmpty()) {
                        break;
                    }
                    x++;
                }
            }
            for (Point p : checkV) {
                int y = p.getY();
                while (y > 0) {
                    y--;
                    if (check[1][p.getX()][y] > i) {
                        check[1][p.getX()][y] = i;
                        source[1][p.getX()][y] = p;
                        if (content[p.getX()][y].isEmpty())
                            htemp.add(new Point(p.getX(), y));
                    }
                    if (!content[p.getX()][y].isEmpty()) {
                        break;
                    }
                }
                y = p.getY() + 1;
                while (y < sizev) {
                    if (check[1][p.getX()][y] > i) {
                        check[1][p.getX()][y] = i;
                        source[1][p.getX()][y] = p;
                        if (content[p.getX()][y].isEmpty())
                            htemp.add(new Point(p.getX(), y));
                    }
                    if (!content[p.getX()][y].isEmpty()) {
                        break;
                    }
                    y++;
                }
            }
            checkH = htemp;
            checkV = vtemp;
        }
        if (check[0][b.getX()][b.getY()] < 4 && check[1][b.getX()][b.getY()]>check[0][b.getX()][b.getY()]) {
            int c = check[0][b.getX()][b.getY()];
            int i = 0;
            Point[] path = new Point[c + 1];
            path[c] = b;
            while (c > 0) {
                c--;
                path[c] = source[i % 2][path[c + 1].getX()][path[c + 1].getY()];
                i++;
            }
            return path;
        } else if (check[1][b.getX()][b.getY()] < 4) {
            int c = check[1][b.getX()][b.getY()];
            int i = 1;
            Point[] path = new Point[c + 1];
            path[c] = b;
            while (c > 0) {
                c--;
                path[c] = source[i % 2][path[c + 1].getX()][path[c + 1].getY()];
                i++;
            }
            return path;
        } else {
            return null;
        }
    }

    public void remove(Point p, Point q) {
        remove(p);
        remove(q);
    }

    private void remove(Point p){
        grid[p.getX()][p.getY()] = false;
        Block b = getBlockAt(p);
        int c = b.getContent();
        groups.get(groups.indexOf(new PointGroup(c))).remove(b);
        b.remove();
        total-=1;
    }

    PointGroup getPointGroup(int c) {
        return groups.get(groups.indexOf(new PointGroup(c)));
    }

    void gravityShift(Point p) {
        if (dynamicG == Gravity.DOWN || dynamicG == Gravity.UP) {
            int i = 3 - dynamicG;
            int z = p.getY(), y = z + i;
            while (y > 0 && y < sizev - 1) {
                if (grid[p.getX()][y]) {
                    grid[p.getX()][y] = false;
                    grid[p.getX()][z] = true;
                    swap(new Point(p.getX(), y), new Point(p.getX(), z));
                    z += i;
                }
                y += i;
            }
        } else if (dynamicG == Gravity.LEFT || dynamicG == Gravity.RIGHT) {
            int i = dynamicG - 2;
            int z = p.getX(), x = z + i;
            while (x > 0 && x < sizeh - 1) {
                if (grid[x][p.getY()]) {
                    grid[x][p.getY()] = false;
                    grid[z][p.getY()] = true;
                    swap(new Point(x, p.getY()), new Point(z, p.getY()));
                    z += i;
                }
                x += i;
            }
        } else if (dynamicG == Gravity.HORIZONTAL_CENTER || dynamicG == Gravity.HORIZONTAL_SEPARATE) {
            int mid = sizeh / 2, mn = 1, mx = mid - 1, i = dynamicG - 7;
            if (p.x >= mid) {
                mn = mid;
                mx = sizeh - 2;
                i = -i;
            }
            int z = p.getX(), x = z + i;
            while (x >= mn && x <= mx) {
                if (grid[x][p.getY()]) {
                    grid[x][p.getY()] = false;
                    grid[z][p.getY()] = true;
                    swap(new Point(x, p.getY()), new Point(z, p.getY()));
                    z += i;
                }
                x += i;
            }
        } else if (dynamicG == Gravity.VERTICAL_CENTER || dynamicG == Gravity.VERTICAL_SEPARATE) {
            int mid = sizev / 2, mn = 1, mx = mid - 1, i = dynamicG-8 ;
            if (p.y >= mid) {
                mn = mid;
                mx = sizev - 2;
                i = -i;
            }
            int z = p.getY(), y = z + i;
            while (y >= mn && y <= mx) {
                if (grid[p.getX()][y]) {
                    grid[p.getX()][y] = false;
                    grid[p.getX()][z] = true;
                    swap(new Point(p.getX(),y), new Point(p.getX(), z));
                    z += i;
                }
                y += i;
            }
        }
    }

    public void setDynamicG(int n) {
        if (gravity == Gravity.DYNAMIC) {
            this.dynamicG = n;
            if (dynamicG == Gravity.DOWN || dynamicG == Gravity.UP) {
                int i = 3 - dynamicG;
                for (int x = 1; x < sizeh - 1; x++) {
                    for (int cur = dynamicG == Gravity.DOWN ? sizev - 2 : 1; cur > 0 && cur < sizev - 1; cur += i) {
                        if (!grid[x][cur]) {
                            gravityShift(new Point(x, cur));
                        }
                    }
                }
            } else if (dynamicG == Gravity.LEFT || dynamicG == Gravity.RIGHT) {
                int i = dynamicG - 2;
                for (int y = 1; y < sizev - 1; y++) {
                    for (int cur = dynamicG == Gravity.RIGHT ? sizeh - 2 : 1; cur > 0 && cur < sizeh - 1; cur += i) {
                        if (!grid[cur][y]) {
                            gravityShift(new Point(cur, y));
                        }
                    }
                }
            }
        }
    }

    public int getGravity() {
        return gravity;
    }

    class Gravity {
        static final int NONE = 0;
        static final int DOWN = 4;
        static final int UP = 2;
        static final int LEFT = 3;
        static final int RIGHT = 1;
        static final int DYNAMIC = 5;
        static final int HORIZONTAL_CENTER = 6;
        static final int VERTICAL_CENTER = 7;
        static final int HORIZONTAL_SEPARATE = 8;
        static final int VERTICAL_SEPARATE = 9;
    }
}
