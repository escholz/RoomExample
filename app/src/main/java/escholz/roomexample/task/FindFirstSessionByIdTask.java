package escholz.roomexample.task;

import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.util.Pair;

import java.lang.ref.WeakReference;
import java.util.List;

import escholz.roomexample.AppDatabase;
import escholz.roomexample.entity.Session;
import escholz.roomexample.entity.Step;

public class FindFirstSessionByIdTask extends AsyncTask<Long, Void, Pair<LiveData<Session>, LiveData<List<Step>>>> {

    public interface Callback {
        void onSessionAvailable(FindFirstSessionByIdTask task, LiveData<Session> session,
                                LiveData<List<Step>> steps);
    }

    private final AppDatabase appDatabase;
    private final WeakReference<Callback> callbackReference;

    public FindFirstSessionByIdTask(AppDatabase appDatabase, Callback callback) {
        this.appDatabase = appDatabase;
        this.callbackReference = new WeakReference<>(callback);
    }

    @Override
    protected Pair<LiveData<Session>, LiveData<List<Step>>> doInBackground(Long... sessionIds) {
        if (sessionIds.length > 0) {
            LiveData<Session> session = appDatabase.sessionDao().findFirstById(sessionIds[0]);
            LiveData<List<Step>> steps = appDatabase.stepDao().findAllBySessionId(sessionIds[0]);
            return Pair.create(session, steps);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Pair<LiveData<Session>, LiveData<List<Step>>> data) {
        super.onPostExecute(data);

        final Callback callback = callbackReference.get();
        if (callback != null)
            callback.onSessionAvailable(this, data.first, data.second);
    }
}
