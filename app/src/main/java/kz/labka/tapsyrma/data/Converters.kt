package kz.labka.tapsyrma.data

import androidx.room.TypeConverter
import java.time.LocalDate

/** Room тікелей сақтай алмайтын типтерді түрлендіру. */
class Converters {
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): Long? = date?.toEpochDay()

    @TypeConverter
    fun toLocalDate(epochDay: Long?): LocalDate? = epochDay?.let(LocalDate::ofEpochDay)

    @TypeConverter
    fun fromPriority(priority: Priority): String = priority.name

    @TypeConverter
    fun toPriority(name: String): Priority =
        Priority.entries.firstOrNull { it.name == name } ?: Priority.MEDIUM
}
