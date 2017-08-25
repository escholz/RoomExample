package escholz.roomexample.task;

import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;
import java.util.List;

import escholz.roomexample.AppDatabase;
import escholz.roomexample.entity.Session;

public class FindAllSessionsTask extends AsyncTask<Void, Void, LiveData<List<Session>>> {

    public interface Callback {
        void onSessionsAvailable(FindAllSessionsTask task, LiveData<List<Session>> sessions);
    }

    private final AppDatabase appDatabase;
    private final WeakReference<Callback> callbackReference;

    public FindAllSessionsTask(AppDatabase appDatabase, Callback callback) {
        this.appDatabase = appDatabase;
        this.callbackReference = new WeakReference<>(callback);
    }

    @Override
    protected LiveData<List<Session>> doInBackground(Void... voids) {
        return appDatabase.sessionDao().findAll();
    }

    @Override
    protected void onPostExecute(LiveData<List<Session>> sessions) {
        super.onPostExecute(sessions);

        final Callback callback = callbackReference.get();
        if (callback != null)
            callback.onSessionsAvailable(this, sessions);
    }
}
