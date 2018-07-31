package h4shCollision.picmatch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MenuActivity extends ActionBarActivity {
    final String FileName = "Data";
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        sharedPref = this.getSharedPreferences(FileName, Context.MODE_PRIVATE);
        ProgressManager.read(sharedPref);
        FinishGameActivity.ma=this;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ProgressManager.write(sharedPref);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void play(View v) {
        startGame(ProgressManager.getLevelProgress());
    }

    void startGame(int i) {
        if (i <= ProgressManager.MAX_LEVEL) {
            Intent intent = new Intent(this, Game.class);
            intent.putExtra(Game.LVL,i);
            startActivity(intent);
        }
    }

    public void about(View v) {
        Intent intent = new Intent(this, About.class);
        startActivity(intent);
    }

    public void level(View v){
        Intent intent = new Intent(this, Levels.class);
        startActivity(intent);
    }
}
