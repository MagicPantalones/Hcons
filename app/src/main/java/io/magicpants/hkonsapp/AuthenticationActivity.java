package io.magicpants.hkonsapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AuthenticationActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);


        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setAllowNewEmailAccounts(false)
                        .setIsSmartLockEnabled(false)
                        .setLogo(R.drawable.app_name_header)
                        .setTheme(R.style.AppTheme)
                        .build(),
                    RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == RC_SIGN_IN){
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK){
                //TODO Remove or move to sign-in activity.
                exportOnLogin();
                makeToast("Success!");
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                if (response == null){
                    makeToast("Sign in Cancelled!");
                    return;
                }
                if (response.getErrorCode() == ErrorCodes.NO_NETWORK){
                    makeToast("No internet connection");
                    return;
                }
                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR){
                    makeToast("Unknown Error");
                }
            }
        }


    }



    private void makeToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    //TODO Remove or migrate to sign-in completion.
    private void exportOnLogin(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();
        String mail = mUser.getEmail();
        String userName = mUser.getDisplayName();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users");

        dbRef.child(uid).child("emailaddress").setValue(mail);
        dbRef.child(uid).child("username").setValue(userName);

    }

}
