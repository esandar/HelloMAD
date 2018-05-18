package chenliu.madcourse.neu.edu.numad18s_chenliu;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        // Check if PHONE_STATE permission is granted
        if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
           /* if (shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)) {

            } else { */
                //request permission
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
                        MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
           // }
        } else {
            //get and set IMEI#
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String device_id = tm.getDeviceId();
            TextView textDeviceID = (TextView) findViewById(R.id.imei_id);
            textDeviceID.setText("IMEI:" + device_id);
        }
    }
    // realtime get and set IMEI#
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_PHONE_STATE
                && permissions[0].equals(Manifest.permission.READ_PHONE_STATE)
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return;
            } else {
                TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                String device_id = tm.getDeviceId();
                TextView textDeviceID = (TextView) findViewById(R.id.imei_id);
                textDeviceID.setText("IMEI:" + device_id);
            }
        }
    }
}


