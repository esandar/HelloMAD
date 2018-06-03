package chenliu.madcourse.neu.edu.numad18s_chenliu;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class AboutActivity extends Activity {
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        this.setTitle("About Me");
        // Check if PHONE_STATE permission is granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
           if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {
                Toast toast = Toast.makeText(this, "Phone permission needed to show IMEI", Toast.LENGTH_LONG);
                View toastView = toast.getView();
                toast.show();
            }
            //request permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
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


