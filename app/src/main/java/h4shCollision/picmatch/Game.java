package h4shCollision.picmatch;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import h4shCollision.picmatch.util.SystemUiHider;

import static android.view.animation.Animation.AnimationListener;

public class Game extends Activity {

    static final String LVL = "lvl";
    static final int ANIMATION_DURATION = 500;
    private static final int BACK_DELAY = 2500;
    protected GameGUIManager gm;
    DrawingView grid;
    Handler h = new Handler();
    Toast t;
    private int time = 0, currentLevel;
    private TextView timerText, scoreText;
    private SystemUiHider hider;
    private long pressTime;
    private TextView tv;
    private Timer timer;
    private boolean countDown;
    private int totalScore = 0;
    RelativeLayout layout;
    boolean showPause = true;
    private Button swapButton;
    private ProgressBar pb;
    LinkedList<ScoreAnimationView> pending = new LinkedList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game);

        final View contentView = findViewById(R.id.fullscreen);

        hider = SystemUiHider.getInstance(this, contentView, SystemUiHider.FLAG_FULLSCREEN);
        hider.setup();

        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hider.toggle();
            }
        });

        tv = (TextView) findViewById(R.id.gravity);

        layout = (RelativeLayout) findViewById(R.id.fullscreen);
        layout.setOnTouchListener(new SwipeListener(this));

        currentLevel = getIntent().getIntExtra(LVL, ProgressManager.getLevelProgress());
        grid = (DrawingView) findViewById(R.id.grid);
        gm = new GameGUIManager(this, currentLevel);

        tv = (TextView) findViewById(R.id.gravity);
        if (gm.gc.getGravity() != 5) {
            tv.setText("" + gm.gc.getGravity());
        }
        timerText = (TextView) findViewById(R.id.timer);

        t = Toast.makeText(this, "Leaving now will cost you this game, press again to leave. ", Toast.LENGTH_SHORT);
        gm.toast = Toast.makeText(this, "Out of moves, shuffle, switch or remove to continue", Toast.LENGTH_SHORT);

        countDown = gm.gc.countdown;
        if (countDown) {
            time = gm.gc.timeLim;
        }
        timerText.setText(Integer.toString(time));

        scoreText = (TextView) findViewById(R.id.scoreTextView);
        scoreText.setText(Integer.toString(totalScore));
        swapButton = (Button) findViewById(R.id.switchButton);
        pb = (ProgressBar) findViewById(R.id.comboBar);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onPause() {
        System.out.println("pause");
        timer.cancel();
        if (showPause) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Game Paused");
            builder.setItems(new CharSequence[]{"Menu", "Resume"}, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {
                        System.out.println("menu");
                    } else if (which == 1) {
                        Game.this.onResume();
                        System.out.println("resume");
                    }
                }
            });
            builder.create().show();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (showPause) {
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (countDown) {
                        time--;
                        h.post(new Runnable() {
                            @Override
                            public void run() {
                                timerText.setText(time + "");
                            }
                        });
                        if (time <= 0) {
                            h.post(new Runnable() {
                                @Override
                                public void run() {
                                    endGame(gm.gc.getTotal() == 0);
                                }
                            });
                        }
                    } else {
                        time++;
                        h.post(new Runnable() {
                            @Override
                            public void run() {
                                timerText.setText(time + "");
                            }
                        });
                    }
                }
            }, 0, 1000);
        }
    }

    public void hint(View view) {
        gm.showHint();
    }

    public void shuffle(View view) {
        gm.shuffle();
    }

    public void remove(View view) {
        gm.swap = false;
        gm.remove();
    }

    public void swap(View view) {
        gm.remove = false;
        gm.swap();
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() < pressTime + BACK_DELAY) {
            t.cancel();
            showPause = false;
            super.onBackPressed();
        } else {
            pressTime = System.currentTimeMillis();
            t.show();
        }
    }

    @Override
    public void onDestroy() {
        if (gm.gc.getTotal() > 0) {
            ProgressManager.lostUpdate(gm.level, totalScore);
        }
        super.onDestroy();
    }

    @Override
    public void onStop() {
        showPause = false;
        super.onStop();
    }

    void handleSwipe(int s) {
        if (gm.gc.getGravity() == 5) {
            gm.swipe(s);
            tv.setText(s + "");
        }
    }

    public void pauseGame(View view) {
        this.onPause();
    }

    void add(int score) {
        ScoreAnimationView tv = new ScoreAnimationView(this, score);
        tv.setTextColor(0xFF00FFFF);
        tv.setText('+' + Integer.toString(score));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        params.topMargin = 100;
        tv.setLayoutParams(params);

        layout.addView(tv);

        TranslateAnimation ta = new TranslateAnimation(0, 0, 100, 0);
        ta.setDuration(ANIMATION_DURATION);
        ta.setFillAfter(true);
        tv.startAnimation(ta);

        Animation fi = new AlphaAnimation(0, 1);
        fi.setInterpolator(new DecelerateInterpolator());
        fi.setDuration(ANIMATION_DURATION / 2);

        Animation fo = new AlphaAnimation(1, 0);
        fo.setInterpolator(new AccelerateInterpolator());
        fo.setStartOffset(ANIMATION_DURATION / 2);
        fo.setDuration(ANIMATION_DURATION / 2);
        fo.setAnimationListener(new ScoreAnimationListener(tv, score));

        AnimationSet anim = new AnimationSet(false);
        anim.addAnimation(fi);
        anim.addAnimation(fo);
        anim.addAnimation(ta);
        tv.setAnimation(anim);

        pending.add(tv);
    }

    void endGame(boolean empty) {
        timer.cancel();
        showPause = false;
        for (ScoreAnimationView tv : pending) {
            totalScore += tv.n;
        }
        Intent i = new Intent(this, FinishGameActivity.class);
        i.putExtra(FinishGameActivity.emptyStr, empty);
        i.putExtra(FinishGameActivity.countDownStr, countDown);
        i.putExtra(FinishGameActivity.curTimeStr, time);
        i.putExtra(FinishGameActivity.scoreStr, totalScore);
        i.putExtra(FinishGameActivity.totTimeStr, gm.gc.timeLim);
        i.putExtra(Game.LVL, currentLevel);
        startActivity(i);
        this.finish();
    }

    private class ScoreAnimationListener implements AnimationListener {
        ScoreAnimationView v;
        int n;

        public ScoreAnimationListener(ScoreAnimationView tv, int score) {
            v = tv;
            n = score;
        }

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            totalScore += n;
            layout.removeView(v);
            scoreText.setText(Integer.toString(totalScore));
            pending.remove(v);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    public class ScoreAnimationView extends TextView {
        int n;

        public ScoreAnimationView(Context context, int num) {
            super(context);
            n = num;
        }
    }

    public void comboUpdate(final int x) {
        h.post(new Runnable() {
            @Override
            public void run() {
                pb.setProgress(x);
            }
        });
    }
}
