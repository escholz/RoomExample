package escholz.roomexample.converter;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

public class DateToLongTypeConverter {
    @TypeConverter
    public static Date longToDate(long value) {
        return new Date(value);
    }

    @TypeConverter
    public static long dateToLong(Date date) {
        return date == null ? 0 : date.getTime();
    }
}
