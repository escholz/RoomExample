package escholz.roomexample.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import escholz.roomexample.entity.Session;

import static android.arch.persistence.room.OnConflictStrategy.ROLLBACK;

@Dao
public interface SessionDao {
    @Insert(onConflict = ROLLBACK)
    public long[] insert(Session... session);

    @Query("SELECT * FROM session")
    public LiveData<List<Session>> findAll();

    @Query(value = "SELECT session.* FROM session WHERE session.id = :id")
    public LiveData<Session> findFirstById(long id);

    @Update(onConflict = ROLLBACK)
    public void update(Session... sessions);

    @Query(value = "UPDATE session SET is_deleted = 1 WHERE id IN(:ids)")
    public int deleteById(int... ids);

    @Delete
    public int remove(Session... session);
}
