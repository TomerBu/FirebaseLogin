package tomerbu.edu.firebaseupdatechildrenandondisconnect.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import tomerbu.edu.firebaseupdatechildrenandondisconnect.R;
import tomerbu.edu.firebaseupdatechildrenandondisconnect.models.User;
import tomerbu.edu.firebaseupdatechildrenandondisconnect.tools.Intents;

public class MainActivity extends AppCompatActivity {

    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    mCurrentUser = firebaseAuth.getCurrentUser();
                    init();
                } else {
                    Intents.gotoLogin(getApplicationContext());
                }
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

/*    private void writeNewPost(String userId, String username, String title, String body) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        //The Push Key
        String pushKey = mDatabase.child("posts").push().getKey();
        Post post = new Post(userId, username, title, body);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + pushKey, postValues);
        childUpdates.put("/user-posts/" + userId + "/" + pushKey, postValues);

        mDatabase.updateChildren(childUpdates);
    }*/

    //TODO: Move to  Login Activity! and test it.
    private void init() {
        User user = new User(mCurrentUser.getUid(), mCurrentUser.getEmail(), true);
        HashMap<String, Object> connectedUpdates = new HashMap<>();
        connectedUpdates.put("/Users/" + mCurrentUser.getUid(), user.toMap());
        FirebaseDatabase.getInstance().getReference().updateChildren(connectedUpdates);


        HashMap<String, Object> disconnectedUpdates = new HashMap<>();
        User u = new User(mCurrentUser.getUid(), mCurrentUser.getEmail(), false);
        disconnectedUpdates.put("/Users/" + mCurrentUser.getUid(), u.toMap());
        FirebaseDatabase.getInstance().getReference().onDisconnect().updateChildren(disconnectedUpdates);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
