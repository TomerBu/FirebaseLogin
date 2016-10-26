package tomerbu.edu.firebaseupdatechildrenandondisconnect.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import org.joda.time.DateTime;

import java.util.HashMap;

import tomerbu.edu.firebaseupdatechildrenandondisconnect.MyReceiver;
import tomerbu.edu.firebaseupdatechildrenandondisconnect.R;
import tomerbu.edu.firebaseupdatechildrenandondisconnect.models.User;
import tomerbu.edu.firebaseupdatechildrenandondisconnect.services.MyJobService;
import tomerbu.edu.firebaseupdatechildrenandondisconnect.tools.Intents;

public class MainActivity extends AppCompatActivity {

    private FirebaseUser mCurrentUser;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    mCurrentUser = firebaseAuth.getCurrentUser();
                    init();
                } else {
                    Intents.gotoLogin(getApplicationContext());
                }
            }
        };


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAlert();
            }
        });
        //


    }

    private void initJob() {
        Driver myDriver = new GooglePlayDriver(this);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(myDriver);

        //Create Your Job Definition
        Job job = dispatcher.newJobBuilder()
                .setService(MyJobService.class)
                .setTag("my-tag")

                .setTrigger(Trigger.executionWindow(10, 20))//The (begin,end) time (in seconds) the job should be run in - Execution window
                .setLifetime(Lifetime.FOREVER) /*Lifetime.FOREVER : Lifetime.UNTIL_NEXT_BOOT*/
                .setRecurring(true)
                //.setConstraints(Constraint.DEVICE_CHARGING, Constraint.ON_UNMETERED_NETWORK)
                .build();

        int result = dispatcher.schedule(job);
        if (result != FirebaseJobDispatcher.SCHEDULE_RESULT_SUCCESS) {
            // handle error
            Toast.makeText(MainActivity.this, "Error " + result, Toast.LENGTH_SHORT).show();
        }
    }


    public void startAlert() {
        DateTime now = DateTime.now();

        TimePickerDialog d = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hours, int minutes) {
                DateTime after = DateTime.now();
                after = after.withHourOfDay(hours);
                after = after.withMinuteOfHour(minutes);
                after = after.withSecondOfMinute(0);

                Intent intent = new Intent(getApplicationContext(), MyReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                        234324243, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                            after.getMillis(), pendingIntent);
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, after.getMillis(), pendingIntent);
                }
            }
        }, now.getHourOfDay(), now.getMinuteOfHour(), true);
        d.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
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
        switch (id) {
            case R.id.action_logout:

                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();

                return true;

            case R.id.action_dispatch_job:
                initJob();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
