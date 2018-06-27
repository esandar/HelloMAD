package chenliu.madcourse.neu.edu.numad18s_chenliu.Scroggle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import chenliu.madcourse.neu.edu.numad18s_chenliu.R;

public class ScroggleStatus extends Activity{

    private TextView t1;
    private TextView t2;
    private TextView t3;
    private TextView t4;
    private TextView t5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wordgame_status);

        t1 = (TextView) findViewById(R.id.textview_scroggle_status_one);
        t2 = (TextView) findViewById(R.id.textview_scroggle_status_two);
        t3 = (TextView) findViewById(R.id.textview_scroggle_status_three);
        t4 = (TextView) findViewById(R.id.textview_scroggle_status_four);
        t5 = (TextView) findViewById(R.id.textview_scroggle_status_five);


        t1.setText("GAME OVER");
        t2.setText("Found Words");
        try {
            t3.setText(ScroggleGameFragment.e.getText().toString()+"\n");
        }catch(NullPointerException e){

        }
            t4.setText("Total Score\n"+String.valueOf(ScroggleGameFragment.currentScore)+"\n");
       t5.setText("Total Valid Clicks\n"+String.valueOf(ScroggleGameFragment.totalClicks));


    }


    @Override
    protected void onPause() {
        super.onPause();

        Intent intent = new Intent(this, ScroggleMainActivity.class);
        intent.putExtra(ScroggleGameActivity.KEY_RESTORE, true);
        //startActivity(intent);
    }
}