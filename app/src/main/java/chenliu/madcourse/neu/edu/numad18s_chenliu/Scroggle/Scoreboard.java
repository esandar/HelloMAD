package chenliu.madcourse.neu.edu.numad18s_chenliu.Scroggle;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import chenliu.madcourse.neu.edu.numad18s_chenliu.R;

public class Scoreboard extends AppCompatActivity {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    String token = FirebaseInstanceId.getInstance().getToken();
    private DatabaseReference msbdb = database.getReference("users").child(token);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);
        setTitle("Scoreboard");
        final TextView usrname = (TextView) findViewById(R.id.scoreboard_username);
        final TextView scores =  (TextView) findViewById(R.id.scoreboard_scores);
        msbdb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User tmp = dataSnapshot.getValue(User.class);
                if (tmp.getName() == null) {
                    tmp.setName(token.substring(0, 10));
                }
                usrname.setText(tmp.getName());
                ArrayList<Integer> a = tmp.getScorelist();
                for(int i = 0; a.get(i) != null; i++) {
                    scores.append(String.valueOf(i) + "." + a.get(i).toString() + "\n");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });

    }


}
