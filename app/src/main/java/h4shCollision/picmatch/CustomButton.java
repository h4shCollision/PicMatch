package h4shCollision.picmatch;

import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

public class CustomButton extends Button {
    static Game g;
    static GameGUIManager gm;
    static final int SELECT = 0xFFFFFFFF;
    static final int HINT = 0xFF00FF00;
    static final int SWAP_SELECT = 0xFF00FFFF;
    static final int DESELECT = 0xFF000000;
    private Block block;

    public CustomButton(Block b, Object l) {
        super(g);
        block = b;
        if (!b.isEmpty()) {
            setBackgroundColor(DESELECT);
            setTextColor(Color.BLUE);
            setPadding(8, 8, 8, 8);
            setOnClickListener((View.OnClickListener) (l));
        } else {
            setVisibility(View.GONE);
        }
        setContent(b.getContent());
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        g.gm.width = w;
    }

    public void select() {
        setBackgroundColor(SELECT);
    }

    public void deselect() {
        setBackgroundColor(DESELECT);
    }

    public void setContent(int n) {
        if (n == 0) {
            setVisibility(View.GONE);
        } else {
            setVisibility(View.VISIBLE);
            block.setContent(n);
            setText(Integer.toString(n));
        }
    }

    public void showHint() {
        setBackgroundColor(HINT);
    }

    public void showSwap() {
        setBackgroundColor(SWAP_SELECT);
    }

    public Block getPoint() {
        return block;
    }

    public void refresh(){
        setContent(block.getContent());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(gm.width, gm.width);
        params.leftMargin = gm.x + block.getX() * gm.width;
        params.topMargin = gm.y + block.getY() * gm.width;
        setLayoutParams(params);
    }

    public void setLocation(Point p){
        block.setLocation(p);
        refresh();
    }

    public void setLocation(int x,int y){
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(gm.width, gm.width);
        params.leftMargin = x;
        params.topMargin = y;
        setLayoutParams(params);
    }
}
