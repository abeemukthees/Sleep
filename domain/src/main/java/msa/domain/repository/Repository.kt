package msa.domain.repository

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import msa.domain.entities.EventRecord

/**
 * Created by Abhi Muktheeswarar.
 */

interface Repository {

    fun getEventRecords(): Observable<List<EventRecord>>

    fun insertEventRecord(eventRecord: EventRecord): Single<Long>

    fun getEventRecord(id: Int): Single<EventRecord>

    fun deleteAllEventRecords(): Completable
}