package escholz.roomexample.task;

import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GetOrCreateDatabaseTask extends AsyncTask<Void, Void, RoomDatabase> {

    public interface Callback {
        void onDatabaseCreated(GetOrCreateDatabaseTask task, RoomDatabase database);
    }

    private static final Object lockObject = new Object();
    private static final Map<String, RoomDatabase> databaseCache = new ConcurrentHashMap<>();
    private final Context applicationContext;
    private final Class<? extends RoomDatabase> databaseClass;
    private final String databaseName;
    private final WeakReference<Callback> callbackReference;

    public GetOrCreateDatabaseTask(Context context, Class<? extends RoomDatabase> databaseClass,
                                   String databaseName, Callback callback) {
        applicationContext = context.getApplicationContext();
        this.databaseClass = databaseClass;
        this.databaseName = databaseName;
        this.callbackReference = new WeakReference<>(callback);
    }

    @Override
    protected RoomDatabase doInBackground(Void... voids) {
        synchronized (lockObject) {
            if (databaseCache.containsKey(databaseName)) {
                return databaseCache.get(databaseName);
            } else {
                final RoomDatabase database = Room.databaseBuilder(applicationContext,
                        databaseClass, databaseName).build();
                databaseCache.put(databaseName, database);
                return database;
            }
        }
    }

    @Override
    protected void onPostExecute(RoomDatabase database) {
        super.onPostExecute(database);

        final Callback callback = callbackReference.get();
        if (callback != null)
            callback.onDatabaseCreated(this, database);
    }
}
