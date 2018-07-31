package h4shCollision.picmatch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;


public class Levels extends ActionBarActivity {

    GridView gv;
    GVAdapter gva;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels);
        gv = (GridView) findViewById(R.id.lvlgv);
        gv.setNumColumns(5);
        gv.setAdapter(gva = new GVAdapter());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_levels, menu);
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

    private class GVAdapter extends BaseAdapter {

        Button[] b = new Button[ProgressManager.MAX_LEVEL];

        private GVAdapter() {
            for (int i = 0; i < ProgressManager.MAX_LEVEL; i++) {
                b[i] = new Button(Levels.this);
                b[i].setText("" + (i + 1));
                b[i].setOnClickListener(new OCL(i + 1));
                if (i >= ProgressManager.getLevelProgress()) {
                    b[i].setEnabled(false);
                }
            }
        }

        @Override
        public int getCount() {
            return ProgressManager.MAX_LEVEL;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return b[position];
        }

        void refresh() {
            for (int i = 0; i < ProgressManager.MAX_LEVEL; i++) {
                b[i].setEnabled(i < ProgressManager.getLevelProgress());
            }
        }

        private class OCL implements View.OnClickListener {
            int n;

            OCL(int i) {
                n = i;
            }

            @Override
            public void onClick(View v) {
                Intent i = new Intent(Levels.this, Game.class);
                i.putExtra(Game.LVL, n);
                Levels.this.startActivity(i);
            }
        }
    }

    @Override
    public void onRestart(){
        super.onRestart();
        gva.refresh();
    }

}
