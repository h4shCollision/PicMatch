package h4shCollision.picmatch;

import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class SwipeListener implements View.OnTouchListener {
    Game game;
    GestureDetectorCompat detector;

    public SwipeListener(Game g) {
        game = g;
        detector = new GestureDetectorCompat(g, new OGL());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return detector.onTouchEvent(event);
    }

    private class OGL implements GestureDetector.OnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float dx = velocityX, dy = velocityY;
            double ang = Math.abs(Math.toDegrees(Math.atan(dy / dx)));
            if (ang <= 30) {
                if (dx > 0)
                    game.handleSwipe(1);
                else
                    game.handleSwipe(3);
                return true;
            } else if (ang >= 60) {
                if (dy < 0)
                    game.handleSwipe(2);
                else
                    game.handleSwipe(4);
                return true;
            }
            return false;
        }
    }
}
