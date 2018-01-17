package io.magicpants.hkonsapp;

import android.util.Log;

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
    String uId;

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed Token: " + refreshedToken);
        sendNewToken(refreshedToken);
    }
    private void sendNewToken(String token){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        DatabaseReference mDbref = FirebaseDatabase.getInstance().getReference("users");
        if (mUser != null) {
            uId = mUser.getUid();
            mDbref.child(uId).child("notificationtoken").setValue(token);
        }
    }
}
