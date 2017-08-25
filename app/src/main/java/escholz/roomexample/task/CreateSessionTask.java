package escholz.roomexample.task;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;

import escholz.roomexample.AppDatabase;
import escholz.roomexample.entity.Session;

public class CreateSessionTask extends AsyncTask<Session, Void, long[]> {

    public interface Callback {
        void onSessionCreated(CreateSessionTask task, long[] sessionIds);
    }

    private final AppDatabase appDatabase;
    private final WeakReference<Callback> callbackReference;

    public CreateSessionTask(AppDatabase appDatabase, Callback callback) {
        this.appDatabase = appDatabase;
        this.callbackReference = new WeakReference<>(callback);
    }

    @Override
    protected long[] doInBackground(Session... sessions) {
        return appDatabase.sessionDao().insert(sessions);
    }

    @Override
    protected void onPostExecute(long[] sessionIds) {
        super.onPostExecute(sessionIds);

        final Callback callback = callbackReference.get();
        if (callback != null)
            callback.onSessionCreated(this, sessionIds);
    }
}
