package chenliu.madcourse.neu.edu.numad18s_chenliu.Scroggle;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
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

import chenliu.madcourse.neu.edu.numad18s_chenliu.GlobalClass;
import chenliu.madcourse.neu.edu.numad18s_chenliu.R;

public class UserActivity extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    String token = FirebaseInstanceId.getInstance().getToken();
    DatabaseReference mynameRef = database.getReference("users").child(token);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        final EditText editText = findViewById(R.id.nameedit);
        Button submitButton = findViewById(R.id.submitname);


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mynameRef == null) {
                    database.getReference("users").setValue(token);
                    User user = new User();
                    user.setName(editText.toString());
                    mynameRef = database.getReference("users").child(token);
                    mynameRef.setValue(user);
                }
                Toast toast = Toast.makeText(
                        UserActivity.this,
                        "User registered successfully!",
                        Toast.LENGTH_LONG
                );
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
    }

}
