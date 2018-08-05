package chenliu.madcourse.neu.edu.numad18s_chenliu.AnimalSudoku;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import chenliu.madcourse.neu.edu.numad18s_chenliu.GlobalClass;
import chenliu.madcourse.neu.edu.numad18s_chenliu.R;
import chenliu.madcourse.neu.edu.numad18s_chenliu.models.ASUser;

public class AS_FriendsActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.as_activity_friends);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        GlobalClass.as_users.clear();

        DatabaseReference ref = mDatabase.child("asusers");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Result will be holded Here
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    ASUser user = dsp.getValue(ASUser.class);
                    GlobalClass.as_users.put(user.getUsername(), user.getToken());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });


    }

    public void startRegister(View view) {
        Intent intent = new Intent(this, AS_RegisterActivity.class);
        startActivity(intent);
    }

    public void startSearch(View view) {
        Intent intent = new Intent(this, AS_SearchAddFriendsActivity.class);
        startActivity(intent);
    }

    public void startLeaderboard(View view) {
        Intent intent = new Intent(this, AS_Leaderboard_Activity.class);
        startActivity(intent);
    }

}
