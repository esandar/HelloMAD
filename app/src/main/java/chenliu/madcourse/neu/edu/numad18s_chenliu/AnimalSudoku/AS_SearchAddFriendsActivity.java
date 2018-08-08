package chenliu.madcourse.neu.edu.numad18s_chenliu.AnimalSudoku;

import android.app.Activity;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.List;

import chenliu.madcourse.neu.edu.numad18s_chenliu.DAOS.UserDao;
import chenliu.madcourse.neu.edu.numad18s_chenliu.FCM.FCMActivity;
import chenliu.madcourse.neu.edu.numad18s_chenliu.GlobalClass;
import chenliu.madcourse.neu.edu.numad18s_chenliu.R;
import chenliu.madcourse.neu.edu.numad18s_chenliu.models.ASUser;
import chenliu.madcourse.neu.edu.numad18s_chenliu.models.Game;
import chenliu.madcourse.neu.edu.numad18s_chenliu.models.User;

public class AS_SearchAddFriendsActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private String token;
    private List<ASUser> friends = new ArrayList<>();
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.as_activity_search_add_friends);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        token = FirebaseInstanceId.getInstance().getToken();
        listView = findViewById(R.id.user_listview);

        Button search = findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });

        Button nearbyUsers = findViewById(R.id.nearby);
        nearbyUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nearbyUsers();
            }
        });

    }

    public void search() {
        friends.clear();

        final EditText editText = (EditText) findViewById(R.id.username);
        final String friendUsername = editText.getText().toString();
//        final ListView listView = findViewById(R.id.user_listview);

        if (GlobalClass.as_users.containsKey(friendUsername)) {
            String friendToken = GlobalClass.as_users.get(friendUsername);

            if (friendToken.equals(token)) {
                Toast.makeText(AS_SearchAddFriendsActivity.this,
                        "This is your own username!", Toast.LENGTH_SHORT).show();
            } else {
                DatabaseReference tokenRef = mDatabase.child("asusers").child(friendToken);

                tokenRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            ASUser friend = dataSnapshot.getValue(ASUser.class);
                            friends.add(friend);
                            Log.d("Friends", friends.toString());
                            listView.setAdapter(new UserListAdapter(AS_SearchAddFriendsActivity.this, friends, token, mDatabase));
                        } else {
                            Toast.makeText(AS_SearchAddFriendsActivity.this,
                                    "There is no user with this username!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }});
            }

        } else {
            Toast.makeText(AS_SearchAddFriendsActivity.this,
                    "Username not exists!", Toast.LENGTH_SHORT).show();
        }

    }

    public void nearbyUsers() {
        friends.clear();
        if (GlobalClass.updatedLocation == null) {
            Toast.makeText(AS_SearchAddFriendsActivity.this,
                    "Location permission is not granted", Toast.LENGTH_SHORT).show();
        } else {
            DatabaseReference ref = mDatabase.child("asusers");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Result will be holded Here
                    for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                        ASUser user = dsp.getValue(ASUser.class);
                        if (!user.getToken().equals(token) && user.getLatitude() != 0.0 && user.getLongitude() != 0.0) {
                            float[] res = new float[1];
                            Location.distanceBetween(GlobalClass.updatedLocation.getLatitude(), GlobalClass.updatedLocation.getLongitude(),
                                    user.getLatitude(), user.getLongitude(), res);
                            if (res[0] <= 80000.0) {
                                friends.add(user);
                            }
                        }
                    }
                    if (friends.isEmpty()) {
                        Toast.makeText(AS_SearchAddFriendsActivity.this,
                                "No nearby users!", Toast.LENGTH_SHORT).show();
                    } else {
                        listView.setAdapter(new UserListAdapter(AS_SearchAddFriendsActivity.this, friends, token, mDatabase));
                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }});
        }
    }
}

class UserListAdapter extends ArrayAdapter {
    private final Activity context;
    private final List<ASUser> users;
    private String token;
    private DatabaseReference mDatabase;

    public UserListAdapter(Activity context, List<ASUser> users, String token, DatabaseReference mDatabase) {
        super(context, R.layout.as_activity_search_add_friends, users);
        this.context = context;
        this.users = users;
        this.token = token;
        this.mDatabase = mDatabase;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.as_friends_row_layout, null, true);

        final ASUser friend = users.get(position);

        final TextView userName = rowView.findViewById(R.id.username);
        TextView userScore = rowView.findViewById(R.id.score);
        TextView userLevel = rowView.findViewById(R.id.level);
        Button addButton = rowView.findViewById(R.id.add_button);

        userName.setText(friend.getUsername());
        userScore.setText(String.valueOf(friend.getScore()));
        userLevel.setText(String.valueOf(friend.getLevel()));
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                DatabaseReference tokenRef = mDatabase.child("asusers").child(token);

                tokenRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            Toast.makeText(getContext(),
                                    "You have not registered!", Toast.LENGTH_SHORT).show();
                        } else {
                            ASUser self = dataSnapshot.getValue(ASUser.class);
                            String friendToken = friend.getToken();
                            if (self.getFriends() != null && self.getFriends().containsValue(friendToken)) {
                                Toast.makeText(getContext(),
                                        "Friend exits!", Toast.LENGTH_SHORT).show();
                            } else {
                                self.addFriends(friendToken);
                                mDatabase.child("asusers").child(token).setValue(self);
                                Toast.makeText(getContext(),
                                        "Added!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }});

            }
        });
        return rowView;
    }
}
