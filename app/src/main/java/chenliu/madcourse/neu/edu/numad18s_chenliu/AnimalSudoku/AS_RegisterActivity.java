package chenliu.madcourse.neu.edu.numad18s_chenliu.AnimalSudoku;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashSet;
import java.util.Set;

import chenliu.madcourse.neu.edu.numad18s_chenliu.GlobalClass;
import chenliu.madcourse.neu.edu.numad18s_chenliu.R;
import chenliu.madcourse.neu.edu.numad18s_chenliu.models.ASUser;

public class AS_RegisterActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.as_activity_register);
        this.setTitle("Register");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        token = FirebaseInstanceId.getInstance().getToken();

        Button register = (Button) findViewById(R.id.subscribe);
        register.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    public void register() {
        // [END subscribe_topics]
        final EditText editText = (EditText) findViewById(R.id.username);

        // Log and toast
        final String username = editText.getText().toString();
        DatabaseReference tokenRef = mDatabase.child("asusers").child(token);

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ASUser user;
                if(!GlobalClass.as_users.containsKey(username)) {
                    //create new user
                    if (dataSnapshot.exists()) {
                        user = dataSnapshot.getValue(ASUser.class);
                        String prevUsername = user.getUsername();
                        user.setUsername(username);
                        GlobalClass.as_users.remove(prevUsername);
                        Toast.makeText(AS_RegisterActivity.this,
                                "Updated your username!", Toast.LENGTH_SHORT).show();
                    } else {
                        user = new ASUser(username, token);
                        FirebaseMessaging.getInstance().subscribeToTopic("AnimalSudoku");
                        Toast.makeText(AS_RegisterActivity.this,
                                "Registered!", Toast.LENGTH_SHORT).show();
                    }
                    if (GlobalClass.updatedLocation != null) {
                        user.setLatitude(GlobalClass.updatedLocation.getLatitude());
                        user.setLongitude(GlobalClass.updatedLocation.getLongitude());
                    }
                    mDatabase.child("asusers").child(token).setValue(user);
                    GlobalClass.as_users.put(username, token);
                } else {
                    Toast.makeText(AS_RegisterActivity.this,
                            "username exists! Please choose another one.", Toast.LENGTH_SHORT).show();
                }
                editText.setText("");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        tokenRef.addListenerForSingleValueEvent(eventListener);

    }

}
