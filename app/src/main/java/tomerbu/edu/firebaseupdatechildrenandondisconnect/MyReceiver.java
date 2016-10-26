package tomerbu.edu.firebaseupdatechildrenandondisconnect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Date;

public class MyReceiver extends BroadcastReceiver {
    public MyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // This method is called when the alarm is triggered.
        // an Intent broadcast.

        Log.e("TomerBu", "Alarm!!!!" + new Date().toString());
    }
}
