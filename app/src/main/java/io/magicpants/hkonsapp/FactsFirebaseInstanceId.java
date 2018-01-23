package io.magicpants.hkonsapp;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Service for handling the user notification tokens
 * Created by Erik on 15.01.2018.
 */

public class FactsFirebaseInstanceId extends FirebaseInstanceIdService {
    private static final String TAG = "FirebaseInstanceIdClass";


    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed Token: " + refreshedToken);
        sendNewToken(refreshedToken);
    }
    private static void sendNewToken(String token){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        DatabaseReference mDbref = FirebaseDatabase.getInstance().getReference("users");
        String uId;
        if (mUser != null) {
            uId = mUser.getUid();
            mDbref.child(uId).child("notificationtoken").setValue(token);
        }
    }

    public static void registerCurrentNotificationToken(Context context){
        if (FirebaseInstanceId.getInstance().getToken() != null){
            sendNewToken(FirebaseInstanceId.getInstance().getToken());
            return;
        }
        Log.d(TAG, "No notification token on user");
    }
}
