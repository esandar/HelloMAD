package chenliu.madcourse.neu.edu.numad18s_chenliu.Scroggle;

/***
 * Excerpted from "Hello, Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material,
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose.
 * Visit http://www.pragmaticprogrammer.com/titles/eband4 for more book information.
 ***/

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;

import chenliu.madcourse.neu.edu.numad18s_chenliu.FCM.FCMActivity;
import chenliu.madcourse.neu.edu.numad18s_chenliu.GlobalClass;
import chenliu.madcourse.neu.edu.numad18s_chenliu.R;

import static android.content.ContentValues.TAG;

public class ScroggleMainActivity extends Activity {
    public MediaPlayer mMediaPlayer;
    // ...

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wordgame_activity_main);
        // Subscribe to cloud messaging
        // [START subscribe_topics]
        FirebaseMessaging.getInstance().subscribeToTopic("Scroggle");
        // [END subscribe_topics]

        // Log and toast
        String msg = getString(R.string.msg_subscribed);
        Log.d(TAG, msg);
        Toast.makeText(ScroggleMainActivity.this, msg, Toast.LENGTH_SHORT).show();

        //Load dictionary
        if (GlobalClass.list.isEmpty()) {
            InputStream strF = null;
            try {
                strF = getResources().getAssets().open("hashmap");
                ObjectInputStream ois=new ObjectInputStream(strF);
                GlobalClass.list = (HashMap<String,ArrayList<String>>)ois.readObject();
                ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch(ClassNotFoundException ce){
                System.err.print(ce);
            }
        }
        //load nineletterwords list
        if(GlobalClass.nineLetterWords.isEmpty()){
            try {
                InputStream strF = getResources().getAssets().open("nineLetterWords");
                ObjectInputStream ois=new ObjectInputStream(strF);

                GlobalClass.nineLetterWords = (ArrayList<String>)ois.readObject();
                ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch(ClassNotFoundException ce){
                System.err.print(ce);
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMediaPlayer = MediaPlayer.create(this, R.raw.background_wg);
        mMediaPlayer.setVolume(0.5f, 0.5f);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mMediaPlayer.release();
    }
}