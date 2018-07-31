package h4shCollision.picmatch;

public class Combo {

    Game g;
    Thread t;
    public void resume(Game game){
        g=game;
    }
    public void pause(){}
    public void renew(){}
    private class Progress implements Runnable{

        @Override
        public void run() {

        }
    }
}
