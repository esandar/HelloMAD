package chenliu.madcourse.neu.edu.numad18s_chenliu.DAOS;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import chenliu.madcourse.neu.edu.numad18s_chenliu.FCM.FCMActivity;
import chenliu.madcourse.neu.edu.numad18s_chenliu.models.Game;

public class GameDao extends BaseDao {

    private DatabaseReference gamesDbRef;
    private DatabaseReference userGamesDbRef;

    public GameDao() {
        super();
        gamesDbRef = getDbRef().child("games");
        gamesDbRef.keepSynced(true);

        userGamesDbRef = getDbRef().child("userGames").child(getClientToken());
        userGamesDbRef.keepSynced(true);
    }

    public String addGame(Game game) {
        game.setClientToken(getClientToken());

        DatabaseReference gameDbRef = gamesDbRef.push();
        gameDbRef.setValue(game);
        userGamesDbRef.child(gameDbRef.getKey()).setValue(game);

        return gameDbRef.getKey();
    }

    public void updateGame(final Game game, final String firebaseKey) {
        game.setClientToken(getClientToken());

        DatabaseReference gameDbRef = gamesDbRef.child(firebaseKey);
        gameDbRef.setValue(game);

        DatabaseReference userGameDbRef = userGamesDbRef.child(firebaseKey);
        userGameDbRef.setValue(game);

        Query queryLeader = queryLeader();
        queryLeader.keepSynced(true);
        queryLeader.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    GenericTypeIndicator<Map<String, Game>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Game>>() {
                    };
                    Map<String, Game> games = dataSnapshot.getValue(genericTypeIndicator);
                    new FCMActivity(null,null,null,false).sendMessageToNews(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public Query queryScoreboard() {
        return userGamesDbRef.orderByChild("score").limitToLast(5);
    }

    public Query queryLeaderboard() {
        return gamesDbRef.orderByChild("score").limitToLast(5);
    }

    public Query queryLeader() {
        return gamesDbRef.orderByChild("score").limitToLast(1);
    }
}
