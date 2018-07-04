package chenliu.madcourse.neu.edu.numad18s_chenliu.Scroggle;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import chenliu.madcourse.neu.edu.numad18s_chenliu.DAOS.GameDao;
import chenliu.madcourse.neu.edu.numad18s_chenliu.DAOS.UserDao;
import chenliu.madcourse.neu.edu.numad18s_chenliu.FCM.FCMActivity;
import chenliu.madcourse.neu.edu.numad18s_chenliu.R;
import chenliu.madcourse.neu.edu.numad18s_chenliu.models.Game;
import chenliu.madcourse.neu.edu.numad18s_chenliu.models.User;

public class Scoreboard extends AppCompatActivity {
    private ListView scoreboardListView;
    final List<Game> topGames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);

        scoreboardListView = findViewById(R.id.scoreboard_listview);
        GameDao gameDao = new GameDao();
        Query query = gameDao.queryScoreboard();
        query.keepSynced(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        topGames.add(0, snapshot.getValue(Game.class));
                    }
                }
                scoreboardListView.setAdapter(new ScoreboardAdapter(Scoreboard.this, topGames));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

class ScoreboardAdapter extends ArrayAdapter {
    private final Activity context;
    private final List<Game> topGames;

    public ScoreboardAdapter(Activity context, List<Game> topGames) {
        super(context, R.layout.activity_scoreboard, topGames);
        this.context = context;
        this.topGames = topGames;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.scoreboard_row_layout, null, true);

        final Game game = topGames.get(position);

        final TextView userName = rowView.findViewById(R.id.scoreboard_username);
        TextView userScore = rowView.findViewById(R.id.scoreboard_score);
        Button kudosButton = rowView.findViewById(R.id.kudos_button);
        DatabaseReference userDbRef = UserDao.getUserDbRef(game.getClientToken());
        userDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                userName.setText(user == null ? "N/A" : user.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        userScore.setText(String.valueOf(game.getScore()));
        kudosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FCMActivity(null, null, null, false).sendMessageToDevice(null, game.getClientToken());
            }
        });
        return rowView;
    }
}