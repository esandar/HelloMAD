package chenliu.madcourse.neu.edu.numad18s_chenliu.Scroggle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import chenliu.madcourse.neu.edu.numad18s_chenliu.R;

public class Leaderboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        setTitle("Leaderboard");
        Button kudosButton = (Button) findViewById(R.id.bt_kudos);
        kudosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //public void sendMessageToDevice(View type);
                Toast toast = Toast.makeText(
                        Leaderboard.this,
                        "Congrats sent successfully!",
                        Toast.LENGTH_SHORT
                );
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
    }


}
