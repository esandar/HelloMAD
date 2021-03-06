package chenliu.madcourse.neu.edu.numad18s_chenliu.AnimalSudoku;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import chenliu.madcourse.neu.edu.numad18s_chenliu.R;


public class AS_ProgressActivity extends AppCompatActivity {
    static public int numToUnlockNextTheme_9x9 = 1;

    static private int mIconIds[] = {
            R.id.icon1, R.id.icon2, R.id.icon3, R.id.icon4, R.id.icon5, R.id.icon6, R.id.icon7, R.id.icon8, R.id.icon9,
            R.id.icon10, R.id.icon11, R.id.icon12, R.id.icon13, R.id.icon14, R.id.icon15, R.id.icon16, R.id.icon17, R.id.icon18,
            R.id.icon19, R.id.icon20, R.id.icon21, R.id.icon22, R.id.icon23, R.id.icon24, R.id.icon25, R.id.icon26, R.id.icon27,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.as_activity_progress);

        SharedPreferences progressPref = getSharedPreferences("progress", MODE_PRIVATE);
        int zooProgress_4x4 = progressPref.getInt(
                AS_GameActivity.THEME_ZOO + "_" + AS_GameActivity.DIFFICULTY_EASY,
                0
        );
        int zooProgress_9x9 = progressPref.getInt(
                AS_GameActivity.THEME_ZOO + "_" + AS_GameActivity.DIFFICULTY_HARD,
                0
        );
        int aquariumProgress_4x4 = progressPref.getInt(
                AS_GameActivity.THEME_AQUARIUM + "_" + AS_GameActivity.DIFFICULTY_EASY,
                0
        );
        int aquariumProgress_9x9 = progressPref.getInt(
                AS_GameActivity.THEME_AQUARIUM + "_" + AS_GameActivity.DIFFICULTY_HARD,
                0
        );
        int birdHabitatProgress_4x4 = progressPref.getInt(
                AS_GameActivity.THEME_BIRD_HABITAT + "_" + AS_GameActivity.DIFFICULTY_EASY,
                0
        );

        SharedPreferences configPref = getSharedPreferences("config", MODE_PRIVATE);
        boolean isCheat = configPref.getBoolean("cheat", false);

        // Zoo is always active
        TextView zooLabel = findViewById(R.id.zoo_label);
        zooLabel.setBackgroundColor(getResources().getColor(R.color.available_color));

        // Aquarium is active only if Zoo 9x9 has been finished X times
        TextView aquariumLabel = findViewById(R.id.aquarium_label);
        if (isCheat || zooProgress_9x9 >= numToUnlockNextTheme_9x9) {
            aquariumLabel.setBackgroundColor(getResources().getColor(R.color.available_color));
        }

        // Aquarium is active only if Aquarium 9x9 has been finished X times
        TextView birdHabitatLabel = findViewById(R.id.bird_habitat_label);
        if (isCheat || aquariumProgress_9x9 >= numToUnlockNextTheme_9x9) {
            birdHabitatLabel.setBackgroundColor(getResources().getColor(R.color.available_color));
        }

        for (int i = 0; i < 9; i++) {
            ASTile zooTile = !isCheat && zooProgress_4x4 + 4 < i + 1
                            ? new ASTile(i + 1, ASTile.status.LOCKED)       // not collected
                            : new ASTile(i + 1, ASTile.status.FILLED_FAIL); // collected
            zooTile.setView(findViewById(mIconIds[i]));
            zooTile.updateDrawableState();

            ASTile aquariumTile = !isCheat && (zooProgress_9x9 < numToUnlockNextTheme_9x9 || aquariumProgress_4x4 + 4 < i + 1)
                    ? new ASTile(i + 10, ASTile.status.LOCKED)              // not collected
                    : new ASTile(i + 10, ASTile.status.FILLED_FAIL);        // collected
            aquariumTile.setView(findViewById(mIconIds[i + 9]));
            aquariumTile.updateDrawableState();

            ASTile birdHabitatTile = !isCheat && (aquariumProgress_9x9 < numToUnlockNextTheme_9x9 || birdHabitatProgress_4x4 + 4 < i + 1)
                    ? new ASTile(i + 19, ASTile.status.LOCKED)              // not collected
                    : new ASTile(i + 19, ASTile.status.FILLED_FAIL);        // collected
            birdHabitatTile.setView(findViewById(mIconIds[i + 18]));
            birdHabitatTile.updateDrawableState();
        }
    }
}
