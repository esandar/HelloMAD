package chenliu.madcourse.neu.edu.numad18s_chenliu.AnimalSudoku;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import chenliu.madcourse.neu.edu.numad18s_chenliu.R;

public class AS_FriendsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.as_activity_friends);
    }

    public void register(View view) {
        Intent intent = new Intent(this, AS_RegisterActivity.class);
        startActivity(intent);
    }
}
