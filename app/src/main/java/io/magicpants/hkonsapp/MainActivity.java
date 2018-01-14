package io.magicpants.hkonsapp;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.view.inputmethod.InputMethodManager;
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



import static io.magicpants.hkonsapp.HconUtils.formatDateForDataBase;
import static io.magicpants.hkonsapp.HconUtils.normalizeMetricDate;

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

        fab.setImageResource(R.drawable.avd_anim_dark);

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, AuthenticationActivity.class));
            return;
        }

        mUser = mAuth.getCurrentUser();

        if (!TextUtils.isEmpty(mUser.getDisplayName())){
            userName = mUser.getDisplayName();
        } else {
            showDialog();
        }

        eMail = mUser.getEmail();
        profilePicture = mUser.getPhotoUrl();
        uid = mUser.getUid();

        Query query = mFactRef.orderByKey().limitToLast(100);

        FirebaseRecyclerOptions<Facts> options = new FirebaseRecyclerOptions.Builder<Facts>()
                .setQuery(query, Facts.class)
                .build();

        mAdapter = new FirebaseRecyclerAdapter<Facts, FactViewHolder>(options) {

            @Override
            public FactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.fact_list_item, parent, false);
                return new FactViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull FactViewHolder holder, int position, @NonNull Facts model) {

                holder.mFactsView.setText(model.getContent());
                //TODO Make a custom textView that measures text after applying font or just another better way to stop custom fonts from being cut off
                holder.mCreatorView.setText(model.getCreator() + " ");
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



        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                TransitionManager.beginDelayedTransition(transitionOverlay, new TransitionSet()
                        .addTransition(new Slide(Gravity.START)));
                visible = !visible;
                etNewFact.setVisibility(visible ? View.VISIBLE : View.GONE);
                fab.setImageResource(visible ? R.drawable.avd_anim_dark : R.drawable.avd_anim_dark_reverse);
                if (!etNewFact.getText().toString().equals("") && (!visible)) {
                    etNewFact.setText(createFact(etNewFact.getText().toString(), userName) ? "" : etNewFact.getText().toString());
                }
                ((Animatable) fab.getDrawable()).start();
                showHideKeyboard(visible, MainActivity.this, etNewFact);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
        super.onStop();
    }

    public Boolean createFact(String content, String userName) {
        if (TextUtils.isEmpty(userName)){
            showDialog();
            return false;
        } else {
            Facts newFact = new Facts(content, userName, formatDateForDataBase());
            mFactRef.push().setValue(newFact);
            return true;
        }
    }

    public void showHideKeyboard(Boolean show, Context context, View v) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if ((show) && (v != null)) {
            v.requestFocus();
            imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
        } else if ((!show) && v != null) {
            v.clearFocus();
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    void showDialog() {
        DialogFragment newFrag = UsernameDialogPrompt.newInstance("New display name.");
        newFrag.show(getFragmentManager(), "new user dialog.");
    }

    void updateUsername(String newname){
        if (TextUtils.isEmpty(newname)){
            Toast.makeText(this, "No username added, try again", Toast.LENGTH_LONG).show();
            showDialog();
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
                    Toast.makeText(MainActivity.this, "Username added", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.log_out, menu);
        return true;
    }

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
                showDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
