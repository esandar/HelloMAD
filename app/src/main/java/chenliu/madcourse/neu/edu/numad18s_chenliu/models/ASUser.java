package chenliu.madcourse.neu.edu.numad18s_chenliu.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ASUser {

    private String username;
    private String token;
    private String datePlayed;
    private Map<String, String> friends;
    private int score;
    private int level;
    private double latitude;
    private double longitude;

    public ASUser() {
    }

    public ASUser(String username, String token) {
        this.username = username;
        this.token = token;
        this.datePlayed = new SimpleDateFormat("yyyy.MM.dd", Locale.US).format(new Date());
        this.friends = new HashMap<>();
        this.score = 0;
        this.level = 0;
        this.latitude = 0.0;
        this.longitude = 0.0;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getUsername() {
        return username;
    }

    public String getToken() {
        return token;
    }

    public Map<String, String> getFriends() {
        return friends;
    }

    public String getDatePlayed() {
        return datePlayed;
    }

    public int getScore() {
        return score;
    }

    public int getLevel() {
        return level;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void addFriends(String token) {
        if (this.friends == null) {
            this.friends = new HashMap<>();
        }
        int size = this.friends.size();
        this.friends.put("friend" + String.valueOf(size + 1), token);
    }
}
