package io.magicpants.hkonsapp;

import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import static io.magicpants.hkonsapp.FactsFirebaseInstanceId.registerCurrentNotificationToken;
import static io.magicpants.hkonsapp.util.HconUtils.formatDateForDataBase;
import static io.magicpants.hkonsapp.util.HconUtils.normalizeMetricDate;
import static io.magicpants.hkonsapp.util.HconUtils.showHideKeyboard;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivityCustom";
    private RecyclerView mRecyclerView;
    ViewGroup transitionOverlay;
    FloatingActionButton fab;
    EditText etNewFact;
    DatabaseReference mDatabase;
    DatabaseReference mFactRef;
    DatabaseReference mUserRef;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String userName;
    String eMail;
    String uid;
    Uri profilePicture;
    Boolean visible;
    private FirebaseRecyclerAdapter<Facts, FactViewHolder> mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setIcon(R.drawable.app_name_header);
        getSupportActionBar().setElevation(0f);

        mRecyclerView = findViewById(R.id.facts_recycler);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFactRef = FirebaseDatabase.getInstance().getReference("facts");
        mUserRef = FirebaseDatabase.getInstance().getReference("users");
        mAuth = FirebaseAuth.getInstance();

        visible = false;
        transitionOverlay = findViewById(R.id.transition_container);
        fab = transitionOverlay.findViewById(R.id.btn_new_fact);
        etNewFact = transitionOverlay.findViewById(R.id.et_new_fact);

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, AuthenticationActivity.class));
            return;
        }

        initializeUserVars();

        Query query = mFactRef.orderByKey().limitToLast(50);

        /* This creates the adapter, viewholder and populates the recycler view.
         */
        FirebaseRecyclerOptions<Facts> options = new FirebaseRecyclerOptions.Builder<Facts>()
                .setQuery(query, Facts.class)
                .setLifecycleOwner(this)
                .build();

        mAdapter = new FirebaseRecyclerAdapter<Facts, FactViewHolder>(options) {

            @Override
            public FactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.fact_list_item_text, parent, false);
                return new FactViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final FactViewHolder holder, int position, @NonNull Facts model) {
                String creator = model.getCreator();
                DatabaseReference dbr = FirebaseDatabase.getInstance().getReference("users").child(creator);

                if (creator.length() <= 15){
                    holder.mCreatorView.setText(model.getCreator() + " ");
                } else {
                    dbr.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Users postUser = dataSnapshot.getValue(Users.class);
                            holder.mCreatorView.setText(postUser.getUsername());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d(TAG, "single event listener: " + databaseError.getMessage());
                        }
                    });
                }
                holder.mFactsView.setText(model.getContent());
                //TODO Make a custom textView that measures text after applying font or just another better way to stop custom fonts from being cut off
                holder.mTimestampView.setText(normalizeMetricDate(model.getTimestamp()) + " ");
            }

            @Override
            public void onChildChanged(@NonNull ChangeEventType type, @NonNull DataSnapshot snapshot, int newIndex, int oldIndex) {
                notifyDataSetChanged();
                mRecyclerView.scrollToPosition(newIndex);
                super.onChildChanged(type, snapshot, newIndex, oldIndex);
            }

            @Override
            public void onError(@NonNull DatabaseError error) {
                super.onError(error);
                Log.d(TAG, "onError: " + error);
            }

        };
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);


        /* On click listener for the floating action button. If the editText area is hidden this will open the keyboard + show the editText
         * If the editText area is visible it will write to the DB.
         * Else if you hide the EditText before you write to the DB, in other words, press back, it will open with the text that you
         * wrote before you hid the edit-text
        */
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String etContent = etNewFact.getText().toString();

                TransitionManager.beginDelayedTransition(transitionOverlay, new TransitionSet()
                        .addTransition(new Slide(Gravity.START)));

                visible = !visible;

                etNewFact.setVisibility(visible ? View.VISIBLE : View.GONE);
                fab.setImageResource(visible ? R.drawable.avd_anim_dark : R.drawable.avd_anim_dark_reverse);

                if (!etNewFact.getText().toString().equals("") && (!visible)) {
                    etNewFact.setText(createFact(etContent) ? "" : etContent);
                }

                ((Animatable) fab.getDrawable()).start();
                showHideKeyboard(visible, MainActivity.this, etNewFact);
            }
        });
    }

    //Method to write new "facts" to the DB
    public Boolean createFact(String content) {
        String timeNow = formatDateForDataBase();
        Facts newFact = new Facts(content, uid, timeNow);
        String pNewFactKey = mFactRef.push().getKey();
        UserPosts userPosts = new UserPosts(content, timeNow);

        mUserRef.child("userposts").child(pNewFactKey).setValue(userPosts);
        mFactRef.child(pNewFactKey).setValue(newFact);

        return true;
    }

    //Shows the fragment to change usernames
    void showChangeUsernameDialog() {
        DialogFragment newFrag = UsernameDialogPrompt.newInstance("New display name.");
        newFrag.show(getFragmentManager(), "new user dialog.");
    }

    //Gets called on click from the fragment class.
    void updateUsername(String newname){
        if (userName.equals("testRobot")){
            Toast.makeText(this, "Hello Robot ^^", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(newname) || newname.equals(eMail)){
            Toast.makeText(this, "Illegal username, try again", Toast.LENGTH_LONG).show();
            showChangeUsernameDialog();
            return;
        }

        UserProfileChangeRequest newUserName = new UserProfileChangeRequest.Builder()
                .setDisplayName(newname)
                .build();
        mUser.updateProfile(newUserName).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    userName = mUser.getDisplayName();
                    mUserRef.child("username").setValue(userName);
                    mAdapter.notifyDataSetChanged();
                    Toast.makeText(MainActivity.this, "Username added", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Method for assigning user variables
    void initializeUserVars(){
        mUser = mAuth.getCurrentUser();

        userName = (mUser.getDisplayName() == null) ? mUser.getEmail() : mUser.getDisplayName();
        eMail = mUser.getEmail();
        profilePicture = mUser.getPhotoUrl();
        uid = mUser.getUid();

        mUserRef = mUserRef.child(uid);
        //TODO temp function while i'm creating user accounts manually
        if (userName.equals(eMail)){
            Toast.makeText(this, "Username can't be same as Email, please change", Toast.LENGTH_LONG).show();
            showChangeUsernameDialog();
        }
        registerCurrentNotificationToken(this);
    }

    //Initiates the top right option menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.log_out, menu);
        return true;
    }

    //The onclick handler for the option buttons
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.log_out_btn:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                startActivity(new Intent(MainActivity.this, AuthenticationActivity.class));
                                finish();
                            }
                        });
                return true;
            case R.id.change_name_btn:
                showChangeUsernameDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
