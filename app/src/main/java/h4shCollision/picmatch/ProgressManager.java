package h4shCollision.picmatch;

import android.content.SharedPreferences;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ProgressManager {
    static final int MAX_LEVEL = 20;
    private static final String nxtlvStr = "nextLevel";
    private static final String hintsStr = "hint";
    private static final String shufflesStr = "shuffle";
    private static final String removesStr = "remove";
    private static final String switchStr = "switch";
    private static final String hpStr = "hasplayed";
    private static int nextLevel = 1;
    private static int hints = 1;
    private static int shuffles = 1;
    private static int switches = 1;
    private static int removes = 1;
    private static boolean hasPlayed = false;
    private static int[] highscores = new int[MAX_LEVEL];

    static void read(SharedPreferences sp) {
        nextLevel = sp.getInt(nxtlvStr, nextLevel);
        hints = sp.getInt(hintsStr, hints);
        shuffles = sp.getInt(shufflesStr, shuffles);
        switches = sp.getInt(switchStr, switches);
        removes = sp.getInt(removesStr, removes);
        hasPlayed = sp.getBoolean(hpStr, hasPlayed);
    }

    static void write(SharedPreferences sp) {
        SharedPreferences.Editor ed = sp.edit();
        ed.putInt(nxtlvStr, ProgressManager.nextLevel);
        ed.putInt(hintsStr, hints);
        ed.putInt(shufflesStr, shuffles);
        ed.putInt(switchStr, switches);
        ed.putInt(removesStr, removes);
        ed.putBoolean(hpStr, hasPlayed);
        ed.apply();
    }

    static void winUpdate(int lev, int score) {
        if (lev == nextLevel) {
            System.out.println(lev);
            if (nextLevel < MAX_LEVEL) {
                nextLevel++;
                hasPlayed = false;
            }
        }
        highscores[lev - 1] = Math.max(highscores[lev - 1], score);
    }

    static void lostUpdate(int lev, int score) {
        if (lev == nextLevel) {
            hasPlayed = true;
            highscores[lev - 1] = Math.max(highscores[lev - 1], score);
        }
    }

    static int getLevelProgress() {
        return nextLevel;
    }

    public static int getHints() {
        return hints;
    }

    public static void setHints(boolean increase) {
        ProgressManager.hints += increase ? 1 : -1;
    }

    public static int getShuffles() {
        return shuffles;
    }

    public static void setShuffles(boolean increase) {
        ProgressManager.hints += increase ? 1 : -1;
    }

    public static int getSwitches() {
        return switches;
    }

    public static void setSwitches(boolean increase) {
        ProgressManager.hints += increase ? 1 : -1;
    }

    public static int getRemoves() {
        return removes;
    }

    public static void setRemoves(boolean increase) {
        ProgressManager.hints += increase ? 1 : -1;
    }

    public static void loadHS(File f) {
        try {
            if (f != null && f.exists() && !f.isDirectory()) {
                BufferedReader br = new BufferedReader(new FileReader(f));
                int n = Integer.parseInt(br.readLine());
                for (int i = 0; i < n && i < MAX_LEVEL; i++) {
                    highscores[i] = Integer.parseInt(br.readLine());
                }
            }
        } catch (Exception e) {
            writeHS(f);
        }
    }

    public static void writeHS(File f) {
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            PrintWriter pw=new PrintWriter(f);
            pw.println(MAX_LEVEL);
            for (int i = 0;  i < MAX_LEVEL; i++) {
                pw.write(highscores[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
