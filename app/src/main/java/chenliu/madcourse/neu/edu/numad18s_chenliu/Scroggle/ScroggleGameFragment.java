package chenliu.madcourse.neu.edu.numad18s_chenliu.Scroggle;

/***
 * Excerpted from "Hello, Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material,
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose.
 * Visit http://www.pragmaticprogrammer.com/titles/eband4 for more book information.
 ***/
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import chenliu.madcourse.neu.edu.numad18s_chenliu.GlobalClass;
import chenliu.madcourse.neu.edu.numad18s_chenliu.R;


public class ScroggleGameFragment extends Fragment {
    static private int mLargeIds[] = {R.id.wd_large1, R.id.wd_large2, R.id.wd_large3,
            R.id.wd_large4, R.id.wd_large5, R.id.wd_large6, R.id.wd_large7, R.id.wd_large8,
            R.id.wd_large9,};
    static private int mSmallIds[] = {R.id.wd_small1, R.id.wd_small2, R.id.wd_small3,
            R.id.wd_small4, R.id.wd_small5, R.id.wd_small6, R.id.wd_small7, R.id.wd_small8,
            R.id.wd_small9,};
    private Handler mHandler = new Handler();
    private ScroggleTile mEntireBoard = new ScroggleTile(this);
    private ScroggleTile mLargeTiles[] = new ScroggleTile[9];
    private ScroggleTile mSmallTiles[][] = new ScroggleTile[9][9];
    private Set<ScroggleTile> mAvailable = new HashSet<ScroggleTile>();
    private int mSoundClick, mSoundSubmit, mSoundMiss, mSoundRewind;
    private SoundPool mSoundPool;
    private float mVolume = 1f;
    private int mLastLarge;
    private int mLastSmall;

    private String enteredStringSroggle="";
    private Button pause;
    private Button doneView;
    private static Boolean done = false;
    private static Boolean donePhaseTwo = false;
    public static int touchedLargeTile =0;
    private boolean atLeastOneClicked = false;
    public static int [] touchedSmallTiles=new int[9];
    //scoreview
    private TextView v1;
    //timerview
    private TextView v;
    //wordlist view
    public static TextView e;
    private boolean popup = false;
    private AlertDialog.Builder builder;
    private AlertDialog mDialog;
    private HashSet<Integer> DoneTiles = new HashSet<Integer>();
    private ArrayList<int[]> adjacencyList = new ArrayList<int[]>();
    private static Boolean comingFirstTime = true;
    int t = 90;
    private HashMap<String, Integer> score = new HashMap<String, Integer>();
    public static int currentScore = 0;
    private static boolean phaseTwo = false;
    public static int totalClicks = 0;
    private Boolean muteClicked = false; //sound flag
    private HashMap<Integer, String> wordsDetectedByUser = new HashMap<Integer, String>();
    public static Map<String, ArrayList<String>> list1 = new HashMap<String, ArrayList<String>>();
    private static boolean notValidWord = false;
    public static int hashKey = 0;
    private static ImageButton muteMusic;
    private boolean gameOver = false;

    // string array to store 9 9-letter-words
    private String[] nineNineLetterWords = new String[9];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Retain this fragment across configuration changes.
        setRetainInstance(true);
        initGame();
        //placeLettersInGrids();
        setAdjacencyList();

        // load the soundpool
        mSoundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        mSoundClick = mSoundPool.load(getActivity(), R.raw.choice, 1);
        mSoundSubmit = mSoundPool.load(getActivity(), R.raw.submit_suc, 1);
        mSoundMiss = mSoundPool.load(getActivity(), R.raw.erkanozan_miss, 1);
        mSoundRewind = mSoundPool.load(getActivity(), R.raw.joanne_rewind, 1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.wordgame_large_board, container, false);

        initViews(rootView);
        loadScores();
        updateAllTiles();

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        v = (TextView) getActivity().findViewById(R.id.wd_timer);
        v1 = (TextView) getActivity().findViewById(R.id.wd_score);
        e = (TextView) getActivity().findViewById(R.id.wd_wordlist);
        muteMusic = (ImageButton) getActivity().findViewById((R.id.bt_wd_sound));
        muteMusic.setImageLevel(1);
        muteMusic.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                muteClicked =!muteClicked;
                if(muteClicked==false){
                    muteMusic.setImageLevel(1);
                }else{
                    muteMusic.setImageLevel(0);
                }
                if(ScroggleGameActivity.mMediaPlayer.isPlaying()){
                    ScroggleGameActivity.mMediaPlayer.pause();}
                else{
                    ScroggleGameActivity.mMediaPlayer.start();}
            }

        });

        pause = (Button) getActivity().findViewById(R.id.bt_wd_pause);
        pause.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                pausePressed();
            }
        });

        doneView= (Button)getActivity().findViewById(R.id.bt_wd_submit);
        doneView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                done = true;
                donePressed();
            }
        });
    }

    //Set tile invisible
    private void noVisibility(){
        for(int i = 0;i<9;i++){
            for(int j = 0; j<9;j++){
                ScroggleTile tile = mSmallTiles[i][j];
                View v = tile.getView();
                v.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(muteClicked){
            ScroggleGameActivity.mMediaPlayer.pause();
            muteMusic.setImageLevel(0);
        }

        //time
        if(!gameOver){
            getCounter();
        }
    }

    private void RunAnimation(TextView v)
    {
        Animation a = AnimationUtils.loadAnimation(getActivity(), R.anim.text_animator);
        a.reset();
        //TextView tv = (TextView) findViewById(R.id.firstTextView);
        v.clearAnimation();
        v.startAnimation(a);
    }

    private void pausePressed(){

        if(ScroggleGameActivity.mMediaPlayer.isPlaying()) {
            ScroggleGameActivity.mMediaPlayer.pause();
        }
        noVisibility();
        mHandler.removeCallbacks(mRunnable);
        builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Game Paused !");
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.resume_label,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(!gameOver){
                            mHandler.postDelayed(mRunnable, 1000);}
                        if((!ScroggleGameActivity.mMediaPlayer.isPlaying())&&!muteClicked){

                            ScroggleGameActivity.mMediaPlayer.start();}
                        for (int k = 0; k < 9; k++) {
                            for (int j = 0; j < 9; j++) {
                                ScroggleTile tile = mSmallTiles[k][j];
                                View v = tile.getView();
                                v.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
        mDialog = builder.show();
        //make the large tile available again
    }

    private void getCounter() {
        mHandler = new Handler();
        mHandler.postDelayed(mRunnable, 1000);
    }

    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            t--;
            v.setText("Time left: "+String.valueOf(t)+"  ");
            v1.setText("Score: "+String.valueOf(currentScore)+"  ");
            if(t<11){
                RunAnimation(v);}
            //while(t!=0) {

            if(t==0){
                if(!phaseTwo) {

                    v.setText("");
                    if(DoneTiles.size()!=0){
                        //for(int i = 0; i<3;i++){
                        v1.setText("Phase two begins..");
                        RunAnimation(v1);

                        // }
                        setPhasetwo();} else {
                        gameOver = true;
                        mHandler.removeCallbacks(mRunnable);
                        clearAvailable();
                        Intent i = new Intent(getActivity(), ScroggleStatus.class);
                        getActivity().startActivity(i);
                    }
                } else {
                    gameOver = true;
                    mHandler.removeCallbacks(mRunnable);
                    //mEntireBoard.getView().setVisibility(View.INVISIBLE);
                    clearAvailable();
                    phaseTwo=false;
                    Intent i = new Intent(getActivity(), ScroggleStatus.class);
                    getActivity().startActivity(i);
                }
            }else {
                mHandler.postDelayed(mRunnable, 1000);
            }
        }
    };

    private void clearAvailable() {
        mAvailable.clear();
    }

    private void addAvailable(ScroggleTile tile) {
        mAvailable.add(tile);
    }

    private void setPhasetwo(){
        t=90;
        atLeastOneClicked =false;
        getCounter();
        //
        phaseTwo = true;
        //done = false;
        for(int i = 0;i<9;i++){
            for(int j = 0;j<9;j++) {

                if (DoneTiles.contains(i)) {
                    ScroggleTile tile = mSmallTiles[i][j];
                    tile.setOwner(ScroggleTile.Owner.AVAILABLE);
                    addAvailable(tile);
                    tile.updateDrawableState('a', 0);
                }else{
                    ScroggleTile tile = mSmallTiles[i][j];
                    tile.setOwner(ScroggleTile.Owner.AVAILABLE);
                    //((Button)tile.getView()).setText("");
                    tile.updateDrawableState(' ', 1);
                }

                ScroggleTile tile = mSmallTiles[i][j];
                if(((Button)mSmallTiles[i][j].getView()).getText().toString().charAt(0)==' '){
                    // Log.d("Yes ", "it came");
                    if(mAvailable.contains(tile)){
                        mAvailable.remove(tile);
                    }

                }
                else{
                    if(!mAvailable.contains(tile)){
                        addAvailable(tile);}
                }

            }
        }


    }

    private void setAdjacencyList(){
        //block #0
        int array[] = {2, 5, 6, 7, 8};
        adjacencyList.add(array);
        //block #1
        int array1[] = {6, 7, 8};
        adjacencyList.add(array1);
        //block #2
        int array2[] = {0, 3, 6, 7, 8};
        adjacencyList.add(array2);
        //block #3
        int array3[] = {2, 5, 8};
        adjacencyList.add(array3);
        //block #4
        int array4[] = {};
        adjacencyList.add(array4);
        //block #5
        int array5[] = {0, 3, 6};
        adjacencyList.add(array5);
        //block #6
        int array6[] = {0, 1, 2, 5, 8};
        adjacencyList.add(array6);
        //block #7
        int array7[] = {0, 1, 2};
        adjacencyList.add(array7);
        //block #8
        int array8[] = {0, 1, 2, 3, 6};
        adjacencyList.add(array8);

    }

    public boolean isAvailable(ScroggleTile tile) {
        return mAvailable.contains(tile);
    }


    private void loadScores(){

        score.put("A", 1);
        score.put("B", 3);
        score.put("C", 3);
        score.put("D", 2);
        score.put("E", 1);
        score.put("F", 4);
        score.put("G", 2);
        score.put("H", 4);
        score.put("I", 1);
        score.put("J", 8);
        score.put("K", 5);
        score.put("L", 1);
        score.put("M", 3);
        score.put("N", 1);
        score.put("O", 1);
        score.put("P", 3);
        score.put("Q", 10);
        score.put("R", 1);
        score.put("S", 1);
        score.put("T", 1);
        score.put("U", 3);
        score.put("V", 4);
        score.put("W", 4);
        score.put("X", 8);
        score.put("Y", 4);
        score.put("Z", 10);
    }

    private String[] generateRandomWords(){
        for (int i = 0; i < 9; i++) {
            Random randomGenerator;
            randomGenerator = new Random();
            int index = randomGenerator.nextInt(GlobalClass.nineLetterWords.size());
            String item = GlobalClass.nineLetterWords.get(index);
            nineNineLetterWords[i] = item;
        }
        return nineNineLetterWords;
    }

//    public void placeLettersInGrids() {
//        List<List<Integer>> positions = new ArrayList<List<Integer>>();
//        positions.addAll(Arrays.asList(
//                Arrays.asList(0, 3, 6, 7, 5, 2, 1, 4, 8),
//                Arrays.asList(0, 1, 4, 6, 3, 7, 8, 5, 2),
//                Arrays.asList(2, 5, 7, 8, 4, 6, 3, 1, 0),
//                Arrays.asList(2, 5, 1, 0, 3, 7, 6, 4, 8),
//                Arrays.asList(3, 0, 4, 6, 7, 8, 5, 1, 2),
//                Arrays.asList(5, 8, 4, 6, 7, 3, 0, 1, 2),
//                Arrays.asList(8, 4, 0, 3, 6, 7, 5, 2, 1),
//                Arrays.asList(1, 5, 8, 7, 6, 3, 0, 4, 2),
//                Arrays.asList(1, 0, 3, 6, 4, 2, 5, 7, 8),
//                Arrays.asList(6, 7, 8, 5, 1, 0, 3, 4, 2),
//                Arrays.asList(6, 3, 1, 0, 4, 2, 5, 8, 7),
//                Arrays.asList(3, 6, 4, 8, 7, 5, 2, 1, 0),
//                Arrays.asList(5, 2, 4, 0, 1, 3, 6, 7, 8),
//                Arrays.asList(4, 1, 2, 5, 8, 7, 6, 3, 0),
//                Arrays.asList(5, 2, 1, 0, 3, 4, 6, 7, 8),
//                Arrays.asList(8, 7, 6, 3, 4, 5, 2, 1, 0),
//                Arrays.asList(6, 4, 3, 0, 1, 2, 5, 8, 7),
//                Arrays.asList(4, 0, 1, 3, 6, 7, 8, 5, 2)
//        ));
//
//        for (int i = 0; i < 9; i++) {
//            Random random = new Random();
//            int index = random.nextInt(positions.size());
//            letterPostions.add(positions.get(index));
//        }
//    }

    private void initViews(View rootView) {

        mEntireBoard.setView(rootView);
        for (int large = 0; large < 9; large++) {
            View outer = rootView.findViewById(mLargeIds[large]);
            mLargeTiles[large].setView(outer);

            for (int small = 0; small < 9; small++) {
                Button inner = (Button) outer.findViewById
                        (mSmallIds[small]);
                final int fLarge = large;
                final int fSmall = small;
                final ScroggleTile smallTile = mSmallTiles[large][small];
                smallTile.setView(inner);

                inner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        smallTile.animate();
                        totalClicks++;

                        if (isAvailable(smallTile)&&(!gameOver)) {
                            mSoundPool.play(mSoundClick, mVolume, mVolume, 1, 0, 1f);

                            makeMove(fLarge, fSmall); //makes the move and sets available the corresponding tile
                            touchedLargeTile =fLarge;
                            touchedSmallTiles[fSmall] = fSmall+1;
                            getButtonText(smallTile); //put button letter to string

                        } else {
                            mSoundPool.play(mSoundMiss, mVolume, mVolume, 1, 0, 1f);
                        }
                    }
                });
            }
        }


    }

    private void getButtonText(ScroggleTile smallTile){
        Button temp = (Button) smallTile.getView();
        enteredStringSroggle += temp.getText();
    }

    public Boolean searchWordInMap(String word) {

        if(GlobalClass.list.get(word.toLowerCase().substring(0, 3)).contains(word.toLowerCase())){
            return true;
        }

        return false;

    }

    private void donePressed() {

        //Need at least three letters
        if (enteredStringSroggle.length() < 2) {
            builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Need at least 3 letters!");
            builder.setCancelable(false);
            builder.setPositiveButton(R.string.ok_label,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
            mDialog = builder.show();
            return;

        }
        // when word is valid
        if (searchWordInMap(enteredStringSroggle)) {
            //Entering text to the screen
            wordsDetectedByUser.put(hashKey++, enteredStringSroggle);
            e.append(enteredStringSroggle + " ");
            if (!phaseTwo) {
                //Clearing off redundant buttons
                for (int i = 0; i < 9; i++) {
                    ScroggleTile tile = mSmallTiles[touchedLargeTile][i];
                    if (tile.getOwner() != ScroggleTile.Owner.CLICKED) {
                        //  if (!phaseTwo) {
                        tile.updateDrawableState(' ', 1);
                        // }
                    } else {
                        updateScore(tile, enteredStringSroggle.length());
                    }

                }
                setAvailableFromLastMove(touchedLargeTile, 0);


                DoneTiles.add(touchedLargeTile);


            } else {
            //phase two
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        ScroggleTile tile = mSmallTiles[i][j];
                        if (tile.getOwner() == ScroggleTile.Owner.CLICKED) {
                            updateScore(tile, enteredStringSroggle.length());
                        }
                    }
                }

            }
        } else {    //not correct word

            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    ScroggleTile tile = mSmallTiles[i][j];
                    if (tile.getOwner() == ScroggleTile.Owner.CLICKED) {
                        if (!DoneTiles.contains(i)) {
                            popup = true;
                        }
                        atLeastOneClicked = true;
                    }
                    if (atLeastOneClicked && popup) break;
                }
            }
            if (atLeastOneClicked) {
                atLeastOneClicked = false;
                if (!phaseTwo) {
                    if (popup) {
                        popup = false;
                        e = (TextView) getActivity().findViewById(R.id.wd_wordlist);
                        // e.a ppend(" ");
                        ScroggleTile tile = mLargeTiles[touchedLargeTile];
                        builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Invalid Word");
                        builder.setCancelable(false);
                        builder.setPositiveButton(R.string.ok_label,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                        mDialog = builder.show();

                        // for(int i =0;i<3;i++){
                        tile.animate();

                        for (int i = 0; i < 9; i++) {
                            ScroggleTile tiles = mSmallTiles[touchedLargeTile][i];
                            tiles.setOwner(ScroggleTile.Owner.AVAILABLE);
                            tiles.updateDrawableState('a', 0);
                            addAvailable(tiles);
                        }
                    }
                } else {
                    e = (TextView) getActivity().findViewById(R.id.wd_wordlist);
                    // e.a ppend(" ");
                    ScroggleTile tile = mLargeTiles[touchedLargeTile];
                    builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Invalid Word !");
                    builder.setCancelable(false);
                    builder.setPositiveButton(R.string.ok_label,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                    mDialog = builder.show();

                    // for(int i =0;i<3;i++){
                    // tile.animate();
                    for (int i = 0; i < 9; i++) {
                        for (int j = 0; j < 9; j++) {
                            ScroggleTile tiles = mSmallTiles[i][j];
                            if (tiles.getOwner() == ScroggleTile.Owner.CLICKED) {
                                tiles.setOwner(ScroggleTile.Owner.AVAILABLE);
                            }
                            tiles.updateDrawableState('a', 0);


                        }
                    }

                }

;
                enteredStringSroggle = "";

            }

        }
        if (phaseTwo) {

            for (int i = 0; i < 9; i++) {
                for (int dest = 0; dest < 9; dest++) {
                    ScroggleTile tile = mSmallTiles[i][dest];
                    if (tile.getOwner() == ScroggleTile.Owner.CLICKED) {
                        tile.setOwner(ScroggleTile.Owner.AVAILABLE);
                        if (mAvailable.contains(tile)) {
                            mAvailable.remove(tile);
                        }
                        tile.updateDrawableState(' ', 1);
                    } else {
                        if (((Button) mSmallTiles[i][dest].getView()).getText().charAt(0) == ' ') {
                            mAvailable.remove(tile);
                        } else {
                            addAvailable(tile);
                        }
                        tile.setOwner(ScroggleTile.Owner.AVAILABLE);
                        tile.updateDrawableState('a', 0);
                    }
                }
            }

        }

        if (!phaseTwo) {
            if (touchedLargeTile == 0) {
                for (int i = 0; i < 9; i++) {
                    ScroggleTile tiles = mSmallTiles[touchedLargeTile][i];
                    if ((tiles.getOwner() == ScroggleTile.Owner.AVAILABLE) && (((Button) tiles.getView()).getText().charAt(0) != ' ')) {

                        addAvailable(tiles);
                    }
                }

            }
        }
        for (int x = 0; x < touchedSmallTiles.length; x++) {
            touchedSmallTiles[x] = 0;
        }
        touchedLargeTile = 0;
        enteredStringSroggle = "";
    }

    private void updateScore(ScroggleTile tile, int factor){
        //length of the word will be the bonus factor
        String x = ((Button) tile.getView()).getText().toString();
        currentScore += (score.get(x))*factor;
    }

//    private void checkUnPressed(){
//
//        for(int j = 0; j<9; j++) {
//
//            if (touchedSmallTiles[j]==0) {
//                ScroggleTile demo = mSmallTiles[touchedLargeTile][j];
//
//                mAvailable.remove(demo);
//                demo.updateDrawableState('a', 0);
//            }
//
//        }
//
//    }

    private void makeMove(int large, int small) {
        mLastLarge = large;
        mLastSmall = small;
        ScroggleTile smallTile = mSmallTiles[large][small];
        ScroggleTile largeTile = mLargeTiles[large];
        smallTile.setOwner(ScroggleTile.Owner.CLICKED);
        //setAvailableFromLastMove(small);
        if(!phaseTwo){
            done = false;}
        setAvailableFromLastMove(large, small); //changed from small to large
        smallTile.updateDrawableState('a', 0);

    }

    public void restartGame() {
        mSoundPool.play(mSoundRewind, mVolume, mVolume, 1, 0, 1f);
        // ...
        mHandler.removeCallbacks(mRunnable);
        //mHandler.postDelayed(mRunnable, 1000);
        initGame();
        gameOver = false;
        done = false;
        //if(e!=null){
        e.setText("");
        //}
        popup =false;
        atLeastOneClicked =false;
        currentScore =0;
        initViews(getView());
        t=90;
        enteredStringSroggle="";
        notValidWord=false;
        //  canShowDialogBox =false;
        phaseTwo=false;
        hashKey=0;
        wordsDetectedByUser.clear();
        mHandler.postDelayed(mRunnable, 1000);

        updateAllTiles();
    }

    private void setAvailableFromLastMove(int large, int smallx) {
        clearAvailable();
        // Make all the tiles at the destination available
        if (large != -1) {


            for (int i = 0; i < 9; i++) {
                for (int dest = 0; dest < 9; dest++) {
                    if (!phaseTwo) {
                        if (!done) {
                            if (i == large) {
                                ScroggleTile tile = mSmallTiles[large][dest];
                                if ((tile.getOwner() == ScroggleTile.Owner.AVAILABLE))
                                    addAvailable(tile);

                                switch (smallx) {
                                    case 0:
                                        int a[] = adjacencyList.get(0);

                                        for (int x : a) {
                                            ScroggleTile tile1 = mSmallTiles[large][x];
                                            //if(mAvailable.contains(tile1)) {
                                            mAvailable.remove(tile1);
                                            //}
                                        }
                                        break;
                                    case 1:
                                        int a1[] = adjacencyList.get(1);

                                        for (int x : a1) {
                                            ScroggleTile tile2 = mSmallTiles[large][x];
                                            // if(mAvailable.contains(tile2)) {
                                            mAvailable.remove(tile2);
                                            //}
                                        }
                                        break;
                                    case 2:
                                        int a2[] = adjacencyList.get(2);
                                        for (int x : a2) {
                                            ScroggleTile tile3 = mSmallTiles[large][x];
                                            // if(mAvailable.contains(tile3)) {
                                            mAvailable.remove(tile3);
                                            // }
                                        }
                                        break;
                                    case 3:
                                        int a3[] = adjacencyList.get(3);
                                        for (int x : a3) {
                                            ScroggleTile tile4 = mSmallTiles[large][x];
                                            //if(mAvailable.contains(tile4)) {
                                            mAvailable.remove(tile4);
                                            // }
                                        }
                                        break;
                                    case 4:
                                        int a4[] = adjacencyList.get(4);
                                        for (int x : a4) {
                                            ScroggleTile tile5 = mSmallTiles[large][x];
                                            //if(mAvailable.contains(tile5)) {
                                            mAvailable.remove(tile5);//}

                                        }
                                        break;
                                    case 5:
                                        int a5[] = adjacencyList.get(5);
                                        for (int x : a5) {
                                            ScroggleTile tile6 = mSmallTiles[large][x];
                                            //if(mAvailable.contains(tile6)) {
                                            mAvailable.remove(tile6);//}

                                        }
                                        break;
                                    case 6:
                                        int a6[] = adjacencyList.get(6);
                                        for (int x : a6) {
                                            ScroggleTile tile7 = mSmallTiles[large][x];
                                            //if(mAvailable.contains(tile7)) {
                                            mAvailable.remove(tile7);//}

                                        }
                                        break;
                                    case 7:
                                        int a7[] = adjacencyList.get(7);
                                        for (int x : a7) {
                                            ScroggleTile tile8 = mSmallTiles[large][x];
                                            // if(mAvailable.contains(tile8)) {
                                            mAvailable.remove(tile8);//}

                                        }
                                        break;
                                    case 8:
                                        int a8[] = adjacencyList.get(8);
                                        for (int x : a8) {
                                            ScroggleTile tile9 = mSmallTiles[large][x];
                                            //if(mAvailable.contains(tile9)) {
                                            mAvailable.remove(tile9);//}

                                        }
                                        break;
                                }

                            } else {
                                if (DoneTiles.contains(i)) {
                                    continue;
                                }
                                ScroggleTile tile = mSmallTiles[i][dest];
                                tile.setOwner(ScroggleTile.Owner.FROZEN);
                                tile.updateDrawableState('a', 0);
                            }
                        } else { //OnDOnePressed
                            if (DoneTiles.contains(i)) {
                                continue;
                            }

                            //  Log.d("Comes ", "Hereeee");
                            if (i != large) {//Correct answer
                                ScroggleTile tile = mSmallTiles[i][dest];
                                tile.setOwner(ScroggleTile.Owner.AVAILABLE);
                                addAvailable(tile);
                                tile.updateDrawableState('a', 0);
                                //done =false;
                            }
                        }


                    }else {

                        if (i == large) {
                            if (dest == smallx) {
                                ScroggleTile tile1 = mSmallTiles[large][dest];
                                tile1.setOwner(ScroggleTile.Owner.CLICKED);
                                if (mAvailable.contains(tile1)) {
                                    mAvailable.remove(tile1);
                                }
                                tile1.updateDrawableState('a', 0);

                            } else {
                                ScroggleTile tile2 = mSmallTiles[large][dest];
                                if (!(tile2.getOwner() == ScroggleTile.Owner.CLICKED)) {

                                    tile2.setOwner(ScroggleTile.Owner.FROZEN);
                                }
                                if (mAvailable.contains(tile2)) {
                                    mAvailable.remove(tile2);
                                }
                                tile2.updateDrawableState('a', 0);
                            }


                        } else {


                            ScroggleTile tile3 = mSmallTiles[i][dest];
                            if (!(tile3.getOwner() == ScroggleTile.Owner.CLICKED)) {
                                tile3.setOwner(ScroggleTile.Owner.AVAILABLE);
                            }
                            //  if(((((Button)mSmallTiles[i][dest].getView()).getText().toString().equals(null))||((Button)mSmallTiles[i][dest].getView()).getText().toString().charAt(0)==' ')||(((Button)mSmallTiles[i][dest].getView()).getText().toString().equals(""))){

                            if ((!mAvailable.contains(tile3))&&(tile3.getView().toString().charAt(0)!=' ')){
                                mAvailable.add(tile3);
                            }


                            tile3.updateDrawableState('a', 0);



                            ScroggleTile tile = mSmallTiles[i][dest];
                            if(((Button)mSmallTiles[i][dest].getView()).getText().toString().charAt(0)==' '){
                                // Log.d("Yes ", "it came");
                                if(mAvailable.contains(tile)){
                                    mAvailable.remove(tile);
                                }

                            }
                            else{
                                if(!mAvailable.contains(tile)){
                                    addAvailable(tile);}
                            }

                        }

                    }
                }
            }
        }
        // If there were none available, make all squares available
        if (mAvailable.isEmpty()&&large==-1) {
            setAllAvailable();
        }
    }

    private void setPhaseTwoLogic() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                ScroggleTile tile = mSmallTiles[i][j];
                if (((Button) mSmallTiles[i][j].getView()).getText().toString().charAt(0) == ' ') {
                    // Log.d("Yes ", "it came");
                    if (mAvailable.contains(tile)) {
                        mAvailable.remove(tile);
                    }

                } else {
                    if (!mAvailable.contains(tile)) {
                        addAvailable(tile);
                    }

                }
            }
        }
    }

    private void setAllAvailable() {
        for (int large = 0; large < 9; large++) {
            for (int small = 0; small < 9; small++) {
                ScroggleTile tile = mSmallTiles[large][small];
                if (tile.getOwner() == ScroggleTile.Owner.AVAILABLE)
                    addAvailable(tile);
            }
        }

    }

    private char[][] generateCharArrays(String [] randomStrings){
        char[][] randomStringsCharArray = new char[9][];
        for(int a = 0; a<9; a++) {
            randomStringsCharArray[a]= randomStrings[a].toCharArray();
        }
        return randomStringsCharArray;
    }

    private int choosePatternNumber(){

        Random r = new Random();
        int num = r.nextInt(8);
        return num;
    }

    private void fixSmallBoards(char [] finalSequenceOfCharacters, int finalPattern, int large){

        switch(finalPattern){

            case 0:
                mSmallTiles[large][0].updateDrawableState((char) (finalSequenceOfCharacters[0]-32), 1);
                mSmallTiles[large][1].updateDrawableState((char) (finalSequenceOfCharacters[1]-32), 1);
                mSmallTiles[large][2].updateDrawableState((char) (finalSequenceOfCharacters[8]-32), 1);
                mSmallTiles[large][3].updateDrawableState((char) (finalSequenceOfCharacters[3]-32), 1);
                mSmallTiles[large][4].updateDrawableState((char) (finalSequenceOfCharacters[2]-32), 1);
                mSmallTiles[large][5].updateDrawableState((char) (finalSequenceOfCharacters[7]-32), 1);
                mSmallTiles[large][6].updateDrawableState((char) (finalSequenceOfCharacters[4]-32), 1);
                mSmallTiles[large][7].updateDrawableState((char) (finalSequenceOfCharacters[5]-32), 1);
                mSmallTiles[large][8].updateDrawableState((char) (finalSequenceOfCharacters[6]-32), 1);
                break;
            case 1:
                mSmallTiles[large][0].updateDrawableState((char) (finalSequenceOfCharacters[0]-32), 1);
                mSmallTiles[large][1].updateDrawableState((char) (finalSequenceOfCharacters[1]-32), 1);
                mSmallTiles[large][2].updateDrawableState((char) (finalSequenceOfCharacters[3]-32), 1);
                mSmallTiles[large][3].updateDrawableState((char) (finalSequenceOfCharacters[8]-32), 1);
                mSmallTiles[large][4].updateDrawableState((char) (finalSequenceOfCharacters[2]-32), 1);
                mSmallTiles[large][5].updateDrawableState((char) (finalSequenceOfCharacters[4]-32), 1);
                mSmallTiles[large][6].updateDrawableState((char) (finalSequenceOfCharacters[7]-32), 1);
                mSmallTiles[large][7].updateDrawableState((char) (finalSequenceOfCharacters[6]-32), 1);
                mSmallTiles[large][8].updateDrawableState((char) (finalSequenceOfCharacters[5]-32), 1);
                break;

            case 2:
                mSmallTiles[large][0].updateDrawableState((char) (finalSequenceOfCharacters[2]-32), 1);
                mSmallTiles[large][1].updateDrawableState((char) (finalSequenceOfCharacters[3]-32), 1);
                mSmallTiles[large][2].updateDrawableState((char) (finalSequenceOfCharacters[4]-32), 1);
                mSmallTiles[large][3].updateDrawableState((char) (finalSequenceOfCharacters[1]-32), 1);
                mSmallTiles[large][4].updateDrawableState((char) (finalSequenceOfCharacters[6]-32), 1);
                mSmallTiles[large][5].updateDrawableState((char) (finalSequenceOfCharacters[5]-32), 1);
                mSmallTiles[large][6].updateDrawableState((char) (finalSequenceOfCharacters[0]-32), 1);
                mSmallTiles[large][7].updateDrawableState((char) (finalSequenceOfCharacters[7]-32), 1);
                mSmallTiles[large][8].updateDrawableState((char) (finalSequenceOfCharacters[8]-32), 1);
                break;

            case 3:
                mSmallTiles[large][0].updateDrawableState((char) (finalSequenceOfCharacters[7]-32), 1);
                mSmallTiles[large][1].updateDrawableState((char) (finalSequenceOfCharacters[8]-32), 1);
                mSmallTiles[large][2].updateDrawableState((char) (finalSequenceOfCharacters[1]-32), 1);
                mSmallTiles[large][3].updateDrawableState((char) (finalSequenceOfCharacters[6]-32), 1);
                mSmallTiles[large][4].updateDrawableState((char) (finalSequenceOfCharacters[0]-32), 1);
                mSmallTiles[large][5].updateDrawableState((char) (finalSequenceOfCharacters[2]-32), 1);
                mSmallTiles[large][6].updateDrawableState((char) (finalSequenceOfCharacters[5]-32), 1);
                mSmallTiles[large][7].updateDrawableState((char) (finalSequenceOfCharacters[4]-32), 1);
                mSmallTiles[large][8].updateDrawableState((char) (finalSequenceOfCharacters[3]-32), 1);
                break;

            case 4:
                mSmallTiles[large][0].updateDrawableState((char) (finalSequenceOfCharacters[3]-32), 1);
                mSmallTiles[large][1].updateDrawableState((char) (finalSequenceOfCharacters[1]-32), 1);
                mSmallTiles[large][2].updateDrawableState((char) (finalSequenceOfCharacters[0]-32), 1);
                mSmallTiles[large][3].updateDrawableState((char) (finalSequenceOfCharacters[4]-32), 1);
                mSmallTiles[large][4].updateDrawableState((char) (finalSequenceOfCharacters[2]-32), 1);
                mSmallTiles[large][5].updateDrawableState((char) (finalSequenceOfCharacters[8]-32), 1);
                mSmallTiles[large][6].updateDrawableState((char) (finalSequenceOfCharacters[5]-32), 1);
                mSmallTiles[large][7].updateDrawableState((char) (finalSequenceOfCharacters[6]-32), 1);
                mSmallTiles[large][8].updateDrawableState((char) (finalSequenceOfCharacters[7]-32), 1);
                break;

            case 5:
                mSmallTiles[large][0].updateDrawableState((char) (finalSequenceOfCharacters[0]-32), 1);
                mSmallTiles[large][1].updateDrawableState((char) (finalSequenceOfCharacters[1]-32), 1);
                mSmallTiles[large][2].updateDrawableState((char) (finalSequenceOfCharacters[2]-32), 1);
                mSmallTiles[large][3].updateDrawableState((char) (finalSequenceOfCharacters[4]-32), 1);
                mSmallTiles[large][4].updateDrawableState((char) (finalSequenceOfCharacters[3]-32), 1);
                mSmallTiles[large][5].updateDrawableState((char) (finalSequenceOfCharacters[8]-32), 1);
                mSmallTiles[large][6].updateDrawableState((char) (finalSequenceOfCharacters[5]-32), 1);
                mSmallTiles[large][7].updateDrawableState((char) (finalSequenceOfCharacters[6]-32), 1);
                mSmallTiles[large][8].updateDrawableState((char) (finalSequenceOfCharacters[7]-32), 1);
                break;

            case 6:

                mSmallTiles[large][0].updateDrawableState((char) (finalSequenceOfCharacters[6]-32), 1);
                mSmallTiles[large][1].updateDrawableState((char) (finalSequenceOfCharacters[5]-32), 1);
                mSmallTiles[large][2].updateDrawableState((char) (finalSequenceOfCharacters[0]-32), 1);
                mSmallTiles[large][3].updateDrawableState((char) (finalSequenceOfCharacters[7]-32), 1);
                mSmallTiles[large][4].updateDrawableState((char) (finalSequenceOfCharacters[4]-32), 1);
                mSmallTiles[large][5].updateDrawableState((char) (finalSequenceOfCharacters[1]-32), 1);
                mSmallTiles[large][6].updateDrawableState((char) (finalSequenceOfCharacters[8]-32), 1);
                mSmallTiles[large][7].updateDrawableState((char) (finalSequenceOfCharacters[3]-32), 1);
                mSmallTiles[large][8].updateDrawableState((char) (finalSequenceOfCharacters[2]-32), 1);
                break;

            case 7:

                mSmallTiles[large][0].updateDrawableState((char) (finalSequenceOfCharacters[4]-32), 1);
                mSmallTiles[large][1].updateDrawableState((char) (finalSequenceOfCharacters[3]-32), 1);
                mSmallTiles[large][2].updateDrawableState((char) (finalSequenceOfCharacters[2]-32), 1);
                mSmallTiles[large][3].updateDrawableState((char) (finalSequenceOfCharacters[5]-32), 1);
                mSmallTiles[large][4].updateDrawableState((char) (finalSequenceOfCharacters[0]-32), 1);
                mSmallTiles[large][5].updateDrawableState((char) (finalSequenceOfCharacters[1]-32), 1);
                mSmallTiles[large][6].updateDrawableState((char) (finalSequenceOfCharacters[6]-32), 1);
                mSmallTiles[large][7].updateDrawableState((char) (finalSequenceOfCharacters[7]-32), 1);
                mSmallTiles[large][8].updateDrawableState((char) (finalSequenceOfCharacters[8]-32), 1);
                break;

            case 8:

                mSmallTiles[large][0].updateDrawableState((char) (finalSequenceOfCharacters[8]-32), 1);
                mSmallTiles[large][1].updateDrawableState((char) (finalSequenceOfCharacters[7]-32), 1);
                mSmallTiles[large][2].updateDrawableState((char) (finalSequenceOfCharacters[6]-32), 1);
                mSmallTiles[large][3].updateDrawableState((char) (finalSequenceOfCharacters[0]-32), 1);
                mSmallTiles[large][4].updateDrawableState((char) (finalSequenceOfCharacters[2]-32), 1);
                mSmallTiles[large][5].updateDrawableState((char) (finalSequenceOfCharacters[5]-32), 1);
                mSmallTiles[large][6].updateDrawableState((char) (finalSequenceOfCharacters[1]-32), 1);
                mSmallTiles[large][7].updateDrawableState((char) (finalSequenceOfCharacters[3]-32), 1);
                mSmallTiles[large][8].updateDrawableState((char) (finalSequenceOfCharacters[4]-32), 1);
                break;
        }
    }


    private void updateAllTiles() {
        String[] randomStrings = generateRandomWords();
        char[][] randomStringsCharArray = generateCharArrays(randomStrings);
        mEntireBoard.updateDrawableState('a', 0);
        for (int large = 0; large < 9; large++) {
            int pattern = choosePatternNumber();

            fixSmallBoards(randomStringsCharArray[large], pattern, large);
            ScroggleTile tile = mLargeTiles[large];
            tile.setOwner(ScroggleTile.Owner.AVAILABLE);
            DoneTiles.clear();

            tile.updateDrawableState('a', 0);

        }
    }

    private void setAvailableAccordingToGamePhase(boolean phaseTwo, int smallx, int large, HashSet<Integer> DoneTiles) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                ScroggleTile tile = mSmallTiles[i][j];

                if (phaseTwo) {

                    if (((Button) tile.getView()).getText().charAt(0) == ' ') {
                        mAvailable.remove(tile);

                    }
                    if (tile.getOwner() == ScroggleTile.Owner.FROZEN) {
                        mAvailable.remove(tile);
                    }


                } else {

                    if (tile.getOwner() == ScroggleTile.Owner.FROZEN) {
                        mAvailable.remove(tile);
                    }

                    if (i == large) {
                        switch (smallx) {
                            case 0:
                                int a[] = adjacencyList.get(0);

                                for (int x : a) {
                                    ScroggleTile tile1 = mSmallTiles[large][x];
                                    //if(mAvailable.contains(tile1)) {
                                    mAvailable.remove(tile1);
                                    //}
                                }
                                break;
                            case 1:
                                int a1[] = adjacencyList.get(1);

                                for (int x : a1) {
                                    ScroggleTile tile2 = mSmallTiles[large][x];
                                    // if(mAvailable.contains(tile2)) {
                                    mAvailable.remove(tile2);
                                    //}
                                }
                                break;
                            case 2:
                                int a2[] = adjacencyList.get(2);
                                for (int x : a2) {
                                    ScroggleTile tile3 = mSmallTiles[large][x];
                                    // if(mAvailable.contains(tile3)) {
                                    mAvailable.remove(tile3);
                                    // }
                                }
                                break;
                            case 3:
                                int a3[] = adjacencyList.get(3);
                                for (int x : a3) {
                                    ScroggleTile tile4 = mSmallTiles[large][x];
                                    //if(mAvailable.contains(tile4)) {
                                    mAvailable.remove(tile4);
                                    // }
                                }
                                break;
                            case 4:
                                int a4[] = adjacencyList.get(4);
                                for (int x : a4) {
                                    ScroggleTile tile5 = mSmallTiles[large][x];
                                    //if(mAvailable.contains(tile5)) {
                                    mAvailable.remove(tile5);//}

                                }
                                break;
                            case 5:
                                int a5[] = adjacencyList.get(5);
                                for (int x : a5) {
                                    ScroggleTile tile6 = mSmallTiles[large][x];
                                    //if(mAvailable.contains(tile6)) {
                                    mAvailable.remove(tile6);//}

                                }
                                break;
                            case 6:
                                int a6[] = adjacencyList.get(6);
                                for (int x : a6) {
                                    ScroggleTile tile7 = mSmallTiles[large][x];
                                    //if(mAvailable.contains(tile7)) {
                                    mAvailable.remove(tile7);//}

                                }
                                break;
                            case 7:
                                int a7[] = adjacencyList.get(7);
                                for (int x : a7) {
                                    ScroggleTile tile8 = mSmallTiles[large][x];
                                    // if(mAvailable.contains(tile8)) {
                                    mAvailable.remove(tile8);//}

                                }
                                break;
                            case 8:
                                int a8[] = adjacencyList.get(8);
                                for (int x : a8) {
                                    ScroggleTile tile9 = mSmallTiles[large][x];
                                    //if(mAvailable.contains(tile9)) {
                                    mAvailable.remove(tile9);//}

                                }
                                break;
                        }

                    }
                    if (DoneTiles.size() == 9) {
                        mAvailable.clear();
                    }

                }

            }
        }
    }
    /** Create a string containing the state of the game. */
    public String getState() {
        StringBuilder builder = new StringBuilder();
        //builder.append(muteMusic.getBackground().getLevel());
        //builder.append(',');
        builder.append(muteClicked);
        builder.append(',');
        builder.append(gameOver);
        builder.append(',');
        builder.append(wordsDetectedByUser.size());
        builder.append(',');
        for(int i =0;i<wordsDetectedByUser.size();i++)
        { builder.append(wordsDetectedByUser.get(i));
            builder.append(',');}
        builder.append(notValidWord);
        builder.append(',');
        //   m1Handler.removeCallbacks(m1Runnable);
        mHandler.removeCallbacks(mRunnable);
        builder.append(phaseTwo);
        builder.append(',');
        builder.append(currentScore); //storing current score
        builder.append(',');
        builder.append(t); //storing timer state
        builder.append(',');
        Object a[] = DoneTiles.toArray();
        builder.append(a.length);
        builder.append(',');
        for(int i=0;i<a.length;i++) {
            builder.append(a[i].toString());
            builder.append(',');
        }
        builder.append(mLastLarge);
        builder.append(',');
        builder.append(mLastSmall);
        builder.append(',');
        for (int large = 0; large < 9; large++) {
            for (int small = 0; small < 9; small++) {
                builder.append(mSmallTiles[large][small].getOwner().name());
                builder.append(',');
                builder.append((((Button)mSmallTiles[large][small].getView()).getText()).toString());
                builder.append(',');
                //Log.d(DoneTiles);
            }
        }
        return builder.toString();
    }

    /** Restore the state of the game from the given string. */
    public void putState(String gameData) {
        String[] fields = gameData.split(",");
        //setPhaseTwoLogic();
        int index = 0;

        muteClicked = Boolean.parseBoolean(fields[index++]);

        gameOver = Boolean.parseBoolean(fields[index++]);


        int size = Integer.parseInt(fields[index++]);
        e = (TextView) getActivity().findViewById(R.id.wd_wordlist);

        e.setText("");

        for(int i = 0; i<size; i++){

            wordsDetectedByUser.put(i, fields[index++]);

            e.append(wordsDetectedByUser.get(i)+" ");

        }
        notValidWord =Boolean.parseBoolean(fields[index++]);
        phaseTwo =Boolean.parseBoolean(fields[index++]);


        currentScore = Integer.parseInt(fields[index++]);
        t = Integer.parseInt(fields[index++]);
        int length = Integer.parseInt((fields[index++]));
        int a[ ]= new int[length];
        for(int i=0;i<length;i++){
            a[i]=Integer.parseInt(fields[index++]);
            DoneTiles.add(a[i]);
        }

        mLastLarge = Integer.parseInt(fields[index++]);
        mLastSmall = Integer.parseInt(fields[index++]);
        for (int large = 0; large < 9; large++) {
            for (int small = 0; small < 9; small++) {
                ScroggleTile.Owner owner = ScroggleTile.Owner.valueOf(fields[index++]);
                mSmallTiles[large][small].setOwner(owner);
                mSmallTiles[large][small].updateDrawableState(fields[index++].charAt(0), 1);
                //Log.d(DoneTiles.toString(), "checkkk");
                // mSmallTiles[large][small].updateDrawableState('a', 0);
            }
        }
        //  setAvailableFromLastMove(mLastLarge, mLastSmall);
        //  updateAllTiles();
        setAvailableAccordingToGamePhase(phaseTwo, mLastSmall, mLastLarge, DoneTiles);

    }

    public void initGame() {
        //
        //
        // phaseTwo = false;
        mEntireBoard = new ScroggleTile(this);
        // Create all the tiles
        for (int large = 0; large < 9; large++) {
            mLargeTiles[large] = new ScroggleTile(this);
            for (int small = 0; small < 9; small++) {
                mSmallTiles[large][small] = new ScroggleTile(this);
            }
            mLargeTiles[large].setSubTiles(mSmallTiles[large]);
        }
        mEntireBoard.setSubTiles(mLargeTiles);

        // If the player moves first, set which spots are available
        mLastSmall = -1;
        mLastLarge = -1;
        setAvailableFromLastMove(mLastLarge, mLastSmall);
    }
}



