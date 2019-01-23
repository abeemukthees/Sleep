package msa.data.local

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import msa.domain.entities.EventRecord

/**
 * Created by Abhi Muktheeswarar.
 */

class LocalDataStore(private val eventRecordDatabase: EventRecordDatabase) {

    fun getEventRecords(): Observable<List<EventRecord>> {
        return eventRecordDatabase.eventRecordDao().getEventRecords()
            .map { eventRecordEntities -> eventRecordEntities.map { EventRecordMapper.transform(it) } }
    }

    fun insertEventRecord(eventRecord: EventRecord): Single<Long> {
        return eventRecordDatabase.eventRecordDao().insertEventRecord(EventRecordMapper.transform(eventRecord))
    }

    fun getEventRecord(id: Int): Single<EventRecord> {
        return eventRecordDatabase.eventRecordDao().getEventRecord(id).map { EventRecordMapper.transform(it) }
    }

    fun deleteAllEventRecords(): Completable {
        return Completable.fromCallable { eventRecordDatabase.clearAllTables() }
    }
}