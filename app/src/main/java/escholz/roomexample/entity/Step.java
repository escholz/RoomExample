package escholz.roomexample.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(indices = {@Index(value = {"session_id", "created_at"})},
        foreignKeys = @ForeignKey(entity = Session.class,
                                  parentColumns = "id",
                                  childColumns = "session_id",
                                  onDelete = CASCADE))
public class Step {
    @PrimaryKey
    public long id;
    public double longitude;
    public double latitude;
    @ColumnInfo(name = "created_at")
    public Date createdAt;
    @ColumnInfo(name = "session_id")
    public long sessionId;
}
