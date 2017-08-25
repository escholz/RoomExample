package escholz.roomexample;

import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.persistence.room.RoomDatabase;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toolbar;

import java.util.List;
import java.util.UUID;

import escholz.roomexample.entity.Session;
import escholz.roomexample.task.CreateSessionTask;
import escholz.roomexample.task.FindAllSessionsTask;
import escholz.roomexample.task.GetOrCreateDatabaseTask;

public class MainActivity extends LifecycleActivity
        implements GetOrCreateDatabaseTask.Callback, FindAllSessionsTask.Callback,
        CreateSessionTask.Callback, SessionViewHolder.OnSessionSelectedListener {

    private RecyclerView recyclerView;
    private AppDatabase appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Session newSession = new Session();
                newSession.name = UUID.randomUUID().toString();
                new CreateSessionTask(appDatabase, MainActivity.this).execute(newSession);
            }
        });

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setAdapter(new SessionsAdapter(this));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        new GetOrCreateDatabaseTask(this, AppDatabase.class, AppDatabase.NAME, this).execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        recyclerView = null;
    }

    /**
     * Callback for {@link GetOrCreateDatabaseTask}
     *
     * @param task
     * @param database
     */
    @Override
    public void onDatabaseCreated(GetOrCreateDatabaseTask task, RoomDatabase database) {
        if (database instanceof AppDatabase)
            appDatabase = (AppDatabase)database;

        new FindAllSessionsTask(appDatabase, this).execute();
    }

    /**
     * Callback for {@link FindAllSessionsTask}
     *
     * @param task
     * @param sessions
     */
    @Override
    public void onSessionsAvailable(FindAllSessionsTask task, LiveData<List<Session>> sessions) {
        ((SessionsAdapter)recyclerView.getAdapter()).setSessions(sessions.getValue());
        sessions.observe(this, new Observer<List<Session>>() {
            @Override
            public void onChanged(@Nullable List<Session> sessions) {
                ((SessionsAdapter)recyclerView.getAdapter()).setSessions(sessions);
            }
        });
    }

    /**
     * Callback when selecting {@link SessionViewHolder}
     *
     * @param viewHolder
     */
    @Override
    public void onSessionSelected(SessionViewHolder viewHolder) {
        if (viewHolder == null)
            return;
        Session session = viewHolder.getSession();
        if (session != null) {
            Intent mapsIntent = new Intent(MainActivity.this, MapsActivity.class);
            mapsIntent.putExtra(MapsActivity.EXTRA_SESSION_ID, session.id);
            startActivity(mapsIntent);
        }
    }

    /**
     * Callback for {@link CreateSessionTask}
     *
     * @param task
     * @param sessionIds
     */
    @Override
    public void onSessionCreated(CreateSessionTask task, long[] sessionIds) {
        if (sessionIds.length > 0) {
            Intent mapsIntent = new Intent(MainActivity.this, MapsActivity.class);
            mapsIntent.putExtra(MapsActivity.EXTRA_SESSION_ID, sessionIds[0]);
            startActivity(mapsIntent);
        }
    }

}
