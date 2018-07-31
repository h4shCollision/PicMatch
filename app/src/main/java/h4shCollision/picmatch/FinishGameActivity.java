package h4shCollision.picmatch;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FinishGameActivity extends ActionBarActivity {
    static MenuActivity ma;
    private int score, curTime, totTime, lev;
    private boolean empty, countdown, win = false;
    static final String scoreStr = "s", curTimeStr = "c", totTimeStr = "t", emptyStr = "e", countDownStr = "d";
    private static final int TIMEBONUS = 2;
    private Button nxt;
    private TextView scoreText, winloseText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_game);
        nxt = (Button) findViewById(R.id.buttonRight);
        scoreText = (TextView) findViewById(R.id.finalscore);
        winloseText = (TextView) findViewById(R.id.winLose);
        score = getIntent().getIntExtra(scoreStr, 0);
        curTime = getIntent().getIntExtra(curTimeStr, 0);
        totTime = getIntent().getIntExtra(totTimeStr, 0);
        empty = getIntent().getBooleanExtra(emptyStr, false);
        countdown = getIntent().getBooleanExtra(countDownStr, false);
        lev = getIntent().getIntExtra(Game.LVL, 1);
        if (countdown) {
            score += curTime * TIMEBONUS;
        }
        System.out.println(scoreText);
        scoreText.setText(Integer.toString(score));
        if (empty && (curTime >= 0 || !countdown)) {
            win = true;
            ProgressManager.winUpdate(lev, score);
            if (ProgressManager.getLevelProgress() == ProgressManager.MAX_LEVEL) {
                nxt.setText("Replay");
            }
            winloseText.setText("winsxcvbnkiuygvbn"+lev);
        } else {
            ProgressManager.lostUpdate(lev, score);
            nxt.setText("Replay");
            winloseText.setText("lost"+lev);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_finish_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void menu(View view) {
        this.finish();
    }

    public void next(View view) {
        ma.startGame(ProgressManager.getLevelProgress());
        this.finish();
    }
}
