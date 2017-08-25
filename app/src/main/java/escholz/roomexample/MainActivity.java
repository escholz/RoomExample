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
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import escholz.roomexample.entity.Session;
import escholz.roomexample.task.CreateSessionTask;
import escholz.roomexample.task.FindAllSessionsTask;
import escholz.roomexample.task.GetOrCreateDatabaseTask;

public class MainActivity extends LifecycleActivity
        implements GetOrCreateDatabaseTask.Callback, FindAllSessionsTask.Callback {

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
                new CreateSessionTask(appDatabase, new CreateSessionTask.Callback() {
                    @Override
                    public void onSessionCreated(CreateSessionTask task, long[] sessionIds) {
                        if (sessionIds.length > 0) {
                            Intent mapsIntent = new Intent(MainActivity.this, MapsActivity.class);
                            mapsIntent.putExtra(MapsActivity.EXTRA_SESSION_ID, sessionIds[0]);
                            startActivity(mapsIntent);
                        }
                    }
                }).execute(newSession);
            }
        });

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setAdapter(new SessionsAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        new GetOrCreateDatabaseTask(this, AppDatabase.class, AppDatabase.NAME, this).execute();
    }

    @Override
    public void onDatabaseCreated(GetOrCreateDatabaseTask task, RoomDatabase database) {
        if (database instanceof AppDatabase)
            appDatabase = (AppDatabase)database;

        new FindAllSessionsTask(appDatabase, this).execute();
    }

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

    public static final class SessionViewHolder extends RecyclerView.ViewHolder {

        private final TextView sessionIdTextView;
        private final TextView nameTextView;
        private final TextView isDeletedTextView;

        public SessionViewHolder(View itemView) {
            super(itemView);

            sessionIdTextView = itemView.findViewById(R.id.session_id);
            nameTextView = itemView.findViewById(R.id.name);
            isDeletedTextView = itemView.findViewById(R.id.is_deleted);
        }

        public void onBind(Session session) {

            sessionIdTextView.setText(Long.toString(session.id));
            nameTextView.setText(session.name);
            isDeletedTextView.setText(Boolean.toString(session.isDeleted));
        }
    }

    public static final class SessionsAdapter extends RecyclerView.Adapter<SessionViewHolder> {

        private List<Session> sessions = new ArrayList<>();

        public void setSessions(List<Session> sessions) {
            this.sessions.clear();
            if (sessions != null)
                this.sessions.addAll(sessions);
            notifyDataSetChanged();
        }

        @Override
        public SessionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View holderLayout = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.session_summary, parent, false);
            return new SessionViewHolder(holderLayout);
        }

        @Override
        public void onBindViewHolder(SessionViewHolder holder, int position) {
            Session session = sessions.get(position);
            holder.onBind(session);
        }

        @Override
        public int getItemCount() {
            return sessions.size();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        recyclerView = null;
    }
}
