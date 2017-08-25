package escholz.roomexample;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.persistence.room.RoomDatabase;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import escholz.roomexample.entity.Session;
import escholz.roomexample.entity.Step;
import escholz.roomexample.task.FindFirstSessionByIdTask;
import escholz.roomexample.task.GetOrCreateDatabaseTask;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GetOrCreateDatabaseTask.Callback, LifecycleRegistryOwner {

    public static final String EXTRA_SESSION_ID = "escholz.roomexample.MapsActivity.sessionId";

    private final LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);

    private GoogleMap mMap;
    private AppDatabase appDatabase;
    private long sessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        final Intent callingIntent = getIntent();
        if (savedInstanceState != null) {
            sessionId = savedInstanceState.getLong(EXTRA_SESSION_ID);
        } else if (callingIntent != null) {
            sessionId = callingIntent.getLongExtra(EXTRA_SESSION_ID, -1);
        }

        new GetOrCreateDatabaseTask(this, AppDatabase.class, AppDatabase.NAME, this).execute();
    }

    @Override
    public void onDatabaseCreated(GetOrCreateDatabaseTask task, RoomDatabase database) {
        if (database instanceof AppDatabase)
            appDatabase = (AppDatabase)database;

        if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
            loadSession(sessionId);
        } else {
            getLifecycle().addObserver(new LifecycleObserver() {
                @OnLifecycleEvent(Lifecycle.Event.ON_START)
                public void onResume() {
                    // TODO: Make a listener that does both of these things.
                    getLifecycle().removeObserver(this);
                    loadSession(sessionId);
                }
            });
        }
    }

    private void loadSession(long sessionId) {
        new FindFirstSessionByIdTask(appDatabase, new FindFirstSessionByIdTask.Callback() {
            @Override
            public void onSessionAvailable(FindFirstSessionByIdTask task, LiveData<Session> session,
                                           LiveData<List<Step>> steps) {
                session.observe(MapsActivity.this, new Observer<Session>() {
                    @Override
                    public void onChanged(@Nullable Session session) {
                        setTitle(session.name);
                    }
                });
            }
        }).execute(sessionId);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putLong(EXTRA_SESSION_ID, sessionId);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public LifecycleRegistry getLifecycle() {
        return lifecycleRegistry;
    }
}
