package tomerbu.edu.firebaseupdatechildrenandondisconnect.services;

import android.os.AsyncTask;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import org.joda.time.DateTime;

public class MyJobService extends JobService {
    private AsyncTask<Object, Object, Object> asyncTask;

    @Override
    public boolean onStartJob(final JobParameters job) {
        // Begin some async work
        asyncTask = new AsyncTask<Object, Object, Object>() {
            protected Object doInBackground(Object... objects) {
                /* do some work */
                Log.e("TomerBu", "Working " + DateTime.now());
                return "Working";
            }

            protected void onPostExecute(Object result) {
                Log.e("TomerBu",  result + " Finished");
                jobFinished(job, false /* no need to reschedule, we're done */);
            }
        };

        asyncTask.execute();

        return true; /* Still doing work */
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        asyncTask.cancel(true);

        return true; /* we're not done, please reschedule */
    }
}