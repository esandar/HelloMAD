package chenliu.madcourse.neu.edu.numad18s_chenliu.Scroggle;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


@IgnoreExtraProperties
public class User {
    private String username;
    private ArrayList<Integer> scorelist;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, Integer score) {
        this.username = username;
        this.scorelist = updatescorelist(score);

    }
    public void setName(String name) {
        this.username = name;
    }
    public String getName() {
        return this.username;
    }

    public ArrayList<Integer> getScorelist() {
        return this.scorelist;
    }

    public ArrayList<Integer> updatescorelist(Integer score) {
        if (scorelist == null) scorelist = new ArrayList<Integer>();
        scorelist.add(score);
        Comparator<Integer> c = new Mycomparator();
        Collections.sort(scorelist, c);
        ArrayList<Integer> newlist = new ArrayList<>();
        for (int i = 0; i < 10 && scorelist.get(i) != null; i++) {
            newlist.add(scorelist.get(i));
        }
        return newlist;
    }

    class Mycomparator implements Comparator<Integer> {

        @Override
        public int compare(Integer o1, Integer o2) {

            if (o1 > o2)
                return -1;
            if (o1 < o2)
                return 1;
            return 0;
        }
    }
}


