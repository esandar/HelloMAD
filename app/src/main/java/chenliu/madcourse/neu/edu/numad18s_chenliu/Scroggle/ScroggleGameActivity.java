package chenliu.madcourse.neu.edu.numad18s_chenliu.Scroggle;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import chenliu.madcourse.neu.edu.numad18s_chenliu.R;

import static chenliu.madcourse.neu.edu.numad18s_chenliu.R.raw.background_mansu;
import static chenliu.madcourse.neu.edu.numad18s_chenliu.R.raw.background_wg;


public class ScroggleGameActivity extends Activity {
    public static final String KEY_RESTORE = "key_restore";
    public static final String PREF_RESTORE = "pref_restore";
    public static MediaPlayer mMediaPlayer;
    private Handler mHandler = new Handler();
    private ScroggleGameFragment mGameFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.wordgame_activity_game);
        mGameFragment = (ScroggleGameFragment) getFragmentManager()
                .findFragmentById(R.id.wd_fragment_game);

        boolean restore = getIntent().getBooleanExtra(KEY_RESTORE, false);
        if (restore) {
            String gameData = getPreferences(MODE_PRIVATE)
                    .getString(PREF_RESTORE, null);
            if (gameData != null) {
                mGameFragment.putState(gameData);
            }
        }
        else{
            ScroggleGameFragment.currentScore=0;
        }

        Log.d("Wordgame", "restore = " + restore);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mMediaPlayer = MediaPlayer.create(this, background_mansu);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(null);
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mMediaPlayer.release();
        /*
        need a function to update the small board tile
         */
        String gameData = mGameFragment.getState();
        getPreferences(MODE_PRIVATE).edit()
                .putString(PREF_RESTORE, gameData)
                .commit();
        Log.d("Wordgame", "state = " + gameData);
        //   System.exit(0);
    }

    public void restartGame() {
        mGameFragment.restartGame();
    }
}
