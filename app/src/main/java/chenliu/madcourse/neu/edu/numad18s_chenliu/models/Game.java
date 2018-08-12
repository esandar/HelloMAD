package chenliu.madcourse.neu.edu.numad18s_chenliu.models;

public class Game {
    String clientToken;
    int score;

    public Game() {
    }

    public Game(int score) {
        this.score = score;
    }

    public String getClientToken() {
        return clientToken;
    }

    public void setClientToken(String clientToken) {
        this.clientToken = clientToken;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

}
