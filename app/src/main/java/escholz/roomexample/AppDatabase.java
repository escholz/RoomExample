package escholz.roomexample;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import escholz.roomexample.converter.DateToLongTypeConverter;
import escholz.roomexample.dao.SessionDao;
import escholz.roomexample.dao.StepDao;
import escholz.roomexample.entity.Session;
import escholz.roomexample.entity.Step;

@Database(entities = {Session.class, Step.class}, version = 1)
@TypeConverters({DateToLongTypeConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    public static final String NAME = "app_database";

    public abstract SessionDao sessionDao();

    public abstract StepDao stepDao();
}
