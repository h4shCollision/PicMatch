package h4shCollision.picmatch;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;
import android.widget.Toast;

class GameGUIManager {

    static final long hintDelay = 1000;
    Game context;
    int sizeh, sizev, level;
    CustomButton[][] buttons;
    Point selected = null, temp1 = null, temp2;
    Thread checking;
    int width, dh, dw, x, y;
    GridContent gc;
    boolean swap = false, remove = false;
    Point[] hints = new Point[2];
    Toast toast;

    public GameGUIManager(Game c, int l) {
        context = c;
        level = l;
        gc = new GridContent(context.getResources().openRawResource(context.getResources().getIdentifier("raw/l" + l, "raw", context.getPackageName())));
        sizeh = gc.getSizeh();
        sizev = gc.getSizev();
        gc.shuffle();
        android.graphics.Point p = new android.graphics.Point();
        context.getWindowManager().getDefaultDisplay().getSize(p);
        dw = p.x;
        dh = p.y;
        width = Math.min(p.x / sizeh, p.y / sizev);
        x = (dw - width * sizeh) / 2;
        y = (dh - width * sizev) / 2;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width * sizeh, width * sizev);
        params.leftMargin = x;
        params.topMargin = y;
        context.grid.setLayoutParams(params);
        CustomButton.g = context;
        CustomButton.gm = this;
        buttons = new CustomButton[sizeh][sizev];
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[0].length; j++) {
                buttons[i][j] = new CustomButton(gc.content[i][j], new Listener());
                params = new RelativeLayout.LayoutParams(width, width);
                params.leftMargin = x + i * width;
                params.topMargin = y + j * width;
                context.layout.addView(buttons[i][j], params);
            }
        }
        AnimationTask.gm = this;
        DrawingView.gm = this;
    }

    private void selectBlock(Point pt, boolean sel) {
        selectBlock(pt, sel, true);
    }

    private void selectBlock(Point pt, boolean sel, boolean normal) {
        if (pt != null) {
            if (sel) {
                selected = pt;
                if (normal) {
                    buttons[pt.getX()][pt.getY()].select();
                } else {
                    buttons[pt.getX()][pt.getY()].showSwap();
                }
            } else {
                selected = null;
                buttons[pt.getX()][pt.getY()].deselect();
            }
        }
    }

    void showHint() {
        if (checking == null) {
            checking = new Thread(new Checker());
            checking.start();
        }
        if (checking.isAlive()) {
            try {
                checking.join();
            } catch (InterruptedException e) {
                Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
            }
        }
        selectBlock(selected, false);
        if (hints[0] != null) {
            buttons[hints[0].getX()][hints[0].getY()].showHint();
            buttons[hints[1].getX()][hints[1].getY()].showHint();
            context.h.postDelayed(new Runnable() {

                @Override
                public void run() {
                    buttons[hints[0].getX()][hints[0].getY()].deselect();
                    buttons[hints[1].getX()][hints[1].getY()].deselect();
                }
            }, hintDelay);
        }
    }

    void shuffle() {
        gc.shuffle();
        refresh();
    }

    void refresh() {
        CustomButton[][] copy=new CustomButton[sizeh][sizev];
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[0].length; j++) {
                CustomButton b=buttons[i][j];
                b.refresh();
                copy[b.getPoint().getX()][b.getPoint().getY()]=b;
                if (!gc.content[i][j].isEmpty()) {
                    if (new Point(i, j).equals(selected)) {
                        if (swap) {
                            b.showSwap();
                        } else {
                            b.select();
                        }
                    } else {
                        b.deselect();
                    }
                }
            }
        }
        startCheck();
        buttons=copy;
    }

    CustomButton getButtonAt(Point p) {
        return buttons[p.getX()][p.getY()];
    }

    /**
     * remove by "remove button"
     * 0 no block selected, 1 not removabe, 2 removes
     */
    int remove() {
        if (selected != null) {
            Point other;
            PointGroup pg = gc.getPointGroup(gc.getBlockAt(selected).getContent());
            if (pg.points.size() < 2) {
                buttons[selected.getX()][selected.getY()].deselect();
                return 1;
            }
            if (selected.equals(pg.points.get(0))) {
                other = pg.points.get(1);
            } else
                other = pg.points.get(0);
            buttons[other.getX()][other.getY()].select();
            removeBlocks(selected, other);
            selected = null;
            remove = false;
            return 2;
        } else {
            remove = true;
            return 0;
        }
    }

    void removeBlocks(Point p, Point q) {
        context.h.postDelayed(new AnimationTask(gc.getBlockAt(p), gc.getBlockAt(q)), Game.ANIMATION_DURATION);
        ScaleAnimation sa1 = new ScaleAnimation(1, 0, 1, 0, width / 2, width / 2);
        sa1.setDuration(Game.ANIMATION_DURATION);
        getButtonAt(p).startAnimation(sa1);
        getButtonAt(q).startAnimation(sa1);
        context.add(1);
        gc.remove(p, q);
        startCheck();
        checkspecial(gc.getBlockAt(p).getContent());
    }

    /**
     * blocks with numbers >20 has special features
     * 21 hint+1
     * 22 switches+1
     * 23 shuffles+1
     * 24 removes+1
     * usable only when first play the level
     */
    private void checkspecial(int content) {
        if (level < ProgressManager.getLevelProgress()) {
            return;
        }
        if (content == 21) {
            ProgressManager.setHints(true);
        } else if (content == 22) {
            ProgressManager.setSwitches(true);
        } else if (content == 23) {
            ProgressManager.setShuffles(true);
        } else if (content == 24) {
            ProgressManager.setRemoves(true);
        }
    }

    void swap() {
        if (swap) {
            if (selected != null)
                selectBlock(selected, false);
            swap = false;
        } else {
            swap = true;
            if (selected != null) {
                selectBlock(selected, true, false);
            }
        }
    }

    void startCheck() {
        if (checking != null && checking.isAlive()) {
            checking.interrupt();
        }
        checking = new Thread(new Checker());
        checking.start();
    }

    public void swipe(int n) {
        gc.setDynamicG(n);
        refresh();
    }

    private class Checker implements Runnable {
        public boolean stop = false;

        @Override
        public void run() {
            boolean b = false;
            outer:
            for (PointGroup g : gc.groups) {
                for (Point p1 : g.points) {
                    for (Point p2 : g.points) {
                        if (stop)
                            break;
                        if ((!Thread.interrupted()) && !p1.equals(p2) && gc.checkValid(p1, p2) != null) {
                            b = true;
                            hints[0] = p1;
                            hints[1] = p2;
                            break outer;
                        }
                    }
                }
            }
            if (gc.getTotal() > 0 && !b) {
                System.out.println("deadend");
                if (toast.getView().getWindowVisibility() != View.VISIBLE) {
                    toast.cancel();
                }
                toast.show();
            }
        }
    }

    public class Listener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            CustomButton cb= (CustomButton) v;
            Block tp = cb.getPoint();
            if (tp.isEmpty()) {
                return;
            }
            if (swap) {
                if (tp.equals(selected)) {
                    selectBlock(tp, false);
                } else if (selected == null) {
                    selectBlock(tp, true, false);
                } else {
                    buttons[tp.getX()][tp.getY()].showSwap();
                    temp1 = gc.getBlockAt(selected);
                    temp2 = tp;
                    gc.swap(tp, selected);
                    context.h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            CustomButton a = buttons[temp1.getX()][temp1.getY()], b = buttons[temp2.getX()][temp2.getY()];
                            Block b1 = a.getPoint(), b2 = b.getPoint();
                            a.deselect();
                            b.deselect();
                            a.refresh();
                            b.refresh();
                            buttons[b1.getX()][b1.getY()]=a;
                            buttons[b2.getX()][b2.getY()]=b;
                        }

                    }, Game.ANIMATION_DURATION);
                    selected = null;
                    swap = false;
                }
            } else {
                if (selected != null && selected.equals(tp)) {
                    selectBlock(tp, false);
                    selected = null;
                } else if (selected == null) {
                    selectBlock(tp, true);
                } else {
                    if (gc.getBlockAt(selected).getContent() == gc.getBlockAt(tp).getContent()) {
                        Point[] path = gc.checkValid(selected, tp);
                        if (path != null) {
                            buttons[tp.getX()][tp.getY()].select();
                            context.grid.setPath(path);
                            ValueAnimator va = ValueAnimator.ofInt(context.grid.lenhalf, 0);
                            va.setDuration(Game.ANIMATION_DURATION);
                            va.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    context.grid.setAnimationActive(true);
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    context.grid.setAnimationActive(false);
                                    context.grid.postInvalidate();
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {
                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {
                                }
                            });
                            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    context.grid.setProgress((Integer) animation.getAnimatedValue());
                                    context.grid.postInvalidate();
                                }
                            });
                            va.setInterpolator(new AccelerateInterpolator(1));
                            va.start();
                            removeBlocks(selected, tp);
                            selected = null;
                        } else {
                            selectBlock(selected, false);
                        }
                    } else {
                        selectBlock(selected, false);
                        selectBlock(tp, true);
                    }
                }
            }
        }
    }

    public int xLoc(int a){
        return a*width+x;
    }

    public int yLoc(int a){
        return a*width+y;
    }
}
