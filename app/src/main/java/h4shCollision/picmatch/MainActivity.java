package h4shCollision.picmatch;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import java.io.File;

public class MainActivity extends ActionBarActivity {
    ProgressBar loading;
    int status;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loading = (ProgressBar) findViewById(R.id.loadingBar);

        new Thread(new Loading()).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    private class Loading implements Runnable{
        private boolean update(){
            if(status<loading.getMax()){
                status++;
                handler.post(new Runnable() {
                    public void run() {
                        loading.setProgress(status);
                    }
                });
                return true;
            }
            return false;
        }

        @Override
        public void run() {
            ProgressManager.loadHS(new File(getFilesDir(), "file.txt"));
            while (update()){

            }
            handler.post(new Runnable() {
                public void run() {
                    startActivity(new Intent(MainActivity.this,MenuActivity.class));
                }
            });
        }
    }
}
