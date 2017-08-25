package escholz.roomexample.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import escholz.roomexample.entity.Step;

import static android.arch.persistence.room.OnConflictStrategy.ABORT;

@Dao
public interface StepDao {
    @Insert(onConflict = ABORT)
    public void insert(Step... steps);

    @Query(value = "SELECT * FROM step WHERE session_id = :sessionId")
    public LiveData<List<Step>> findAllBySessionId(long sessionId);
}
