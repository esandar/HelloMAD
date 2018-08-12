package chenliu.madcourse.neu.edu.numad18s_chenliu;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlobalClass {
    public static Map<String, ArrayList<String>> list = new HashMap<String, ArrayList<String>>();
    public static List<String> nineLetterWords = new ArrayList<String>();
    public static Map<String, ArrayList<Integer>> scoreMap = new HashMap<String, ArrayList<Integer>>();
    public static String curUser = new String();
    public static Map<String, String> as_users = new HashMap<>();

}
