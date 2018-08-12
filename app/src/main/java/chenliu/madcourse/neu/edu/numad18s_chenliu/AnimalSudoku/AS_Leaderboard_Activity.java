package chenliu.madcourse.neu.edu.numad18s_chenliu.AnimalSudoku;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chenliu.madcourse.neu.edu.numad18s_chenliu.R;
import chenliu.madcourse.neu.edu.numad18s_chenliu.models.ASUser;

public class AS_Leaderboard_Activity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private String token;
    private List<String> friends = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.as_activity_leaderboard);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        token = FirebaseInstanceId.getInstance().getToken();

        //final TextView score = findViewById(R.id.score);
        final TextView level = findViewById(R.id.level);

        DatabaseReference tokenRef = mDatabase.child("asusers").child(token);

        tokenRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ASUser self = dataSnapshot.getValue(ASUser.class);
                    //int scoreNum = self.getScore();
                    int levelNum = self.getLevel();
                    Log.d("Friends", "level is "+ String.valueOf(levelNum));
                    level.setText(String.valueOf(levelNum));
                    //score.setText(String.valueOf(scoreNum));
                    if (self.getFriends() == null) {
                        Toast.makeText(AS_Leaderboard_Activity.this,
                                "You have not added any friend!", Toast.LENGTH_SHORT).show();
                    } else {
                        Map<String, String> friendTokens = new HashMap<>();
                        friendTokens.putAll(self.getFriends());
                        friends.addAll(friendTokens.values());
                    }
                } else {
                    Toast.makeText(AS_Leaderboard_Activity.this,
                            "You have not registered to share!", Toast.LENGTH_SHORT).show();
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }});
        ListView listView = findViewById(R.id.leaderboard_listview);
        listView.setAdapter(new LeaderboardAdapter(AS_Leaderboard_Activity.this, friends, mDatabase));

    }
}

class LeaderboardAdapter extends ArrayAdapter {
    private final Activity context;
    private final List<String> friendTokens;
    private DatabaseReference mDatabase;

    public LeaderboardAdapter(Activity context, List<String> friends, DatabaseReference mDatabase) {
        super(context, R.layout.as_activity_leaderboard, friends);
        this.context = context;
        this.friendTokens = friends;
        this.mDatabase = mDatabase;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.as_leaderboard_row_layout, null, true);

        final String friendToken = friendTokens.get(position);

        final TextView friendName = rowView.findViewById(R.id.username);
        //final TextView friendScore = rowView.findViewById(R.id.friend_score);
        final TextView friendLevel = rowView.findViewById(R.id.friend_level);
        DatabaseReference tokenRef = mDatabase.child("asusers").child(friendToken);
        tokenRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ASUser user = dataSnapshot.getValue(ASUser.class);
                friendName.setText(user.getUsername());
                //friendScore.setText(String.valueOf(user.getScore()));
                friendLevel.setText(String.valueOf(user.getLevel()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Button kudoButton = rowView.findViewById(R.id.kudo_button);
        kudoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AS_FCMActivity(null, null, null, false).sendMessageToDevice(null, friendToken);
            }
        });
        return rowView;
    }
}
