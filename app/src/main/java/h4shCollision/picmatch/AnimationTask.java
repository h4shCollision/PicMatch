package h4shCollision.picmatch;

import android.animation.Animator;
import android.animation.ValueAnimator;

public class AnimationTask implements Runnable, Animator.AnimatorListener {
    static GameGUIManager gm;
    private Point a, b;
    private int[][][] before =new int[gm.sizeh][gm.sizev][2],after=new int[gm.sizeh][gm.sizev][2];

    AnimationTask(Point a, Point b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public void run() {
        f();
    }

    public void f() {
        gm.getButtonAt(a).setContent(0);
        gm.getButtonAt(b).setContent(0);
        gm.gc.gravityShift(a);
        gm.gc.gravityShift(b);
        ValueAnimator va=ValueAnimator.ofInt(0,gm.width);
        va.setDuration(Game.ANIMATION_DURATION);
        va.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                int[][][] b=AnimationTask.this.before, a=AnimationTask.this.after;
                for (int i = 0; i < gm.sizeh; i++) {
                    for (int j = 0; j < gm.sizev; j++) {
                        b[i][j][0]=(int)gm.buttons[i][j].getX();
                        b[i][j][1]=(int)gm.buttons[i][j].getY();
                        a[i][j][0]=gm.xLoc(gm.buttons[i][j].getPoint().getX());
                        a[i][j][1]=gm.yLoc(gm.buttons[i][j].getPoint().getY());
                    }
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                gm.refresh();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                gm.refresh();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int a= (int) animation.getAnimatedValue();
                int[][][] before=AnimationTask.this.before,after=AnimationTask.this.after;
                for (int i = 0; i < gm.sizeh; i++) {
                    for (int j = 0; j < gm.sizev; j++) {
                        int x=DrawingView.getBetween(before[i][j][0],after[i][j][0],a,gm.width);
                        int y=DrawingView.getBetween(before[i][j][1],after[i][j][1],a,gm.width);
                        gm.buttons[i][j].setLocation(x,y);
                    }
                }
            }
        });
        va.start();
        if (gm.gc.getTotal() == 0) {
            gm.context.endGame(true);
        } else {
            gm.startCheck();
        }
    }

    @Override
    public void onAnimationStart(Animator animation) {
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        f();
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        f();
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
    }
}
