package escholz.roomexample.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(indices = {@Index(value = {"name"}, unique = true)})
public class Session {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String name;
    @ColumnInfo(name = "is_deleted")
    public boolean isDeleted;
}
