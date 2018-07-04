package chenliu.madcourse.neu.edu.numad18s_chenliu.Scroggle;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import java.util.ArrayList;
import java.util.List;

import chenliu.madcourse.neu.edu.numad18s_chenliu.DAOS.GameDao;
import chenliu.madcourse.neu.edu.numad18s_chenliu.R;
import chenliu.madcourse.neu.edu.numad18s_chenliu.models.Game;

import static android.content.ContentValues.TAG;

public class Leaderboard extends AppCompatActivity {
    private ListView leaderboardListView;
    final List<Game> topGames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        leaderboardListView = findViewById(R.id.leaderboard_listview);
        GameDao gameDao = new GameDao();
        Query query = gameDao.queryLeaderboard();
        query.keepSynced(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        topGames.add(0, snapshot.getValue(Game.class));
                    }
                }
                leaderboardListView.setAdapter(new ScoreboardAdapter(Leaderboard.this, topGames));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }


}
