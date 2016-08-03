package com.che.acommon;

import android.os.AsyncTask;

/**
 * Created by liuyixian on 15/11/21.
 */
public abstract class AsyncTaskImpl extends AsyncTask<Integer,Integer, Integer>{
    @Override
    protected Integer doInBackground(Integer... params) {
        doInBackground();
        return null;
    }

    protected abstract void doInBackground();

    public void cancel(){
        super.cancel(true);
    }
}
