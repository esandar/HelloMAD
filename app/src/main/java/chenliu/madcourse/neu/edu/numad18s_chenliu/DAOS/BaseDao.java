package chenliu.madcourse.neu.edu.numad18s_chenliu.DAOS;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class BaseDao {
    private static FirebaseDatabase db;
    private static DatabaseReference dbRef;
    private static String clientToken;

    public BaseDao() {
        if (db == null) {
            db = FirebaseDatabase.getInstance();
            db.setPersistenceEnabled(true);
        }
        dbRef = db.getReference();
        clientToken = FirebaseInstanceId.getInstance().getToken();
    }

    public static DatabaseReference getDbRef() {
        return dbRef;
    }

    public static String getClientToken() {
        return clientToken;
    }

}
