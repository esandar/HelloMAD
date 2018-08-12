package chenliu.madcourse.neu.edu.numad18s_chenliu.AnimalSudoku;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

import chenliu.madcourse.neu.edu.numad18s_chenliu.R;
import chenliu.madcourse.neu.edu.numad18s_chenliu.models.ASUser;

import static chenliu.madcourse.neu.edu.numad18s_chenliu.AnimalSudoku.AS_ProgressActivity.numToUnlockNextTheme_9x9;
import static chenliu.madcourse.neu.edu.numad18s_chenliu.R.raw.background_mansu;

public class AS_GameActivity extends AppCompatActivity {
    public static final String KEY_DIFFICULTY = "chenliu.madcourse.neu.edu.numad18s_chenliu.AnimalSudoku.difficulty";
    public static final String KEY_THEME = "chenliu.madcourse.neu.edu.numad18s_chenliu.AnimalSudoku.theme";

    static private int mLargeIds[] = {R.id.aslarge1, R.id.aslarge2, R.id.aslarge3, R.id.aslarge4, R.id.aslarge5, R.id.aslarge6, R.id.aslarge7, R.id.aslarge8, R.id.aslarge9,};
    static private int mSmallIds[] = {R.id.assmall1, R.id.assmall2, R.id.assmall3, R.id.assmall4, R.id.assmall5, R.id.assmall6, R.id.assmall7, R.id.assmall8, R.id.assmall9,};
    static private int mStockIds[] = {R.id.asstock1, R.id.asstock2, R.id.asstock3, R.id.asstock4, R.id.asstock5, R.id.asstock6, R.id.asstock7, R.id.asstock8, R.id.asstock9};

    private ASTile mEntireBoard = null;
    private ASTile mLargeTiles[] = new ASTile[9];
    private ASTile mSmallTiles[][] = new ASTile[9][9];
    private AlertDialog mDialog;

    private int mSoundClick, mSoundSubmit, mSoundMiss, mSoundRewind;
    private SoundPool mSoundPool;
    public static MediaPlayer mMediaPlayer;
    private float mVolume = 1f;

    private int puzzleSize;     // 4 or 9
    private ASTile stockTiles[];  // tiles on the left for choose
    private int currentNumber;  // selected number
    private Deque<ASTile> moves;  // the moves a player has made

    private int theme;
    private int difficulty;

    private String token;
    private DatabaseReference mDatabase;

    public static final int DIFFICULTY_EASY = 0;
    public static final int DIFFICULTY_HARD = 1;
    public static final int THEME_ZOO = 0;
    public static final int THEME_AQUARIUM = 1;
    public static final int THEME_BIRD_HABITAT = 2;

    // Pools for generating games
    private final String[][] puzzlePools = {{
            "0001000200030004",
            "0200030041000200",
            "0003000000100020",
    }, {
            "360004000000230004000800200070820500460000013003014020001007000900048000000300045",
            "650000014000506000070000005007002000009314700000700800500000030000201000630000097",
            "009080501000605078000020000000706004000040000700102000000090000720301000903080600",
    }};

    // Randomly select game from pools
    private String getPuzzle() {
        String[] pool = puzzlePools[difficulty];
        int rnd = new Random().nextInt(pool.length);
        return pool[rnd];
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // load the soundpool
        mSoundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        mSoundClick = mSoundPool.load(this, R.raw.choice, 1);
        mSoundSubmit = mSoundPool.load(this, R.raw.submit_suc, 1);
        mSoundMiss = mSoundPool.load(this, R.raw.erkanozan_miss, 1);
        mSoundRewind = mSoundPool.load(this, R.raw.joanne_rewind, 1);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        token = FirebaseInstanceId.getInstance().getToken();

        // Choose 4x4 or 9x9
        difficulty = getIntent().getIntExtra(KEY_DIFFICULTY, DIFFICULTY_EASY);
        if (difficulty == DIFFICULTY_EASY) {
            puzzleSize = 4;
            setContentView(R.layout.activity_game_4x4);
        } else if (difficulty == DIFFICULTY_HARD) {
            puzzleSize = 9;
            setContentView(R.layout.activity_game_9x9);
        } else {
            puzzleSize = 4;
            setContentView(R.layout.activity_game_4x4);
        }

        // Choose background image to use
        theme = getIntent().getIntExtra(KEY_THEME, THEME_ZOO);
        ImageView background = findViewById(R.id.theme_background);
        if (theme == THEME_ZOO) {
            background.setImageResource(R.drawable.background_1);
        } else if (theme == THEME_AQUARIUM) {
            background.setImageResource(R.drawable.background_2);
        } else if (theme == THEME_BIRD_HABITAT) {
            background.setImageResource(R.drawable.background_3);
        } else {
            background.setImageResource(R.drawable.background_1);
        }

        initGame();
        initViews();
        updateAllTiles();
    }

    @Override
    protected void onResume(){
        super.onResume();
        mMediaPlayer = MediaPlayer.create(this, background_mansu);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();
    }

    private void initGame() {
        SharedPreferences progressPref = getSharedPreferences("progress", MODE_PRIVATE);
        int numOfUnlocks = progressPref.getInt(theme + "_" + AS_GameActivity.DIFFICULTY_EASY, 0);

        String puzzle = getPuzzle();
        mEntireBoard = new ASTile(0, ASTile.status.LOCKED);
        // Create all the tiles
        for (int large = 0; large < puzzleSize; large++) {
            mLargeTiles[large] = new ASTile(0, ASTile.status.LOCKED);
            for (int small = 0; small < puzzleSize; small++) {
                int number = puzzle.charAt(large * puzzleSize + small) - '0';
                // if number is 0, then it's empty, need to fill
                // if number is not empty, set 1 ~ 9 for Zoo, 10 ~ 18 for aquarium, 19 ~ 27 for bird habitat
                if (number == 0) {
                    mSmallTiles[large][small] = new ASTile(number, ASTile.status.AVAILABLE);
                } else {
                    mSmallTiles[large][small] = new ASTile(
                            number + theme * 9 + Math.min(numOfUnlocks, 5),
                            ASTile.status.LOCKED
                    );
                }
            }
            mLargeTiles[large].setSubTiles(mSmallTiles[large]);
        }
        mEntireBoard.setSubTiles(mLargeTiles);

        // initialize the tiles on the left to choose
        // 1 ~ 9 for Zoo, 10 ~ 18 for aquarium, 19 ~ 27 for bird habitat
        stockTiles = new ASTile[puzzleSize];
        for (int i = 0; i < puzzleSize; i++) {
            stockTiles[i] = new ASTile(
                    i + 1 + theme * 9 + Math.min(numOfUnlocks, 5),
                    ASTile.status.NUMBER_NOT_SELECTED
            );
        }

        currentNumber = 0;
        moves = new ArrayDeque<ASTile>();
    }

    private void initViews() {
        View entire = findViewById(R.id.entire);
        mEntireBoard.setView(entire);
        for (int large = 0; large < puzzleSize; large++) {
            View outer = findViewById(mLargeIds[large]);
            mLargeTiles[large].setView(outer);

            for (int small = 0; small < puzzleSize; small++) {
                ImageButton inner = outer.findViewById(mSmallIds[small]);
                final int fLarge = large;
                final int fSmall = small;
                final ASTile smallTile = mSmallTiles[large][small];
                smallTile.setView(inner);
                inner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mSoundPool.play(mSoundClick, mVolume, mVolume, 1, 0, 1f);
                        ASTile operatedTile = mSmallTiles[fLarge][fSmall];
                        if (currentNumber == 0) {
                            // if use want to fill a empty tile, but has not selected a number first
                            AlertDialog.Builder builder = new AlertDialog.Builder(AS_GameActivity.this);
                            builder.setMessage(R.string.select_number_first_text);
                            builder.setCancelable(false);
                            builder.setPositiveButton(
                                    R.string.ok_label,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            // nothing
                                        }
                                    }
                            );
                            mDialog = builder.show();
                        } else if (operatedTile.getStatus() != ASTile.status.LOCKED) {
                            operatedTile.setNumber(currentNumber);
                            if (checkPass(fLarge, fSmall)) {
                                operatedTile.setStatus(ASTile.status.FILLED_PASS);

                                if (checkPass()) {
                                    // Update progress
                                    SharedPreferences progressPref = getSharedPreferences("progress", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = progressPref.edit();
                                    String key = theme + "_" + difficulty;
                                    int numOfUnlocks = progressPref.getInt(key, 0);
                                    editor.putInt(key, numOfUnlocks + 1);
                                    editor.commit();

                                    // Popup success screen
                                    AlertDialog.Builder builder = new AlertDialog.Builder(AS_GameActivity.this);
                                    LayoutInflater inflater = getLayoutInflater();
                                    View finishView = inflater.inflate(R.layout.finish_game, null);
                                    final TextView levelNo = finishView.findViewById(R.id.level_no);

                                    DatabaseReference tokenRef = mDatabase.child("asusers").child(token);
                                    tokenRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                ASUser self = dataSnapshot.getValue(ASUser.class);
                                                int levelNum = self.getLevel();
                                                Log.d("User", "level is "+ String.valueOf(levelNum));
                                                int currentLevel = levelNum + 1;
                                                self.setLevel(currentLevel);
                                                levelNo.setText(String.valueOf(currentLevel));
                                                mDatabase.child("asusers").child(token).setValue(self);
                                            }

                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }});

                                    if (difficulty == DIFFICULTY_EASY && numOfUnlocks < 5) {
                                        TextView unlock = finishView.findViewById(R.id.unlock_message);
                                        unlock.setText("You have unlocked new animal: ");
                                        ImageButton unlock_icon = finishView.findViewById(R.id.unlock_icon);
                                        ASTile tile = new ASTile(4 + numOfUnlocks + 1 + theme * 9, ASTile.status.FILLED_FAIL);
                                        tile.setView(unlock_icon);
                                        tile.updateDrawableState();
                                    } else if (difficulty == DIFFICULTY_HARD && numOfUnlocks + 1 == numToUnlockNextTheme_9x9) {
                                        ImageButton unlock_icon = finishView.findViewById(R.id.unlock_icon);
                                        unlock_icon.setVisibility(View.INVISIBLE);
                                        TextView unlock = finishView.findViewById(R.id.unlock_message);
                                        String nextTheme = theme == THEME_ZOO ? "Aquarium" : theme == THEME_AQUARIUM ? "Bird Habitat" : "";
                                        unlock.setText("You have unlocked new theme! " + nextTheme);
                                    }

                                    builder.setView(finishView);
                                    builder.setPositiveButton(
                                            R.string.play_again,
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    initGame();
                                                    initViews();
                                                    updateAllTiles();
                                                }
                                            });
                                    builder.setNegativeButton(
                                            R.string.back_to_main,
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    Intent intent = new Intent(AS_GameActivity.this, AS_MainActivity.class);
                                                    startActivity(intent);
                                                }
                                            });
                                    mDialog = builder.show();
                                }
                            } else {
                                operatedTile.setStatus(ASTile.status.FILLED_FAIL);
                            }
                            moves.addLast(operatedTile);
                            updateAllTiles();
                        }
                    }
                });
            }
        }

        // if choose one number, deselect other numbers
        for (int i = 0; i < puzzleSize; i++) {
            ImageButton stockButton = findViewById(mStockIds[i]);
            final ASTile stockTile = stockTiles[i];
            stockTile.setView(stockButton);
            stockButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSoundPool.play(mSoundClick, mVolume, mVolume, 1, 0, 1f);
                    currentNumber = stockTile.getNumber();
                    for (ASTile t : stockTiles) {
                        t.setStatus(ASTile.status.NUMBER_NOT_SELECTED);
                    }
                    stockTile.setStatus(ASTile.status.NUMBER_SELECTED);
                    updateAllTiles();
                }
            });
        }

        ImageButton revertButton = findViewById(R.id.revert_button);
        revertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSoundPool.play(mSoundMiss, mVolume, mVolume, 1, 0, 1f);
                if (moves.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AS_GameActivity.this);
                    builder.setMessage(R.string.no_move_text);
                    builder.setCancelable(false);
                    builder.setPositiveButton(
                            R.string.ok_label,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // nothing
                                }
                            }
                    );
                    mDialog = builder.show();
                } else {
                    ASTile revertedTile = moves.pollLast();
                    revertedTile.setNumber(0);
                    revertedTile.setStatus(ASTile.status.AVAILABLE);

                    for (ASTile t : stockTiles) {
                        t.setStatus(ASTile.status.NUMBER_NOT_SELECTED);
                    }
                    currentNumber = 0;

                    updateAllTiles();
                }
            }
        });
    }


    private void updateAllTiles() {
        mEntireBoard.updateDrawableState();
        for (int large = 0; large < puzzleSize; large++) {
            mLargeTiles[large].updateDrawableState();
            for (int small = 0; small < puzzleSize; small++) {
                mSmallTiles[large][small].updateDrawableState();
            }
        }

        for (int i = 0; i < puzzleSize; i++) {
            stockTiles[i].updateDrawableState();
        }
    }

    public boolean checkPass(int l, int s) {
        ASTile currentTile = mSmallTiles[l][s];
        for (int small = 0; small < puzzleSize; small++) {
            ASTile smallTile = mSmallTiles[l][small];
            if (s != small && smallTile.getNumber() == currentTile.getNumber()) {
                return false;
            }
        }

        int baseSize = (int) Math.sqrt(puzzleSize);
        int largeRow = l / baseSize;
        int smallRow = s / baseSize;

        for (int i = 0; i < baseSize; i++) {
            for (int j = 0; j < baseSize; j++) {
                int large = largeRow * baseSize + i;
                int small = smallRow * baseSize + j;
                ASTile smallTile = mSmallTiles[large][small];
                if (l == large && s == small) {
                    continue;
                }
                if (smallTile.getNumber() == currentTile.getNumber()) {
                    return false;
                }
            }
        }

        int largeCol = l % baseSize;
        int smallCol = s % baseSize;
        for (int i = 0; i < baseSize; i++) {
            for (int j = 0; j < baseSize; j++) {
                int large = i * baseSize + largeCol;
                int small = j * baseSize + smallCol;
                ASTile smallTile = mSmallTiles[large][small];
                if (l == large && s == small) {
                    continue;
                }
                if (smallTile.getNumber() == currentTile.getNumber()) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean checkPass() {
        for (int large = 0; large < puzzleSize; large++) {
            for (int small = 0; small < puzzleSize; small++) {
                ASTile tile = mSmallTiles[large][small];
                if (tile.getStatus() == ASTile.status.AVAILABLE || tile.getStatus() == ASTile.status.FILLED_FAIL) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();

        // Get rid of the about dialog if it's still up
        if (mDialog != null)
            mDialog.dismiss();
    }
}
