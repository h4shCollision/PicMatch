package h4shCollision.picmatch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class DrawingView extends View {
    private int[][] p1, p2;
    private int[] seg1, seg2;
    private Paint paint;
    static GameGUIManager gm;
    public int progress, lenhalf;
    private boolean animationActive = false;

    public DrawingView(Context context) {
        super(context);
        paintInit();
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paintInit();
    }

    public DrawingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paintInit();
    }

    void paintInit() {
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
    }

    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);
        if (animationActive) {
            Path pth1 = getPath(p1, seg1, progress), pth2 = getPath(p2, seg2, progress);
            c.drawPath(pth1, paint);
            c.drawPath(pth2, paint);
        }
    }

    public void setPath(Point[] path) {
        if (path != null) {
            lenhalf = 0;
            int n = path.length;
            int[][] pat = new int[n][2];//x,y
            int[] lentot = new int[n], lenseg = new int[n];
            lentot[0] = 0;
            for (int i = 0; i < n; i++) {
                pat[i][0] = path[i].getX() * gm.width + gm.width / 2;
                pat[i][1] = path[i].getY() * gm.width + gm.width / 2;
                if (i > 0) {
                    lenseg[i] = Math.abs(pat[i][0] - pat[i - 1][0]) + Math.abs(pat[i][1] - pat[i - 1][1]);
                    lentot[i] = lenseg[i] + lentot[i - 1];
                }
            }
            lenhalf = lentot[n - 1] / 2;
            for (int i = 0; i < n; i++) {
                if (lentot[i] > lenhalf) {
                    p1 = new int[i + 1][2];
                    seg1 = new int[i + 1];
                    p2 = new int[n - i + 1][2];
                    seg2 = new int[n - i + 1];
                    int tp = lenhalf-lentot[i-1], tt = lentot[i] - lentot[i - 1], x = getBetween(pat[i - 1][0], pat[i][0], tp, tt), y = getBetween(pat[i - 1][1], pat[i][1], tp, tt);
                    for (int j = 0; j < i; j++) {
                        p1[j][0] = pat[j][0];
                        p1[j][1] = pat[j][1];
                        seg1[j] = lentot[j];
                    }
                    p1[i][0] = x;
                    p1[i][1] = y;
                    seg1[i] = lenhalf;
                    for (int j = 0; n - 1 - j >= i; j++) {
                        p2[j][0] = pat[n - 1 - j][0];
                        p2[j][1] = pat[n - 1 - j][1];
                        seg2[j] = lentot[n - 1] - lentot[n - 1 - j];
                    }
                    p2[n - i][0] = x;
                    p2[n - i][1] = y;
                    seg2[n - i] = lenhalf;
                    break;
                }
            }
        }
    }

    public static int getBetween(int x1, int x2, int a, int t) {
        if (t != 0) {
            return x1 + (x2 - x1) * a / t;
        } else {
            return x1;
        }
    }

    private Path getPath(int[][] p, int[] seg, int prog) {
        Path pth = new Path();
        pth.moveTo(p[0][0], p[0][1]);
        for (int i = 1; i < p.length; i++) {
            if (seg[i] <= prog) {
                pth.lineTo(p[i][0], p[i][1]);
            } else {
                int t1 = prog - seg[i - 1], t2 = seg[i] - seg[i - 1];
                pth.lineTo(getBetween(p[i - 1][0], p[i][0], t1, t2), getBetween(p[i - 1][1], p[i][1], t1, t2));
                break;
            }
        }
        return pth;
    }

    public void setAnimationActive(boolean b) {
        animationActive = b;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
