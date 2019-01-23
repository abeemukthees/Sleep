package msa.data

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import msa.domain.entities.EventRecord
import msa.domain.repository.Repository

/**
 * Created by Abhi Muktheeswarar.
 */

class EventRecordRepository(private val dataStoreFactory: DataStoreFactory) : Repository {

    override fun getEventRecords(): Observable<List<EventRecord>> = dataStoreFactory.localDataStore.getEventRecords()

    override fun insertEventRecord(eventRecord: EventRecord): Single<Long> =
        dataStoreFactory.localDataStore.insertEventRecord(eventRecord)

    override fun getEventRecord(id: Int): Single<EventRecord> = dataStoreFactory.localDataStore.getEventRecord(id)

    override fun deleteAllEventRecords(): Completable = dataStoreFactory.localDataStore.deleteAllEventRecords()
}