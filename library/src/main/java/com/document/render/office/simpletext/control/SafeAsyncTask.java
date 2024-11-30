
package com.document.render.office.simpletext.control;

import android.os.AsyncTask;

import java.util.concurrent.RejectedExecutionException;


public abstract class SafeAsyncTask<Params, Progress, Result> extends
        AsyncTask<Params, Progress, Result> {

    public void safeExecute(Params... params) {
        try {
            execute(params);
        } catch (RejectedExecutionException e) {

            onPreExecute();
            if (isCancelled()) {
                onCancelled();
            } else {
                onPostExecute(doInBackground(params));
            }
        }
    }
}
