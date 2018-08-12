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
import android.app.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import chenliu.madcourse.neu.edu.numad18s_chenliu.DAOS.GameDao;
import chenliu.madcourse.neu.edu.numad18s_chenliu.DAOS.UserDao;
import chenliu.madcourse.neu.edu.numad18s_chenliu.GlobalClass;
import chenliu.madcourse.neu.edu.numad18s_chenliu.R;
import chenliu.madcourse.neu.edu.numad18s_chenliu.models.Game;
import chenliu.madcourse.neu.edu.numad18s_chenliu.models.User;


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
    private User user;
    private UserDao userDao;
    private Game game;
    private String gameFirebaseKey;
    private GameDao gameDao;
    private String enteredStringSroggle = "";
    private Button pause;
    private Button doneView;
    private static Boolean done = false;
    private static Boolean donePhaseTwo = false;
    public static int touchedLargeTile = 0;
    private boolean atLeastOneClicked = false;
    public static int[] touchedSmallTiles = new int[9];
    private TextView v1;//scoreview
    private TextView v; //timer view
    public static TextView e; //wordlist view
    private boolean popup = false;
    private AlertDialog.Builder builder;
    private AlertDialog mDialog;
    private HashSet<Integer> DoneTiles = new HashSet<Integer>();
    private int finishedtiles = 0;
    private ArrayList<int[]> adjacencyList = new ArrayList<int[]>();
    private ArrayList<int[]> patternList = new ArrayList<int[]>();
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
        setPatternList();
        setAdjacencyList();
        user = new User();
        userDao = new UserDao();
        userDao.addUser(user);
        gameDao = new GameDao();
        initGame();


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
        generateRandomWords();
        fillboards();
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
        muteMusic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                muteClicked = !muteClicked;
                if (muteClicked == false) {
                    muteMusic.setImageLevel(1);
                } else {
                    muteMusic.setImageLevel(0);
                }
                if (ScroggleGameActivity.mMediaPlayer.isPlaying()) {
                    ScroggleGameActivity.mMediaPlayer.pause();
                } else {
                    ScroggleGameActivity.mMediaPlayer.start();
                }
            }

        });

        pause = (Button) getActivity().findViewById(R.id.bt_wd_pause);
        pause.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                pausePressed();
            }
        });

        doneView = (Button) getActivity().findViewById(R.id.bt_wd_submit);
        doneView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                done = true;
                donePressed();
            }
        });
    }

    //Set tile invisible
    private void noVisibility() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                ScroggleTile tile = mSmallTiles[i][j];
                View v = tile.getView();
                v.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (muteClicked) {
            ScroggleGameActivity.mMediaPlayer.pause();
            muteMusic.setImageLevel(0);
        }

        //time
        if (!gameOver) {
            getCounter();
        }
    }

    private void RunAnimation(TextView v) {
        Animation a = AnimationUtils.loadAnimation(getActivity(), R.anim.text_animator);
        a.reset();
        v.clearAnimation();
        v.startAnimation(a);
    }

    private void pausePressed() {

        if (ScroggleGameActivity.mMediaPlayer.isPlaying()) {
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
                        if (!gameOver) {
                            mHandler.postDelayed(mRunnable, 1000);
                        }
                        if ((!ScroggleGameActivity.mMediaPlayer.isPlaying()) && !muteClicked) {

                            ScroggleGameActivity.mMediaPlayer.start();
                        }
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
            v.setText("Time left: " + String.valueOf(t) + "  ");
            v1.setText("Score: " + String.valueOf(currentScore) + "  ");
            if (t < 11) {
                RunAnimation(v);
            }
            if (t == 0) {
                if (!phaseTwo) {

                    v.setText("");
                    if (finishedtiles >= 3) {
                        v1.setText("Phase two begins..");
                        RunAnimation(v1);
                        clearMoves();
                        setPhasetwo();
                        updateAllTiles();
                    } else {
                        builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("No enough words for Phase 2!");
                        builder.setCancelable(false);
                        builder.setPositiveButton(R.string.ok_label,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                        mDialog = builder.show();
                        gameOver = true;
                        mHandler.removeCallbacks(mRunnable);
                        clearAvailable();
                        game.setScore(currentScore);
                        gameDao.updateGame(game, gameFirebaseKey);
                        Intent i = new Intent(getActivity(), ScroggleStatus.class);
                        getActivity().startActivity(i);
                    }
                } else {
                    gameOver = true;
                    mHandler.removeCallbacks(mRunnable);
                    clearAvailable();
                    phaseTwo = false;
                    game.setScore(currentScore);
                    gameDao.updateGame(game, gameFirebaseKey);
                    Intent i = new Intent(getActivity(), ScroggleStatus.class);
                    getActivity().startActivity(i);
                }
            } else {
                mHandler.postDelayed(mRunnable, 1000);
            }
        }
    };

    private void clearAvailable() {
        mAvailable.clear();
    }

    private void setPhasetwo() {
        t = 90;
        getCounter();
        phaseTwo = true;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                ScroggleTile tile = mSmallTiles[i][j];
                if (!tile.getText().equals(" ") && tile.getOwner() == ScroggleTile.Owner.TAKEN) {
                    tile.setOwner(ScroggleTile.Owner.AVAILABLE);
                } else {
                    tile.setOwner(ScroggleTile.Owner.NA);
                    tile.setText(" ");
                }
            }
        }
    }



    private void setAdjacencyList() {
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

    private void setPatternList() {
        //total 19
        patternList.add(new int[]{0, 1, 4, 6, 3, 7, 8, 5, 2});
        patternList.add(new int[]{0, 3, 6, 7, 5, 2, 1, 4, 8});
        patternList.add(new int[]{0, 1, 4, 6, 3, 7, 8, 5, 2});
        patternList.add(new int[]{2, 5, 7, 8, 4, 6, 3, 1, 0});
        patternList.add(new int[]{2, 5, 1, 0, 3, 7, 6, 4, 8});
        patternList.add(new int[]{3, 0, 4, 6, 7, 8, 5, 1, 2});
        patternList.add(new int[]{5, 8, 4, 6, 7, 3, 0, 1, 2});
        patternList.add(new int[]{8, 4, 0, 3, 6, 7, 5, 2, 1});
        patternList.add(new int[]{1, 5, 8, 7, 6, 3, 0, 4, 2});
        patternList.add(new int[]{1, 0, 3, 6, 4, 2, 5, 7, 8});
        patternList.add(new int[]{6, 7, 8, 5, 1, 0, 3, 4, 2});
        patternList.add(new int[]{6, 3, 1, 0, 4, 2, 5, 8, 7});
        patternList.add(new int[]{3, 6, 4, 8, 7, 5, 2, 1, 0});
        patternList.add(new int[]{5, 2, 4, 0, 1, 3, 6, 7, 8});
        patternList.add(new int[]{4, 1, 2, 5, 8, 7, 6, 3, 0});
        patternList.add(new int[]{5, 2, 1, 0, 3, 4, 6, 7, 8});
        patternList.add(new int[]{8, 7, 6, 3, 4, 5, 2, 1, 0});
        patternList.add(new int[]{6, 4, 3, 0, 1, 2, 5, 8, 7});
        patternList.add(new int[]{4, 0, 1, 3, 6, 7, 8, 5, 2});
    }

    private void loadScores() {

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

    private void generateRandomWords() {
        for (int i = 0; i < 9; i++) {
            Random randomGenerator;
            randomGenerator = new Random();
            int index = randomGenerator.nextInt(GlobalClass.nineLetterWords.size());
            String item = GlobalClass.nineLetterWords.get(index);
            nineNineLetterWords[i] = item;
        }
    }


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
                        if (smallTile.getOwner() == ScroggleTile.Owner.AVAILABLE && (!gameOver)) {
                            mSoundPool.play(mSoundClick, mVolume, mVolume, 1, 0, 1f);
                            makeMove(fLarge, fSmall);
                            getButtonText(smallTile);
                            touchedLargeTile = fLarge;
                        } else {
                            mSoundPool.play(mSoundMiss, mVolume, mVolume, 1, 0, 1f);
                        }
                    }
                });
            }
        }
    }

    private void makeMove(int large, int small) {
        mLastLarge = large;
        mLastSmall = small;
        ScroggleTile smallTile = mSmallTiles[large][small];
        ScroggleTile largeTile = mLargeTiles[large];
        smallTile.setOwner(ScroggleTile.Owner.CLICKED);
        if (!phaseTwo) {
            done = false;
            setNextAvailable1(large, small);
        }
        if (phaseTwo) {
            setNextAvailable2(large, small);
        }
        updateAllTiles();
    }

    //if tile is on the adjacencylist
    private boolean contains(int[] a, int i) {
        for (int x : a) {
            if (x == i) return true;
        }
        return false;
    }

    //Phase one set available
    private void setNextAvailable1(int lastlarge, int lastsmall) {
        int[] list = adjacencyList.get(lastsmall);
        for (int large = 0; large < 9; large++) {
            for (int small = 0; small < 9; small++) {
                ScroggleTile tile = mSmallTiles[large][small];
                if (large != lastlarge) {
                    if (tile.getOwner() != ScroggleTile.Owner.TAKEN)
                        tile.setOwner(ScroggleTile.Owner.FROZEN);
                } else {
                    if (contains(list, small) && tile.getOwner() != ScroggleTile.Owner.CLICKED) {
                        tile.setOwner(ScroggleTile.Owner.FROZEN);
                    } else {
                        if (tile.getOwner() != ScroggleTile.Owner.CLICKED)
                            tile.setOwner(ScroggleTile.Owner.AVAILABLE);
                    }
                }
            }
        }
    }

    //Phase two set availableTile
    private void setNextAvailable2(int lastlarge, int lastmall) {
        for (int small = 0; small < 9; small++) {
            ScroggleTile tile = mSmallTiles[lastlarge][small];
            if (tile.getOwner() == ScroggleTile.Owner.AVAILABLE)
                tile.setOwner(ScroggleTile.Owner.FROZEN);
        }
    }

    private void getButtonText(ScroggleTile smallTile) {
        Button temp = (Button) smallTile.getView();
        enteredStringSroggle += temp.getText();
    }

    public Boolean searchWordInMap(String word) {
        if (GlobalClass.list.get(word.toLowerCase().substring(0, 3)) == null) return false;
        if (GlobalClass.list.get(word.toLowerCase().substring(0, 3)).contains(word.toLowerCase())) {
            return true;
        }

        return false;

    }

    private void donePressed() {

        //Need at least three letters
        if (enteredStringSroggle.length() < 3) {
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
            clearMoves();
            updateAllTiles();
            touchedLargeTile = -1;
            enteredStringSroggle = "";
            return;
        }
        // when word is valid
        if (searchWordInMap(enteredStringSroggle)) {
            //Entering text to the screen
            wordsDetectedByUser.put(hashKey++, enteredStringSroggle);
            e.append(enteredStringSroggle + " ");
            updateScore(enteredStringSroggle, enteredStringSroggle.length());
            finishedtiles++;

            // PHASE 1
            if (!phaseTwo) {
                for (int small = 0; small < 9; small++) {
                    ScroggleTile tile = mSmallTiles[touchedLargeTile][small];
                    if (tile.getOwner() != ScroggleTile.Owner.CLICKED) {
                        tile.setText(" ");
                    }
                    tile.setOwner(ScroggleTile.Owner.TAKEN);
                }
            }

            if (phaseTwo) {
                for (int large = 0; large < 9; large++) {
                    for (int small = 0; small < 9; small++) {
                        ScroggleTile tile = mSmallTiles[large][small];
                        if (tile.getOwner() == ScroggleTile.Owner.CLICKED) {
                            tile.setOwner(ScroggleTile.Owner.TAKEN);
                        }

                    }
                }
            }
            //restore frozen tiles
            restoreFrozenTiles();
        } else {
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
            clearMoves();
        }
        updateAllTiles();
        touchedLargeTile = -1;
        enteredStringSroggle = "";
    }

    private void restoreFrozenTiles() {
        for (int large = 0; large < 9; large++) {
            for (int small = 0; small < 9; small++) {
                ScroggleTile tile = mSmallTiles[large][small];
                if (tile.getOwner() == ScroggleTile.Owner.FROZEN)
                    tile.setOwner(ScroggleTile.Owner.AVAILABLE);
            }
        }
    }

    private void clearMoves() {
        for (int large = 0; large < 9; large++) {
            for (int small = 0; small < 9; small++) {
                ScroggleTile tile = mSmallTiles[large][small];
                if (tile.getOwner() != ScroggleTile.Owner.TAKEN && tile.getOwner() != ScroggleTile.Owner.NA) {
                    tile.setOwner(ScroggleTile.Owner.AVAILABLE);
                }
            }
        }

    }

    private void updateScore(String word, int factor) {
        //length of the word will be the bonus factor
        for (char x : word.toCharArray()) {
            currentScore += (score.get(String.valueOf(x))) * factor;
        }
    }


    public void restartGame() {
        mSoundPool.play(mSoundRewind, mVolume, mVolume, 1, 0, 1f);
        // ...
        mHandler.removeCallbacks(mRunnable);
        //mHandler.postDelayed(mRunnable, 1000);
        initGame();
        initViews(getView());
        generateRandomWords();
        fillboards();
        gameOver = false;
        done = false;
        //if(e!=null){
        e.setText("");
        //}
        popup = false;
        currentScore = 0;
        t = 90;
        enteredStringSroggle = "";
        notValidWord = false;
        phaseTwo = false;
        hashKey = 0;
        wordsDetectedByUser.clear();
        mHandler.postDelayed(mRunnable, 1000);
        finishedtiles = 0;

        updateAllTiles();
    }

    private int choosePatternNumber() {

        Random r = new Random();
        int num = r.nextInt(14);
        return num;
    }

    private void updateAllTiles() {
        mEntireBoard.updateDrawableState('a', 0);
        for (int large = 0; large < 9; large++) {
            mLargeTiles[large].updateDrawableState('a', 0);
            for (int small = 0; small < 9; small++) {
                ScroggleTile tile = mSmallTiles[large][small];
                tile.updateDrawableState('a', 0);
            }
        }
        DoneTiles.clear();
    }

    private void fillboards() {
        for (int large = 0; large < 9; large++) {
            int[] position = patternList.get(choosePatternNumber());
            //test String word = "BLIZZARDS";
            for (int small = 0; small < 9; small++) {
                ScroggleTile tile = mSmallTiles[large][position[small]];
                //tile.updateDrawableState(word.charAt(small),1);
                tile.updateDrawableState(nineNineLetterWords[large].toUpperCase().charAt(small), 1);
            }
        }
    }

    /**
     * Create a string containing the state of the game.
     */
    public String getState() {
        StringBuilder builder = new StringBuilder();
        builder.append(muteClicked);
        builder.append(',');
        builder.append(gameOver);
        builder.append(',');
        builder.append(wordsDetectedByUser.size());
        builder.append(',');
        for (int i = 0; i < wordsDetectedByUser.size(); i++) {
            builder.append(wordsDetectedByUser.get(i));
            builder.append(',');
        }
        //   m1Handler.removeCallbacks(m1Runnable);
        mHandler.removeCallbacks(mRunnable);
        builder.append(phaseTwo);
        builder.append(',');
        builder.append(currentScore); //storing current score
        builder.append(',');
        builder.append(t); //storing timer state
        builder.append(',');
        builder.append(mLastLarge);
        builder.append(',');
        builder.append(mLastSmall);
        builder.append(',');
        for (int large = 0; large < 9; large++) {
            for (int small = 0; small < 9; small++) {
                builder.append(mSmallTiles[large][small].getOwner().name());
                builder.append(',');
                builder.append(mSmallTiles[large][small].getText());
                builder.append(',');
            }
        }
        builder.append(enteredStringSroggle);
        builder.append(',');
        return builder.toString();
    }

    /**
     * Restore the state of the game from the given string.
     */
    public void putState(String gameData) {
        String[] fields = gameData.split(",");
        int index = 0;
        muteClicked = Boolean.parseBoolean(fields[index++]);
        gameOver = Boolean.parseBoolean(fields[index++]);
        int size = Integer.parseInt(fields[index++]);
        e = (TextView) getActivity().findViewById(R.id.wd_wordlist);
        e.setText("");
        for (int i = 0; i < size; i++) {
            wordsDetectedByUser.put(i, fields[index++]);
            e.append(wordsDetectedByUser.get(i) + " ");
        }
        phaseTwo = Boolean.parseBoolean(fields[index++]);
        currentScore = Integer.parseInt(fields[index++]);
        t = Integer.parseInt(fields[index++]);
        mLastLarge = Integer.parseInt(fields[index++]);
        mLastSmall = Integer.parseInt(fields[index++]);
        for (int large = 0; large < 9; large++) {
            for (int small = 0; small < 9; small++) {
                ScroggleTile.Owner owner = ScroggleTile.Owner.valueOf(fields[index++]);
                mSmallTiles[large][small].setOwner(owner);
                mSmallTiles[large][small].setText(fields[index++]);
            }
        }
        enteredStringSroggle = fields[index++];
        updateAllTiles();

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
        game = new Game(currentScore);
        gameFirebaseKey = gameDao.addGame(game);
    }
}



