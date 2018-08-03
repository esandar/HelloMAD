package chenliu.madcourse.neu.edu.numad18s_chenliu.models;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ASUser {

    private String username;
    private String token;
    private String datePlayed;
    private List<ASUser> friends;
    private String score;
    private String level;

    public ASUser() {
    }

    public ASUser(String username, String token) {
        this.username = username;
        this.token = token;
        this.datePlayed = new SimpleDateFormat("yyyy.MM.dd", Locale.US).format(new Date());
        this.friends = new ArrayList<>();
        this.score = "0";
        this.level = "0";
    }

    public String getUsername() {
        return username;
    }

    public String getToken() {
        return token;
    }

    public List<ASUser> getFriends() {
        return friends;
    }

    public String getScore() {
        return score;
    }

    public String getLevel() {
        return level;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
