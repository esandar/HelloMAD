package chenliu.madcourse.neu.edu.numad18s_chenliu;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Acknowledgement extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acknowledgement);
        this.setTitle("Acknowledgement");

        TextView textView = (TextView) findViewById(R.id.textView_ack);
        textView.setText(R.string.acknowledgement_text);

        Button bt_ack_back = (Button) findViewById(R.id.bt_ack_back);
        bt_ack_back.setOnClickListener(new View.OnClickListener(){
            @Override

            public void onClick(View v) {
                Intent intent = new Intent(Acknowledgement.this, TestDictionary.class);
                Acknowledgement.this.startActivity(intent);
            }
        });
    }
}
