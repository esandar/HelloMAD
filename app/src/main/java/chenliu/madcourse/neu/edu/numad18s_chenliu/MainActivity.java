package chenliu.madcourse.neu.edu.numad18s_chenliu;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import chenliu.madcourse.neu.edu.numad18s_chenliu.Scroggle.ScroggleMainActivity;
import chenliu.madcourse.neu.edu.numad18s_chenliu.UTTT.UTTTMainActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set title bar
        getSupportActionBar().setTitle("Chen Liu");

        // Get version code
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            TextView textViewVersionCode = findViewById(R.id.txt_version);
            textViewVersionCode.setText(
                    "Version Name: " +
                    pInfo.versionName + "\n" +
                    "Version Code: " +
                    pInfo.versionCode
            );
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
    //throw an realtime execption to make app fail
    public void generateError(View view) {
        String testLabSetting =
                Settings.System.getString(getApplicationContext().getContentResolver(), "firebase.test.lab");
        if ("true".equals(testLabSetting)) {
            // Do something when running in Test Lab
            return;
        }
        throw new RuntimeException("ErrorGenerated");
    }

    //About button
    public void openAbout(View view) {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    //Dictionary button
    public void test_dictionary(View view) {
        Intent intent = new Intent(this, TestDictionary.class);
        startActivity(intent);
    }

    //UTTT button
    public void open_UTTT(View view) {
        Intent intent = new Intent(this, UTTTMainActivity.class);
        startActivity(intent);
    }

    //wordgame button
    public void open_wordgame(View view) {
        Intent intent = new Intent(this, ScroggleMainActivity.class);
        startActivity(intent);
    }
}

