package chenliu.madcourse.neu.edu.numad18s_chenliu.AnimalSudoku;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import chenliu.madcourse.neu.edu.numad18s_chenliu.FCM.FCMActivity;
import chenliu.madcourse.neu.edu.numad18s_chenliu.R;

public class AS_FCMActivity extends AppCompatActivity {

    private static final String TAG = AS_FCMActivity.class.getSimpleName();

    // Please add the server key from your firebase console in the follwoing format "key=<serverKey>"
    private static final String SERVER_KEY = "key=AAAA6ivyzfs:APA91bF9YxFW98GFwXH6jB5pKvMmG5inlsThtHV10pM9MjBmIpposwi_2gqpOSwpQtVx7MfLCWy9GxzERlMfXpmZWaLULqBPoRcNZGjrz5TpMpIbSKmNSTRfE46vcev5ukVFCbSs9H_r";
    private String target;
    private String notifTitle;
    private String notifBody;
    private boolean isGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String token = FirebaseInstanceId.getInstance().getToken();

        // Log and toast
        String msg = getString(R.string.msg_token_fmt, token);
        Log.d(TAG, msg);
        Toast.makeText(AS_FCMActivity.this, msg, Toast.LENGTH_SHORT).show();


    }
//    public void subscribeToNews(View view){
//        // [START subscribe_topics]
//        FirebaseMessaging.getInstance().subscribeToTopic("Scroggle");
//        // [END subscribe_topics]
//
//        // Log and toast
//        String msg = getString(R.string.msg_subscribed);
//        Log.d(TAG, msg);
//        Toast.makeText(FCMActivity.this, msg, Toast.LENGTH_SHORT).show();
//    }

    public AS_FCMActivity(String target, String notifTitle, String notifBody, boolean isGroup) {
        this.target = target;
        this.notifTitle = notifTitle;
        this.notifBody = notifBody;
        this.isGroup = isGroup;
    }

    /**
     * Button Handler; creates a new thread that sends off a message
     * @param type
     */
    public void sendMessageToNews(View type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendMessageToNews();
            }
        }).start();
    }

    private void sendMessageToNews(){
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        try {
            jNotification.put("message", "New Leader!");
            jNotification.put("body", "Your friend has passed you!");
            jNotification.put("sound", "default");
            jNotification.put("badge", "1");


            // Populate the Payload object.
            // Note that "to" is a topic, not a token representing an app instance
            jPayload.put("to", "/topics/AnimalSudoku");
            jPayload.put("priority", "high");
            jPayload.put("notification", jNotification);

            // Open the HTTP connection and send the payload
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", SERVER_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Send FCM message content.
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jPayload.toString().getBytes());
            outputStream.close();

            // Read FCM response.
            InputStream inputStream = conn.getInputStream();
            final String resp = convertStreamToString(inputStream);

//            Handler h = new Handler(Looper.getMainLooper());
//            h.post(new Runnable() {
//                @Override
//                public void run() {
//                    Log.e(TAG, "run: " + resp);
//                    Toast.makeText(FCMActivity.this ,resp,Toast.LENGTH_LONG).show();
//                }
//            });
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }


    public void sendMessageToDevice(View type, String token) {

        final String targetToken = token;

        new Thread(new Runnable() {
            @Override
            public void run() {
                sendMessageToDevice(targetToken);
            }
        }).start();
    }

    /**
     * Pushes a notification to a given device-- in particular, this device,
     * because that's what the instanceID token is defined to be.
     */
    private void sendMessageToDevice(String targetToken) {
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        try {
            jNotification.put("title", "Congrats");
            jNotification.put("body", "Congrats for your level :) ");
            jNotification.put("sound", "default");
            jNotification.put("badge", "1");
            /*
            // We can add more details into the notification if we want.
            // We happen to be ignoring them for this demo.
            jNotification.put("click_action", "OPEN_ACTIVITY_1");
            */

            /***
             * The Notification object is now populated.
             * Next, build the Payload that we send to the server.
             */

            // If sending to a single client
            jPayload.put("to", targetToken); // CLIENT_REGISTRATION_TOKEN);

            /*
            // If sending to multiple clients (must be more than 1 and less than 1000)
            JSONArray ja = new JSONArray();
            ja.put(CLIENT_REGISTRATION_TOKEN);
            // Add Other client tokens
            ja.put(FirebaseInstanceId.getInstance().getToken());
            jPayload.put("registration_ids", ja);
            */

            jPayload.put("priority", "high");
            jPayload.put("notification", jNotification);


            /***
             * The Payload object is now populated.
             * Send it to Firebase to send the message to the appropriate recipient.
             */
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", SERVER_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Send FCM message content.
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jPayload.toString().getBytes());
            outputStream.close();

            // Read FCM response.
            InputStream inputStream = conn.getInputStream();
            final String resp = convertStreamToString(inputStream);

//            Handler h = new Handler(Looper.getMainLooper());
//            h.post(new Runnable() {
//                @Override
//                public void run() {
//                    Log.e(TAG, "run: " + resp);
//                    Toast.makeText(FCMActivity.this,resp,Toast.LENGTH_LONG).show();
//                }
//            });

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper function
     * @param is
     * @return
     */
    private String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next().replace(",", ",\n") : "";
    }
}
