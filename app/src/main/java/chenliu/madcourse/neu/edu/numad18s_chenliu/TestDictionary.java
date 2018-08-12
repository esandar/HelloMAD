package chenliu.madcourse.neu.edu.numad18s_chenliu;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;



public class TestDictionary extends Activity {

    ToneGenerator tone = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_dictionary);
        this.setTitle("Test Dictionary");

        if (GlobalClass.list.isEmpty()) {
            InputStream strF = null;
            try {
                strF = getResources().getAssets().open("hashmap");
                ObjectInputStream ois=new ObjectInputStream(strF);
                GlobalClass.list = (HashMap<String,ArrayList<String>>)ois.readObject();
                ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch(ClassNotFoundException ce){
                System.err.print(ce);
            }



        }

        EditText et = (EditText) findViewById(R.id.text_input);
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int count, int after) {
                boolean hasIt = false;
                String word = s.toString();

                if (word.length() >= 3) {
                    hasIt = searchWordInMap(word); //searchWordInMap(word);
                }

                if (hasIt) {
                    EditText editText = (EditText) findViewById(R.id.text_input);
                    TextView textView = (TextView) findViewById(R.id.text_list);

                    if (!textView.getText().toString().contains(editText.getText().toString())) {
                        beep();
                        textView.append(editText.getText() + "\n");
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) { }
        });


    }
    //Clear button
    public void clear_textlist (View view){
        EditText editText = (EditText) findViewById(R.id.text_input);
        TextView textView = (TextView) findViewById(R.id.text_list);
        editText.setText("");
        textView.setText("");
    }

    //Return button
    public void return_menu (View view){
        GlobalClass.list.clear();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    //Acknowledge button
    public void acknowledge_activity (View view){
        Intent intent = new Intent(this, Acknowledgement.class);
        startActivity(intent);
    }

    //Make beeping sound
    private void beep() {

        tone.startTone(ToneGenerator.TONE_CDMA_PIP, 200);

    }

    public Boolean searchWordInMap(String word) {

        if(GlobalClass.list.get(word.toLowerCase().substring(0,3)).contains(word.toLowerCase())){
            return true;
        }

        return false;

    }


}
