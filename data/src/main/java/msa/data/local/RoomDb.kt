package msa.data.local

import androidx.room.*
import io.reactivex.Observable
import io.reactivex.Single
import msa.domain.entities.EventRecord
import msa.domain.entities.EventRecordType
import java.util.*

/**
 * Created by Abhi Muktheeswarar.
 */

@Entity
data class EventRecordEntity(
    @PrimaryKey(autoGenerate = true) var id: Int? = null, var startDate: Date,
    var endDate: Date,
    var durationInMilliSeconds: Long
)

@Dao
interface EventRecordDao {

    @Query("SELECT * FROM eventrecordentity")
    fun getEventRecords(): Observable<List<EventRecordEntity>>

    @Query("SELECT * FROM eventrecordentity WHERE id == :id")
    fun getEventRecord(id: Int): Single<EventRecordEntity>

    @Insert
    fun insertEventRecord(eventRecordEntity: EventRecordEntity): Single<Long>

    @Update
    fun updateEventRecord(eventRecordEntity: EventRecordEntity)

    @Delete
    fun deleteEventRecord(eventRecordEntity: EventRecordEntity)

}

class Converters {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromEventRecordType(value: String?): EventRecordType? {
        return value?.let { EventRecordType.valueOf(it) }
    }

    @TypeConverter
    fun eventRecordTypeToString(eventRecordType: EventRecordType?): String? {
        return eventRecordType?.name
    }
}

@Database(entities = [EventRecordEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class EventRecordDatabase : RoomDatabase() {

    abstract fun eventRecordDao(): EventRecordDao
}

object EventRecordMapper {

    fun transform(eventRecordEntity: EventRecordEntity) =
        EventRecord(
            id = eventRecordEntity.id!!,
            startDate = eventRecordEntity.startDate,
            endDate = eventRecordEntity.endDate,
            durationInMilliSeconds = eventRecordEntity.durationInMilliSeconds
        )

    fun transform(eventRecord: EventRecord) =
        EventRecordEntity(
            id = if (eventRecord.id != -1) eventRecord.id else null,
            startDate = eventRecord.startDate,
            endDate = eventRecord.endDate,
            durationInMilliSeconds = eventRecord.durationInMilliSeconds
        )
}

